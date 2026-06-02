/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.particle.AirParticle
 *  com.simibubi.create.foundation.particle.AirParticleData
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.SimpleAnimatedParticle
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.particles;

import com.simibubi.create.foundation.particle.AirParticle;
import com.simibubi.create.foundation.particle.AirParticleData;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.particle.ParticleSubLevelKickable;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={AirParticle.class})
public abstract class AirParticleMixin
extends SimpleAnimatedParticle
implements ParticleSubLevelKickable {
    @Shadow
    private float twirlAngleOffset;
    @Shadow
    private float twirlRadius;
    @Shadow
    private float drag;
    @Unique
    private double sable$originX;
    @Unique
    private double sable$originZ;
    @Unique
    private double sable$originY;
    @Unique
    private double sable$targetY;
    @Unique
    private double sable$targetX;
    @Unique
    private double sable$targetZ;
    @Shadow
    private Direction.Axis twirlAxis;

    protected AirParticleMixin(ClientLevel arg, double d, double e, double f, SpriteSet arg2, float g) {
        super(arg, d, e, f, arg2, g);
    }

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void sable$postInit(ClientLevel world, AirParticleData data, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprite, CallbackInfo ci) {
        this.sable$originX = x;
        this.sable$originY = y;
        this.sable$originZ = z;
        this.sable$targetX = x + dx;
        this.sable$targetY = y + dy;
        this.sable$targetZ = z + dz;
    }

    @Overwrite
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        float progress = (float)Math.pow((float)this.age / (float)this.lifetime, this.drag);
        float angle = (progress * 2.0f * 360.0f + this.twirlAngleOffset) % 360.0f;
        Vec3 twirl = VecHelper.rotate((Vec3)new Vec3(0.0, (double)this.twirlRadius, 0.0), (double)angle, (Direction.Axis)this.twirlAxis);
        double desiredX = Mth.lerp((double)progress, (double)this.sable$originX, (double)this.sable$targetX) + twirl.x;
        double desiredY = Mth.lerp((double)progress, (double)this.sable$originY, (double)this.sable$targetY) + twirl.y;
        double desiredZ = Mth.lerp((double)progress, (double)this.sable$originZ, (double)this.sable$targetZ) + twirl.z;
        Vector3d desiredVec = Sable.HELPER.projectOutOfSubLevel((Level)this.level, new Vector3d(desiredX, desiredY, desiredZ));
        this.xd = desiredVec.x - this.x;
        this.yd = desiredVec.y - this.y;
        this.zd = desiredVec.z - this.z;
        this.setSpriteFromAge(this.sprites);
        this.move(this.xd, this.yd, this.zd);
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
