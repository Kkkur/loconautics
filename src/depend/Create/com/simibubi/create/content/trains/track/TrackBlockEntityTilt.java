/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.client.model.data.ModelProperty
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackPropagator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class TrackBlockEntityTilt {
    public static final ModelProperty<Double> ASCENDING_PROPERTY = new ModelProperty();
    public Optional<Double> smoothingAngle;
    private Couple<Pair<Vec3, Integer>> previousSmoothingHandles;
    private TrackBlockEntity blockEntity;

    public TrackBlockEntityTilt(TrackBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.smoothingAngle = Optional.empty();
    }

    public void tryApplySmoothing() {
        if (this.smoothingAngle.isPresent()) {
            return;
        }
        Couple discoveredSlopes = Couple.create(null, null);
        Vec3 axis = null;
        BlockState blockState = this.blockEntity.getBlockState();
        BlockPos worldPosition = this.blockEntity.getBlockPos();
        Level level = this.blockEntity.getLevel();
        Block block = blockState.getBlock();
        if (!(block instanceof ITrackBlock)) {
            return;
        }
        ITrackBlock itb = (ITrackBlock)block;
        List<Vec3> axes = itb.getTrackAxes((BlockGetter)level, worldPosition, blockState);
        if (axes.size() != 1) {
            return;
        }
        if (axes.get((int)0).y != 0.0) {
            return;
        }
        if (this.blockEntity.boundLocation != null) {
            return;
        }
        for (BezierConnection bezierConnection : this.blockEntity.connections.values()) {
            if (((Vec3)bezierConnection.starts.getFirst()).y == ((Vec3)bezierConnection.starts.getSecond()).y) continue;
            Vec3 normedAxis = ((Vec3)bezierConnection.axes.getFirst()).normalize();
            if (axis != null) {
                if (discoveredSlopes.getSecond() != null) {
                    return;
                }
                if (normedAxis.dot(axis) > -0.984375) {
                    return;
                }
                discoveredSlopes.setSecond((Object)bezierConnection);
                continue;
            }
            axis = normedAxis;
            discoveredSlopes.setFirst((Object)bezierConnection);
        }
        if (discoveredSlopes.either(Objects::isNull)) {
            return;
        }
        if (((Vec3)((BezierConnection)discoveredSlopes.getFirst()).starts.getSecond()).y > ((Vec3)((BezierConnection)discoveredSlopes.getSecond()).starts.getSecond()).y) {
            discoveredSlopes = discoveredSlopes.swap();
        }
        Couple<Vec3> lowStarts = ((BezierConnection)discoveredSlopes.getFirst()).starts;
        Couple<Vec3> highStarts = ((BezierConnection)discoveredSlopes.getSecond()).starts;
        Vec3 lowestPoint = (Vec3)lowStarts.getSecond();
        Vec3 highestPoint = (Vec3)highStarts.getSecond();
        if (lowestPoint.y > ((Vec3)lowStarts.getFirst()).y) {
            return;
        }
        if (highestPoint.y < ((Vec3)highStarts.getFirst()).y) {
            return;
        }
        this.blockEntity.removeInboundConnections(false);
        this.blockEntity.connections.clear();
        TrackPropagator.onRailRemoved((LevelAccessor)level, worldPosition, blockState);
        double hDistance = ((BezierConnection)discoveredSlopes.getFirst()).getLength() + ((BezierConnection)discoveredSlopes.getSecond()).getLength();
        Vec3 baseAxis = (Vec3)((BezierConnection)discoveredSlopes.getFirst()).axes.getFirst();
        double baseAxisLength = baseAxis.x != 0.0 && baseAxis.z != 0.0 ? Math.sqrt(2.0) : 1.0;
        double vDistance = highestPoint.y - lowestPoint.y;
        double m = vDistance / hDistance;
        Vec3 diff = ((Vec3)highStarts.getFirst()).subtract((Vec3)lowStarts.getFirst());
        boolean flipRotation = diff.dot(new Vec3(1.0, 0.0, 2.0).normalize()) <= 0.0;
        this.smoothingAngle = Optional.of(Math.toDegrees(Mth.atan2((double)m, (double)1.0)) * (double)(flipRotation ? -1 : 1));
        int smoothingParam = Mth.clamp((int)((int)(m * baseAxisLength * 16.0)), (int)0, (int)15);
        Couple smoothingResult = Couple.create((Object)0, (Object)smoothingParam);
        Vec3 raisedOffset = diff.normalize().add(0.0, Mth.clamp((double)m, (double)0.0, (double)0.998046875), 0.0).normalize().scale(baseAxisLength);
        highStarts.setFirst((Object)((Vec3)lowStarts.getFirst()).add(raisedOffset));
        boolean first = true;
        for (BezierConnection bezierConnection : discoveredSlopes) {
            int smoothingToApply = (Integer)smoothingResult.get(first);
            if (bezierConnection.smoothing == null) {
                bezierConnection.smoothing = Couple.create((Object)0, (Object)0);
            }
            bezierConnection.smoothing.setFirst((Object)smoothingToApply);
            bezierConnection.axes.setFirst((Object)((Vec3)bezierConnection.axes.getFirst()).add(0.0, (double)(first ? 1 : -1) * -m, 0.0).normalize());
            first = false;
            BlockPos otherPosition = bezierConnection.getKey();
            BlockState otherState = level.getBlockState(otherPosition);
            if (!(otherState.getBlock() instanceof TrackBlock)) continue;
            level.setBlockAndUpdate(otherPosition, (BlockState)otherState.setValue((Property)TrackBlock.HAS_BE, (Comparable)Boolean.valueOf(true)));
            BlockEntity otherBE = level.getBlockEntity(otherPosition);
            if (!(otherBE instanceof TrackBlockEntity)) continue;
            TrackBlockEntity tbe = (TrackBlockEntity)otherBE;
            this.blockEntity.addConnection(bezierConnection);
            tbe.addConnection(bezierConnection.secondary());
        }
    }

    public void captureSmoothingHandles() {
        boolean first = true;
        this.previousSmoothingHandles = Couple.create(null, null);
        for (BezierConnection bezierConnection : this.blockEntity.connections.values()) {
            this.previousSmoothingHandles.set(first, (Object)Pair.of((Object)((Vec3)bezierConnection.starts.getFirst()), (Object)(bezierConnection.smoothing == null ? 0 : (Integer)bezierConnection.smoothing.getFirst())));
            first = false;
        }
    }

    public void undoSmoothing() {
        BlockPos otherPosition;
        if (this.smoothingAngle.isEmpty()) {
            return;
        }
        if (this.previousSmoothingHandles == null) {
            return;
        }
        if (this.blockEntity.connections.size() == 2) {
            return;
        }
        BlockState blockState = this.blockEntity.getBlockState();
        BlockPos worldPosition = this.blockEntity.getBlockPos();
        Level level = this.blockEntity.getLevel();
        ArrayList<BezierConnection> validConnections = new ArrayList<BezierConnection>();
        for (BezierConnection bezierConnection : this.blockEntity.connections.values()) {
            otherPosition = bezierConnection.getKey();
            BlockEntity otherBE = level.getBlockEntity(otherPosition);
            if (!(otherBE instanceof TrackBlockEntity)) continue;
            TrackBlockEntity tbe = (TrackBlockEntity)otherBE;
            if (!tbe.connections.containsKey(worldPosition)) continue;
            validConnections.add(bezierConnection);
        }
        this.blockEntity.removeInboundConnections(false);
        TrackPropagator.onRailRemoved((LevelAccessor)level, worldPosition, blockState);
        this.blockEntity.connections.clear();
        this.smoothingAngle = Optional.empty();
        for (BezierConnection bezierConnection : validConnections) {
            this.blockEntity.addConnection(this.restoreToOriginalCurve(bezierConnection));
            otherPosition = bezierConnection.getKey();
            BlockState otherState = level.getBlockState(otherPosition);
            if (!(otherState.getBlock() instanceof TrackBlock)) continue;
            level.setBlockAndUpdate(otherPosition, (BlockState)otherState.setValue((Property)TrackBlock.HAS_BE, (Comparable)Boolean.valueOf(true)));
            BlockEntity otherBE = level.getBlockEntity(otherPosition);
            if (!(otherBE instanceof TrackBlockEntity)) continue;
            TrackBlockEntity tbe = (TrackBlockEntity)otherBE;
            tbe.addConnection(bezierConnection.secondary());
        }
        this.blockEntity.notifyUpdate();
        this.previousSmoothingHandles = null;
        TrackPropagator.onRailAdded((LevelAccessor)level, worldPosition, blockState);
    }

    public BezierConnection restoreToOriginalCurve(BezierConnection bezierConnection) {
        if (bezierConnection.smoothing != null) {
            bezierConnection.smoothing.setFirst((Object)0);
            if ((Integer)bezierConnection.smoothing.getFirst() == 0 && (Integer)bezierConnection.smoothing.getSecond() == 0) {
                bezierConnection.smoothing = null;
            }
        }
        Vec3 raisedStart = (Vec3)bezierConnection.starts.getFirst();
        bezierConnection.starts.setFirst((Object)new TrackNodeLocation(raisedStart).getLocation());
        bezierConnection.axes.setFirst((Object)((Vec3)bezierConnection.axes.getFirst()).multiply(1.0, 0.0, 1.0).normalize());
        return bezierConnection;
    }

    public int getYOffsetForAxisEnd(Vec3 end) {
        if (this.smoothingAngle.isEmpty()) {
            return 0;
        }
        for (BezierConnection bezierConnection : this.blockEntity.connections.values()) {
            if (!TrackBlockEntityTilt.compareHandles((Vec3)bezierConnection.starts.getFirst(), end)) continue;
            return bezierConnection.yOffsetAt(end);
        }
        if (this.previousSmoothingHandles == null) {
            return 0;
        }
        for (Pair handle : this.previousSmoothingHandles) {
            if (handle == null || !TrackBlockEntityTilt.compareHandles((Vec3)handle.getFirst(), end)) continue;
            return (Integer)handle.getSecond();
        }
        return 0;
    }

    public static boolean compareHandles(Vec3 handle1, Vec3 handle2) {
        return new TrackNodeLocation(handle1).getLocation().multiply(1.0, 0.0, 1.0).distanceToSqr(new TrackNodeLocation(handle2).getLocation().multiply(1.0, 0.0, 1.0)) < 0.001953125;
    }
}
