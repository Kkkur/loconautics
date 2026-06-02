/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.entity.vehicle.MinecartFurnace
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseRailBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.RailShape
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.redstone.rail;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ControllerRailBlock
extends BaseRailBlock
implements IWrenchable {
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    public static final BooleanProperty BACKWARDS = BooleanProperty.create((String)"backwards");
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final MapCodec<ControllerRailBlock> CODEC = ControllerRailBlock.simpleCodec(ControllerRailBlock::new);

    public ControllerRailBlock(BlockBehaviour.Properties properties) {
        super(true, properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)POWER, (Comparable)Integer.valueOf(0))).setValue((Property)BACKWARDS, (Comparable)Boolean.valueOf(false))).setValue(SHAPE, (Comparable)RailShape.NORTH_SOUTH)).setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    public static Vec3i getAccelerationVector(BlockState state) {
        Direction pointingTo = ControllerRailBlock.getPointingTowards(state);
        return (ControllerRailBlock.isStateBackwards(state) ? pointingTo.getOpposite() : pointingTo).getNormal();
    }

    private static Direction getPointingTowards(BlockState state) {
        switch ((RailShape)state.getValue(SHAPE)) {
            case ASCENDING_WEST: 
            case EAST_WEST: {
                return Direction.WEST;
            }
            case ASCENDING_EAST: {
                return Direction.EAST;
            }
            case ASCENDING_SOUTH: {
                return Direction.SOUTH;
            }
        }
        return Direction.NORTH;
    }

    protected BlockState updateDir(Level world, BlockPos pos, BlockState state, boolean p_208489_4_) {
        BlockState updatedState = super.updateDir(world, pos, state, p_208489_4_);
        if (updatedState.getValue(SHAPE) == state.getValue(SHAPE)) {
            return updatedState;
        }
        BlockState reversedUpdatedState = updatedState;
        if (ControllerRailBlock.getPointingTowards(state).getAxis() != ControllerRailBlock.getPointingTowards(updatedState).getAxis()) {
            for (boolean opposite : Iterate.trueAndFalse) {
                Direction offset = ControllerRailBlock.getPointingTowards(updatedState);
                if (opposite) {
                    offset = offset.getOpposite();
                }
                for (BlockPos adjPos : Iterate.hereBelowAndAbove((BlockPos)pos.relative(offset))) {
                    BlockState adjState = world.getBlockState(adjPos);
                    if (!AllBlocks.CONTROLLER_RAIL.has(adjState) || ControllerRailBlock.getPointingTowards(adjState).getAxis() != offset.getAxis() || adjState.getValue((Property)BACKWARDS) == reversedUpdatedState.getValue((Property)BACKWARDS)) continue;
                    reversedUpdatedState = (BlockState)reversedUpdatedState.cycle((Property)BACKWARDS);
                }
            }
        }
        if (reversedUpdatedState != updatedState) {
            world.setBlockAndUpdate(pos, reversedUpdatedState);
        }
        return reversedUpdatedState;
    }

    private static void decelerateCart(BlockPos pos, AbstractMinecart cart) {
        Vec3 diff = VecHelper.getCenterOf((Vec3i)pos).subtract(cart.position());
        cart.setDeltaMovement(diff.x / 16.0, 0.0, diff.z / 16.0);
        if (cart instanceof MinecartFurnace) {
            MinecartFurnace fme = (MinecartFurnace)cart;
            fme.zPush = 0.0;
            fme.xPush = 0.0;
        }
    }

    private static boolean isStableWith(BlockState testState, BlockGetter world, BlockPos pos) {
        return ControllerRailBlock.canSupportRigidBlock((BlockGetter)world, (BlockPos)pos.below()) && (!((RailShape)testState.getValue(SHAPE)).isAscending() || ControllerRailBlock.canSupportRigidBlock((BlockGetter)world, (BlockPos)pos.relative(ControllerRailBlock.getPointingTowards(testState))));
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_196258_1_) {
        Direction direction = p_196258_1_.getHorizontalDirection();
        BlockState base = super.getStateForPlacement(p_196258_1_);
        return (BlockState)(base == null ? this.defaultBlockState() : base).setValue((Property)BACKWARDS, (Comparable)Boolean.valueOf(direction.getAxisDirection() == Direction.AxisDirection.POSITIVE));
    }

    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(new Property[]{SHAPE, POWER, BACKWARDS, WATERLOGGED});
    }

    public void onMinecartPass(BlockState state, Level world, BlockPos pos, AbstractMinecart cart) {
        Vec3 motion;
        if (world.isClientSide) {
            return;
        }
        Vec3 accelerationVec = Vec3.atLowerCornerOf((Vec3i)ControllerRailBlock.getAccelerationVector(state));
        double targetSpeed = cart.getMaxSpeedWithRail() * (double)((Integer)state.getValue((Property)POWER)).intValue() / 15.0;
        if (cart instanceof MinecartFurnace) {
            MinecartFurnace fme = (MinecartFurnace)cart;
            fme.xPush = accelerationVec.x;
            fme.zPush = accelerationVec.z;
        }
        if (((motion = cart.getDeltaMovement()).dot(accelerationVec) >= 0.0 || motion.lengthSqr() < 1.0E-4) && targetSpeed > 0.0) {
            cart.setDeltaMovement(accelerationVec.scale(targetSpeed));
        } else {
            ControllerRailBlock.decelerateCart(pos, cart);
        }
    }

    protected void updateState(BlockState state, Level world, BlockPos pos, Block block) {
        int newPower = this.calculatePower(world, pos);
        if ((Integer)state.getValue((Property)POWER) != newPower) {
            this.placeAndNotify((BlockState)state.setValue((Property)POWER, (Comparable)Integer.valueOf(newPower)), pos, world);
        }
    }

    private int calculatePower(Level world, BlockPos pos) {
        BlockPos testPos;
        int i;
        int newPower = world.getBestNeighborSignal(pos);
        if (newPower != 0) {
            return newPower;
        }
        int forwardDistance = 0;
        int backwardsDistance = 0;
        BlockPos lastForwardRail = pos;
        BlockPos lastBackwardsRail = pos;
        int forwardPower = 0;
        int backwardsPower = 0;
        for (i = 0; i < 15 && (testPos = this.findNextRail(lastForwardRail, (BlockGetter)world, false)) != null; ++i) {
            ++forwardDistance;
            lastForwardRail = testPos;
            forwardPower = world.getBestNeighborSignal(testPos);
            if (forwardPower != 0) break;
        }
        for (i = 0; i < 15 && (testPos = this.findNextRail(lastBackwardsRail, (BlockGetter)world, true)) != null; ++i) {
            ++backwardsDistance;
            lastBackwardsRail = testPos;
            backwardsPower = world.getBestNeighborSignal(testPos);
            if (backwardsPower != 0) break;
        }
        if (forwardDistance > 8 && backwardsDistance > 8) {
            return 0;
        }
        if (backwardsPower == 0 && forwardDistance <= 8) {
            return forwardPower;
        }
        if (forwardPower == 0 && backwardsDistance <= 8) {
            return backwardsPower;
        }
        if (backwardsPower != 0 && forwardPower != 0) {
            return Mth.ceil((double)((double)(backwardsPower * forwardDistance + forwardPower * backwardsDistance) / (double)(forwardDistance + backwardsDistance)));
        }
        return 0;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockPos pos = context.getClickedPos();
        for (Rotation testRotation : new Rotation[]{Rotation.CLOCKWISE_90, Rotation.CLOCKWISE_180, Rotation.COUNTERCLOCKWISE_90}) {
            BlockState testState = this.rotate(state, testRotation);
            if (!ControllerRailBlock.isStableWith(testState, (BlockGetter)world, pos)) continue;
            this.placeAndNotify(testState, pos, world);
            return InteractionResult.SUCCESS;
        }
        BlockState testState = (BlockState)state.setValue((Property)BACKWARDS, (Comparable)Boolean.valueOf((Boolean)state.getValue((Property)BACKWARDS) == false));
        if (ControllerRailBlock.isStableWith(testState, (BlockGetter)world, pos)) {
            this.placeAndNotify(testState, pos, world);
        }
        return InteractionResult.SUCCESS;
    }

    private void placeAndNotify(BlockState state, BlockPos pos, Level world) {
        world.setBlock(pos, state, 3);
        world.updateNeighborsAt(pos.below(), (Block)this);
        if (((RailShape)state.getValue(SHAPE)).isAscending()) {
            world.updateNeighborsAt(pos.above(), (Block)this);
        }
    }

    @Nullable
    private BlockPos findNextRail(BlockPos from, BlockGetter world, boolean reversed) {
        BlockState current = world.getBlockState(from);
        if (!(current.getBlock() instanceof ControllerRailBlock)) {
            return null;
        }
        Vec3i accelerationVec = ControllerRailBlock.getAccelerationVector(current);
        BlockPos baseTestPos = reversed ? from.subtract(accelerationVec) : from.offset(accelerationVec);
        for (BlockPos testPos : Iterate.hereBelowAndAbove((BlockPos)baseTestPos)) {
            BlockState testState;
            if (testPos.getY() > from.getY() && !((RailShape)current.getValue(SHAPE)).isAscending() || !((testState = world.getBlockState(testPos)).getBlock() instanceof ControllerRailBlock) || !ControllerRailBlock.getAccelerationVector(testState).equals((Object)accelerationVec)) continue;
            return testPos;
        }
        return null;
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return (Integer)state.getValue((Property)POWER);
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        if (rotation == Rotation.NONE) {
            return state;
        }
        RailShape railshape = (RailShape)((BlockState)Blocks.POWERED_RAIL.defaultBlockState().setValue(SHAPE, (Comparable)((RailShape)state.getValue(SHAPE)))).rotate(rotation).getValue(SHAPE);
        state = (BlockState)state.setValue(SHAPE, (Comparable)railshape);
        if (rotation == Rotation.CLOCKWISE_180 || ControllerRailBlock.getPointingTowards(state).getAxis() == Direction.Axis.Z == (rotation == Rotation.COUNTERCLOCKWISE_90)) {
            return (BlockState)state.cycle((Property)BACKWARDS);
        }
        return state;
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return state;
        }
        RailShape railshape = (RailShape)((BlockState)Blocks.POWERED_RAIL.defaultBlockState().setValue(SHAPE, (Comparable)((RailShape)state.getValue(SHAPE)))).mirror(mirror).getValue(SHAPE);
        if (ControllerRailBlock.getPointingTowards(state = (BlockState)state.setValue(SHAPE, (Comparable)railshape)).getAxis() == Direction.Axis.Z == (mirror == Mirror.LEFT_RIGHT)) {
            return (BlockState)state.cycle((Property)BACKWARDS);
        }
        return state;
    }

    public static boolean isStateBackwards(BlockState state) {
        return (Boolean)state.getValue((Property)BACKWARDS) ^ ControllerRailBlock.isReversedSlope(state);
    }

    public static boolean isReversedSlope(BlockState state) {
        return state.getValue(SHAPE) == RailShape.ASCENDING_SOUTH || state.getValue(SHAPE) == RailShape.ASCENDING_EAST;
    }

    protected MapCodec<? extends BaseRailBlock> codec() {
        return CODEC;
    }
}
