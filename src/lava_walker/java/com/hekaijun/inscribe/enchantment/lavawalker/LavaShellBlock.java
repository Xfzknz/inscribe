package com.hekaijun.inscribe.enchantment.lavawalker;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class LavaShellBlock extends Block {
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 2);
    public static final int STAGE_TICKS = 60;

    public LavaShellBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.scheduleTick(pos, this, STAGE_TICKS);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (LavaWalkerEvents.hasInnerPlayer(level, pos)) {
            if (state.getValue(STAGE) != 0) {
                level.setBlock(pos, state.setValue(STAGE, 0), 3);
            }
            level.scheduleTick(pos, this, STAGE_TICKS);
            return;
        }

        int stage = state.getValue(STAGE);
        if (stage < 2) {
            level.setBlock(pos, state.setValue(STAGE, stage + 1), 3);
            level.scheduleTick(pos, this, STAGE_TICKS);
        } else {
            level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
        }
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (player.getAbilities().instabuild) {
            return 1.0F;
        }

        ItemStack tool = player.getMainHandItem();
        if (!(tool.getItem() instanceof PickaxeItem pickaxe)) {
            return 0.0F;
        }
        if (pickaxe.getTier().getLevel() < 1) {
            return 0.0F;
        }
        // 所有石镐及以上固定 10 tick，不随材料速度变化。
        return 0.1F;
    }
}
