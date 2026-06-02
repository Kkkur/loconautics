/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.Mth
 */
package com.simibubi.create.foundation.utility;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class CameraAngleAnimationService {
    private static final LerpedFloat yRotation = LerpedFloat.angular().startWithValue(0.0);
    private static final LerpedFloat xRotation = LerpedFloat.angular().startWithValue(0.0);
    private static Mode animationMode = Mode.LINEAR;
    private static float animationSpeed = -1.0f;

    public static void tick() {
        yRotation.tickChaser();
        xRotation.tickChaser();
        if (Minecraft.getInstance().player != null) {
            if (!yRotation.settled()) {
                Minecraft.getInstance().player.setYRot(yRotation.getValue(1.0f));
            }
            if (!xRotation.settled()) {
                Minecraft.getInstance().player.setXRot(xRotation.getValue(1.0f));
            }
        }
    }

    public static boolean isYawAnimating() {
        return !yRotation.settled();
    }

    public static boolean isPitchAnimating() {
        return !xRotation.settled();
    }

    public static float getYaw(float partialTicks) {
        return yRotation.getValue(partialTicks);
    }

    public static float getPitch(float partialTicks) {
        return xRotation.getValue(partialTicks);
    }

    public static void setAnimationMode(Mode mode) {
        animationMode = mode;
    }

    public static void setAnimationSpeed(float speed) {
        animationSpeed = speed;
    }

    public static void setYawTarget(float yaw) {
        float currentYaw = CameraAngleAnimationService.getCurrentYaw();
        yRotation.startWithValue((double)currentYaw);
        CameraAngleAnimationService.setupChaser(yRotation, currentYaw + AngleHelper.getShortestAngleDiff((double)currentYaw, (double)Mth.wrapDegrees((float)yaw)));
    }

    public static void setPitchTarget(float pitch) {
        float currentPitch = CameraAngleAnimationService.getCurrentPitch();
        xRotation.startWithValue((double)currentPitch);
        CameraAngleAnimationService.setupChaser(xRotation, currentPitch + AngleHelper.getShortestAngleDiff((double)currentPitch, (double)Mth.wrapDegrees((float)pitch)));
    }

    private static float getCurrentYaw() {
        if (Minecraft.getInstance().player == null) {
            return 0.0f;
        }
        return Mth.wrapDegrees((float)Minecraft.getInstance().player.getYRot());
    }

    private static float getCurrentPitch() {
        if (Minecraft.getInstance().player == null) {
            return 0.0f;
        }
        return Mth.wrapDegrees((float)Minecraft.getInstance().player.getXRot());
    }

    private static void setupChaser(LerpedFloat rotation, float target) {
        if (animationMode == Mode.LINEAR) {
            rotation.chase((double)target, animationSpeed > 0.0f ? (double)animationSpeed : 2.0, LerpedFloat.Chaser.LINEAR);
        } else if (animationMode == Mode.EXPONENTIAL) {
            rotation.chase((double)target, animationSpeed > 0.0f ? (double)animationSpeed : 0.25, LerpedFloat.Chaser.EXP);
        }
    }

    public static enum Mode {
        LINEAR,
        EXPONENTIAL;

    }
}
