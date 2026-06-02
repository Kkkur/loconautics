/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.entity.EnchantingTableBlockEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.mixin.enchanting_table;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={EnchantingTableBlockEntity.class})
public class EnchantingTableBlockEntityMixin {
    @Redirect(method={"bookAnimationTick"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/player/Player;getX()D"))
    private static double sable$getPlayerX(Player instance, @Local(argsOnly=true) BlockPos blockPos) {
        SubLevel subLevel = Sable.HELPER.getContaining(instance.level(), (Vec3i)blockPos);
        if (subLevel != null) {
            return subLevel.logicalPose().transformPositionInverse(instance.getEyePosition()).x();
        }
        return instance.getX();
    }

    @Redirect(method={"bookAnimationTick"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/player/Player;getZ()D"))
    private static double sable$getPlayerZ(Player instance, @Local(argsOnly=true) BlockPos blockPos) {
        SubLevel subLevel = Sable.HELPER.getContaining(instance.level(), (Vec3i)blockPos);
        if (subLevel != null) {
            return subLevel.logicalPose().transformPositionInverse(instance.getEyePosition()).z();
        }
        return instance.getZ();
    }
}
