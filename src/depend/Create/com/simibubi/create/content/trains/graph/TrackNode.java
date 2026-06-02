/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.graph;

import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import net.minecraft.world.phys.Vec3;

public class TrackNode {
    int netId;
    Vec3 normal;
    TrackNodeLocation location;

    public TrackNode(TrackNodeLocation location, int netId, Vec3 normal) {
        this.location = location;
        this.netId = netId;
        this.normal = normal;
    }

    public TrackNodeLocation getLocation() {
        return this.location;
    }

    public int getNetId() {
        return this.netId;
    }

    public Vec3 getNormal() {
        return this.normal;
    }
}
