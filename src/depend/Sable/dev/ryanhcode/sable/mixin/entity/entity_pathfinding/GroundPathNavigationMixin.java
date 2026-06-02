/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ai.navigation.GroundPathNavigation
 *  net.minecraft.world.level.pathfinder.Path
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.entity.entity_pathfinding;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={GroundPathNavigation.class})
public abstract class GroundPathNavigationMixin {
    @Shadow
    public abstract Path createPath(BlockPos var1, int var2);

    @Inject(method={"createPath(Lnet/minecraft/world/entity/Entity;I)Lnet/minecraft/world/level/pathfinder/Path;"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$createPath(Entity entity, int i, CallbackInfoReturnable<Path> cir) {
        SubLevel trackingSubLevel = Sable.HELPER.getTrackingSubLevel(entity);
        if (trackingSubLevel != null) {
            BlockPos localPos = BlockPos.containing((Position)trackingSubLevel.logicalPose().transformPositionInverse(entity.position()));
            cir.setReturnValue((Object)this.createPath(localPos, i));
        }
    }
}
