/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorShape;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public static class ChainConveyorShape.ChainConveyorOBB
extends ChainConveyorShape {
    BlockPos connection;
    double yaw;
    double pitch;
    AABB bounds;
    Vec3 pivot;
    final double radius = 0.175;
    VoxelShape voxelShape;
    Vec3[] linePoints;

    public ChainConveyorShape.ChainConveyorOBB(BlockPos connection, Vec3 start, Vec3 end) {
        this.connection = connection;
        Vec3 diff = end.subtract(start);
        double d = diff.length();
        double dxz = diff.multiply(1.0, 0.0, 1.0).length();
        this.yaw = 57.2957763671875 * Mth.atan2((double)diff.x, (double)diff.z);
        this.pitch = 57.2957763671875 * Mth.atan2((double)(-diff.y), (double)dxz);
        this.bounds = new AABB(start, start).expandTowards(new Vec3(0.0, 0.0, d)).inflate(0.175, 0.175, 0.0);
        this.pivot = start;
        this.voxelShape = Shapes.create((AABB)this.bounds);
    }

    @Override
    public Vec3 intersect(Vec3 from, Vec3 to) {
        Vec3 result = this.bounds.clip(from = this.counterTransform(from), to = this.counterTransform(to)).orElse(null);
        if (result == null) {
            return null;
        }
        result = this.transform(result);
        return result;
    }

    private Vec3 counterTransform(Vec3 from) {
        from = from.subtract(this.pivot);
        from = VecHelper.rotate((Vec3)from, (double)(-this.yaw), (Direction.Axis)Direction.Axis.Y);
        from = VecHelper.rotate((Vec3)from, (double)(-this.pitch), (Direction.Axis)Direction.Axis.X);
        from = from.add(this.pivot);
        return from;
    }

    private Vec3 transform(Vec3 result) {
        result = result.subtract(this.pivot);
        result = VecHelper.rotate((Vec3)result, (double)this.pitch, (Direction.Axis)Direction.Axis.X);
        result = VecHelper.rotate((Vec3)result, (double)this.yaw, (Direction.Axis)Direction.Axis.Y);
        result = result.add(this.pivot);
        return result;
    }

    @Override
    public void drawOutline(BlockPos anchor, PoseStack ms, VertexConsumer vb) {
        ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)ms).translate(this.pivot)).rotateYDegrees((float)this.yaw)).rotateXDegrees((float)this.pitch)).translateBack(this.pivot);
        TrackBlockOutline.renderShape(this.voxelShape, ms, vb, null);
    }

    @Override
    public float getChainPosition(Vec3 intersection) {
        int dots = (int)Math.round(Vec3.atLowerCornerOf((Vec3i)this.connection).length() - 3.0);
        double length = this.bounds.getZsize();
        double selection = Math.min(this.bounds.getZsize(), intersection.distanceTo(this.pivot));
        double margin = length - (double)dots;
        selection = Mth.clamp((double)(selection - margin), (double)0.0, (double)(length - margin * 2.0));
        selection = Math.round(selection);
        return (float)(selection + margin + 0.025);
    }

    @Override
    public Vec3 getVec(BlockPos anchor, float position) {
        float x = (float)this.bounds.getCenter().x;
        float y = (float)this.bounds.getCenter().y;
        Vec3 from = new Vec3((double)x, (double)y, this.bounds.minZ);
        Vec3 to = new Vec3((double)x, (double)y, this.bounds.maxZ);
        Vec3 point = from.lerp(to, Mth.clamp((double)((double)position / from.distanceTo(to)), (double)0.0, (double)1.0));
        point = this.transform(point);
        return point.add(Vec3.atLowerCornerOf((Vec3i)anchor));
    }
}
