package com.hekaijun.inscribe.enchantment.sunder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.hekaijun.inscribe.Inscribe;
import com.hekaijun.inscribe.core.BookOfferHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Inscribe.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SunderEvents {
    private static final int DURATION_TICKS = 200;
    private static final int COOLDOWN_TICKS = 140;
    private static final int PENDING_TICKS = 5;

    private static final String OWNER = "inscribe:sunder_owner";
    private static final String LEVEL = "inscribe:sunder_level";
    private static final String UNTIL = "inscribe:sunder_until";

    private static final float[] ARMOR_REDUCTION = {0.0F, 0.05F, 0.09F, 0.14F, 0.20F};
    private static final float[] TOUGHNESS_REDUCTION = {0.0F, 0.15F, 0.25F, 0.35F, 0.50F};

    private static final Map<UUID, PendingSunder> PENDING = new HashMap<>();
    private static final Map<UUID, Long> LAST_SUNDER = new HashMap<>();
    private static final Map<UUID, Float> RAW_DAMAGE = new HashMap<>();

    private SunderEvents() {
    }

    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {
        Player player = event.getEntity();
        Entity targetEntity = event.getTarget();
        if (player.level().isClientSide || !(targetEntity instanceof LivingEntity)) {
            return;
        }
        LivingEntity target = (LivingEntity) targetEntity;
        if (!(player.getMainHandItem().getItem() instanceof AxeItem)) {
            return;
        }

        int level = player.getMainHandItem().getEnchantmentLevel(SunderModule.SUNDER.get());
        if (level <= 0 || !hasArmor(target)) {
            return;
        }

        long now = player.level().getGameTime();
        Long last = LAST_SUNDER.get(player.getUUID());
        if (last != null && now - last < COOLDOWN_TICKS) {
            return;
        }

        PENDING.put(target.getUUID(), new PendingSunder(player.getUUID(), level, now + PENDING_TICKS));
    }

    @SubscribeEvent
    public static void onShieldBlock(ShieldBlockEvent event) {
        LivingEntity target = event.getEntity();
        PendingSunder pending = PENDING.remove(target.getUUID());
        if (pending == null || event.isCanceled()) {
            return;
        }

        Entity attacker = event.getDamageSource().getEntity();
        if (attacker == null || !pending.attacker().equals(attacker.getUUID())) {
            return;
        }

        long now = target.level().getGameTime();
        if (now > pending.expiresAt() || !event.shieldTakesDamage()) {
            return;
        }

        ItemStack shield = target.getUseItem();
        if (!(shield.getItem() instanceof ShieldItem)) {
            return;
        }

        // 原版盾耐久约为 1 + floor(blockedDamage)，额外补 50%。
        int base = 1 + (int) Math.floor(event.getBlockedDamage());
        int extra = Math.max(1, Math.round(base * 0.5F));
        shield.hurtAndBreak(extra, target, p -> p.broadcastBreakEvent(target.getUsedItemHand()));
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();

        Entity direct = event.getSource().getDirectEntity();
        if (!(direct instanceof Player)) {
            return;
        }
        Player attacker = (Player) direct;

        long now = target.level().getGameTime();
        PendingSunder pending = PENDING.get(target.getUUID());
        if (pending != null && attacker.getUUID().equals(pending.attacker())
                && now <= pending.expiresAt()) {
            PENDING.remove(target.getUUID());
            if (hasArmor(target)) {
                applySunder(target, pending, now);
                LAST_SUNDER.put(attacker.getUUID(), now);
            }
        }

        SunderState state = getState(target, now);
        if (state == null || !state.attacker().equals(attacker.getUUID())
                || isPotion(event.getSource())) {
            return;
        }

        // 记住护甲计算前的伤害，用于 LivingDamageEvent 计算穿透增量。
        RAW_DAMAGE.put(target.getUUID(), event.getAmount());
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();

        Float raw = RAW_DAMAGE.remove(target.getUUID());
        if (raw == null) {
            return;
        }

        Entity sourceEntity = event.getSource().getEntity();
        if (sourceEntity == null) {
            return;
        }

        long now = target.level().getGameTime();
        SunderState state = getState(target, now);
        if (state == null || !state.attacker().equals(sourceEntity.getUUID())
                || isPotion(event.getSource())) {
            return;
        }

        float armor = (float) target.getAttributeValue(Attributes.ARMOR);
        float toughness = (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        if (armor <= 0.0F) {
            return;
        }

        int level = state.level();
        float armored = CombatRules.getDamageAfterAbsorb(raw, armor, toughness);
        float penetrated = CombatRules.getDamageAfterAbsorb(
                raw,
                armor * (1.0F - ARMOR_REDUCTION[level]),
                toughness * (1.0F - TOUGHNESS_REDUCTION[level])
        );
        float extra = penetrated - armored;
        if (extra > 0.0F) {
            event.setAmount(event.getAmount() + extra);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        UUID id = event.getEntity().getUUID();
        PENDING.remove(id);
        RAW_DAMAGE.remove(id);
    }

    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID id = event.getEntity().getUUID();
        LAST_SUNDER.remove(id);
        RAW_DAMAGE.remove(id);
        PENDING.entrySet().removeIf(entry -> entry.getValue().attacker().equals(id));
    }

    private static void applySunder(LivingEntity target, PendingSunder pending, long now) {
        CompoundTag tag = target.getPersistentData();
        tag.putUUID(OWNER, pending.attacker());
        tag.putInt(LEVEL, pending.level());
        tag.putLong(UNTIL, now + DURATION_TICKS);

        if (target.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    SunderModule.SUNDER_PARTICLE.get(),
                    target.getX(),
                    target.getY() + target.getBbHeight() + 0.35D,
                    target.getZ(),
                    1,
                    0.0D,
                    0.05D,
                    0.0D,
                    0.0D
            );
        }
    }

    private static SunderState getState(LivingEntity target, long now) {
        CompoundTag tag = target.getPersistentData();
        if (!tag.hasUUID(OWNER) || tag.getLong(UNTIL) < now) {
            return null;
        }
        int level = tag.getInt(LEVEL);
        if (level < 1 || level > 4) {
            return null;
        }
        return new SunderState(tag.getUUID(OWNER), level);
    }

    static String describeSunder(LivingEntity target) {
        CompoundTag tag = target.getPersistentData();
        if (!tag.hasUUID(OWNER)) {
            return "none";
        }
        long now = target.level().getGameTime();
        long until = tag.getLong(UNTIL);
        if (until < now) {
            return "expired";
        }
        return "level=" + tag.getInt(LEVEL)
                + ",remaining=" + (until - now)
                + ",owner=" + tag.getUUID(OWNER);
    }

    private static boolean isPotion(DamageSource source) {
        return source.is(DamageTypes.MAGIC) || source.is(DamageTypes.INDIRECT_MAGIC);
    }

    private static boolean hasArmor(LivingEntity target) {
        return target.getArmorValue() > 0;
    }

    private record PendingSunder(UUID attacker, int level, long expiresAt) {
    }

    private record SunderState(UUID attacker, int level) {
    }
}
