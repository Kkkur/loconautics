/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import net.minecraft.world.phys.Vec3;

public record TrackBlockOutline.BezierPointSelection(TrackBlockEntity blockEntity, BezierTrackPointLocation loc, Vec3 vec, Vec3 angles, Vec3 direction) {
}
