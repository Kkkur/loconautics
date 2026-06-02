/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.entity.entity_rotations_and_riding;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.mixinhelpers.entity.entity_riding_sub_level_vehicle.EntityRidingSubLevelVehicleHelper;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EntityType.class})
public class EntityTypeMixin {
    @Inject(method={"method_17843", "lambda$loadEntityRecursive$7"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;startRiding(Lnet/minecraft/world/entity/Entity;Z)Z")})
    private static void sable$startRidingEntity(CompoundTag compoundTag, Level level, Function function, Entity entity, CallbackInfoReturnable<Entity> cir, @Local(ordinal=1) Entity newEntity) {
        SubLevel vehicleSubLevel = Sable.HELPER.getContaining(entity);
        if (vehicleSubLevel != null && EntitySubLevelUtil.shouldKick(newEntity)) {
            Vec3 pos = EntityRidingSubLevelVehicleHelper.kickRidingEntity(newEntity, vehicleSubLevel);
            newEntity.setPos(pos);
        }
    }
}
