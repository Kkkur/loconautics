/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.levelWrappers.WrappedLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.logistics.tunnel;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlockEntity;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelShapes;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.levelWrappers.WrappedLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeltTunnelBlock
extends Block
implements IBE<BeltTunnelBlockEntity>,
IWrenchable {
    public static final Property<Shape> SHAPE = EnumProperty.create((String)"shape", Shape.class);
    public static final Property<Direction.Axis> HORIZONTAL_AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public BeltTunnelBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(SHAPE, (Comparable)((Object)Shape.STRAIGHT)));
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return BeltTunnelShapes.getShape(state);
    }

    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockState blockState = worldIn.getBlockState(pos.below());
        if (!this.isValidPositionForPlacement(state, worldIn, pos)) {
            return false;
        }
        return (Boolean)blockState.getValue((Property)BeltBlock.CASING) != false;
    }

    public boolean isValidPositionForPlacement(BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockState blockState = worldIn.getBlockState(pos.below());
        if (!AllBlocks.BELT.has(blockState)) {
            return false;
        }
        return blockState.getValue(BeltBlock.SLOPE) == BeltSlope.HORIZONTAL;
    }

    public static boolean hasWindow(BlockState state) {
        return state.getValue(SHAPE) == Shape.WINDOW || state.getValue(SHAPE) == Shape.CLOSED;
    }

    public static boolean isStraight(BlockState state) {
        return BeltTunnelBlock.hasWindow(state) || state.getValue(SHAPE) == Shape.STRAIGHT;
    }

    public static boolean isJunction(BlockState state) {
        Shape shape = (Shape)((Object)state.getValue(SHAPE));
        return shape == Shape.CROSS || shape == Shape.T_LEFT || shape == Shape.T_RIGHT;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.getTunnelState((BlockGetter)context.getLevel(), context.getClickedPos());
    }

    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
        if (!(world instanceof WrappedLevel) && !world.isClientSide()) {
            this.withBlockEntityDo((BlockGetter)world, pos, BeltTunnelBlockEntity::updateTunnelConnections);
        }
    }

    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        BlockState tunnelState;
        if (facing.getAxis().isVertical()) {
            return state;
        }
        if (!(worldIn instanceof WrappedLevel) && !worldIn.isClientSide()) {
            this.withBlockEntityDo((BlockGetter)worldIn, currentPos, BeltTunnelBlockEntity::updateTunnelConnections);
        }
        if ((tunnelState = this.getTunnelState((BlockGetter)worldIn, currentPos)).getValue(HORIZONTAL_AXIS) == state.getValue(HORIZONTAL_AXIS) && BeltTunnelBlock.hasWindow(tunnelState) == BeltTunnelBlock.hasWindow(state)) {
            return state;
        }
        return tunnelState;
    }

    public void updateTunnel(LevelAccessor world, BlockPos pos) {
        BlockState newTunnel;
        BlockState tunnel = world.getBlockState(pos);
        if (tunnel != (newTunnel = this.getTunnelState((BlockGetter)world, pos)) && !world.isClientSide()) {
            world.setBlock(pos, newTunnel, 3);
            BlockEntity be = world.getBlockEntity(pos);
            if (be != null && be instanceof BeltTunnelBlockEntity) {
                ((BeltTunnelBlockEntity)be).updateTunnelConnections();
            }
        }
    }

    private BlockState getTunnelState(BlockGetter reader, BlockPos pos) {
        boolean canHaveWindow;
        BlockState state = this.defaultBlockState();
        BlockState belt = reader.getBlockState(pos.below());
        if (AllBlocks.BELT.has(belt)) {
            state = (BlockState)state.setValue(HORIZONTAL_AXIS, (Comparable)((Direction)belt.getValue(BeltBlock.HORIZONTAL_FACING)).getAxis());
        }
        Direction.Axis axis = (Direction.Axis)state.getValue(HORIZONTAL_AXIS);
        Direction left = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis).getClockWise();
        boolean onLeft = this.hasValidOutput(reader, pos.below(), left);
        boolean onRight = this.hasValidOutput(reader, pos.below(), left.getOpposite());
        if (onLeft && onRight) {
            state = (BlockState)state.setValue(SHAPE, (Comparable)((Object)Shape.CROSS));
        } else if (onLeft) {
            state = (BlockState)state.setValue(SHAPE, (Comparable)((Object)Shape.T_LEFT));
        } else if (onRight) {
            state = (BlockState)state.setValue(SHAPE, (Comparable)((Object)Shape.T_RIGHT));
        }
        if (state.getValue(SHAPE) == Shape.STRAIGHT && (canHaveWindow = this.canHaveWindow(reader, pos, axis))) {
            state = (BlockState)state.setValue(SHAPE, (Comparable)((Object)Shape.WINDOW));
        }
        return state;
    }

    protected boolean canHaveWindow(BlockGetter reader, BlockPos pos, Direction.Axis axis) {
        Direction fw = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis);
        BlockState blockState1 = reader.getBlockState(pos.relative(fw));
        BlockState blockState2 = reader.getBlockState(pos.relative(fw.getOpposite()));
        boolean funnel1 = blockState1.getBlock() instanceof BeltFunnelBlock && blockState1.getValue(BeltFunnelBlock.SHAPE) == BeltFunnelBlock.Shape.EXTENDED && blockState1.getValue((Property)BeltFunnelBlock.HORIZONTAL_FACING) == fw.getOpposite();
        boolean funnel2 = blockState2.getBlock() instanceof BeltFunnelBlock && blockState2.getValue(BeltFunnelBlock.SHAPE) == BeltFunnelBlock.Shape.EXTENDED && blockState2.getValue((Property)BeltFunnelBlock.HORIZONTAL_FACING) == fw;
        boolean valid1 = blockState1.getBlock() instanceof BeltTunnelBlock || funnel1;
        boolean valid2 = blockState2.getBlock() instanceof BeltTunnelBlock || funnel2;
        boolean canHaveWindow = valid1 && valid2 && (!funnel1 || !funnel2);
        return canHaveWindow;
    }

    private boolean hasValidOutput(BlockGetter world, BlockPos pos, Direction side) {
        BlockState blockState = world.getBlockState(pos.relative(side));
        if (AllBlocks.BELT.has(blockState)) {
            return ((Direction)blockState.getValue(BeltBlock.HORIZONTAL_FACING)).getAxis() == side.getAxis();
        }
        DirectBeltInputBehaviour behaviour = BlockEntityBehaviour.get(world, pos.relative(side), DirectBeltInputBehaviour.TYPE);
        return behaviour != null && behaviour.canInsertFromSide(side);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (!BeltTunnelBlock.hasWindow(state)) {
            return InteractionResult.PASS;
        }
        Shape shape = (Shape)((Object)state.getValue(SHAPE));
        shape = shape == Shape.CLOSED ? Shape.WINDOW : Shape.CLOSED;
        Level world = context.getLevel();
        if (!world.isClientSide) {
            world.setBlock(context.getClickedPos(), (BlockState)state.setValue(SHAPE, (Comparable)((Object)shape)), 2);
        }
        return InteractionResult.SUCCESS;
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        Direction fromAxis = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)((Direction.Axis)state.getValue(HORIZONTAL_AXIS)));
        Direction rotated = rotation.rotate(fromAxis);
        return (BlockState)state.setValue(HORIZONTAL_AXIS, (Comparable)rotated.getAxis());
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (worldIn.isClientSide) {
            return;
        }
        if (fromPos.equals((Object)pos.below()) && !this.canSurvive(state, (LevelReader)worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
            return;
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{HORIZONTAL_AXIS, SHAPE});
        super.createBlockStateDefinition(builder);
    }

    @Override
    public Class<BeltTunnelBlockEntity> getBlockEntityClass() {
        return BeltTunnelBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BeltTunnelBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.ANDESITE_TUNNEL.get();
    }

    public static enum Shape implements StringRepresentable
    {
        STRAIGHT,
        WINDOW,
        CLOSED,
        T_LEFT,
        T_RIGHT,
        CROSS;


        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }
    }
}
