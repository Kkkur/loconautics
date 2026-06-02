/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.interaction_distance;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Player.class})
public abstract class PlayerMixin
extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract double blockInteractionRange();

    @Inject(method={"canInteractWithBlock"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$canInteractWithBlock(BlockPos pos, double slop, CallbackInfoReturnable<Boolean> cir) {
        SubLevel subLevel = Sable.HELPER.getContaining(this.level(), (Vec3i)pos);
        if (subLevel != null) {
            boolean closeEnough;
            double rangeWithSlop = this.blockInteractionRange() + slop;
            Vec3 eyePos = subLevel.logicalPose().transformPositionInverse(this.getEyePosition());
            boolean bl = closeEnough = new AABB(pos).distanceToSqr(eyePos) < rangeWithSlop * rangeWithSlop;
            if (closeEnough) {
                cir.setReturnValue((Object)true);
            }
        }
    }

    @Inject(method={"canInteractWithEntity(Lnet/minecraft/world/phys/AABB;D)Z"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$canInteractWithEntity(AABB aabb, double slop, CallbackInfoReturnable<Boolean> cir) {
        SubLevel subLevel = Sable.HELPER.getContaining(this.level(), (Position)aabb.getBottomCenter());
        if (subLevel != null) {
            boolean closeEnough;
            double rangeWithSlop = this.blockInteractionRange() + slop;
            Vec3 eyePos = subLevel.logicalPose().transformPositionInverse(this.getEyePosition());
            boolean bl = closeEnough = aabb.distanceToSqr(eyePos) < rangeWithSlop * rangeWithSlop;
            if (closeEnough) {
                cir.setReturnValue((Object)true);
            }
        }
    }
}
