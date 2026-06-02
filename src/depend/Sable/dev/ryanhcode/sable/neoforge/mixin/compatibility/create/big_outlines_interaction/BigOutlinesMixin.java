/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.foundation.block.BigOutlines
 *  com.simibubi.create.foundation.utility.RaycastHelper$PredicateTraceResult
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.big_outlines_interaction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.foundation.block.BigOutlines;
import com.simibubi.create.foundation.utility.RaycastHelper;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.raycasts.SableRaycastHelper;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import java.util.function.Predicate;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={BigOutlines.class})
public class BigOutlinesMixin {
    @Unique
    private static ClientSubLevel sable$predicateSubLevel = null;

    @WrapOperation(method={"pick"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D")})
    private static double sable$modifyMaxRange(Vec3 worldHitPos, Vec3 origin, Operation<Double> original, @Local Minecraft minecraft) {
        ClientSubLevel containing = Sable.HELPER.getContainingClient((Position)worldHitPos);
        float pt = AnimationTickHolder.getPartialTicks((LevelAccessor)minecraft.level);
        if (containing != null) {
            worldHitPos = containing.renderPose(pt).transformPosition(worldHitPos);
        }
        return (Double)original.call(new Object[]{worldHitPos, origin});
    }

    @Redirect(method={"pick"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/foundation/utility/RaycastHelper;rayTraceUntil(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Ljava/util/function/Predicate;)Lcom/simibubi/create/foundation/utility/RaycastHelper$PredicateTraceResult;"))
    private static RaycastHelper.PredicateTraceResult sable$useSubLevelInclusiveCast(Vec3 worldOrigin, Vec3 worldTarget, Predicate<BlockPos> predicate, @Local Minecraft minecraft) {
        return SableRaycastHelper.rayCastUntilWithSublevels((Level)minecraft.level, worldOrigin, worldTarget, (subLevel, pos) -> {
            sable$predicateSubLevel = (ClientSubLevel)subLevel;
            return predicate.test((BlockPos)pos);
        });
    }

    @WrapOperation(method={"lambda$pick$0"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/phys/shapes/VoxelShape;clip(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/BlockHitResult;")})
    private static BlockHitResult sable$clipUsingLocalSubLevel(VoxelShape instance, Vec3 origin, Vec3 target, BlockPos blockPos, Operation<BlockHitResult> original) {
        float pt = AnimationTickHolder.getPartialTicks((LevelAccessor)Minecraft.getInstance().level);
        if (sable$predicateSubLevel == null) {
            return (BlockHitResult)original.call(new Object[]{instance, origin, target, blockPos});
        }
        Vec3 localOrigin = sable$predicateSubLevel.renderPose(pt).transformPositionInverse(origin);
        Vec3 localTarget = sable$predicateSubLevel.renderPose(pt).transformPositionInverse(target);
        return (BlockHitResult)original.call(new Object[]{instance, localOrigin, localTarget, blockPos});
    }

    @WrapOperation(method={"lambda$pick$0"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D")})
    private static double sable$distanceToWithSubLevel(Vec3 instance, Vec3 origin, Operation<Double> original) {
        float pt = AnimationTickHolder.getPartialTicks((LevelAccessor)Minecraft.getInstance().level);
        if (sable$predicateSubLevel == null) {
            return (Double)original.call(new Object[]{instance, origin});
        }
        Vec3 localOrigin = sable$predicateSubLevel.renderPose(pt).transformPositionInverse(origin);
        return (Double)original.call(new Object[]{instance, localOrigin});
    }
}
