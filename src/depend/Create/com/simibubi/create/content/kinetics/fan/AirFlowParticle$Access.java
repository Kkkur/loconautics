/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.particles.ParticleOptions
 */
package com.simibubi.create.content.kinetics.fan;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.minecraft.core.particles.ParticleOptions;

private class AirFlowParticle.Access
implements FanProcessingType.AirFlowParticleAccess {
    private AirFlowParticle.Access() {
    }

    @Override
    public void setColor(int color) {
        AirFlowParticle.this.setColor(color);
    }

    @Override
    public void setAlpha(float alpha) {
        AirFlowParticle.this.setAlpha(alpha);
    }

    @Override
    public void spawnExtraParticle(ParticleOptions options, float speedMultiplier) {
        AirFlowParticle.this.level.addParticle(options, AirFlowParticle.this.x, AirFlowParticle.this.y, AirFlowParticle.this.z, AirFlowParticle.this.xd * (double)speedMultiplier, AirFlowParticle.this.yd * (double)speedMultiplier, AirFlowParticle.this.zd * (double)speedMultiplier);
    }
}
