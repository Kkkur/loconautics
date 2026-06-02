/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.simibubi.create.api.behaviour.movement.MovementBehaviour
 *  com.simibubi.create.content.contraptions.behaviour.MovementContext
 *  com.simibubi.create.content.kinetics.base.BlockBreakingMovementBehaviour
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.behaviour_compatibility.block_breaking_behaviour;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.kinetics.base.BlockBreakingMovementBehaviour;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.block_breakers.SubLevelBlockBreakingUtility;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={BlockBreakingMovementBehaviour.class})
public abstract class BlockBreakingMovementBehaviourMixin
implements MovementBehaviour {
    @Shadow
    public abstract boolean canBreak(Level var1, BlockPos var2, BlockState var3);

    @WrapMethod(method={"visitNewPosition"})
    public void sable$checkPosition(MovementContext context, BlockPos pos, Operation<Void> original) {
        if (!context.stall) {
            original.call(new Object[]{context, pos});
            if (!context.stall) {
                Vec3 localCenter = context.localPos.getCenter();
                Vec3 sublevelLocalCenter = context.contraption.entity.toGlobalVector(localCenter, 1.0f);
                Vec3 subLevelLocalDir = (Vec3)context.rotation.apply(this.getActiveAreaOffset(context));
                BlockPos breakingPosWSublevel = SubLevelBlockBreakingUtility.findBreakingPos((blockPos, state) -> this.canBreak(context.world, (BlockPos)blockPos, (BlockState)state), Sable.HELPER.getContaining(context.world, (Vec3i)context.contraption.anchor), context.world, subLevelLocalDir, sublevelLocalCenter, pos);
                original.call(new Object[]{context, breakingPosWSublevel});
            }
        }
    }

    @Inject(method={"tick"}, at={@At(value="HEAD")}, cancellable=true)
    public void sable$testBreakingPosDist(MovementContext context, CallbackInfo ci) {
        BlockPos blockPos;
        CompoundTag data = context.data;
        if ((data.contains("BreakingPos") || data.contains("LastPos")) && (blockPos = NbtUtils.readBlockPos((CompoundTag)data, (String)"BreakingPos").orElseGet(() -> NbtUtils.readBlockPos((CompoundTag)data, (String)"LastPos").orElse(null))) != null) {
            Vec3 localCenter = context.localPos.getCenter();
            Vec3 sublevelLocalCenter = context.contraption.entity.toGlobalVector(localCenter, 1.0f);
            Vec3 targetCenter = blockPos.getCenter();
            ActiveSableCompanion helper = Sable.HELPER;
            SubLevel parentSublevel = helper.getContaining(context.world, (Vec3i)context.contraption.anchor);
            SubLevel targetSubLevel = helper.getContaining(context.world, (Vec3i)blockPos);
            if (parentSublevel != null) {
                sublevelLocalCenter = parentSublevel.logicalPose().transformPosition(sublevelLocalCenter);
            }
            if (targetSubLevel != null) {
                targetCenter = targetSubLevel.logicalPose().transformPosition(targetCenter);
            }
            if (sublevelLocalCenter.distanceToSqr(targetCenter) > 4.0) {
                data.remove("Progress");
                data.remove("TicksUntilNextProgress");
                data.remove("BreakingPos");
                data.remove("LastPos");
                data.remove("WaitingTicks");
                context.stall = false;
                context.world.destroyBlockProgress(data.getInt("BreakerId"), blockPos, -1);
                ci.cancel();
            }
        }
    }
}
