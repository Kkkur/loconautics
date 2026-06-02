/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.network.protocol.game.DebugPackets
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraft.world.ticks.TickPriority
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.fluids.pipes.valve;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.IAxisPipe;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlockEntity;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;

public class FluidValveBlock
extends DirectionalAxisKineticBlock
implements IAxisPipe,
IBE<FluidValveBlockEntity>,
ProperWaterloggedBlock {
    public static final BooleanProperty ENABLED = BooleanProperty.create((String)"enabled");

    public FluidValveBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)ENABLED, (Comparable)Boolean.valueOf(false))).setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    public VoxelShape getShape(BlockState state, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return AllShapes.FLUID_VALVE.get(FluidValveBlock.getPipeAxis(state));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{ENABLED, WATERLOGGED}));
    }

    @Override
    protected boolean prefersConnectionTo(LevelReader reader, BlockPos pos, Direction facing, boolean shaftAxis) {
        if (!shaftAxis) {
            BlockPos offset = pos.relative(facing);
            BlockState blockState = reader.getBlockState(offset);
            return FluidPipeBlock.canConnectTo((BlockAndTintGetter)reader, offset, blockState, facing);
        }
        return super.prefersConnectionTo(reader, pos, facing, shaftAxis);
    }

    @NotNull
    public static Direction.Axis getPipeAxis(BlockState state) {
        if (!(state.getBlock() instanceof FluidValveBlock)) {
            throw new IllegalStateException("Provided BlockState is for a different block.");
        }
        Direction facing = (Direction)state.getValue((Property)FACING);
        boolean alongFirst = (Boolean)state.getValue((Property)AXIS_ALONG_FIRST_COORDINATE) == false;
        for (Direction.Axis axis : Iterate.axes) {
            if (axis == facing.getAxis()) continue;
            if (!alongFirst) {
                alongFirst = true;
                continue;
            }
            return axis;
        }
        throw new IllegalStateException("Impossible axis.");
    }

    @Override
    public Direction.Axis getAxis(BlockState state) {
        return FluidValveBlock.getPipeAxis(state);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        boolean blockTypeChanged;
        boolean bl = blockTypeChanged = !state.is(newState.getBlock());
        if (blockTypeChanged && !world.isClientSide) {
            FluidPropagator.propagateChangedPipe((LevelAccessor)world, pos, state);
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    public boolean canSurvive(BlockState p_196260_1_, LevelReader p_196260_2_, BlockPos p_196260_3_) {
        return true;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        if (world.isClientSide) {
            return;
        }
        if (state != oldState) {
            world.scheduleTick(pos, (Block)this, 1, TickPriority.HIGH);
        }
    }

    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block otherBlock, BlockPos neighborPos, boolean isMoving) {
        DebugPackets.sendNeighborsUpdatePacket((Level)world, (BlockPos)pos);
        Direction d = FluidPropagator.validateNeighbourChange(state, world, pos, otherBlock, neighborPos, isMoving);
        if (d == null) {
            return;
        }
        if (!FluidValveBlock.isOpenAt(state, d)) {
            return;
        }
        world.scheduleTick(pos, (Block)this, 1, TickPriority.HIGH);
    }

    public static boolean isOpenAt(BlockState state, Direction d) {
        return d.getAxis() == FluidValveBlock.getPipeAxis(state);
    }

    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource r) {
        FluidPropagator.propagateChangedPipe((LevelAccessor)world, pos, state);
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public Class<FluidValveBlockEntity> getBlockEntityClass() {
        return FluidValveBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FluidValveBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.FLUID_VALVE.get();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.withWater(super.getStateForPlacement(context), context);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor world, BlockPos pos, BlockPos neighbourPos) {
        this.updateWater(world, state, pos);
        return state;
    }

    public FluidState getFluidState(BlockState state) {
        return this.fluidState(state);
    }
}
