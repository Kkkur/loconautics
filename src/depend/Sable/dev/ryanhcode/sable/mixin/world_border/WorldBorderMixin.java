/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.border.WorldBorder
 *  net.minecraft.world.phys.AABB
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.world_border;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.mixinterface.world_border.WorldBorderExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={WorldBorder.class})
public class WorldBorderMixin
implements WorldBorderExtension {
    @Unique
    private Level sable$level;

    @Inject(method={"isWithinBounds(DDD)Z"}, at={@At(value="HEAD")}, cancellable=true)
    public void sable$isWithinBounds(double x, double z, double offset, CallbackInfoReturnable<Boolean> cir) {
        if (this.sable$level == null) {
            return;
        }
        SubLevelContainer container = SubLevelContainer.getContainer(this.sable$level);
        if (container != null && container.inBounds(Mth.floor((double)x) >> 4, Mth.floor((double)z) >> 4)) {
            cir.setReturnValue((Object)true);
        }
    }

    @Inject(method={"clampToBounds(DDD)Lnet/minecraft/core/BlockPos;"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$clampToBounds(double x, double y, double z, CallbackInfoReturnable<BlockPos> cir) {
        if (this.sable$level == null) {
            return;
        }
        SubLevelContainer container = SubLevelContainer.getContainer(this.sable$level);
        if (container != null && container.inBounds(Mth.floor((double)x) >> 4, Mth.floor((double)z) >> 4)) {
            cir.setReturnValue((Object)BlockPos.containing((double)x, (double)y, (double)z));
        }
    }

    @Inject(method={"isInsideCloseToBorder"}, at={@At(value="HEAD")}, cancellable=true)
    public void sable$isInsideCloseToBorder(Entity entity, AABB aABB, CallbackInfoReturnable<Boolean> cir) {
        if (this.sable$level == null) {
            return;
        }
        SubLevelContainer container = SubLevelContainer.getContainer(this.sable$level);
        if (container != null && Sable.HELPER.getContaining(entity) != null) {
            cir.setReturnValue((Object)false);
        }
    }

    @Override
    public void sable$setLevel(Level level) {
        this.sable$level = level;
    }
}
