/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.SignalGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.PushReaction
 */
package com.simibubi.create.content.kinetics.chainDrive;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.chainDrive.ChainGearshiftBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;

public class ChainDriveBlock
extends RotatedPillarKineticBlock
implements IBE<KineticBlockEntity>,
TransformableBlock {
    public static final Property<Part> PART = EnumProperty.create((String)"part", Part.class);
    public static final BooleanProperty CONNECTED_ALONG_FIRST_COORDINATE = DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE;

    public ChainDriveBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(PART, (Comparable)((Object)Part.NONE)));
    }

    public boolean shouldCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
        return false;
    }

    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.NORMAL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{PART, CONNECTED_ALONG_FIRST_COORDINATE}));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction.Axis axis;
        Direction.Axis placedAxis = context.getNearestLookingDirection().getAxis();
        Direction.Axis axis2 = axis = context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? placedAxis : ChainDriveBlock.getPreferredAxis(context);
        if (axis == null) {
            axis = placedAxis;
        }
        BlockState state = (BlockState)this.defaultBlockState().setValue((Property)AXIS, (Comparable)axis);
        for (Direction facing : Iterate.directions) {
            if (facing.getAxis() == axis) continue;
            BlockPos pos = context.getClickedPos();
            BlockPos offset = pos.relative(facing);
            state = this.updateShape(state, facing, context.getLevel().getBlockState(offset), (LevelAccessor)context.getLevel(), pos, offset);
        }
        return state;
    }

    public BlockState updateShape(BlockState stateIn, Direction face, BlockState neighbour, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        Direction.Axis otherConnectionAxis;
        boolean positive;
        Part part = (Part)((Object)stateIn.getValue(PART));
        Direction.Axis axis = (Direction.Axis)stateIn.getValue((Property)AXIS);
        boolean connectionAlongFirst = (Boolean)stateIn.getValue((Property)CONNECTED_ALONG_FIRST_COORDINATE);
        Direction.Axis connectionAxis = connectionAlongFirst ? (axis == Direction.Axis.X ? Direction.Axis.Y : Direction.Axis.X) : (axis == Direction.Axis.Z ? Direction.Axis.Y : Direction.Axis.Z);
        Direction.Axis faceAxis = face.getAxis();
        boolean facingAlongFirst = axis == Direction.Axis.X ? faceAxis.isVertical() : faceAxis == Direction.Axis.X;
        boolean bl = positive = face.getAxisDirection() == Direction.AxisDirection.POSITIVE;
        if (axis == faceAxis) {
            return stateIn;
        }
        if (!(neighbour.getBlock() instanceof ChainDriveBlock)) {
            if (facingAlongFirst != connectionAlongFirst || part == Part.NONE) {
                return stateIn;
            }
            if (part == Part.MIDDLE) {
                return (BlockState)stateIn.setValue(PART, (Comparable)((Object)(positive ? Part.END : Part.START)));
            }
            if (part == Part.START == positive) {
                return (BlockState)stateIn.setValue(PART, (Comparable)((Object)Part.NONE));
            }
            return stateIn;
        }
        Part otherPart = (Part)((Object)neighbour.getValue(PART));
        Direction.Axis otherAxis = (Direction.Axis)neighbour.getValue((Property)AXIS);
        boolean otherConnection = (Boolean)neighbour.getValue((Property)CONNECTED_ALONG_FIRST_COORDINATE);
        Direction.Axis axis2 = otherConnection ? (otherAxis == Direction.Axis.X ? Direction.Axis.Y : Direction.Axis.X) : (otherConnectionAxis = otherAxis == Direction.Axis.Z ? Direction.Axis.Y : Direction.Axis.Z);
        if (neighbour.getValue((Property)AXIS) == faceAxis) {
            return stateIn;
        }
        if (otherPart != Part.NONE && otherConnectionAxis != faceAxis) {
            return stateIn;
        }
        if (part == Part.NONE) {
            Part part2 = part = positive ? Part.START : Part.END;
            connectionAlongFirst = axis == Direction.Axis.X ? faceAxis.isVertical() : faceAxis == Direction.Axis.X;
        } else if (connectionAxis != faceAxis) {
            return stateIn;
        }
        if (part == Part.START != positive) {
            part = Part.MIDDLE;
        }
        return (BlockState)((BlockState)stateIn.setValue(PART, (Comparable)((Object)part))).setValue((Property)CONNECTED_ALONG_FIRST_COORDINATE, (Comparable)Boolean.valueOf(connectionAlongFirst));
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        if (originalState.getValue(PART) == Part.NONE) {
            return super.getRotatedBlockState(originalState, targetedFace);
        }
        return super.getRotatedBlockState(originalState, Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)ChainDriveBlock.getConnectionAxis(originalState)));
    }

    @Override
    public BlockState updateAfterWrenched(BlockState newState, UseOnContext context) {
        Direction.Axis axis = (Direction.Axis)newState.getValue((Property)AXIS);
        newState = (BlockState)this.defaultBlockState().setValue((Property)AXIS, (Comparable)axis);
        if (newState.hasProperty((Property)BlockStateProperties.POWERED)) {
            newState = (BlockState)newState.setValue((Property)BlockStateProperties.POWERED, (Comparable)Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos())));
        }
        for (Direction facing : Iterate.directions) {
            if (facing.getAxis() == axis) continue;
            BlockPos pos = context.getClickedPos();
            BlockPos offset = pos.relative(facing);
            newState = this.updateShape(newState, facing, context.getLevel().getBlockState(offset), (LevelAccessor)context.getLevel(), pos, offset);
        }
        return newState;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue((Property)AXIS);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return (Direction.Axis)state.getValue((Property)AXIS);
    }

    public static boolean areBlocksConnected(BlockState state, BlockState other, Direction facing) {
        Part part = (Part)((Object)state.getValue(PART));
        Direction.Axis connectionAxis = ChainDriveBlock.getConnectionAxis(state);
        Direction.Axis otherConnectionAxis = ChainDriveBlock.getConnectionAxis(other);
        if (otherConnectionAxis != connectionAxis) {
            return false;
        }
        if (facing.getAxis() != connectionAxis) {
            return false;
        }
        if (facing.getAxisDirection() == Direction.AxisDirection.POSITIVE && (part == Part.MIDDLE || part == Part.START)) {
            return true;
        }
        return facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE && (part == Part.MIDDLE || part == Part.END);
    }

    protected static Direction.Axis getConnectionAxis(BlockState state) {
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)AXIS);
        boolean connectionAlongFirst = (Boolean)state.getValue((Property)CONNECTED_ALONG_FIRST_COORDINATE);
        Direction.Axis connectionAxis = connectionAlongFirst ? (axis == Direction.Axis.X ? Direction.Axis.Y : Direction.Axis.X) : (axis == Direction.Axis.Z ? Direction.Axis.Y : Direction.Axis.Z);
        return connectionAxis;
    }

    public static float getRotationSpeedModifier(KineticBlockEntity from, KineticBlockEntity to) {
        float fromMod = 1.0f;
        float toMod = 1.0f;
        if (from instanceof ChainGearshiftBlockEntity) {
            fromMod = ((ChainGearshiftBlockEntity)from).getModifier();
        }
        if (to instanceof ChainGearshiftBlockEntity) {
            toMod = ((ChainGearshiftBlockEntity)to).getModifier();
        }
        return fromMod / toMod;
    }

    @Override
    public Class<KineticBlockEntity> getBlockEntityClass() {
        return KineticBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.ENCASED_SHAFT.get();
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return this.rotate(state, rot, Direction.Axis.Y);
    }

    protected BlockState rotate(BlockState pState, Rotation rot, Direction.Axis rotAxis) {
        Direction.Axis connectionAxis = ChainDriveBlock.getConnectionAxis(pState);
        Direction direction = Direction.fromAxisAndDirection((Direction.Axis)connectionAxis, (Direction.AxisDirection)Direction.AxisDirection.POSITIVE);
        Direction normal = Direction.fromAxisAndDirection((Direction.Axis)((Direction.Axis)pState.getValue((Property)AXIS)), (Direction.AxisDirection)Direction.AxisDirection.POSITIVE);
        for (int i = 0; i < rot.ordinal(); ++i) {
            direction = direction.getClockWise(rotAxis);
            normal = normal.getClockWise(rotAxis);
        }
        if (direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
            pState = this.reversePart(pState);
        }
        Direction.Axis newAxis = normal.getAxis();
        Direction.Axis newConnectingDirection = direction.getAxis();
        boolean alongFirst = newAxis == Direction.Axis.X && newConnectingDirection == Direction.Axis.Y || newAxis != Direction.Axis.X && newConnectingDirection == Direction.Axis.X;
        return (BlockState)((BlockState)pState.setValue((Property)AXIS, (Comparable)newAxis)).setValue((Property)CONNECTED_ALONG_FIRST_COORDINATE, (Comparable)Boolean.valueOf(alongFirst));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        Direction.Axis connectionAxis = ChainDriveBlock.getConnectionAxis(pState);
        if (pMirror.mirror(Direction.fromAxisAndDirection((Direction.Axis)connectionAxis, (Direction.AxisDirection)Direction.AxisDirection.POSITIVE)).getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            return pState;
        }
        return this.reversePart(pState);
    }

    protected BlockState reversePart(BlockState pState) {
        Part part = (Part)((Object)pState.getValue(PART));
        if (part == Part.START) {
            return (BlockState)pState.setValue(PART, (Comparable)((Object)Part.END));
        }
        if (part == Part.END) {
            return (BlockState)pState.setValue(PART, (Comparable)((Object)Part.START));
        }
        return pState;
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        return this.rotate(this.mirror(state, transform.mirror), transform.rotation, transform.rotationAxis);
    }

    public static enum Part implements StringRepresentable
    {
        START,
        MIDDLE,
        END,
        NONE;


        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }
    }
}
