/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ArmAngleTarget {
    static final ArmAngleTarget NO_TARGET = new ArmAngleTarget();
    float baseAngle;
    float lowerArmAngle;
    float upperArmAngle;
    float headAngle;

    private ArmAngleTarget() {
        this.lowerArmAngle = 135.0f;
        this.upperArmAngle = 45.0f;
        this.headAngle = 0.0f;
    }

    public ArmAngleTarget(BlockPos armPos, Vec3 pointTarget, Direction clawFacing, boolean ceiling) {
        Vec3 target = pointTarget;
        Vec3 origin = VecHelper.getCenterOf((Vec3i)armPos).add(0.0, ceiling ? -0.375 : 0.375, 0.0);
        Vec3 clawTarget = target;
        target = target.add(Vec3.atLowerCornerOf((Vec3i)clawFacing.getOpposite().getNormal()).scale(0.5));
        Vec3 diff = target.subtract(origin);
        float horizontalDistance = (float)diff.multiply(1.0, 0.0, 1.0).length();
        float baseAngle = AngleHelper.deg((double)Mth.atan2((double)diff.x, (double)diff.z)) + 180.0f;
        if (ceiling) {
            diff = diff.multiply(1.0, -1.0, 1.0);
            baseAngle = 180.0f - baseAngle;
        }
        float alphaOffset = AngleHelper.deg((double)Mth.atan2((double)diff.y, (double)horizontalDistance));
        float a = 0.875f;
        float a2 = a * a;
        float b = 0.9375f;
        float b2 = b * b;
        float diffLength = Mth.clamp((float)Mth.sqrt((float)((float)(diff.y * diff.y + (double)(horizontalDistance * horizontalDistance)))), (float)0.125f, (float)(a + b));
        float diffLength2 = diffLength * diffLength;
        float alphaRatio = (-b2 + a2 + diffLength2) / (2.0f * a * diffLength);
        float alpha = AngleHelper.deg((double)Math.acos(alphaRatio)) + alphaOffset;
        float betaRatio = (-diffLength2 + a2 + b2) / (2.0f * b * a);
        float beta = AngleHelper.deg((double)Math.acos(betaRatio));
        if (Float.isNaN(alpha)) {
            alpha = 0.0f;
        }
        if (Float.isNaN(beta)) {
            beta = 0.0f;
        }
        Vec3 headPos = new Vec3(0.0, 0.0, 0.0);
        headPos = VecHelper.rotate((Vec3)headPos.add(0.0, (double)b, 0.0), (double)(beta + 180.0f), (Direction.Axis)Direction.Axis.X);
        headPos = VecHelper.rotate((Vec3)headPos.add(0.0, (double)a, 0.0), (double)(alpha - 90.0f), (Direction.Axis)Direction.Axis.X);
        headPos = VecHelper.rotate((Vec3)headPos, (double)baseAngle, (Direction.Axis)Direction.Axis.Y);
        headPos = VecHelper.rotate((Vec3)headPos, (double)(ceiling ? 180.0 : 0.0), (Direction.Axis)Direction.Axis.X);
        headPos = headPos.add(origin);
        Vec3 headDiff = clawTarget.subtract(headPos);
        if (ceiling) {
            headDiff = headDiff.multiply(1.0, -1.0, 1.0);
        }
        float horizontalHeadDistance = (float)headDiff.multiply(1.0, 0.0, 1.0).length();
        float headAngle = alpha + beta + 135.0f - AngleHelper.deg((double)Mth.atan2((double)headDiff.y, (double)horizontalHeadDistance));
        this.lowerArmAngle = alpha;
        this.upperArmAngle = beta;
        this.headAngle = -headAngle;
        this.baseAngle = baseAngle;
    }
}
