/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.chassis;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.contraptions.chassis.AbstractChassisBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class LinearChassisBlock
extends AbstractChassisBlock {
    public static final BooleanProperty STICKY_TOP = BooleanProperty.create((String)"sticky_top");
    public static final BooleanProperty STICKY_BOTTOM = BooleanProperty.create((String)"sticky_bottom");

    public LinearChassisBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)STICKY_TOP, (Comparable)Boolean.valueOf(false))).setValue((Property)STICKY_BOTTOM, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{STICKY_TOP, STICKY_BOTTOM});
        super.createBlockStateDefinition(builder);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos placedOnPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
        BlockState blockState = context.getLevel().getBlockState(placedOnPos);
        if (context.getPlayer() == null || !context.getPlayer().isShiftKeyDown()) {
            if (LinearChassisBlock.isChassis(blockState)) {
                return (BlockState)this.defaultBlockState().setValue((Property)AXIS, (Comparable)((Direction.Axis)blockState.getValue((Property)AXIS)));
            }
            return (BlockState)this.defaultBlockState().setValue((Property)AXIS, (Comparable)context.getNearestLookingDirection().getAxis());
        }
        return super.getStateForPlacement(context);
    }

    public BlockState updateShape(BlockState state, Direction side, BlockState other, LevelAccessor p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        BooleanProperty property = this.getGlueableSide(state, side);
        if (property == null || !LinearChassisBlock.sameKind(state, other) || state.getValue((Property)AXIS) != other.getValue((Property)AXIS)) {
            return state;
        }
        return (BlockState)state.setValue((Property)property, (Comparable)Boolean.valueOf(false));
    }

    @Override
    public BooleanProperty getGlueableSide(BlockState state, Direction face) {
        if (face.getAxis() != state.getValue((Property)AXIS)) {
            return null;
        }
        return face.getAxisDirection() == Direction.AxisDirection.POSITIVE ? STICKY_TOP : STICKY_BOTTOM;
    }

    @Override
    protected boolean glueAllowedOnSide(BlockGetter world, BlockPos pos, BlockState state, Direction side) {
        BlockState other = world.getBlockState(pos.relative(side));
        return !LinearChassisBlock.sameKind(other, state) || state.getValue((Property)AXIS) != other.getValue((Property)AXIS);
    }

    public static boolean isChassis(BlockState state) {
        return AllBlocks.LINEAR_CHASSIS.has(state) || AllBlocks.SECONDARY_LINEAR_CHASSIS.has(state);
    }

    public static boolean sameKind(BlockState state1, BlockState state2) {
        return state1.getBlock() == state2.getBlock();
    }

    public static class ChassisCTBehaviour
    extends ConnectedTextureBehaviour.Base {
        @Override
        public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
            Block block = state.getBlock();
            BooleanProperty glueableSide = ((LinearChassisBlock)block).getGlueableSide(state, direction);
            if (glueableSide == null) {
                return AllBlocks.LINEAR_CHASSIS.has(state) ? AllSpriteShifts.CHASSIS_SIDE : AllSpriteShifts.SECONDARY_CHASSIS_SIDE;
            }
            return (Boolean)state.getValue((Property)glueableSide) != false ? AllSpriteShifts.CHASSIS_STICKY : AllSpriteShifts.CHASSIS;
        }

        @Override
        protected Direction getUpDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
            Direction.Axis axis = (Direction.Axis)state.getValue((Property)RotatedPillarBlock.AXIS);
            if (face.getAxis() == axis) {
                return super.getUpDirection(reader, pos, state, face);
            }
            return Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis);
        }

        @Override
        protected Direction getRightDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
            Direction.Axis axis = (Direction.Axis)state.getValue((Property)RotatedPillarBlock.AXIS);
            return axis != face.getAxis() && axis.isHorizontal() ? (face.getAxis().isHorizontal() ? Direction.DOWN : (axis == Direction.Axis.X ? Direction.NORTH : Direction.EAST)) : super.getRightDirection(reader, pos, state, face);
        }

        @Override
        protected boolean reverseUVsHorizontally(BlockState state, Direction face) {
            boolean side;
            Direction.Axis axis = (Direction.Axis)state.getValue((Property)RotatedPillarBlock.AXIS);
            boolean bl = side = face.getAxis() != axis;
            if (side && axis == Direction.Axis.X && face.getAxis().isHorizontal()) {
                return true;
            }
            return super.reverseUVsHorizontally(state, face);
        }

        @Override
        protected boolean reverseUVsVertically(BlockState state, Direction face) {
            return super.reverseUVsVertically(state, face);
        }

        @Override
        public boolean reverseUVs(BlockState state, Direction face) {
            boolean end;
            Direction.Axis axis = (Direction.Axis)state.getValue((Property)RotatedPillarBlock.AXIS);
            boolean bl = end = face.getAxis() == axis;
            if (end && axis.isHorizontal() && face.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                return true;
            }
            if (!end && axis.isHorizontal() && face == Direction.DOWN) {
                return true;
            }
            return super.reverseUVs(state, face);
        }

        @Override
        public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
            Direction.Axis axis = (Direction.Axis)state.getValue((Property)RotatedPillarBlock.AXIS);
            boolean superConnect = face.getAxis() == axis ? super.connectsTo(state, other, reader, pos, otherPos, face) : LinearChassisBlock.sameKind(state, other);
            return superConnect && axis == other.getValue((Property)RotatedPillarBlock.AXIS);
        }
    }
}
