/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.level.CollisionGetter
 *  net.minecraft.world.level.pathfinder.PathfindingContext
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.mixin.entity.entity_pathfinding;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PathfindingContext.class})
public class PathfindingContextMixin {
    @Shadow
    @Final
    @Mutable
    private BlockPos mobPosition;

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void sable$init(CollisionGetter collisionGetter, Mob mob, CallbackInfo ci) {
        SubLevel trackingSubLevel = Sable.HELPER.getTrackingSubLevel((Entity)mob);
        if (trackingSubLevel != null) {
            this.mobPosition = BlockPos.containing((Position)trackingSubLevel.logicalPose().transformPositionInverse(mob.position()));
        }
    }
}
