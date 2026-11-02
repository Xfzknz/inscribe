package com.hekaijun.inscribe.enchantment.thunderstrike;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.hekaijun.inscribe.Inscribe;
import com.hekaijun.inscribe.core.BookOfferHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Inscribe.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ThunderstrikeEvents {
    private static final Map<UUID, Long> LAST_CAST = new HashMap<>();
    private static final Map<UUID, Long> IMMUNE_UNTIL = new HashMap<>();

    private ThunderstrikeEvents() {
    }

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (event.isCanceled() || player.level().isClientSide
                || !(event.getTarget() instanceof LivingEntity target)
                || target instanceof ArmorStand) {
            return;
        }

        int level = player.getMainHandItem()
                .getEnchantmentLevel(ThunderstrikeModule.THUNDERSTRIKE.get());
        if (level <= 0) {
            return;
        }

        long cooldown = switch (level) {
            case 1 -> 120L;
            case 2 -> 60L;
            default -> 30L;
        };
        long now = player.level().getGameTime();
        Long previous = LAST_CAST.get(player.getUUID());
        if (previous != null && now - previous < cooldown) {
            return;
        }

        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
        if (bolt == null) {
            return;
        }
        bolt.moveTo(target.getX(), target.getY(), target.getZ());
        if (player instanceof ServerPlayer serverPlayer) {
            bolt.setCause(serverPlayer);
        }

        if (serverLevel.addFreshEntity(bolt)) {
            LAST_CAST.put(player.getUUID(), now);
            // 真实闪电的范围伤害/火焰仍可能出现；只取消短窗口内的直接雷击伤害。
            IMMUNE_UNTIL.put(player.getUUID(), now + 10L);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof ArmorStand && event.getSource().is(DamageTypes.LIGHTNING_BOLT)) {
            event.setCanceled(true);
            return;
        }
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!event.getSource().is(DamageTypes.LIGHTNING_BOLT)) {
            return;
        }

        Long immuneUntil = IMMUNE_UNTIL.get(player.getUUID());
        if (immuneUntil != null && player.level().getGameTime() <= immuneUntil) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID id = event.getEntity().getUUID();
        LAST_CAST.remove(id);
        IMMUNE_UNTIL.remove(id);
    }
}
