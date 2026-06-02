/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.RotationPropagator
 *  com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock
 *  com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.ticks.TickPriority
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.directional_gearshift;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.Nullable;

public class DirectionalGearshiftBlock
extends DirectionalAxisKineticBlock
implements IBE<SplitShaftBlockEntity>,
IRotate {
    public static final BooleanProperty LEFT_POWERED = BooleanProperty.create((String)"left_powered");
    public static final BooleanProperty RIGHT_POWERED = BooleanProperty.create((String)"right_powered");

    public DirectionalGearshiftBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)LEFT_POWERED, (Comparable)Boolean.valueOf(false)));
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)RIGHT_POWERED, (Comparable)Boolean.valueOf(false)));
    }

    public BlockState updateAfterWrenched(BlockState newState, UseOnContext context) {
        return super.updateAfterWrenched(this.getPoweredState(context.getLevel(), newState, context.getClickedPos()), context);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(new Property[]{LEFT_POWERED}).add(new Property[]{RIGHT_POWERED}));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction darkDirection;
        Direction lookingDirection = pContext.getNearestLookingDirection();
        boolean shiftKeyDown = pContext.getPlayer().isShiftKeyDown();
        Direction.Axis preferredAxis = RotatedPillarKineticBlock.getPreferredAxis((BlockPlaceContext)pContext);
        boolean axisAlongFirst = false;
        if (preferredAxis != null && preferredAxis != lookingDirection.getAxis() && !shiftKeyDown) {
            darkDirection = lookingDirection;
            if (preferredAxis == Direction.Axis.X) {
                axisAlongFirst = true;
            } else if (preferredAxis == Direction.Axis.Y && lookingDirection.getAxis() == Direction.Axis.X) {
                axisAlongFirst = true;
            }
        } else if (lookingDirection.getAxis().isHorizontal()) {
            darkDirection = lookingDirection.getCounterClockWise();
            if (lookingDirection.getAxis() == Direction.Axis.X) {
                axisAlongFirst = true;
            }
        } else {
            darkDirection = pContext.getHorizontalDirection().getCounterClockWise();
            if (pContext.getHorizontalDirection().getAxis() == Direction.Axis.Z) {
                axisAlongFirst = true;
            }
        }
        if (shiftKeyDown) {
            darkDirection = darkDirection.getOpposite();
        }
        BlockState state = (BlockState)((BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)darkDirection)).setValue((Property)AXIS_ALONG_FIRST_COORDINATE, (Comparable)Boolean.valueOf(axisAlongFirst));
        return this.getPoweredState(pContext.getLevel(), state, pContext.getClickedPos());
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) {
            return;
        }
        boolean previouslyLeftPowered = (Boolean)state.getValue((Property)LEFT_POWERED);
        boolean previouslyRightPowered = (Boolean)state.getValue((Property)RIGHT_POWERED);
        BlockState newState = this.getPoweredState(level, state, pos);
        if (previouslyLeftPowered != (Boolean)newState.getValue((Property)LEFT_POWERED) || previouslyRightPowered != (Boolean)newState.getValue((Property)RIGHT_POWERED)) {
            this.detachKinetics(level, pos, true);
            level.setBlock(pos, newState, 2);
        }
    }

    public BlockState getPoweredState(Level level, BlockState state, BlockPos pos) {
        boolean previouslyRightPowered;
        Direction leftDirection = this.getLeftDirection(state);
        Direction rightDirection = this.getRightDirection(state);
        int leftSignal = level.getSignal(pos.offset(leftDirection.getNormal()), leftDirection);
        int rightSignal = level.getSignal(pos.offset(rightDirection.getNormal()), rightDirection);
        boolean previouslyLeftPowered = (Boolean)state.getValue((Property)LEFT_POWERED);
        if (previouslyLeftPowered != leftSignal > 0) {
            state = (BlockState)state.cycle((Property)LEFT_POWERED);
        }
        if ((previouslyRightPowered = ((Boolean)state.getValue((Property)RIGHT_POWERED)).booleanValue()) != rightSignal > 0) {
            state = (BlockState)state.cycle((Property)RIGHT_POWERED);
        }
        return state;
    }

    public void detachKinetics(Level worldIn, BlockPos pos, boolean reAttachNextTick) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (!(be instanceof KineticBlockEntity)) {
            return;
        }
        RotationPropagator.handleRemoved((Level)worldIn, (BlockPos)pos, (KineticBlockEntity)((KineticBlockEntity)be));
        if (reAttachNextTick) {
            worldIn.scheduleTick(pos, (Block)this, 1, TickPriority.EXTREMELY_HIGH);
        }
    }

    public Direction getLeftDirection(BlockState state) {
        return (Direction)state.getValue((Property)FACING);
    }

    public Direction getRightDirection(BlockState state) {
        return this.getLeftDirection(state).getOpposite();
    }

    public Class<SplitShaftBlockEntity> getBlockEntityClass() {
        return SplitShaftBlockEntity.class;
    }

    public BlockEntityType<? extends SplitShaftBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.DIRECTIONAL_GEARSHIFT.get();
    }

    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (!(be instanceof KineticBlockEntity)) {
            return;
        }
        KineticBlockEntity kte = (KineticBlockEntity)be;
        RotationPropagator.handleAdded((Level)worldIn, (BlockPos)pos, (KineticBlockEntity)kte);
    }

    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        InteractionResult interactionResult = super.onWrenched(state, context);
        if (interactionResult.consumesAction() && !context.getLevel().isClientSide) {
            this.detachKinetics(context.getLevel(), context.getClickedPos(), true);
        }
        return interactionResult;
    }
}
