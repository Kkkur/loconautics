/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorShape;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public static class ChainConveyorShape.ChainConveyorBB
extends ChainConveyorShape {
    Vec3 lb;
    Vec3 rb;
    final double radius = 0.875;
    AABB bounds;

    public ChainConveyorShape.ChainConveyorBB(Vec3 center) {
        this.lb = center.add(0.0, 0.0, 0.0);
        this.rb = center.add(0.0, 0.5, 0.0);
        this.bounds = new AABB(this.lb, this.rb).inflate(1.0, 0.0, 1.0);
    }

    @Override
    public Vec3 intersect(Vec3 from, Vec3 to) {
        return this.bounds.clip(from, to).orElse(null);
    }

    @Override
    public void drawOutline(BlockPos anchor, PoseStack ms, VertexConsumer vb) {
        TrackBlockOutline.renderShape(AllShapes.CHAIN_CONVEYOR_INTERACTION, ms, vb, null);
    }

    @Override
    public float getChainPosition(Vec3 intersection) {
        Vec3 diff = this.bounds.getCenter().subtract(intersection);
        float angle = (float)(57.2957763671875 * Mth.atan2((double)diff.x, (double)diff.z) + 360.0 + 180.0) % 360.0f;
        float rounded = (float)Math.round(angle / 45.0f) * 45.0f;
        return rounded;
    }

    @Override
    public Vec3 getVec(BlockPos anchor, float position) {
        Vec3 point = this.bounds.getCenter();
        point = point.add(VecHelper.rotate((Vec3)new Vec3(0.0, 0.0, 0.875), (double)position, (Direction.Axis)Direction.Axis.Y));
        return point.add(Vec3.atLowerCornerOf((Vec3i)anchor)).add(0.0, -0.125, 0.0);
    }
}
