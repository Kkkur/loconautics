/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.equipment.bell;

import com.simibubi.create.content.equipment.bell.SoulParticle;

public static class SoulParticle.StartAnimation
extends SoulParticle.AnimationStage {
    public SoulParticle.StartAnimation(SoulParticle particle) {
        super(particle);
    }

    @Override
    public void tick() {
        super.tick();
        this.particle.setFrame(this.particle.firstStartFrame + (int)(this.getAnimAge() / (float)this.particle.startTicks * (float)this.particle.startFrames));
    }

    @Override
    public SoulParticle.AnimationStage getNext() {
        if (this.animAge < this.particle.startTicks) {
            return this;
        }
        return new SoulParticle.LoopAnimation(this.particle);
    }
}
