/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  com.simibubi.create.content.kinetics.fan.AirFlowParticle
 *  com.simibubi.create.content.kinetics.fan.IAirCurrentSource
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.SimpleAnimatedParticle
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.particles;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.kinetics.fan.AirFlowParticle;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={AirFlowParticle.class})
public abstract class AirFlowParticleMixin
extends SimpleAnimatedParticle {
    @Unique
    Vec3 sable$subLevelOrientation;
    @Shadow
    @Final
    private IAirCurrentSource source;

    protected AirFlowParticleMixin(ClientLevel arg, double d, double e, double f, SpriteSet arg2, float g) {
        super(arg, d, e, f, arg2, g);
    }

    @Inject(method={"tick"}, at={@At(value="HEAD")}, cancellable=true)
    public void sable$fixAirflowParticle(CallbackInfo ci) {
        if (this.source == null || this.source.getAirCurrent() == null || this.source.getAirCurrent().direction == null) {
            this.remove();
            ci.cancel();
        }
    }

    @Redirect(method={"tick"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/AABB;contains(DDD)Z", ordinal=0))
    public boolean sable$reverseProjectPos(AABB instance, double x, double y, double z) {
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Vec3i)this.source.getAirCurrentPos());
        if (subLevel != null) {
            return true;
        }
        return instance.contains(x, y, z);
    }

    @Redirect(method={"tick"}, at=@At(value="NEW", target="(DDD)Lnet/minecraft/world/phys/Vec3;", ordinal=0))
    public Vec3 sable$reverseProjectPos2(double x, double y, double z) {
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Vec3i)this.source.getAirCurrentPos());
        if (subLevel != null) {
            return subLevel.logicalPose().transformPositionInverse(new Vec3(x, y, z));
        }
        return new Vec3(x, y, z);
    }

    @Inject(method={"tick"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/fan/IAirCurrentSource;getAirCurrent()Lcom/simibubi/create/content/kinetics/fan/AirCurrent;", ordinal=1)})
    public void sable$transformNormal(CallbackInfo ci, @Local(ordinal=1) LocalRef<Vec3> motion) {
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Vec3i)this.source.getAirCurrentPos());
        if (subLevel != null) {
            if (this.sable$subLevelOrientation == null) {
                this.sable$subLevelOrientation = subLevel.logicalPose().transformNormal((Vec3)motion.get());
            }
        } else {
            this.sable$subLevelOrientation = (Vec3)motion.get();
        }
        motion.set((Object)this.sable$subLevelOrientation);
    }
}
