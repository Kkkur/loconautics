/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  net.minecraft.world.level.ChunkPos
 */
package dev.ryanhcode.sable.neoforge.compatibility.flywheel;

import dev.ryanhcode.sable.companion.math.Pose3d;
import java.util.UUID;
import net.minecraft.world.level.ChunkPos;

public static class FlywheelCompatNeoForge.SubLevelFlwRenderState {
    public int sceneID;
    public final Pose3d renderPose = new Pose3d();
    public UUID subLevelID;
    public float latestSkyLightScale;
    public ChunkPos centerChunk;
}
