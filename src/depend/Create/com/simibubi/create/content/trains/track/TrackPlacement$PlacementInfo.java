/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public static class TrackPlacement.PlacementInfo {
    BezierConnection curve = null;
    boolean valid = false;
    int end1Extent = 0;
    int end2Extent = 0;
    String message = null;
    public int requiredTracks = 0;
    public boolean hasRequiredTracks = false;
    public int requiredPavement = 0;
    public boolean hasRequiredPavement = false;
    public final TrackMaterial trackMaterial;
    Vec3 end1;
    Vec3 end2;
    Vec3 normal1;
    Vec3 normal2;
    Vec3 axis1;
    Vec3 axis2;
    BlockPos pos1;
    BlockPos pos2;

    public TrackPlacement.PlacementInfo(TrackMaterial material) {
        this.trackMaterial = material;
    }

    public TrackPlacement.PlacementInfo withMessage(String message) {
        this.message = "track." + message;
        return this;
    }

    public TrackPlacement.PlacementInfo tooJumbly() {
        this.curve = null;
        return this;
    }
}
