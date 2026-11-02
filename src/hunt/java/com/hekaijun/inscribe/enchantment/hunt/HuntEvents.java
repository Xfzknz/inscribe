package com.hekaijun.inscribe.enchantment.hunt;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.hekaijun.inscribe.Inscribe;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = Inscribe.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class HuntEvents {
    private static final int MAX_STACKS = 5;
    private static final int DURATION_TICKS = 4 * 20;
    private static final Map<UUID, HuntState> STATES = new ConcurrentHashMap<>();
    private static final Map<UUID, PrimaryTarget> PRIMARY_TARGETS = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (event.isCanceled() || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (event.getTarget() instanceof LivingEntity target) {
            long now = player.level().getGameTime();
            PRIMARY_TARGETS.put(player.getUUID(), new PrimaryTarget(target.getUUID(), now + 2));
        }
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.isCanceled() || event.getEntity().level().isClientSide) {
            return;
        }
        Entity source = event.getSource().getDirectEntity();
        if (!(source instanceof ServerPlayer player)) {
            return;
        }
        LivingEntity target = event.getEntity();
        if (target == player) {
            return;
        }

        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof SwordItem)) {
            return;
        }
        int level = Math.min(3, weapon.getEnchantmentLevel(HuntModule.HUNT.get()));
        if (level <= 0) {
            return;
        }

        long now = player.level().getGameTime();
        PrimaryTarget primary = PRIMARY_TARGETS.get(player.getUUID());
        if (primary != null
                && now <= primary.until()
                && !target.getUUID().equals(primary.target())) {
            // 同一次挥击的横扫/群伤次要目标：不叠层、不转火、不清零。
            return;
        }

        HuntState old = STATES.get(player.getUUID());
        int stacks = old != null
                && old.target().equals(target.getUUID())
                && now < old.expiresAt()
                ? old.stacks()
                : 0;
        stacks = Math.min(MAX_STACKS, stacks + 1);

        double bonus = perStack(level) * stacks;
        if (target instanceof Player) {
            bonus *= 0.5D;
        }
        event.setAmount((float) (event.getAmount() * (1.0D + bonus)));

        STATES.put(player.getUUID(), new HuntState(target.getUUID(), stacks, now + DURATION_TICKS));
        applyEffect(player, stacks);
    }

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        long now = player.level().getGameTime();
        PrimaryTarget primary = PRIMARY_TARGETS.get(player.getUUID());
        if (primary != null && now >= primary.until()) {
            PRIMARY_TARGETS.remove(player.getUUID());
        }

        HuntState state = STATES.get(player.getUUID());
        if (state == null) {
            if (player.hasEffect(HuntModule.HUNT_EFFECT.get())) {
                player.removeEffect(HuntModule.HUNT_EFFECT.get());
            }
            return;
        }

        Entity target = player.serverLevel().getEntity(state.target());
        if (now >= state.expiresAt()
                || !(target instanceof LivingEntity living)
                || !living.isAlive()) {
            reset(player);
            return;
        }

        if (!player.hasEffect(HuntModule.HUNT_EFFECT.get())) {
            applyEffect(player, state.stacks());
        }
        if (state.stacks() >= 3) {
            spawnOrbitParticles(player);
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            reset(player);
        }
        UUID dead = event.getEntity().getUUID();
        STATES.entrySet().removeIf(entry -> entry.getValue().target().equals(dead));
    }

    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            reset(player);
        }
    }

    @SubscribeEvent
    public static void onLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            STATES.remove(player.getUUID());
            PRIMARY_TARGETS.remove(player.getUUID());
            player.removeEffect(HuntModule.HUNT_EFFECT.get());
        }
    }

    public static void reset(ServerPlayer player) {
        STATES.remove(player.getUUID());
        PRIMARY_TARGETS.remove(player.getUUID());
        player.removeEffect(HuntModule.HUNT_EFFECT.get());
    }

    private static void applyEffect(ServerPlayer player, int stacks) {
        player.removeEffect(HuntModule.HUNT_EFFECT.get());
        player.addEffect(new MobEffectInstance(
                HuntModule.HUNT_EFFECT.get(),
                -1,
                stacks - 1,
                false,
                true,
                true
        ));
    }

    private static void spawnOrbitParticles(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        DustParticleOptions dust = new DustParticleOptions(
                new org.joml.Vector3f(0.9F, 0.05F, 0.05F),
                0.8F
        );
        double baseY = player.getY() + player.getBbHeight() * 0.6D;
        for (int i = 0; i < 2; i++) {
            double angle = player.tickCount * 0.25D + Math.PI * i;
            double x = player.getX() + Math.cos(angle) * 0.8D;
            double z = player.getZ() + Math.sin(angle) * 0.8D;
            double y = baseY + Math.sin(player.tickCount * 0.15D + i) * 0.18D;
            level.sendParticles(dust, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private static double perStack(int level) {
        return switch (level) {
            case 1 -> 0.03D;
            case 2 -> 0.05D;
            default -> 0.07D;
        };
    }

    private record HuntState(UUID target, int stacks, long expiresAt) {
    }

    private record PrimaryTarget(UUID target, long until) {
    }

    private HuntEvents() {
    }
}
