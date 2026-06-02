/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package com.simibubi.create.foundation.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={Mob.class})
public class MobMixin {
    @ModifyExpressionValue(method={"getAttackBoundingBox"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/Mob;getVehicle()Lnet/minecraft/world/entity/Entity;")})
    public Entity create$mobRidingContraptionsMaintainTheirAttackBox(Entity original) {
        return original instanceof AbstractContraptionEntity ? null : original;
    }
}
