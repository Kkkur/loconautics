/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.trains.track.TrackPlacement
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.tracks;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.trains.track.TrackPlacement;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={TrackPlacement.class})
public class TrackPlacementMixin {
    @Redirect(method={"tryConnect"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/player/Player;getLookAngle()Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 sable$getLookAngle(Player instance, @Local(argsOnly=true) BlockPos blockPos) {
        Level level = instance.level();
        BlockPos clickedPos = blockPos;
        SubLevel subLevel = Sable.HELPER.getContaining(level, (Vec3i)clickedPos);
        Vec3 lookAngle = instance.getLookAngle();
        if (subLevel != null) {
            lookAngle = subLevel.logicalPose().transformNormalInverse(lookAngle);
        }
        return lookAngle;
    }
}
