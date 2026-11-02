package com.hekaijun.inscribe.enchantment.frostthorns;

import java.util.UUID;

import com.hekaijun.inscribe.Inscribe;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Inscribe.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FrostThornsEvents {
    private static final UUID SLOW_ID = UUID.fromString("8f0e14d2-0b1a-4c2e-9a77-5d4f5d0e7a61");
    private static final UUID ATTACK_ID = UUID.fromString("8f0e14d2-0b1a-4c2e-9a77-5d4f5d0e7a62");
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        LivingEntity wearer = event.getEntity();
        if (wearer.level().isClientSide || !(event.getSource().getEntity() instanceof LivingEntity attacker)
                || attacker == wearer) {
            return;
        }

        int level = frostThornsLevel(wearer);
        boolean attackerPlayer = attacker instanceof Player;
        if (level <= 0 || wearer.getRandom().nextInt(100) >= chance(level, attackerPlayer)) {
            return;
        }

        int duration = durationTicks(level, attackerPlayer);

        MobEffectInstance effect = new MobEffectInstance(
                FrostThornsModule.FROST_THORNS_EFFECT.get(),
                duration,
                level - 1,
                false,
                true,
                true
        );
        // addEffect 会处理“免疫负面状态”的情况；免疫时返回 false，不施加。
        attacker.addEffect(effect);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }

        MobEffectInstance effect = entity.getEffect(FrostThornsModule.FROST_THORNS_EFFECT.get());
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance attackSpeed = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (effect == null) {
            remove(speed, SLOW_ID);
            remove(attackSpeed, ATTACK_ID);
            return;
        }

        int level = Math.min(3, effect.getAmplifier() + 1);
        boolean player = entity instanceof Player;
        double slow = slowPercent(level, player);
        int miningAmplifier = miningAmplifier(level, player);

        set(speed, SLOW_ID, "inscribe:frost_thorns_slow", -slow);
        set(attackSpeed, ATTACK_ID, "inscribe:frost_thorns_mining", -0.1D * (miningAmplifier + 1));
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        MobEffectInstance effect = player.getEffect(FrostThornsModule.FROST_THORNS_EFFECT.get());
        if (effect == null) {
            return;
        }
        int level = Math.min(3, effect.getAmplifier() + 1);
        int miningAmplifier = miningAmplifier(level, true);
        // 对齐原版挖掘疲劳的 30%/9% 量级。
        event.setNewSpeed(event.getNewSpeed() * (float) Math.pow(0.3D, miningAmplifier + 1));
    }

    private static int frostThornsLevel(LivingEntity entity) {
        int level = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            level = Math.max(level, stack.getEnchantmentLevel(FrostThornsModule.FROST_THORNS.get()));
        }
        return level;
    }

    private static int chance(int level, boolean player) {
        if (player) {
            return switch (level) {
                case 1 -> 20;
                case 2 -> 35;
                default -> 50;
            };
        }
        return switch (level) {
            case 1 -> 40;
            case 2 -> 60;
            default -> 90;
        };
    }

    private static int durationTicks(int level, boolean player) {
        int seconds = player
                ? switch (level) {
                    case 1 -> 3;
                    case 2 -> 4;
                    default -> 5;
                }
                : switch (level) {
                    case 1 -> 3;
                    case 2 -> 5;
                    default -> 8;
                };
        return seconds * 20;
    }

    private static int miningAmplifier(int level, boolean player) {
        if (player) {
            return level >= 3 ? 1 : 0;
        }
        return level >= 2 ? 1 : 0;
    }

    private static double slowPercent(int level, boolean player) {
        if (player) {
            return switch (level) {
                case 1 -> 0.15D;
                case 2 -> 0.20D;
                default -> 0.30D;
            };
        }
        return switch (level) {
            case 1 -> 0.20D;
            case 2 -> 0.30D;
            default -> 0.50D;
        };
    }

    private static void set(AttributeInstance instance, UUID id, String name, double amount) {
        if (instance == null) {
            return;
        }
        AttributeModifier old = instance.getModifier(id);
        if (old != null && Math.abs(old.getAmount() - amount) < 1.0E-6D) {
            return;
        }
        if (old != null) {
            instance.removeModifier(old);
        }
        instance.addTransientModifier(new AttributeModifier(
                id, name, amount, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    private static void remove(AttributeInstance instance, UUID id) {
        if (instance == null) {
            return;
        }
        AttributeModifier old = instance.getModifier(id);
        if (old != null) {
            instance.removeModifier(old);
        }
    }

    private FrostThornsEvents() {
    }
}
