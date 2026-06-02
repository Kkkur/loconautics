/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.item.PrimedTnt
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.entity.tnt_jumps;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PrimedTnt.class})
public abstract class PrimedTntMixin
extends Entity {
    public PrimedTntMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method={"<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/entity/LivingEntity;)V"}, at={@At(value="TAIL")})
    private void sable$setTntJump(Level level, double d, double e, double f, LivingEntity livingEntity, CallbackInfo ci) {
        SubLevel subLevel = Sable.HELPER.getContaining(level, (Vec3i)this.blockPosition());
        if (subLevel != null) {
            this.setDeltaMovement(subLevel.logicalPose().transformNormalInverse(this.getDeltaMovement()));
        }
    }
}
