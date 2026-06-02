/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.gameevent.EuclideanGameEventListenerRegistry
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.sculk_vibrations;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.EuclideanGameEventListenerRegistry;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={EuclideanGameEventListenerRegistry.class})
public class EuclideanGameEventListenerRegistryMixin {
    @WrapOperation(method={"getPostableListenerPosition"}, at={@At(value="INVOKE", target="Lnet/minecraft/core/BlockPos;distSqr(Lnet/minecraft/core/Vec3i;)D")})
    private static double replaceDistance(BlockPos from, Vec3i to, Operation<Double> original, @Local(argsOnly=true) ServerLevel level) {
        return Sable.HELPER.distanceSquaredWithSubLevels((Level)level, (Vector3dc)JOMLConversion.atLowerCornerOf((Vec3i)from), (Vector3dc)JOMLConversion.atLowerCornerOf((Vec3i)to));
    }
}
