package com.hekaijun.inscribe.enchantment.lavawalker;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.hekaijun.inscribe.Inscribe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Inscribe.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LavaWalkerEvents {
    private static final Map<UUID, BlockPos> LAST_POS = new HashMap<>();

    private LavaWalkerEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END
                || !(event.player instanceof ServerPlayer player)
                || player.tickCount % 2 != 0
                || player.isSpectator()
                || !player.onGround()) {
            return;
        }

        int level = lavaWalkerLevel(player);
        if (level <= 0) {
            return;
        }

        BlockPos base = player.blockPosition().below();
        BlockPos previous = LAST_POS.put(player.getUUID(), base);
        if (previous != null && previous.equals(base)) {
            return;
        }

        int radius = radiusFor(level);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) {
                    continue;
                }
                replaceLava(player.serverLevel(), base.offset(dx, 0, dz));
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getState().getBlock() instanceof LavaShellBlock)) {
            return;
        }
        Player player = event.getPlayer();
        if (lavaWalkerLevel(player) <= 0 || !isInActiveArea(player, event.getPos())) {
            return;
        }

        LevelAccessor accessor = event.getLevel();
        if (!(accessor instanceof ServerLevel level)) {
            return;
        }

        event.setCanceled(true);
        if (isMoving(player)) {
            level.setBlockAndUpdate(event.getPos(), LavaWalkerModule.LAVA_SHELL.get().defaultBlockState());
        } else {
            level.setBlockAndUpdate(event.getPos(), Blocks.LAVA.defaultBlockState());
        }
    }

    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        LAST_POS.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        LAST_POS.remove(event.getEntity().getUUID());
    }

    static boolean hasInnerPlayer(ServerLevel level, BlockPos pos) {
        for (ServerPlayer player : level.players()) {
            int enchantmentLevel = lavaWalkerLevel(player);
            if (enchantmentLevel <= 0) {
                continue;
            }
            BlockPos base = player.blockPosition().below();
            int radius = radiusFor(enchantmentLevel);
            int dx = pos.getX() - base.getX();
            int dz = pos.getZ() - base.getZ();
            if (pos.getY() == base.getY() && dx * dx + dz * dz <= radius * radius) {
                return true;
            }
        }
        return false;
    }

    private static void replaceLava(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.LAVA) && state.getFluidState().isSource()) {
            level.setBlockAndUpdate(pos, LavaWalkerModule.LAVA_SHELL.get().defaultBlockState());
        }
    }

    private static boolean isInActiveArea(Player player, BlockPos pos) {
        int level = lavaWalkerLevel(player);
        if (level <= 0) {
            return false;
        }
        int radius = radiusFor(level);
        BlockPos base = player.blockPosition().below();
        int dx = pos.getX() - base.getX();
        int dz = pos.getZ() - base.getZ();
        return pos.getY() == base.getY() && dx * dx + dz * dz <= radius * radius;
    }

    private static boolean isMoving(Player player) {
        return player.walkDist != player.walkDistO
                || player.getX() != player.xOld
                || player.getZ() != player.zOld;
    }

    private static int radiusFor(int level) {
        return Math.min(16, 2 + level);
    }

    private static int lavaWalkerLevel(Player player) {
        return player.getItemBySlot(EquipmentSlot.FEET)
                .getEnchantmentLevel(LavaWalkerModule.LAVA_WALKER.get());
    }
}
