/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.gameevent.BlockPositionSource
 *  net.minecraft.world.level.gameevent.PositionSource
 *  net.minecraft.world.level.gameevent.vibrations.VibrationInfo
 *  net.minecraft.world.level.gameevent.vibrations.VibrationSystem$Ticker
 *  net.minecraft.world.level.gameevent.vibrations.VibrationSystem$User
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.sculk_vibrations;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={VibrationSystem.Ticker.class})
public interface VibrationSystemTickerMixin {
    @WrapOperation(method={"receiveVibration"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/gameevent/vibrations/VibrationInfo;pos()Lnet/minecraft/world/phys/Vec3;")})
    private static Vec3 sable$useGlobalPos(VibrationInfo instance, Operation<Vec3> original, @Local(argsOnly=true) ServerLevel level) {
        return Sable.HELPER.projectOutOfSubLevel((Level)level, (Vec3)original.call(new Object[]{instance}));
    }

    @WrapOperation(method={"receiveVibration", "lambda$trySelectAndScheduleVibration$0", "method_51408", "tryReloadVibrationParticle"}, expect=3, require=3, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/gameevent/vibrations/VibrationSystem$User;getPositionSource()Lnet/minecraft/world/level/gameevent/PositionSource;")})
    private static PositionSource sable$useGlobalDestPos(VibrationSystem.User instance, Operation<PositionSource> original, @Local(argsOnly=true) ServerLevel level) {
        PositionSource origSource = (PositionSource)original.call(new Object[]{instance});
        Optional optPos = origSource.getPosition((Level)level);
        if (optPos.isPresent()) {
            return new BlockPositionSource(BlockPos.containing((Position)Sable.HELPER.projectOutOfSubLevel((Level)level, (Vec3)optPos.get())));
        }
        return origSource;
    }
}
