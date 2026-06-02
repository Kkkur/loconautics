/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.DirectionalShaftHalvesBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.chainDrive.ChainDriveBlock;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlock;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlockEntity;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.LinkedList;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class RotationPropagator {
    private static final int MAX_FLICKER_SCORE = 128;

    private static float getRotationSpeedModifier(KineticBlockEntity from, KineticBlockEntity to) {
        boolean connectedByGears;
        IRotate definitionFrom;
        Block toBlock;
        Block fromBlock;
        BlockState stateTo;
        BlockState stateFrom;
        block17: {
            block16: {
                stateFrom = from.getBlockState();
                stateTo = to.getBlockState();
                fromBlock = stateFrom.getBlock();
                toBlock = stateTo.getBlock();
                if (!(fromBlock instanceof IRotate)) break block16;
                definitionFrom = (IRotate)fromBlock;
                if (toBlock instanceof IRotate) break block17;
            }
            return 0.0f;
        }
        IRotate definitionTo = (IRotate)toBlock;
        BlockPos diff = to.getBlockPos().subtract((Vec3i)from.getBlockPos());
        Direction direction = Direction.getNearest((float)diff.getX(), (float)diff.getY(), (float)diff.getZ());
        Level world = from.getLevel();
        boolean alignedAxes = true;
        for (Direction.Axis axis : Direction.Axis.values()) {
            if (axis == direction.getAxis() || axis.choose(diff.getX(), diff.getY(), diff.getZ()) == 0) continue;
            alignedAxes = false;
        }
        boolean connectedByAxis = alignedAxes && definitionFrom.hasShaftTowards((LevelReader)world, from.getBlockPos(), stateFrom, direction) && definitionTo.hasShaftTowards((LevelReader)world, to.getBlockPos(), stateTo, direction.getOpposite());
        float custom = from.propagateRotationTo(to, stateFrom, stateTo, diff, connectedByAxis, connectedByGears = ICogWheel.isSmallCog(stateFrom) && ICogWheel.isSmallCog(stateTo));
        if (custom != 0.0f) {
            return custom;
        }
        if (connectedByAxis) {
            float axisModifier = RotationPropagator.getAxisModifier(to, direction.getOpposite());
            if (axisModifier != 0.0f) {
                axisModifier = 1.0f / axisModifier;
            }
            return RotationPropagator.getAxisModifier(from, direction) * axisModifier;
        }
        if (fromBlock instanceof ChainDriveBlock && toBlock instanceof ChainDriveBlock) {
            boolean connected = ChainDriveBlock.areBlocksConnected(stateFrom, stateTo, direction);
            return connected ? ChainDriveBlock.getRotationSpeedModifier(from, to) : 0.0f;
        }
        if (RotationPropagator.isLargeToLargeGear(stateFrom, stateTo, diff)) {
            Direction.Axis sourceAxis = (Direction.Axis)stateFrom.getValue((Property)BlockStateProperties.AXIS);
            Direction.Axis targetAxis = (Direction.Axis)stateTo.getValue((Property)BlockStateProperties.AXIS);
            int sourceAxisDiff = sourceAxis.choose(diff.getX(), diff.getY(), diff.getZ());
            int targetAxisDiff = targetAxis.choose(diff.getX(), diff.getY(), diff.getZ());
            return sourceAxisDiff > 0 ^ targetAxisDiff > 0 ? -1.0f : 1.0f;
        }
        if (ICogWheel.isLargeCog(stateFrom) && ICogWheel.isSmallCog(stateTo) && RotationPropagator.isLargeToSmallCog(stateFrom, stateTo, definitionTo, diff)) {
            return -2.0f;
        }
        if (ICogWheel.isLargeCog(stateTo) && ICogWheel.isSmallCog(stateFrom) && RotationPropagator.isLargeToSmallCog(stateTo, stateFrom, definitionFrom, diff)) {
            return -0.5f;
        }
        if (connectedByGears) {
            if (diff.distManhattan((Vec3i)BlockPos.ZERO) != 1) {
                return 0.0f;
            }
            if (ICogWheel.isLargeCog(stateTo)) {
                return 0.0f;
            }
            if (direction.getAxis() == definitionFrom.getRotationAxis(stateFrom)) {
                return 0.0f;
            }
            if (definitionFrom.getRotationAxis(stateFrom) == definitionTo.getRotationAxis(stateTo)) {
                return -1.0f;
            }
        }
        return 0.0f;
    }

    private static float getConveyedSpeed(KineticBlockEntity from, KineticBlockEntity to) {
        BlockState stateTo;
        BlockState stateFrom = from.getBlockState();
        if (RotationPropagator.isLargeCogToSpeedController(stateFrom, stateTo = to.getBlockState(), to.getBlockPos().subtract((Vec3i)from.getBlockPos()))) {
            return SpeedControllerBlockEntity.getConveyedSpeed(from, to, true);
        }
        if (RotationPropagator.isLargeCogToSpeedController(stateTo, stateFrom, from.getBlockPos().subtract((Vec3i)to.getBlockPos()))) {
            return SpeedControllerBlockEntity.getConveyedSpeed(to, from, false);
        }
        float rotationSpeedModifier = RotationPropagator.getRotationSpeedModifier(from, to);
        return from.getTheoreticalSpeed() * rotationSpeedModifier;
    }

    private static boolean isLargeToLargeGear(BlockState from, BlockState to, BlockPos diff) {
        Direction.Axis toAxis;
        if (!ICogWheel.isLargeCog(from) || !ICogWheel.isLargeCog(to)) {
            return false;
        }
        Direction.Axis fromAxis = (Direction.Axis)from.getValue((Property)BlockStateProperties.AXIS);
        if (fromAxis == (toAxis = (Direction.Axis)to.getValue((Property)BlockStateProperties.AXIS))) {
            return false;
        }
        for (Direction.Axis axis : Direction.Axis.values()) {
            int axisDiff = axis.choose(diff.getX(), diff.getY(), diff.getZ());
            if (!(axis == fromAxis || axis == toAxis ? axisDiff == 0 : axisDiff != 0)) continue;
            return false;
        }
        return true;
    }

    private static float getAxisModifier(KineticBlockEntity be, Direction direction) {
        if (!be.hasSource() && !be.isSource() || !(be instanceof DirectionalShaftHalvesBlockEntity)) {
            return 1.0f;
        }
        Direction source = ((DirectionalShaftHalvesBlockEntity)be).getSourceFacing();
        if (be instanceof GearboxBlockEntity) {
            return direction.getAxis() == source.getAxis() ? (direction == source ? 1.0f : -1.0f) : (direction.getAxisDirection() == source.getAxisDirection() ? -1.0f : 1.0f);
        }
        if (be instanceof SplitShaftBlockEntity) {
            return ((SplitShaftBlockEntity)be).getRotationSpeedModifier(direction);
        }
        return 1.0f;
    }

    private static boolean isLargeToSmallCog(BlockState from, BlockState to, IRotate defTo, BlockPos diff) {
        Direction.Axis axisFrom = (Direction.Axis)from.getValue((Property)BlockStateProperties.AXIS);
        if (axisFrom != defTo.getRotationAxis(to)) {
            return false;
        }
        if (axisFrom.choose(diff.getX(), diff.getY(), diff.getZ()) != 0) {
            return false;
        }
        for (Direction.Axis axis : Direction.Axis.values()) {
            if (axis == axisFrom || Math.abs(axis.choose(diff.getX(), diff.getY(), diff.getZ())) == 1) continue;
            return false;
        }
        return true;
    }

    private static boolean isLargeCogToSpeedController(BlockState from, BlockState to, BlockPos diff) {
        if (!ICogWheel.isLargeCog(from) || !AllBlocks.ROTATION_SPEED_CONTROLLER.has(to)) {
            return false;
        }
        if (!diff.equals((Object)BlockPos.ZERO.below())) {
            return false;
        }
        Direction.Axis axis = (Direction.Axis)from.getValue((Property)CogWheelBlock.AXIS);
        if (axis.isVertical()) {
            return false;
        }
        return to.getValue(SpeedControllerBlock.HORIZONTAL_AXIS) != axis;
    }

    public static void handleAdded(Level worldIn, BlockPos pos, KineticBlockEntity addedTE) {
        if (worldIn.isClientSide) {
            return;
        }
        if (!worldIn.isLoaded(pos)) {
            return;
        }
        RotationPropagator.propagateNewSource(addedTE);
    }

    private static void propagateNewSource(KineticBlockEntity currentTE) {
        BlockPos pos = currentTE.getBlockPos();
        Level world = currentTE.getLevel();
        for (KineticBlockEntity neighbourTE : RotationPropagator.getConnectedNeighbours(currentTE)) {
            float prevSpeed;
            boolean speedChangedTooOften;
            float speedOfCurrent = currentTE.getTheoreticalSpeed();
            float speedOfNeighbour = neighbourTE.getTheoreticalSpeed();
            float newSpeed = RotationPropagator.getConveyedSpeed(currentTE, neighbourTE);
            float oppositeSpeed = RotationPropagator.getConveyedSpeed(neighbourTE, currentTE);
            if (newSpeed == 0.0f && oppositeSpeed == 0.0f) continue;
            boolean incompatible = Math.signum(newSpeed) != Math.signum(speedOfNeighbour) && newSpeed != 0.0f && speedOfNeighbour != 0.0f;
            boolean tooFast = Math.abs(newSpeed) > (float)((Integer)AllConfigs.server().kinetics.maxRotationSpeed.get()).intValue() || Math.abs(oppositeSpeed) > (float)((Integer)AllConfigs.server().kinetics.maxRotationSpeed.get()).intValue();
            boolean bl = speedChangedTooOften = currentTE.getFlickerScore() > 128;
            if (tooFast || speedChangedTooOften) {
                world.destroyBlock(pos, true);
                return;
            }
            if (incompatible) {
                world.destroyBlock(pos, true);
                return;
            }
            if (Math.abs(oppositeSpeed) > Math.abs(speedOfCurrent)) {
                prevSpeed = currentTE.getSpeed();
                currentTE.setSource(neighbourTE.getBlockPos());
                currentTE.setSpeed(RotationPropagator.getConveyedSpeed(neighbourTE, currentTE));
                currentTE.onSpeedChanged(prevSpeed);
                currentTE.sendData();
                RotationPropagator.propagateNewSource(currentTE);
                return;
            }
            if (Math.abs(newSpeed) >= Math.abs(speedOfNeighbour)) {
                if (!currentTE.hasNetwork() || currentTE.network.equals(neighbourTE.network)) {
                    float epsilon = Math.abs(speedOfNeighbour) / 256.0f / 256.0f;
                    if (!(Math.abs(newSpeed) > Math.abs(speedOfNeighbour) + epsilon)) continue;
                    world.destroyBlock(pos, true);
                    continue;
                }
                if (currentTE.hasSource() && currentTE.source.equals((Object)neighbourTE.getBlockPos())) {
                    currentTE.removeSource();
                }
                prevSpeed = neighbourTE.getSpeed();
                neighbourTE.setSource(currentTE.getBlockPos());
                neighbourTE.setSpeed(RotationPropagator.getConveyedSpeed(currentTE, neighbourTE));
                neighbourTE.onSpeedChanged(prevSpeed);
                neighbourTE.sendData();
                RotationPropagator.propagateNewSource(neighbourTE);
                continue;
            }
            if (Math.abs(neighbourTE.getTheoreticalSpeed() - newSpeed) <= 1.0E-4f) continue;
            prevSpeed = neighbourTE.getSpeed();
            neighbourTE.setSpeed(newSpeed);
            neighbourTE.setSource(currentTE.getBlockPos());
            neighbourTE.onSpeedChanged(prevSpeed);
            neighbourTE.sendData();
            RotationPropagator.propagateNewSource(neighbourTE);
        }
    }

    public static void handleRemoved(Level worldIn, BlockPos pos, KineticBlockEntity removedBE) {
        if (worldIn.isClientSide) {
            return;
        }
        if (removedBE == null) {
            return;
        }
        if (removedBE.getTheoreticalSpeed() == 0.0f) {
            return;
        }
        for (BlockPos neighbourPos : RotationPropagator.getPotentialNeighbourLocations(removedBE)) {
            KineticBlockEntity neighbourBE;
            BlockEntity blockEntity;
            BlockState neighbourState = worldIn.getBlockState(neighbourPos);
            if (!(neighbourState.getBlock() instanceof IRotate) || !((blockEntity = worldIn.getBlockEntity(neighbourPos)) instanceof KineticBlockEntity) || !(neighbourBE = (KineticBlockEntity)blockEntity).hasSource() || !neighbourBE.source.equals((Object)pos)) continue;
            RotationPropagator.propagateMissingSource(neighbourBE);
        }
    }

    private static void propagateMissingSource(KineticBlockEntity updateTE) {
        BlockPos missingSource;
        Level world = updateTE.getLevel();
        LinkedList<KineticBlockEntity> potentialNewSources = new LinkedList<KineticBlockEntity>();
        LinkedList<BlockPos> frontier = new LinkedList<BlockPos>();
        frontier.add(updateTE.getBlockPos());
        BlockPos blockPos = missingSource = updateTE.hasSource() ? updateTE.source : null;
        while (!frontier.isEmpty()) {
            BlockPos pos = (BlockPos)frontier.remove(0);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof KineticBlockEntity)) continue;
            KineticBlockEntity currentBE = (KineticBlockEntity)blockEntity;
            currentBE.removeSource();
            currentBE.sendData();
            for (KineticBlockEntity neighbourBE : RotationPropagator.getConnectedNeighbours(currentBE)) {
                if (neighbourBE.getBlockPos().equals((Object)missingSource) || !neighbourBE.hasSource()) continue;
                if (!neighbourBE.source.equals((Object)pos)) {
                    potentialNewSources.add(neighbourBE);
                    continue;
                }
                if (neighbourBE.isSource()) {
                    potentialNewSources.add(neighbourBE);
                }
                frontier.add(neighbourBE.getBlockPos());
            }
        }
        for (KineticBlockEntity newSource : potentialNewSources) {
            if (!newSource.hasSource() && !newSource.isSource()) continue;
            RotationPropagator.propagateNewSource(newSource);
            return;
        }
    }

    private static KineticBlockEntity findConnectedNeighbour(KineticBlockEntity currentTE, BlockPos neighbourPos) {
        BlockState neighbourState = currentTE.getLevel().getBlockState(neighbourPos);
        if (!(neighbourState.getBlock() instanceof IRotate)) {
            return null;
        }
        if (!neighbourState.hasBlockEntity()) {
            return null;
        }
        BlockEntity neighbourBE = currentTE.getLevel().getBlockEntity(neighbourPos);
        if (!(neighbourBE instanceof KineticBlockEntity)) {
            return null;
        }
        KineticBlockEntity neighbourKBE = (KineticBlockEntity)neighbourBE;
        if (!(neighbourKBE.getBlockState().getBlock() instanceof IRotate)) {
            return null;
        }
        if (!RotationPropagator.isConnected(currentTE, neighbourKBE) && !RotationPropagator.isConnected(neighbourKBE, currentTE)) {
            return null;
        }
        return neighbourKBE;
    }

    public static boolean isConnected(KineticBlockEntity from, KineticBlockEntity to) {
        BlockState stateTo;
        BlockState stateFrom = from.getBlockState();
        return RotationPropagator.isLargeCogToSpeedController(stateFrom, stateTo = to.getBlockState(), to.getBlockPos().subtract((Vec3i)from.getBlockPos())) || RotationPropagator.getRotationSpeedModifier(from, to) != 0.0f || from.isCustomConnection(to, stateFrom, stateTo);
    }

    private static List<KineticBlockEntity> getConnectedNeighbours(KineticBlockEntity be) {
        LinkedList<KineticBlockEntity> neighbours = new LinkedList<KineticBlockEntity>();
        for (BlockPos neighbourPos : RotationPropagator.getPotentialNeighbourLocations(be)) {
            KineticBlockEntity neighbourBE = RotationPropagator.findConnectedNeighbour(be, neighbourPos);
            if (neighbourBE == null) continue;
            neighbours.add(neighbourBE);
        }
        return neighbours;
    }

    private static List<BlockPos> getPotentialNeighbourLocations(KineticBlockEntity be) {
        LinkedList<BlockPos> neighbours = new LinkedList<BlockPos>();
        BlockPos blockPos = be.getBlockPos();
        Level level = be.getLevel();
        if (!level.isLoaded(blockPos)) {
            return neighbours;
        }
        for (Direction facing : Iterate.directions) {
            BlockPos relative = blockPos.relative(facing);
            if (!level.isLoaded(relative)) continue;
            neighbours.add(relative);
        }
        BlockState blockState = be.getBlockState();
        Block block = blockState.getBlock();
        if (!(block instanceof IRotate)) {
            return neighbours;
        }
        IRotate block2 = (IRotate)block;
        return be.addPropagationLocations(block2, blockState, neighbours);
    }
}
