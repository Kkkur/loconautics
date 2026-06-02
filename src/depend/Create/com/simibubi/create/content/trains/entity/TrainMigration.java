/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphLocation;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import java.util.Map;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class TrainMigration {
    Couple<TrackNodeLocation> locations;
    double positionOnOldEdge;
    boolean curve;
    Vec3 fallback;

    public TrainMigration() {
    }

    public TrainMigration(TravellingPoint point) {
        double t = point.position / point.edge.getLength();
        this.fallback = point.edge.getPosition(null, t);
        this.curve = point.edge.isTurn();
        this.positionOnOldEdge = point.position;
        this.locations = Couple.create((Object)((Object)point.node1.getLocation()), (Object)((Object)point.node2.getLocation()));
    }

    public TrackGraphLocation tryMigratingTo(TrackGraph graph) {
        TrackEdge edge;
        TrackNode node1 = graph.locateNode((TrackNodeLocation)((Object)this.locations.getFirst()));
        TrackNode node2 = graph.locateNode((TrackNodeLocation)((Object)this.locations.getSecond()));
        if (node1 != null && node2 != null && (edge = graph.getConnectionsFrom(node1).get(node2)) != null) {
            TrackGraphLocation graphLocation = new TrackGraphLocation();
            graphLocation.graph = graph;
            graphLocation.edge = this.locations;
            graphLocation.position = this.positionOnOldEdge;
            return graphLocation;
        }
        if (this.curve) {
            return null;
        }
        Vec3 prevDirection = ((TrackNodeLocation)((Object)this.locations.getSecond())).getLocation().subtract(((TrackNodeLocation)((Object)this.locations.getFirst())).getLocation()).normalize();
        for (TrackNodeLocation loc : graph.getNodes()) {
            Vec3 nodeVec = loc.getLocation();
            if (nodeVec.distanceToSqr(this.fallback) > 1024.0) continue;
            TrackNode newNode1 = graph.locateNode(loc);
            for (Map.Entry<TrackNode, TrackEdge> entry : graph.getConnectionsFrom(newNode1).entrySet()) {
                Vec3 intersectSphere;
                TrackEdge edge2 = entry.getValue();
                if (edge2.isTurn()) continue;
                TrackNode newNode2 = entry.getKey();
                float radius = 0.015625f;
                Vec3 direction = edge2.getDirection(true);
                if (!Mth.equal((double)direction.dot(prevDirection), (double)1.0) || (intersectSphere = VecHelper.intersectSphere((Vec3)nodeVec, (Vec3)direction, (Vec3)this.fallback, (double)radius)) == null || !Mth.equal((double)direction.dot(intersectSphere.subtract(nodeVec).normalize()), (double)1.0)) continue;
                double edgeLength = edge2.getLength();
                double position = intersectSphere.distanceTo(nodeVec) - (double)radius;
                if (Double.isNaN(position) || position < 0.0 || position > edgeLength) continue;
                TrackGraphLocation graphLocation = new TrackGraphLocation();
                graphLocation.graph = graph;
                graphLocation.edge = Couple.create((Object)((Object)loc), (Object)((Object)newNode2.getLocation()));
                graphLocation.position = position;
                return graphLocation;
            }
        }
        return null;
    }

    public CompoundTag write(DimensionPalette dimensions) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Curve", this.curve);
        tag.put("Fallback", (Tag)VecHelper.writeNBT((Vec3)this.fallback));
        tag.putDouble("Position", this.positionOnOldEdge);
        tag.put("Nodes", (Tag)this.locations.serializeEach(l -> l.write(dimensions)));
        return tag;
    }

    public static TrainMigration read(CompoundTag tag, DimensionPalette dimensions) {
        TrainMigration trainMigration = new TrainMigration();
        trainMigration.curve = tag.getBoolean("Curve");
        trainMigration.fallback = VecHelper.readNBT((ListTag)tag.getList("Fallback", 6));
        trainMigration.positionOnOldEdge = tag.getDouble("Position");
        trainMigration.locations = Couple.deserializeEach((ListTag)tag.getList("Nodes", 10), c -> TrackNodeLocation.read(c, dimensions));
        return trainMigration;
    }
}
