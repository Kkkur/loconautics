/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.minecart;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

static class CouplingRenderer.CartEndpoint {
    double x;
    double y;
    double z;
    float yaw;
    float pitch;
    float roll;
    float offset;
    boolean flip;

    public CouplingRenderer.CartEndpoint(double x, double y, double z, float yaw, float pitch, float roll, float offset, boolean flip) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        this.offset = offset;
        this.flip = flip;
    }

    public Vec3 apply(Vec3 vec) {
        vec = vec.add((double)this.offset, 0.0, 0.0);
        vec = VecHelper.rotate((Vec3)vec, (double)this.roll, (Direction.Axis)Direction.Axis.X);
        vec = VecHelper.rotate((Vec3)vec, (double)this.pitch, (Direction.Axis)Direction.Axis.Z);
        vec = VecHelper.rotate((Vec3)vec, (double)this.yaw, (Direction.Axis)Direction.Axis.Y);
        return vec.add(this.x, this.y, this.z);
    }

    public void apply(PoseStack ms, Vec3 camera) {
        ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)ms).translate(camera.scale(-1.0).add(this.x, this.y, this.z))).rotateYDegrees(this.yaw)).rotateZDegrees(this.pitch)).rotateXDegrees(this.roll)).translate(this.offset, 0.0f, 0.0f).rotateYDegrees(this.flip ? 180.0f : 0.0f);
    }
}
