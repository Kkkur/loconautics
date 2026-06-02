/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.FlameParticle
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.world.phys.AABB
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.sable.mixin.particle;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.mixinterface.particle.ParticleExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={FlameParticle.class})
public abstract class FlameParticleMixin
extends Particle
implements ParticleExtension {
    protected FlameParticleMixin(ClientLevel clientLevel, double d, double e, double f) {
        super(clientLevel, d, e, f);
    }

    @WrapMethod(method={"move"})
    public void move(double motionX, double motionY, double motionZ, Operation<Void> original) {
        SubLevel trackingSubLevel = this.sable$getTrackingSubLevel();
        if (trackingSubLevel == null || trackingSubLevel.isRemoved()) {
            original.call(new Object[]{motionX, motionY, motionZ});
            return;
        }
        Pose3d pose = trackingSubLevel.logicalPose();
        Pose3dc last = trackingSubLevel.lastPose();
        Vector3d globalBoundsCenter = JOMLConversion.getAABBCenter((AABB)this.getBoundingBox());
        Vector3d localPosition = last.transformPositionInverse((Vector3dc)globalBoundsCenter, new Vector3d());
        Vector3d newGlobalPosition = pose.transformPosition(localPosition);
        original.call(new Object[]{motionX + newGlobalPosition.x - globalBoundsCenter.x(), motionY + newGlobalPosition.y - globalBoundsCenter.y(), motionZ + newGlobalPosition.z - globalBoundsCenter.z()});
    }
}
