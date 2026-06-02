/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  dev.ryanhcode.sable.api.SubLevelAssemblyHelper
 *  dev.ryanhcode.sable.api.SubLevelAssemblyHelper$AssemblyTransform
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  dev.ryanhcode.sable.util.BoundedBitVolume3i
 *  dev.simulated_team.simulated.util.SimDirectionUtil
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.LevelAccessor
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.eriksonn.aeronautics.mixin.balloon;

import com.llamalad7.mixinextras.sugar.Local;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.Balloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map.BalloonMap;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.util.BoundedBitVolume3i;
import dev.simulated_team.simulated.util.SimDirectionUtil;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={SubLevelAssemblyHelper.class})
public class SubLevelAssemblyHelperMixin {
    @Inject(method={"needsBitSet"}, at={@At(value="HEAD")}, cancellable=true)
    private static void aeronautics$needsBitSet(ServerLevel level, BoundingBox3ic bounds, List<Entity> entities, CallbackInfoReturnable<Boolean> cir) {
        BalloonMap balloonMap = (BalloonMap)BalloonMap.MAP.get((LevelAccessor)level);
        for (Balloon balloon : balloonMap.getBalloons()) {
            if (!balloon.getBounds().intersects(bounds)) continue;
            cir.setReturnValue((Object)true);
            return;
        }
    }

    @Inject(method={"moveOtherStuff"}, at={@At(value="TAIL")})
    private static void aeronautics$assemble(ServerLevel level, SubLevelAssemblyHelper.AssemblyTransform transform, Iterable<BlockPos> blocks, BoundingBox3ic bounds, CallbackInfo ci, @Local BoundedBitVolume3i volume) {
        BalloonMap balloonMap = (BalloonMap)BalloonMap.MAP.get((LevelAccessor)level);
        for (Balloon balloon : balloonMap.getBalloons()) {
            if (!balloon.getBounds().intersects(bounds)) continue;
            boolean shouldMoveBalloon = false;
            for (Direction direction : SimDirectionUtil.VALUES) {
                BlockPos relativePos = balloon.getControllerPos().relative(direction);
                if (!volume.getOccupied(relativePos.getX(), relativePos.getY(), relativePos.getZ())) continue;
                shouldMoveBalloon = true;
                break;
            }
            if (!shouldMoveBalloon) continue;
            balloon.setAssembling(transform);
        }
    }
}
