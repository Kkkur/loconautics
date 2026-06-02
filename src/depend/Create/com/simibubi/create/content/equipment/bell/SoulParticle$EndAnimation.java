/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.equipment.bell;

import com.simibubi.create.content.equipment.bell.SoulParticle;

public static class SoulParticle.EndAnimation
extends SoulParticle.AnimationStage {
    public SoulParticle.EndAnimation(SoulParticle particle) {
        super(particle);
    }

    @Override
    public void tick() {
        super.tick();
        this.particle.setFrame(this.particle.firstEndFrame + (int)(this.getAnimAge() / (float)this.particle.endTicks * (float)this.particle.endFrames));
    }

    @Override
    public SoulParticle.AnimationStage getNext() {
        if (this.animAge < this.particle.endTicks) {
            return this;
        }
        return null;
    }
}
