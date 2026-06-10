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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;

public class TransmissionBlock extends AbstractEncasedShaftBlock
        implements IBE<TransmissionBlockEntity> {

    /** Visual engagement stage. 0 = disengaged, 1–5 = speed bands. */
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 5);

    /**
     * Visual indicator only — mirrors {@code directionActive} on the BE.
     * Written by the BE on every direction change so the model can reflect it.
     */
    public static final BooleanProperty DIRECTION_ACTIVE = BooleanProperty.create("direction_active");

    // ------------------------------------------------------------------ constructor

    public TransmissionBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(STAGE, 0)
                        .setValue(DIRECTION_ACTIVE, false));
    }

    // ------------------------------------------------------------------ blockstate

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE, DIRECTION_ACTIVE);
        super.createBlockStateDefinition(builder); // adds AXIS
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context)
                .setValue(STAGE, 0)
                .setValue(DIRECTION_ACTIVE, false);
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

    /** Crouch + right-click opens the frequency GUI. Plain right-click does nothing extra. */
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
     *
     * This coexists with the link-network receivers — whichever source delivers a
     * signal at any given moment is used.
     */
    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos,
                                Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        if (worldIn.isClientSide) return;

        BlockEntity be = worldIn.getBlockEntity(pos);
        if (!(be instanceof TransmissionBlockEntity tbe)) return;

        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        Direction plusFace  = getPlusFace(axis);
        Direction minusFace = getMinusFace(axis);

        int     speedSignal = worldIn.getSignal(pos.relative(plusFace),  plusFace);
        boolean dirSignal   = worldIn.getSignal(pos.relative(minusFace), minusFace) > 0;

        tbe.setRedstonePower(speedSignal);
        tbe.setDirectionActive(dirSignal);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return false;
    }

    // ------------------------------------------------------------------ face helpers

    /**
     * PLUS face (speed) — the face showing the + texture.
     * For axis=Z: west. For axis=X: north. For axis=Y: west.
     * Confirmed with north-face placement as anchor.
     */
    public static Direction getPlusFace(Direction.Axis axis) {
        return switch (axis) {
            case Z -> Direction.EAST;  // facing north: speed (plus) is left = east
            case X -> Direction.SOUTH; // facing west:  speed (plus) is left = south
            case Y -> Direction.EAST;  // shaft up:     speed (plus) is left = east
        };
    }

    /**
     * MINUS face (direction) — opposite of plus.
     */
    public static Direction getMinusFace(Direction.Axis axis) {
        return getPlusFace(axis).getOpposite();
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