package com.lycoris.loconautics.content.transmission;

import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.AbstractEncasedShaftBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;

// TODO: Fix all 6 sides rotation

public class TransmissionBlock extends AbstractEncasedShaftBlock
        implements IBE<TransmissionBlockEntity> {

    /** Visual engagement stage. 0 = disengaged, 1–5 = speed bands. */
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 5);

    /**
     * Visual indicator only — mirrors {@code directionActive} on the BE.
     * Written by the BE on every direction change so the model can reflect it.
     */
    public static final BooleanProperty DIRECTION_ACTIVE = BooleanProperty.create("direction_active");

    /**
     * Flips which perpendicular face is PLUS (speed) vs MINUS (direction).
     * false = default side, true = opposite side.
     * Toggled by right-clicking without sneaking.
     *
     * axis=Z: false→PLUS=WEST,  true→PLUS=EAST
     * axis=X: false→PLUS=NORTH, true→PLUS=SOUTH
     * axis=Y: false→PLUS=WEST,  true→PLUS=EAST
     */
    public static final BooleanProperty PLUS_SIDE = BooleanProperty.create("plus_side");

    // ------------------------------------------------------------------ constructor

    public TransmissionBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(STAGE, 0)
                        .setValue(DIRECTION_ACTIVE, false)
                        .setValue(PLUS_SIDE, false));
    }

    // ------------------------------------------------------------------ blockstate

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder); // adds AXIS first
        builder.add(STAGE, DIRECTION_ACTIVE, PLUS_SIDE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context)
                .setValue(STAGE, 0)
                .setValue(DIRECTION_ACTIVE, false)
                .setValue(PLUS_SIDE, false);
    }

    // ------------------------------------------------------------------ IBE

    @Override
    public Class<TransmissionBlockEntity> getBlockEntityClass() {
        return TransmissionBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TransmissionBlockEntity> getBlockEntityType() {
        return LoconauticsRegistries.TRANSMISSION_BE.get();
    }

    // ------------------------------------------------------------------ interaction

    /**
     * Sneak + right-click → open the frequency GUI.
     * Plain right-click → pass through so block placement works normally.
     */
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos,
                                            Player player, BlockHitResult hit) {
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;
        if (worldIn.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (be instanceof TransmissionBlockEntity tbe) {
            player.openMenu(tbe, tbe::sendToMenu);
        }
        return InteractionResult.CONSUME;
    }

    // ------------------------------------------------------------------ redstone

    /**
     * Reads direct redstone signals on the two side faces (perpendicular to the shaft axis).
     * PLUS face = speed signal (0–15 → RPM).
     * MINUS face = direction signal (>0 → flip output).
     */
    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos,
                                Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        if (worldIn.isClientSide) return;

        BlockEntity be = worldIn.getBlockEntity(pos);
        if (!(be instanceof TransmissionBlockEntity tbe)) return;

        Direction.Axis axis     = state.getValue(BlockStateProperties.AXIS);
        boolean        plusSide = state.getValue(PLUS_SIDE);
        Direction plusFace  = getPlusFace(axis, plusSide);
        Direction minusFace = plusFace.getOpposite();

        int     speedSignal = worldIn.getSignal(pos.relative(plusFace),  plusFace.getOpposite());
        boolean dirSignal   = worldIn.getSignal(pos.relative(minusFace), minusFace.getOpposite()) > 0;

        tbe.setRedstonePower(speedSignal);
        tbe.setDirectionActive(dirSignal);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return false;
    }

    // ------------------------------------------------------------------ face helpers

    /**
     * Returns the PLUS (speed) face given the shaft axis and plus_side toggle.
     *
     * axis=Z: false→WEST,  true→EAST
     * axis=X: false→NORTH, true→SOUTH
     * axis=Y: false→WEST,  true→EAST
     */
    public static Direction getPlusFace(Direction.Axis axis, boolean plusSide) {
        Direction base = switch (axis) {
            case Z -> Direction.WEST;
            case X -> Direction.NORTH;
            case Y -> Direction.WEST;
        };
        return plusSide ? base.getOpposite() : base;
    }

    /** Convenience overload — reads PLUS_SIDE from the blockstate. */
    public static Direction getPlusFace(BlockState state) {
        return getPlusFace(
                state.getValue(BlockStateProperties.AXIS),
                state.getValue(PLUS_SIDE));
    }

    public static Direction getMinusFace(BlockState state) {
        return getPlusFace(state).getOpposite();
    }

    // ------------------------------------------------------------------ wrench

    /**
     * Extends Create's default axis rotation to also handle {@code PLUS_SIDE}.
     *
     * Wrench behaviour:
     * - If the clicked face is along the current shaft axis → cycle PLUS_SIDE
     *   (the axis can't rotate further along its own axis, so we use that click
     *   to swap which perpendicular face is PLUS vs MINUS).
     * - Otherwise → let the parent rotate the AXIS property as normal, and reset
     *   PLUS_SIDE to false (canonical orientation for the new axis).
     */
    @Override
    public BlockState getRotatedBlockState(BlockState state, Direction targetedFace) {
        Direction.Axis currentAxis = state.getValue(BlockStateProperties.AXIS);
        if (targetedFace.getAxis() == currentAxis) {
            // Same axis: can't rotate axis, so toggle PLUS_SIDE instead
            return state.cycle(PLUS_SIDE);
        }
        // Different axis: rotate normally, reset PLUS_SIDE to default
        return super.getRotatedBlockState(state, targetedFace).setValue(PLUS_SIDE, false);
    }

    // ------------------------------------------------------------------ detach / re-attach kinetics

    public void detachKinetics(Level worldIn, BlockPos pos, boolean reAttachNextTick) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (!(be instanceof KineticBlockEntity kbe)) return;
        RotationPropagator.handleRemoved(worldIn, pos, kbe);
        if (reAttachNextTick) {
            worldIn.scheduleTick(pos, this, 1, TickPriority.EXTREMELY_HIGH);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (!(be instanceof KineticBlockEntity kbe)) return;
        RotationPropagator.handleAdded(worldIn, pos, kbe);
    }
}