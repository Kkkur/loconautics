package com.lycoris.loconautics.content.transmission;

import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Transmission block — a kinetic speed/direction regulator driven by two redstone signals.
 *
 * Blockstate properties:
 *   FACING         — the direction the output shaft points (all 6 directions)
 *   STAGE          — 0 (off) … 5 (full speed), drives speed-side texture
 *   DIRECTION_ACTIVE — whether the direction-override redstone signal is on
 *
 * Redstone faces (relative to FACING):
 *   Plus side  (left when looking at output face)  → direction override
 *   Minus side (right when looking at output face) → speed signal
 *
 * When DIRECTION_ACTIVE is true, output direction is reversed vs input.
 * When false, output direction follows input shaft direction.
 */
public class TransmissionBlock extends KineticBlock implements IBE<TransmissionBlockEntity> {

    /** Visual speed stage: 0 = off, 1–5 = power bands. */
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 5);

    /** True when the direction-override redstone signal is powered. */
    public static final BooleanProperty DIRECTION_ACTIVE = BooleanProperty.create("direction_active");

    /** The direction the output shaft exits the block. */
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public TransmissionBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(FACING, Direction.SOUTH)
                        .setValue(STAGE, 0)
                        .setValue(DIRECTION_ACTIVE, false)
        );
    }

    // ------------------------------------------------------------------ state

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, STAGE, DIRECTION_ACTIVE);
    }

    // ------------------------------------------------------------------ placement

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        // Face the player (output toward player)
        Direction facing = ctx.getNearestLookingDirection().getOpposite();
        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(STAGE, 0)
                .setValue(DIRECTION_ACTIVE, false);
    }

    // ------------------------------------------------------------------ interaction

    /**
     * Shift+right-click opens the frequency-binding GUI.
     * Plain right-click is left for any wrench/goggles behaviour from KineticBlock.
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (!player.isShiftKeyDown()) {
            return super.useWithoutItem(state, level, pos, player, hit);
        }
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TransmissionBlockEntity tbe && player instanceof ServerPlayer sp) {
            sp.openMenu(tbe, tbe::sendToMenu);
        }
        return InteractionResult.CONSUME;
    }

    // ------------------------------------------------------------------ redstone

    /**
     * Vanilla redstone fallback — fires when an adjacent block changes its signal.
     * This coexists with the link-network receivers: whichever source provides the
     * higher signal at any given moment wins (same semantics as Create's own link blocks).
     */
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block changedBlock, BlockPos changedFrom, boolean isMoving) {
        super.neighborChanged(state, level, pos, changedBlock, changedFrom, isMoving);
        if (level.isClientSide) return;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TransmissionBlockEntity tbe) {
            Direction facing = state.getValue(FACING);

            Direction speedFace = getMinusFace(facing);
            Direction dirFace   = speedFace.getOpposite();

            int     speedSignal = level.getSignal(pos.relative(speedFace), speedFace);
            boolean dirSignal   = level.getSignal(pos.relative(dirFace),   dirFace)   > 0;

            tbe.setVanillaRedstonePower(speedSignal, dirSignal);
        }
    }

    /**
     * Returns the "minus" (speed) face — the face to the right when you look
     * from behind the block toward its output (FACING direction).
     * Uses a 90° clockwise rotation of FACING about the Y axis for horizontal
     * facings, and stable mappings for up/down.
     */
    public static Direction getMinusFace(Direction facing) {
        return switch (facing) {
            case NORTH -> Direction.EAST;   // looking south→north, right = east
            case SOUTH -> Direction.WEST;   // looking north→south, right = west
            case EAST  -> Direction.SOUTH;  // looking west→east,   right = south
            case WEST  -> Direction.NORTH;  // looking east→west,   right = north
            case UP    -> Direction.EAST;   // shaft pointing up,   speed on east
            case DOWN  -> Direction.WEST;   // shaft pointing down, speed on west
        };
    }

    // ------------------------------------------------------------------ shaft axis / connections

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(net.minecraft.world.level.LevelReader world, BlockPos pos,
                                   BlockState state, Direction face) {
        // Shafts only on the two faces along the block's axis
        if (face.getAxis() != state.getValue(FACING).getAxis()) return false;
        // Disengaged: no shaft connections so rotation can't propagate
        return state.getValue(STAGE) != 0;
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
     */
    public static int powerToStage(int power) {
        if (power == 0) return 0;
        return (int) Math.ceil(power / 3.0);
    }
}