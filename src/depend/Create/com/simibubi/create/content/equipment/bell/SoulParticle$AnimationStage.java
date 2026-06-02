/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.equipment.bell;

import com.simibubi.create.content.equipment.bell.SoulParticle;

public static abstract class SoulParticle.AnimationStage {
    protected final SoulParticle particle;
    protected int ticks;
    protected int animAge;

    public SoulParticle.AnimationStage(SoulParticle particle) {
        this.particle = particle;
    }

    public void tick() {
        ++this.ticks;
        if (this.ticks % this.particle.ticksPerFrame == 0) {
            ++this.animAge;
        }
    }

    public float getAnimAge() {
        return this.animAge;
    }

    public abstract SoulParticle.AnimationStage getNext();
}
