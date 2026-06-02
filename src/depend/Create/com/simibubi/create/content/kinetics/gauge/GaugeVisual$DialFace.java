/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.Direction
 */
package com.simibubi.create.content.kinetics.gauge;

import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.Direction;

protected class GaugeVisual.DialFace
extends Couple<TransformedInstance> {
    Direction face;

    public GaugeVisual.DialFace(Direction face, TransformedInstance first, TransformedInstance second) {
        super((Object)first, (Object)second);
        this.face = face;
    }

    private void setupTransform(TransformStack<?> msr, float progress) {
        float dialPivot = 0.359375f;
        msr.pushPose();
        this.rotateToFace(msr);
        ((TransformedInstance)this.getSecond()).setTransform(GaugeVisual.this.ms).setChanged();
        ((TransformStack)((TransformStack)msr.translate(0.0f, dialPivot, dialPivot)).rotate((float)(1.5707963267948966 * (double)(-progress)), Direction.EAST)).translate(0.0f, -dialPivot, -dialPivot);
        ((TransformedInstance)this.getFirst()).setTransform(GaugeVisual.this.ms).setChanged();
        msr.popPose();
    }

    private void updateTransform(TransformStack<?> msr, float progress) {
        float dialPivot = 0.359375f;
        msr.pushPose();
        ((TransformStack)((TransformStack)this.rotateToFace(msr).translate(0.0f, dialPivot, dialPivot)).rotate((float)(1.5707963267948966 * (double)(-progress)), Direction.EAST)).translate(0.0f, -dialPivot, -dialPivot);
        ((TransformedInstance)this.getFirst()).setTransform(GaugeVisual.this.ms).setChanged();
        msr.popPose();
    }

    protected TransformStack<?> rotateToFace(TransformStack<?> msr) {
        return (TransformStack)((TransformStack)((TransformStack)msr.center()).rotate((float)((double)((-this.face.toYRot() - 90.0f) / 180.0f) * Math.PI), Direction.UP)).uncenter();
    }

    private void delete() {
        ((TransformedInstance)this.getFirst()).delete();
        ((TransformedInstance)this.getSecond()).delete();
    }
}
