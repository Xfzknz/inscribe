package com.hekaijun.inscribe.enchantment.immolation;

import com.hekaijun.inscribe.Inscribe;
import com.hekaijun.inscribe.core.BookOfferHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Inscribe.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ImmolationEvents {
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity target = event.getEntity();
        if (event.isCanceled()
                || target instanceof Player
                || target.getType().is(Tags.EntityTypes.BOSSES)
                || isElite(target)) {
            return;
        }
        if (target.getType().getCategory() != MobCategory.MONSTER) {
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

        ItemStack weapon = player.getMainHandItem();
        int level = weapon.getEnchantmentLevel(ImmolationModule.IMMOLATION.get());
        if (level <= 0 || event.getDrops().isEmpty()) {
            return;
        }

        int xpPerItem = switch (level) {
            case 1 -> 2;
            case 2 -> 4;
            default -> 6;
        };
        int itemCount = 0;
        for (ItemEntity drop : event.getDrops()) {
            itemCount += drop.getItem().getCount();
        }
        if (itemCount <= 0) {
            return;
        }

        event.getDrops().clear();
        player.giveExperiencePoints(itemCount * xpPerItem);
    }

    private static boolean isElite(LivingEntity target) {
        return target.getType() == EntityType.WARDEN
                || target.getType() == EntityType.ELDER_GUARDIAN
                || target.getType() == EntityType.RAVAGER
                || target.getType() == EntityType.EVOKER
                || target.getType() == EntityType.PIGLIN_BRUTE
                || target.getMaxHealth() >= 80.0F;
    }

    private ImmolationEvents() {
    }
}
