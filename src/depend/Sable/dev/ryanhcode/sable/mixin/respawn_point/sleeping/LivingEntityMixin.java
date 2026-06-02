/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.mixin.respawn_point.sleeping;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={LivingEntity.class})
public abstract class LivingEntityMixin
extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Overwrite
    private void setPosToBed(BlockPos blockPos) {
        Vector3d coords = JOMLConversion.upFromBottomCenterOf((Vec3i)blockPos, (double)0.6875);
        this.setPos(JOMLConversion.toMojang((Vector3dc)Sable.HELPER.projectOutOfSubLevel(this.level(), coords)));
    }

    @Redirect(method={"method_18404", "lambda$stopSleeping$12"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/LivingEntity;setPos(DDD)V"), expect=1, require=1)
    private void sable$stopSleeping(LivingEntity instance, double x, double y, double z) {
        double halfHeight = this.getBoundingBox().getYsize() / 2.0;
        Vector3d coords = new Vector3d(x, y + halfHeight, z);
        Sable.HELPER.projectOutOfSubLevel(this.level(), coords).sub(0.0, halfHeight, 0.0);
        instance.setPos(JOMLConversion.toMojang((Vector3dc)coords));
    }
}
