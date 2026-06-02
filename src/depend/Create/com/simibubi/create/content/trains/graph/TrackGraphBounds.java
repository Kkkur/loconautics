/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.graph;

import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.track.BezierConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TrackGraphBounds {
    public AABB box = null;
    public List<BezierConnection> beziers = new ArrayList<BezierConnection>();

    public TrackGraphBounds(TrackGraph graph, ResourceKey<Level> dimension) {
        for (TrackNode node : graph.nodes.values()) {
            if (node.location.dimension != dimension) continue;
            this.include(node);
            Map<TrackNode, TrackEdge> connections = graph.getConnectionsFrom(node);
            for (TrackEdge edge : connections.values()) {
                if (edge.turn == null || !edge.turn.isPrimary()) continue;
                this.beziers.add(edge.turn);
            }
        }
        if (this.box != null) {
            this.box = this.box.inflate(2.0);
        }
    }

    private void include(TrackNode node) {
        Vec3 v = node.location.getLocation();
        AABB aabb = new AABB(v, v);
        this.box = this.box == null ? aabb : this.box.minmax(aabb);
    }
}
