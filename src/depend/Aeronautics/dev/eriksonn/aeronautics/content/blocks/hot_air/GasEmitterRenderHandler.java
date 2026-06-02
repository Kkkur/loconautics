/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air;

import net.createmod.catnip.animation.LerpedFloat;

public class GasEmitterRenderHandler {
    private final LerpedFloat position = LerpedFloat.linear();
    private final LerpedFloat fade = LerpedFloat.linear();

    public GasEmitterRenderHandler() {
        this.position.chase(0.0, 0.2, LerpedFloat.Chaser.EXP);
        this.fade.chase(0.0, 0.2, LerpedFloat.Chaser.EXP);
    }

    public void targetFromRedstoneSignal(int signal) {
        this.targetFromValue((float)signal / 15.0f);
    }

    public void targetFromValue(float value) {
        this.position.updateChaseTarget(value);
    }

    public void tick() {
        this.position.tickChaser();
        this.fade.updateChaseTarget(this.position.getChaseTarget() > 0.0f || (double)this.position.getValue() > 0.5 ? 1.0f : 0.0f);
        this.fade.tickChaser();
    }

    public int getAlpha(float partialTick) {
        return (int)(this.fade.getValue(partialTick) * 255.0f);
    }

    public float getPosition(float partialTick) {
        return this.position.getValue(partialTick);
    }
}
