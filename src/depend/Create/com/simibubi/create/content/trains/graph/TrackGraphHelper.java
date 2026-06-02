/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.graph;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import java.util.Collection;
import java.util.Map;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TrackGraphHelper {
    @Nullable
    public static TrackGraphLocation getGraphLocationAt(Level level, BlockPos pos, Direction.AxisDirection targetDirection, Vec3 targetAxis) {
        TrackNode node;
        BlockState trackBlockState = level.getBlockState(pos);
        Block block = trackBlockState.getBlock();
        if (!(block instanceof ITrackBlock)) {
            return null;
        }
        ITrackBlock track = (ITrackBlock)block;
        Vec3 axis = targetAxis.scale((double)targetDirection.getStep());
        double length = axis.length();
        TrackGraph graph = null;
        TrackNodeLocation location = new TrackNodeLocation(Vec3.atBottomCenterOf((Vec3i)pos).add(0.0, track.getElevationAtCenter((BlockGetter)level, pos, trackBlockState), 0.0)).in(level);
        graph = Create.RAILWAYS.sided((LevelAccessor)level).getGraph((LevelAccessor)level, location);
        if (graph != null && (node = graph.locateNode(location)) != null) {
            Map<TrackNode, TrackEdge> connectionsFrom = graph.getConnectionsFrom(node);
            for (Map.Entry<TrackNode, TrackEdge> entry : connectionsFrom.entrySet()) {
                TrackNode backNode = entry.getKey();
                Vec3 direction = entry.getValue().getDirection(true);
                if (direction.scale(length).distanceToSqr(axis.scale(-1.0)) > 2.44140625E-4) continue;
                TrackGraphLocation graphLocation = new TrackGraphLocation();
                graphLocation.edge = Couple.create((Object)((Object)node.getLocation()), (Object)((Object)backNode.getLocation()));
                graphLocation.position = 0.0;
                graphLocation.graph = graph;
                return graphLocation;
            }
        }
        Collection<TrackNodeLocation.DiscoveredLocation> ends = track.getConnected((BlockGetter)level, pos, trackBlockState, true, null);
        Vec3 start = Vec3.atBottomCenterOf((Vec3i)pos).add(0.0, track.getElevationAtCenter((BlockGetter)level, pos, trackBlockState), 0.0);
        TrackNode frontNode = null;
        TrackNode backNode = null;
        double position = 0.0;
        boolean singleTrackPiece = true;
        block1: for (TrackNodeLocation.DiscoveredLocation current : ends) {
            boolean backwards;
            Vec3 offset = current.getLocation().subtract(start).normalize().scale(length);
            Vec3 compareOffset = offset.multiply(1.0, 0.0, 1.0).normalize();
            boolean forward = compareOffset.distanceToSqr(axis.multiply(-1.0, 0.0, -1.0).normalize()) < 2.44140625E-4;
            boolean bl = backwards = compareOffset.distanceToSqr(axis.multiply(1.0, 0.0, 1.0).normalize()) < 2.44140625E-4;
            if (!forward && !backwards) continue;
            TrackNodeLocation.DiscoveredLocation previous = null;
            double distance = 0.0;
            block2: for (int i = 0; i < 100 && distance < 32.0; ++i) {
                TrackNodeLocation.DiscoveredLocation loc = current;
                if (graph == null) {
                    graph = Create.RAILWAYS.sided((LevelAccessor)level).getGraph((LevelAccessor)level, loc);
                }
                if (graph == null || graph.locateNode(loc) == null) {
                    singleTrackPiece = false;
                    Collection<TrackNodeLocation.DiscoveredLocation> list = ITrackBlock.walkConnectedTracks((BlockGetter)level, loc, true);
                    for (TrackNodeLocation.DiscoveredLocation discoveredLocation : list) {
                        if (discoveredLocation == previous) continue;
                        Vec3 diff = discoveredLocation.getLocation().subtract(loc.getLocation());
                        if ((forward ? axis.scale(-1.0) : axis).distanceToSqr(diff.normalize().scale(length)) > 2.44140625E-4) continue;
                        previous = current;
                        current = discoveredLocation;
                        distance += diff.length();
                        continue block2;
                    }
                    continue;
                }
                TrackNode node2 = graph.locateNode(loc);
                if (forward) {
                    frontNode = node2;
                }
                if (!backwards) continue block1;
                backNode = node2;
                position = distance + axis.length() / 2.0;
                continue block1;
            }
        }
        if (frontNode == null || backNode == null) {
            return null;
        }
        if (singleTrackPiece) {
            position = frontNode.getLocation().getLocation().distanceTo(backNode.getLocation().getLocation()) / 2.0;
        }
        TrackGraphLocation graphLocation = new TrackGraphLocation();
        graphLocation.edge = Couple.create((Object)((Object)backNode.getLocation()), (Object)((Object)frontNode.getLocation()));
        graphLocation.position = position;
        graphLocation.graph = graph;
        return graphLocation;
    }

    @Nullable
    public static TrackGraphLocation getBezierGraphLocationAt(Level level, BlockPos pos, Direction.AxisDirection targetDirection, BezierTrackPointLocation targetBezier) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (!(block instanceof ITrackBlock)) {
            return null;
        }
        ITrackBlock track = (ITrackBlock)block;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof TrackBlockEntity)) {
            return null;
        }
        TrackBlockEntity trackBE = (TrackBlockEntity)blockEntity;
        BezierConnection bc = trackBE.getConnections().get(targetBezier.curveTarget());
        if (bc == null || !bc.isPrimary()) {
            return null;
        }
        TrackNodeLocation targetLoc = new TrackNodeLocation((Vec3)bc.starts.getSecond()).in(level);
        if (bc.smoothing != null) {
            targetLoc.yOffsetPixels = (Integer)bc.smoothing.getSecond();
        }
        for (TrackNodeLocation.DiscoveredLocation location : track.getConnected((BlockGetter)level, pos, state, true, null)) {
            TrackNode node;
            TrackEdge edge;
            TrackNode targetNode;
            TrackGraph graph = Create.RAILWAYS.sided((LevelAccessor)level).getGraph((LevelAccessor)level, location);
            if (graph == null || (targetNode = graph.locateNode(targetLoc)) == null || (edge = graph.getConnectionsFrom(node = graph.locateNode(location)).get(targetNode)) == null) continue;
            TrackGraphLocation graphLocation = new TrackGraphLocation();
            graphLocation.graph = graph;
            graphLocation.edge = Couple.create((Object)((Object)location), (Object)((Object)targetLoc));
            graphLocation.position = (float)(targetBezier.segment() + 1) / 2.0f;
            if (targetDirection == Direction.AxisDirection.POSITIVE) {
                graphLocation.edge = graphLocation.edge.swap();
                graphLocation.position = edge.getLength() - graphLocation.position;
            }
            return graphLocation;
        }
        return null;
    }
}
