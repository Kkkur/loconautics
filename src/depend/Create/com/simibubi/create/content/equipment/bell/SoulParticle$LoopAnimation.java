/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.equipment.bell;

import com.simibubi.create.content.equipment.bell.SoulParticle;

public static class SoulParticle.LoopAnimation
extends SoulParticle.AnimationStage {
    int loops;

    public SoulParticle.LoopAnimation(SoulParticle particle) {
        super(particle);
    }

    @Override
    public void tick() {
        super.tick();
        int loopTick = this.getLoopTick();
        if (loopTick == 0) {
            ++this.loops;
        }
        this.particle.setFrame(this.particle.firstLoopFrame + loopTick);
    }

    private int getLoopTick() {
        return this.animAge % this.particle.loopFrames;
    }

    @Override
    public SoulParticle.AnimationStage getNext() {
        if (this.loops <= this.particle.numLoops) {
            return this;
        }
        return new SoulParticle.EndAnimation(this.particle);
    }
}
