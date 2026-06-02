/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.entity.entities_stick_sublevels.player;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.mixinterface.entity.entity_sublevel_collision.EntityMovementExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LocalPlayer.class})
public abstract class LocalPlayerMixin
extends Entity {
    @Unique
    private Vec3 sable$oldPos;

    public LocalPlayerMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method={"sendPosition"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;getX()D", ordinal=0, shift=At.Shift.BEFORE)})
    private void sable$preSendPosition(CallbackInfo ci) {
        this.sable$oldPos = null;
        SubLevel trackingSubLevel = Sable.HELPER.getTrackingSubLevel(this);
        if (trackingSubLevel != null && !trackingSubLevel.isRemoved()) {
            Vec3 pos = this.position();
            this.sable$oldPos = new Vec3(pos.x, pos.y, pos.z);
            Vec3 localPosition = trackingSubLevel.logicalPose().transformPositionInverse(pos);
            ((EntityMovementExtension)((Object)this)).sable$setPosField(localPosition);
        }
    }

    @Inject(method={"sendPosition"}, at={@At(value="RETURN")})
    private void sable$postSendPosition(CallbackInfo ci) {
        if (this.sable$oldPos != null) {
            ((EntityMovementExtension)((Object)this)).sable$setPosField(this.sable$oldPos);
            this.sable$oldPos = null;
        }
    }
}
