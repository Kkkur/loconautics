/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.goal.EatBlockGoal
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.mixin.entity.entity_ai;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={EatBlockGoal.class})
public class EatBlockGoalMixin {
    @WrapOperation(method={"tick", "canUse"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/Mob;blockPosition()Lnet/minecraft/core/BlockPos;")})
    private BlockPos sable$blockPosition(Mob instance, Operation<BlockPos> original) {
        BlockPos pos = (BlockPos)original.call(new Object[]{instance});
        SubLevel subLevel = Sable.HELPER.getTrackingSubLevel((Entity)instance);
        if (subLevel != null) {
            Vec3 transformed = subLevel.logicalPose().transformPositionInverse(instance.position());
            pos = BlockPos.containing((Position)transformed);
        }
        return pos;
    }
}
