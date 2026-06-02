/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.block_placement;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelHelper;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={UseOnContext.class})
public abstract class UseOnContextMixin {
    @Shadow
    @Final
    private Level level;
    @Shadow
    @Final
    @Nullable
    private Player player;

    @Shadow
    public abstract BlockPos getClickedPos();

    @Inject(method={"getHorizontalDirection"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$getHorizontalDirection(CallbackInfoReturnable<Direction> cir) {
        if (this.player == null) {
            return;
        }
        SubLevel subLevel = Sable.HELPER.getContaining(this.level, (Vec3i)this.getClickedPos());
        if (subLevel != null) {
            SubLevelHelper.pushEntityLocal(subLevel, (Entity)this.player);
            Direction dir = this.player.getDirection();
            SubLevelHelper.popEntityLocal(subLevel, (Entity)this.player);
            cir.setReturnValue((Object)dir);
        }
    }

    @Inject(method={"getRotation"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$getRotation(CallbackInfoReturnable<Float> cir) {
        if (this.player == null) {
            return;
        }
        SubLevel subLevel = Sable.HELPER.getContaining(this.level, (Vec3i)this.getClickedPos());
        if (subLevel != null) {
            SubLevelHelper.pushEntityLocal(subLevel, (Entity)this.player);
            float yRot = this.player.getYRot();
            SubLevelHelper.popEntityLocal(subLevel, (Entity)this.player);
            cir.setReturnValue((Object)Float.valueOf(yRot));
        }
    }
}
