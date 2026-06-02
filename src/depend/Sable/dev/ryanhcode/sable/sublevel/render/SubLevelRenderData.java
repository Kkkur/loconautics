/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.client.Camera
 *  net.minecraft.client.PrioritizeChunkUpdates
 *  net.minecraft.client.renderer.chunk.RenderRegionCache
 *  org.joml.Matrix4f
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.sublevel.render;

import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import java.io.Closeable;
import net.minecraft.client.Camera;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import org.joml.Matrix4f;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface SubLevelRenderData
extends Closeable {
    @Override
    public void close();

    public void rebuild();

    public boolean isSectionCompiled(int var1, int var2, int var3);

    public void setDirty(int var1, int var2, int var3, boolean var4);

    public void compileSections(PrioritizeChunkUpdates var1, RenderRegionCache var2, Camera var3);

    default public Matrix4f getTransformation(double camX, double camY, double camZ) {
        return this.getTransformation(camX, camY, camZ, new Matrix4f());
    }

    default public Matrix4f getTransformation(double camX, double camY, double camZ, Matrix4f store) {
        store.identity();
        Pose3dc pose = this.getSubLevel().renderPose();
        Vector3dc pos = pose.position();
        Vector3dc scale = pose.scale();
        Quaterniondc orientation = pose.orientation();
        store.translate((float)(pos.x() - camX), (float)(pos.y() - camY), (float)(pos.z() - camZ));
        store.rotate((Quaternionfc)new Quaternionf(orientation));
        store.scale((float)scale.x(), (float)scale.y(), (float)scale.z());
        return store;
    }

    public ClientSubLevel getSubLevel();

    default public Vector3d getChunkOffset() {
        return this.getChunkOffset(new Vector3d());
    }

    default public Vector3d getChunkOffset(Vector3d dest) {
        return this.getSubLevel().renderPose().rotationPoint().negate(dest);
    }
}
