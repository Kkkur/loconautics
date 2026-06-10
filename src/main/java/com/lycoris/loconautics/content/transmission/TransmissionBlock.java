package com.lycoris.loconautics.content.transmission;

import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.AbstractEncasedShaftBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;

/**
 * Transmission Block.
 *
 * <p>Extends {@link AbstractEncasedShaftBlock} which provides:
 * <ul>
 *   <li>{@code AXIS} property — the rotation axis, placed along the player's look direction</li>
 *   <li>{@link #hasShaftTowards} — returns true for both faces on the AXIS when engaged</li>
 *   <li>{@link #getRotationAxis} — returns the AXIS value</li>
 * </ul>
 *
 * <p>Additional blockstate property:
 * <ul>
 *   <li>{@link #STAGE} — visual engagement stage, 0 = disengaged, 1–5 = engaged.
 *       Driven by the analog signal strength. Used purely for the model; all logic
 *       lives in {@link TransmissionBlockEntity}.</li>
 * </ul>
 *
 * <p>NOTE: {@code FACING} and {@code DIRECTION_ACTIVE} are NOT blockstate properties.
 * The axis is bidirectional — "input" and "output" are determined at runtime by which
 * side the kinetic source is on ({@code getSourceFacing()}), not baked into the blockstate.
 * Direction-active state lives on the BE only.
 */
public class TransmissionBlock extends AbstractEncasedShaftBlock
        implements IBE<TransmissionBlockEntity> {

    /** Visual engagement stage. 0 = disengaged. 1–5 = engaged (5 levels for model variation). */
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 5);

    // ------------------------------------------------------------------ constructor

    public TransmissionBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(STAGE, 0));
    }

    // ------------------------------------------------------------------ blockstate

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
        super.createBlockStateDefinition(builder); // adds AXIS
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // AXIS placement comes from the parent; STAGE starts at 0 (disengaged).
        return super.getStateForPlacement(context).setValue(STAGE, 0);
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

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos,
                                            Player player, BlockHitResult hit) {
        if (worldIn.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (be instanceof TransmissionBlockEntity tbe) {
            player.openMenu(tbe, tbe::sendToMenu);
        }
        return InteractionResult.CONSUME;
    }

    // ------------------------------------------------------------------ detach / re-attach kinetics
    //
    // Mirrors the GearshiftBlock pattern exactly:
    //   detachKinetics  → RotationPropagator.handleRemoved + scheduleNextTick
    //   tick            → RotationPropagator.handleAdded
    //
    // This forces the propagator to recompute the output speed whenever speed or
    // direction state changes on the BE.

    /**
     * Removes kinetics from the propagator and optionally schedules a re-attach on the
     * next tick. Call this whenever speed or direction changes on the BE.
     */
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

    // ------------------------------------------------------------------ neighbour changes
    //
    // The Transmission receives its signals exclusively through Create redstone-link
    // receivers registered in the BE. Neighbour changes are a no-op here.

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos,
                                Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        // Redstone links are handled via IRedstoneLinkable receivers in the BE.
    }
}