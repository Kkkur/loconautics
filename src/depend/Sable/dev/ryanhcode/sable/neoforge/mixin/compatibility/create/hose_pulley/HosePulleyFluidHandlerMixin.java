/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.simibubi.create.content.fluids.hosePulley.HosePulleyFluidHandler
 *  com.simibubi.create.content.fluids.transfer.FluidDrainingBehaviour
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.hose_pulley;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyFluidHandler;
import com.simibubi.create.content.fluids.transfer.FluidDrainingBehaviour;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={HosePulleyFluidHandler.class})
public abstract class HosePulleyFluidHandlerMixin {
    @Shadow
    private FluidDrainingBehaviour drainer;
    @Shadow
    private Supplier<BlockPos> rootPosGetter;
    @Unique
    private BlockPos sable$lastValidPos = null;

    @Inject(method={"drainInternal"}, at={@At(value="HEAD")})
    public void sable$updateLastValidPos(int maxDrain, FluidStack resource, IFluidHandler.FluidAction action, CallbackInfoReturnable<FluidStack> cir) {
        ActiveSableCompanion helper = Sable.HELPER;
        Level level = this.drainer.getWorld();
        float distance = 1.5f;
        this.sable$lastValidPos = helper.runIncludingSubLevels(level, this.rootPosGetter.get().getCenter(), true, helper.getContaining(level, (Vec3i)this.drainer.getPos()), (sublevel, pos) -> {
            if (HosePulleyFluidHandlerMixin.sable$hasFluid(level, pos)) {
                if (this.sable$lastValidPos == null || this.sable$lastValidPos.distSqr((Vec3i)pos) > 2.25) {
                    return pos;
                }
                return this.sable$lastValidPos;
            }
            return null;
        });
    }

    @WrapOperation(method={"drainInternal"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/fluids/transfer/FluidDrainingBehaviour;getDrainableFluid(Lnet/minecraft/core/BlockPos;)Lnet/neoforged/neoforge/fluids/FluidStack;")})
    public FluidStack sable$modifyGetDrainableFluid(FluidDrainingBehaviour instance, BlockPos rootPos, Operation<FluidStack> original) {
        if (this.sable$lastValidPos != null) {
            return (FluidStack)original.call(new Object[]{instance, this.sable$lastValidPos});
        }
        return (FluidStack)original.call(new Object[]{instance, rootPos});
    }

    @WrapOperation(method={"drainInternal"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/fluids/transfer/FluidDrainingBehaviour;pullNext(Lnet/minecraft/core/BlockPos;Z)Z")})
    public boolean sable$modifyPullNext(FluidDrainingBehaviour instance, BlockPos root, boolean simulate, Operation<Boolean> original) {
        if (this.sable$lastValidPos != null) {
            return (Boolean)original.call(new Object[]{instance, this.sable$lastValidPos, simulate});
        }
        return (Boolean)original.call(new Object[]{instance, root, simulate});
    }

    @WrapOperation(method={"getFluidInTank"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/fluids/transfer/FluidDrainingBehaviour;getDrainableFluid(Lnet/minecraft/core/BlockPos;)Lnet/neoforged/neoforge/fluids/FluidStack;")})
    public FluidStack sable$modifyGetFluidInTank(FluidDrainingBehaviour instance, BlockPos rootPos, Operation<FluidStack> original) {
        if (this.sable$lastValidPos != null) {
            return (FluidStack)original.call(new Object[]{instance, this.sable$lastValidPos});
        }
        return (FluidStack)original.call(new Object[]{instance, rootPos});
    }

    @Unique
    private static boolean sable$hasFluid(Level level, BlockPos pos) {
        return !level.getFluidState(pos).isEmpty();
    }
}
