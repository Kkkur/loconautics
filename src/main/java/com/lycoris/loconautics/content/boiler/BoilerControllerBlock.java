package com.lycoris.loconautics.content.boiler;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Boiler Controller Block — the single required controller for the steam boiler multiblock.
 *
 * <p>Must be placed adjacent to at least one boiler body block. On placement (and on
 * neighbour change) the controller scans the connected structure via
 * {@link BoilerMultiblockValidator} and caches the result in the BE.
 *
 * <p>Handles the UI (Phase 3), fluid I/O (Phase 4), and drives the boiler state
 * machine every tick.
 */
public class BoilerControllerBlock extends BaseEntityBlock {

    public BoilerControllerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(BoilerControllerBlock::new);
    }

    // ------------------------------------------------------------------ neighbour / placement

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos,
                        BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        triggerScan(level, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                net.minecraft.world.level.block.Block block,
                                BlockPos fromPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, block, fromPos, movedByPiston);
        triggerScan(level, pos);
    }

    private void triggerScan(Level level, BlockPos pos) {
        if (level.isClientSide) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BoilerControllerBlockEntity controller) {
            controller.onStructureChanged();
        }
    }

    // ------------------------------------------------------------------ block entity

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BoilerBlocks.createBoilerControllerBE(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return type == BoilerBlocks.BOILER_CONTROLLER_BE.get()
                ? (lvl, pos, st, be) -> ((BoilerControllerBlockEntity) be).tick()
                : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}