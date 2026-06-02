/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.graph;

import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackMaterial;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public static class TrackNodeLocation.DiscoveredLocation
extends TrackNodeLocation {
    BezierConnection turn = null;
    boolean forceNode = false;
    Vec3 direction;
    Vec3 normal;
    TrackMaterial materialA;
    TrackMaterial materialB;

    public TrackNodeLocation.DiscoveredLocation(Level level, double x, double y, double z) {
        super(x, y, z);
        this.in(level);
    }

    public TrackNodeLocation.DiscoveredLocation(ResourceKey<Level> dimension, Vec3 vec) {
        super(vec);
        this.in(dimension);
    }

    public TrackNodeLocation.DiscoveredLocation(Level level, Vec3 vec) {
        this((ResourceKey<Level>)level.dimension(), vec);
    }

    public TrackNodeLocation.DiscoveredLocation materialA(TrackMaterial material) {
        this.materialA = material;
        return this;
    }

    public TrackNodeLocation.DiscoveredLocation materialB(TrackMaterial material) {
        this.materialB = material;
        return this;
    }

    public TrackNodeLocation.DiscoveredLocation materials(TrackMaterial materialA, TrackMaterial materialB) {
        this.materialA = materialA;
        this.materialB = materialB;
        return this;
    }

    public TrackNodeLocation.DiscoveredLocation viaTurn(BezierConnection turn) {
        this.turn = turn;
        if (turn != null) {
            this.forceNode();
        }
        return this;
    }

    public TrackNodeLocation.DiscoveredLocation forceNode() {
        this.forceNode = true;
        return this;
    }

    public TrackNodeLocation.DiscoveredLocation withNormal(Vec3 normal) {
        this.normal = normal;
        return this;
    }

    public TrackNodeLocation.DiscoveredLocation withYOffset(int yOffsetPixels) {
        this.yOffsetPixels = yOffsetPixels;
        return this;
    }

    public TrackNodeLocation.DiscoveredLocation withDirection(Vec3 direction) {
        this.direction = direction == null ? null : direction.normalize();
        return this;
    }

    public boolean connectedViaTurn() {
        return this.turn != null;
    }

    public BezierConnection getTurn() {
        return this.turn;
    }

    public boolean shouldForceNode() {
        return this.forceNode;
    }

    public boolean differentMaterials() {
        return this.materialA != this.materialB;
    }

    public boolean notInLineWith(Vec3 direction) {
        return this.direction != null && Math.max(direction.dot(this.direction), direction.dot(this.direction.scale(-1.0))) < 0.875;
    }

    public Vec3 getDirection() {
        return this.direction;
    }
}
