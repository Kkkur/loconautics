/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.fluids;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.content.fluids.pipes.AxisPipeBlock;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.VanillaFluidTargets;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class FluidPropagator {
    public static CreateAdvancement[] getSharedTriggers() {
        return new CreateAdvancement[]{AllAdvancements.WATER_SUPPLY, AllAdvancements.CROSS_STREAMS, AllAdvancements.HONEY_DRAIN};
    }

    public static void propagateChangedPipe(LevelAccessor world, BlockPos pipePos, BlockState pipeState) {
        ArrayList<Pair> frontier = new ArrayList<Pair>();
        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        HashSet<Pair> discoveredPumps = new HashSet<Pair>();
        frontier.add(Pair.of((Object)0, (Object)pipePos));
        while (!frontier.isEmpty()) {
            Pair pair2 = (Pair)frontier.remove(0);
            BlockPos currentPos = (BlockPos)pair2.getSecond();
            if (visited.contains(currentPos)) continue;
            visited.add(currentPos);
            BlockState currentState = currentPos.equals((Object)pipePos) ? pipeState : world.getBlockState(currentPos);
            FluidTransportBehaviour pipe = FluidPropagator.getPipe((BlockGetter)world, currentPos);
            if (pipe == null) continue;
            pipe.wipePressure();
            for (Direction direction : FluidPropagator.getPipeConnections(currentState, pipe)) {
                Integer distance;
                FluidTransportBehaviour targetPipe;
                Level l;
                BlockPos target = currentPos.relative(direction);
                if (world instanceof Level && !(l = (Level)world).isLoaded(target)) continue;
                BlockEntity blockEntity = world.getBlockEntity(target);
                BlockState targetState = world.getBlockState(target);
                if (blockEntity instanceof PumpBlockEntity) {
                    if (!(targetState.getBlock() instanceof PumpBlock) || ((Direction)targetState.getValue((Property)PumpBlock.FACING)).getAxis() != direction.getAxis()) continue;
                    discoveredPumps.add(Pair.of((Object)((PumpBlockEntity)blockEntity), (Object)direction.getOpposite()));
                    continue;
                }
                if (visited.contains(target) || (targetPipe = FluidPropagator.getPipe((BlockGetter)world, target)) == null || (distance = (Integer)pair2.getFirst()) >= FluidPropagator.getPumpRange() && !targetPipe.hasAnyPressure() || !targetPipe.canHaveFlowToward(targetState, direction.getOpposite())) continue;
                frontier.add(Pair.of((Object)(distance + 1), (Object)target));
            }
        }
        discoveredPumps.forEach(pair -> ((PumpBlockEntity)pair.getFirst()).updatePipesOnSide((Direction)pair.getSecond()));
    }

    public static void resetAffectedFluidNetworks(Level world, BlockPos start, Direction side) {
        ArrayList<BlockPos> frontier = new ArrayList<BlockPos>();
        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        frontier.add(start);
        while (!frontier.isEmpty()) {
            BlockPos pos = (BlockPos)frontier.remove(0);
            if (visited.contains(pos)) continue;
            visited.add(pos);
            FluidTransportBehaviour pipe = FluidPropagator.getPipe((BlockGetter)world, pos);
            if (pipe == null) continue;
            for (Direction d : Iterate.directions) {
                PipeConnection connection;
                BlockPos target;
                if (pos.equals((Object)start) && d != side || visited.contains(target = pos.relative(d)) || (connection = pipe.getConnection(d)) == null || !connection.hasFlow()) continue;
                PipeConnection.Flow flow = connection.flow.get();
                if (!flow.inbound) continue;
                connection.resetNetwork();
                frontier.add(target);
            }
        }
    }

    public static Direction validateNeighbourChange(BlockState state, Level world, BlockPos pos, Block otherBlock, BlockPos neighborPos, boolean isMoving) {
        if (world.isClientSide) {
            return null;
        }
        otherBlock = world.getBlockState(neighborPos).getBlock();
        if (otherBlock instanceof FluidPipeBlock) {
            return null;
        }
        if (otherBlock instanceof AxisPipeBlock) {
            return null;
        }
        if (otherBlock instanceof PumpBlock) {
            return null;
        }
        if (otherBlock instanceof LiquidBlock) {
            return null;
        }
        if (FluidPropagator.getStraightPipeAxis(state) == null && !(state.getBlock() instanceof EncasedPipeBlock)) {
            return null;
        }
        for (Direction d : Iterate.directions) {
            if (!pos.relative(d).equals((Object)neighborPos)) continue;
            return d;
        }
        return null;
    }

    public static FluidTransportBehaviour getPipe(BlockGetter reader, BlockPos pos) {
        return BlockEntityBehaviour.get(reader, pos, FluidTransportBehaviour.TYPE);
    }

    public static boolean isOpenEnd(BlockGetter reader, BlockPos pos, Direction side) {
        BlockPos connectedPos = pos.relative(side);
        BlockState connectedState = reader.getBlockState(connectedPos);
        FluidTransportBehaviour pipe = FluidPropagator.getPipe(reader, connectedPos);
        if (pipe != null && pipe.canHaveFlowToward(connectedState, side.getOpposite())) {
            return false;
        }
        if (PumpBlock.isPump(connectedState) && ((Direction)connectedState.getValue((Property)PumpBlock.FACING)).getAxis() == side.getAxis()) {
            return false;
        }
        if (VanillaFluidTargets.canProvideFluidWithoutCapability(connectedState)) {
            return true;
        }
        if (BlockHelper.hasBlockSolidSide(connectedState, reader, connectedPos, side.getOpposite()) && !AllTags.AllBlockTags.FAN_TRANSPARENT.matches(connectedState)) {
            return false;
        }
        if (FluidPropagator.hasFluidCapability(reader, connectedPos, side.getOpposite())) {
            return false;
        }
        return connectedState.canBeReplaced() && connectedState.getDestroySpeed(reader, connectedPos) != -1.0f || connectedState.hasProperty((Property)BlockStateProperties.WATERLOGGED);
    }

    public static List<Direction> getPipeConnections(BlockState state, FluidTransportBehaviour pipe) {
        ArrayList<Direction> list = new ArrayList<Direction>();
        for (Direction d : Iterate.directions) {
            if (!pipe.canHaveFlowToward(state, d)) continue;
            list.add(d);
        }
        return list;
    }

    public static int getPumpRange() {
        return (Integer)AllConfigs.server().fluids.mechanicalPumpRange.get();
    }

    public static boolean hasFluidCapability(BlockGetter world, BlockPos pos, Direction side) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null || blockEntity.getLevel() == null) {
            return false;
        }
        IFluidHandler capability = (IFluidHandler)blockEntity.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, blockEntity.getBlockPos(), (Object)side);
        return capability != null;
    }

    @Nullable
    public static Direction.Axis getStraightPipeAxis(BlockState state) {
        if (state.getBlock() instanceof PumpBlock) {
            return ((Direction)state.getValue((Property)PumpBlock.FACING)).getAxis();
        }
        if (state.getBlock() instanceof AxisPipeBlock) {
            return (Direction.Axis)state.getValue((Property)AxisPipeBlock.AXIS);
        }
        if (!FluidPipeBlock.isPipe(state)) {
            return null;
        }
        Direction.Axis axisFound = null;
        int connections = 0;
        for (Direction.Axis axis : Iterate.axes) {
            Direction d1 = Direction.get((Direction.AxisDirection)Direction.AxisDirection.NEGATIVE, (Direction.Axis)axis);
            Direction d2 = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)axis);
            boolean openAt1 = FluidPipeBlock.isOpenAt(state, d1);
            boolean openAt2 = FluidPipeBlock.isOpenAt(state, d2);
            if (openAt1) {
                ++connections;
            }
            if (openAt2) {
                ++connections;
            }
            if (!openAt1 || !openAt2) continue;
            if (axisFound != null) {
                return null;
            }
            axisFound = axis;
        }
        return connections == 2 ? axisFound : null;
    }
}
