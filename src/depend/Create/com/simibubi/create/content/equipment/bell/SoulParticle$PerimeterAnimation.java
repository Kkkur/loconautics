/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.equipment.bell;

import com.simibubi.create.content.equipment.bell.SoulParticle;

public static class SoulParticle.PerimeterAnimation
extends SoulParticle.AnimationStage {
    public SoulParticle.PerimeterAnimation(SoulParticle particle) {
        super(particle);
    }

    @Override
    public void tick() {
        super.tick();
        this.particle.setFrame((int)this.getAnimAge() % this.particle.perimeterFrames);
    }

    @Override
    public SoulParticle.AnimationStage getNext() {
        if (this.animAge < (this.particle.isExpandingPerimeter ? 8 : this.particle.startTicks + this.particle.endTicks + this.particle.numLoops * this.particle.loopLength)) {
            return this;
        }
        return null;
    }
}
