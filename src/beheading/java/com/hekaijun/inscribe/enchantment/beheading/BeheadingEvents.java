package com.hekaijun.inscribe.enchantment.beheading;

import com.mojang.authlib.GameProfile;
import com.hekaijun.inscribe.Inscribe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Inscribe.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BeheadingEvents {
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.isCanceled()) {
            return;
        }

        // 只认玩家亲手击杀：直接伤害来源必须是玩家，不能是箭、宠物或火焰。
        Entity source = event.getSource().getDirectEntity();
        if (!(source instanceof Player killer)
                || !(killer.getMainHandItem().getItem() instanceof AxeItem)) {
            return;
        }
        if (killer.getMainHandItem().getEnchantmentLevel(BeheadingModule.BEHEADING.get()) <= 0) {
            return;
        }

        LivingEntity target = event.getEntity();
        Item head = headFor(target);
        if (head == null) {
            return;
        }

        ItemStack stack = new ItemStack(head);
        if (target instanceof Player) {
            GameProfile profile = killer.getGameProfile();
            CompoundTag owner = NbtUtils.writeGameProfile(new CompoundTag(), profile);
            stack.getOrCreateTag().put("SkullOwner", owner);
        }

        ItemEntity drop = new ItemEntity(
                target.level(),
                target.getX(),
                target.getY() + 0.5D,
                target.getZ(),
                stack
        );
        drop.setPickUpDelay(10);
        event.getDrops().add(drop);
    }

    private static Item headFor(LivingEntity target) {
        EntityType<?> type = target.getType();
        if (type == EntityType.ZOMBIE) {
            return Items.ZOMBIE_HEAD;
        }
        if (type == EntityType.SKELETON) {
            return Items.SKELETON_SKULL;
        }
        if (type == EntityType.WITHER_SKELETON) {
            return Items.WITHER_SKELETON_SKULL;
        }
        if (type == EntityType.CREEPER) {
            return Items.CREEPER_HEAD;
        }
        if (type == EntityType.ENDER_DRAGON) {
            return Items.DRAGON_HEAD;
        }
        if (target instanceof Player) {
            return Items.PLAYER_HEAD;
        }
        return null;
    }

    private BeheadingEvents() {
    }
}
