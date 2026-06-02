/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.SuspendedParticle
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.sable.mixin.particle;

import dev.ryanhcode.sable.api.particle.ParticleSubLevelKickable;
import dev.ryanhcode.sable.sublevel.water_occlusion.WaterOcclusionContainer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SuspendedParticle;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={SuspendedParticle.class})
public abstract class SuspendedParticleMixin
extends Particle
implements ParticleSubLevelKickable {
    protected SuspendedParticleMixin(ClientLevel clientLevel, double d, double e, double f) {
        super(clientLevel, d, e, f);
    }

    public void move(double d, double e, double f) {
        super.move(d, e, f);
        WaterOcclusionContainer<?> container = WaterOcclusionContainer.getContainer((Level)this.level);
        if (container != null && container.isOccluded(new Vec3(this.x, this.y, this.z))) {
            this.remove();
        }
    }

    @Override
    public boolean sable$shouldKickFromTracking() {
        return false;
    }

    @Override
    public boolean sable$shouldCollideWithTrackingSubLevel() {
        return false;
    }
}
