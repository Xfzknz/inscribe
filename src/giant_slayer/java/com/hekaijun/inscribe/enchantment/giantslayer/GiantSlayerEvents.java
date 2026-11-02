package com.hekaijun.inscribe.enchantment.giantslayer;

import com.hekaijun.inscribe.Inscribe;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Inscribe.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GiantSlayerEvents {
    private static final float MAX_HEALTH_THRESHOLD = 500.0F;

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.isCanceled()) {
            return;
        }

        LivingEntity target = event.getEntity();
        if (target.getMaxHealth() > MAX_HEALTH_THRESHOLD) {
            // >500 的追加伤害在护甲结算之后加，走 onLivingDamage。
            return;
        }

        int level = meleeLevel(event.getSource().getDirectEntity());
        if (level <= 0) {
            return;
        }
        event.setAmount(event.getAmount() + bonusFor(target, level));
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.isCanceled()) {
            return;
        }

        LivingEntity target = event.getEntity();
        if (target.getMaxHealth() <= MAX_HEALTH_THRESHOLD) {
            return;
        }

        int level = meleeLevel(event.getSource().getDirectEntity());
        if (level <= 0) {
            return;
        }
        // 此时护甲/保护已结算，追加部分相当于绕过护甲的真实伤害。
        event.setAmount(event.getAmount() + bonusFor(target, level));
    }

    private static int meleeLevel(Entity direct) {
        if (!(direct instanceof Player player) || player.level().isClientSide) {
            return 0;
        }
        ItemStack weapon = player.getMainHandItem();
        return weapon.getEnchantmentLevel(GiantSlayerModule.GIANT_SLAYER.get());
    }

    private static float bonusFor(LivingEntity target, int level) {
        float percent = switch (level) {
            case 1 -> 0.01F;
            case 2 -> 0.015F;
            default -> 0.02F;
        };
        float bonus = target.getMaxHealth() * percent;

        if (target.getMaxHealth() <= MAX_HEALTH_THRESHOLD) {
            float cap = switch (level) {
                case 1 -> 2.0F;
                case 2 -> 4.0F;
                default -> 8.0F;
            };
            bonus = Math.min(bonus, cap);
        }
        return bonus;
    }

    private GiantSlayerEvents() {
    }
}
