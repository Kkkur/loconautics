/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.mixin.entity.entity_sublevel_collision;

import dev.ryanhcode.sable.Sable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={ItemEntity.class})
public abstract class ItemEntityMixin
extends Entity {
    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(method={"tick"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;horizontalDistanceSqr()D"))
    private double sable$shouldTickPhysics(Vec3 instance) {
        if (Sable.HELPER.getTrackingSubLevel(this) != null) {
            return 1.0;
        }
        return instance.horizontalDistance();
    }
}
