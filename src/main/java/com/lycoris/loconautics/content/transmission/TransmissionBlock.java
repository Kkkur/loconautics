package com.lycoris.loconautics.content.transmission;

import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * Transmission block — a kinetic speed regulator driven by redstone.
 *
 * Blockstate properties:
 *   AXIS  (from RotatedPillarKineticBlock) — X / Y / Z, determines shaft orientation
 *   STAGE — 0 (off) … 5 (full), drives texture variant for visual feedback
 *
 * The block itself just:
 *   1. Detects redstone neighbour changes and forwards the signal to the BE.
 *   2. Keeps STAGE in sync for the renderer/blockstate variants.
 */
public class TransmissionBlock extends RotatedPillarKineticBlock implements IBE<TransmissionBlockEntity> {

    /** Visual stage: 0 = off, 1–5 = power bands 1-3 / 4-6 / 7-9 / 10-12 / 13-15 */
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 5);

    public TransmissionBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(STAGE, 0)
        );
    }

    // ------------------------------------------------------------------ state

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STAGE);
    }

    // ------------------------------------------------------------------ placement

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        if (state == null) return null;
        return state.setValue(STAGE, 0);
    }

    // ------------------------------------------------------------------ redstone

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block changedBlock, BlockPos changedFrom, boolean isMoving) {
        super.neighborChanged(state, level, pos, changedBlock, changedFrom, isMoving);
        if (level.isClientSide) return;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TransmissionBlockEntity tbe) {
            int signal = level.getBestNeighborSignal(pos);
            tbe.setRedstonePower(signal);
        }
    }

    // ------------------------------------------------------------------ block entity

    @Override
    public Class<TransmissionBlockEntity> getBlockEntityClass() {
        return TransmissionBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TransmissionBlockEntity> getBlockEntityType() {
        return LoconauticsRegistries.TRANSMISSION_BE.get();
    }

    // ------------------------------------------------------------------ helpers

    /**
     * Maps a redstone power value (0–15) to a visual stage (0–5).
     * Stage 0 = off; stages 1–5 map bands of 3 signal levels each.
     */
    public static int powerToStage(int power) {
        if (power == 0) return 0;
        return (int) Math.ceil(power / 3.0);
    }

    // ------------------------------------------------------------------ shaft axis

    /**
     * The transmission has shafts on the back and front faces (along its axis).
     * RotatedPillarKineticBlock already gives us the AXIS property; we just tell Create
     * which directions are valid shaft connections. Both ends of the axis are valid.
     */
    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public boolean hasShaftTowards(net.minecraft.world.level.LevelReader world, BlockPos pos,
                                   BlockState state, Direction face) {
        return face.getAxis() == state.getValue(AXIS);
    }
}