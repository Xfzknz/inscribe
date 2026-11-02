package com.hekaijun.inscribe.enchantment.lifesiphon;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.hekaijun.inscribe.Inscribe;
import com.hekaijun.inscribe.core.BookOfferHelper;
import com.hekaijun.inscribe.core.InscribeNetwork;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;

@Mod.EventBusSubscriber(modid = Inscribe.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LifeSiphonEvents {
    private static final int COOLDOWN_TICKS = 20;
    private static final int MAX_LIFESTEAL = 4;
    private static final int KILL_HEAL = 2;

    private static final Map<UUID, Long> LAST_SIPHON = new HashMap<>();

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        Entity source = event.getSource().getEntity();
        if (!(source instanceof Player)) {
            return;
        }
        Player player = (Player) source;
        if (player.level().isClientSide) {
            return;
        }

        if (target.getType().is(EntityTypeTags.SKELETONS)) {
            return;
        }
        ItemStack weapon = player.getMainHandItem();
        if (weapon.getEnchantmentLevel(LifeSiphonModule.LIFE_SIPHON.get()) <= 0) {
            return;
        }
        if (player.getHealth() >= player.getMaxHealth()) {
            return;
        }

        long now = player.level().getGameTime();
        Long last = LAST_SIPHON.get(player.getUUID());
        if (last != null && now - last < COOLDOWN_TICKS) {
            return;
        }

        float dealt = Math.min(event.getAmount(), target.getHealth());
        int heal = Math.min(MAX_LIFESTEAL, (int) (dealt * 0.20F));
        if (heal <= 0) {
            return;
        }

        player.heal(heal);
        if (player instanceof ServerPlayer serverPlayer) {
            InscribeNetwork.sendShake(serverPlayer);
        }
        LAST_SIPHON.put(player.getUUID(), now);
        spawnHearts(target, heal);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        if (target.getType().is(EntityTypeTags.SKELETONS)) {
            return;
        }
        Entity source = event.getSource().getEntity();
        if (!(source instanceof Player)) {
            return;
        }
        Player player = (Player) source;
        if (player.level().isClientSide) {
            return;
        }

        if (player.getMainHandItem().getEnchantmentLevel(LifeSiphonModule.LIFE_SIPHON.get()) <= 0) {
            return;
        }
        if (player.getHealth() >= player.getMaxHealth()) {
            return;
        }

        player.heal(KILL_HEAL);
        if (player instanceof ServerPlayer serverPlayer) {
            InscribeNetwork.sendShake(serverPlayer);
        }
        spawnHearts(target, KILL_HEAL);
    }

    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        LAST_SIPHON.remove(event.getEntity().getUUID());
    }

    private static void spawnHearts(LivingEntity target, int count) {
        if (!(target.level() instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel) target.level();
        double x = target.getX();
        double y = target.getY() + target.getBbHeight() * 0.6D;
        double z = target.getZ();

        // 红色尘暴，数量多、向四周扩散，确保肉眼可见。
        serverLevel.sendParticles(
                new DustParticleOptions(new Vector3f(1.0F, 0.05F, 0.05F), 1.1F),
                x, y, z,
                24,
                0.7D, 0.7D, 0.7D,
                0.05D
        );
        serverLevel.sendParticles(
                ParticleTypes.DAMAGE_INDICATOR,
                x, y + 0.3D, z,
                8,
                0.5D, 0.6D, 0.5D,
                0.12D
        );
        serverLevel.sendParticles(
                ParticleTypes.HEART,
                x, y + 0.4D, z,
                4,
                0.4D, 0.5D, 0.4D,
                0.08D
        );
    }

    private LifeSiphonEvents() {
    }
}
