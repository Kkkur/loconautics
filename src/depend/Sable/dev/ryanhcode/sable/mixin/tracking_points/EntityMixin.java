/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.tracking_points;

import dev.ryanhcode.sable.mixinterface.player_freezing.PlayerFreezeExtension;
import dev.ryanhcode.sable.sublevel.tracking_points.SubLevelTrackingPointSavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Entity.class})
public abstract class EntityMixin {
    @Shadow
    private Level level;

    @Shadow
    public abstract void setPosRaw(double var1, double var3, double var5);

    @Inject(method={"load"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;setPosRaw(DDD)V", shift=At.Shift.AFTER)})
    private void sable$load(CompoundTag compoundTag, CallbackInfo ci) {
        SubLevelTrackingPointSavedData data;
        SubLevelTrackingPointSavedData.TakenLoginPoint point;
        if (compoundTag.contains("LoginPoint") && (point = (data = SubLevelTrackingPointSavedData.getOrLoad((ServerLevel)this.level)).take(compoundTag.getUUID("LoginPoint"), true)) != null) {
            EntityMixin entityMixin;
            Vector3dc position = point.position();
            this.setPosRaw(position.x(), position.y(), position.z());
            if (point.subLevelId() != null && (entityMixin = this) instanceof PlayerFreezeExtension) {
                PlayerFreezeExtension extension = (PlayerFreezeExtension)((Object)entityMixin);
                extension.sable$freezeTo(point.subLevelId(), (Vector3dc)point.localAnchor().add(0.0, 0.2, 0.0));
            }
            compoundTag.remove("RootVehicle");
        }
    }
}
