/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 */
package com.simibubi.create.content.trains;

import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;

public class CameraDistanceModifier {
    private static final LerpedFloat multiplier = LerpedFloat.linear().startWithValue(1.0);

    public static float getMultiplier() {
        return CameraDistanceModifier.getMultiplier(AnimationTickHolder.getPartialTicks());
    }

    public static float getMultiplier(float partialTicks) {
        return multiplier.getValue(partialTicks);
    }

    public static void tick() {
        multiplier.tickChaser();
    }

    public static void reset() {
        multiplier.chase(1.0, 0.1, LerpedFloat.Chaser.EXP);
    }

    public static void zoomOut() {
        CameraDistanceModifier.zoomOut(AllConfigs.client().mountedZoomMultiplier.getF());
    }

    public static void zoomOut(float targetMultiplier) {
        multiplier.chase((double)targetMultiplier, 0.075, LerpedFloat.Chaser.EXP);
    }
}
