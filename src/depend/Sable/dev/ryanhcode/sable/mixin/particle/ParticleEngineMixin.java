/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleEngine
 *  net.minecraft.client.particle.TerrainParticle
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.particle;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.mixinterface.particle.ParticleExtension;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ParticleEngine.class})
public abstract class ParticleEngineMixin {
    @Shadow
    protected ClientLevel level;

    @Shadow
    public abstract void add(Particle var1);

    @Inject(method={"add"}, at={@At(value="TAIL")})
    private void sable$onParticleAdd(Particle particle, CallbackInfo ci) {
        ((ParticleExtension)particle).sable$initialKickOut();
    }

    @WrapOperation(method={"*"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/particle/Particle;tick()V")})
    private void sable$onParticleTick(Particle instance, Operation<Void> original) {
        ParticleExtension extension = (ParticleExtension)instance;
        extension.sable$initialKickOut();
        original.call(new Object[]{instance});
        extension.sable$moveWithInheritedVelocity();
    }

    @Redirect(method={"crack"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/particle/TerrainParticle;setPower(F)Lnet/minecraft/client/particle/Particle;"))
    private Particle sable$addCrackParticle(TerrainParticle particle, float v, @Local(argsOnly=true) BlockPos pos, @Local BlockState state) {
        Vec3 particlePosition = new Vec3(particle.x, particle.y, particle.z);
        SubLevel subLevel = Sable.HELPER.getContaining((Level)this.level, (Position)particlePosition);
        if (subLevel != null) {
            Vec3 velocity = new Vec3(particle.xd, particle.yd, particle.zd);
            Vec3 globalVelocity = subLevel.logicalPose().transformNormal(velocity);
            particle.xd = globalVelocity.x;
            particle.yd = globalVelocity.y;
            particle.zd = globalVelocity.z;
            particle.setPower(v);
            Vec3 localVelocity = subLevel.logicalPose().transformNormalInverse(new Vec3(particle.xd, particle.yd, particle.zd));
            particle.xd = localVelocity.x;
            particle.yd = localVelocity.y;
            particle.zd = localVelocity.z;
            ((ParticleExtension)particle).sable$setTrackingSubLevel((ClientSubLevel)subLevel, particlePosition);
            return particle;
        }
        return particle.setPower(v);
    }
}
