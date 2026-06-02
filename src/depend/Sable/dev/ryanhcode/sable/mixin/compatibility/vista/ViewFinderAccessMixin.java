/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.mehvahdjukaar.vista.common.view_finder.ViewFinderAccess$Block
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.player.Player
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.compatibility.vista;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import net.mehvahdjukaar.vista.common.view_finder.ViewFinderAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={ViewFinderAccess.Block.class})
public class ViewFinderAccessMixin {
    @WrapOperation(method={"stillValid"}, at={@At(value="INVOKE", target="Lnet/minecraft/core/BlockPos;distToCenterSqr(Lnet/minecraft/core/Position;)D")})
    private static double sable$distToCenterSqr(BlockPos instance, Position position, Operation<Double> original, @Local(argsOnly=true) Player player) {
        return Sable.HELPER.distanceSquaredWithSubLevels(player.level(), position, (double)instance.getX() + 0.5, (double)instance.getY() + 0.5, (double)instance.getZ() + 0.5);
    }
}
