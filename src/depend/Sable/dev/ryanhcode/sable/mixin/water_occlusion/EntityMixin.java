/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.core.BlockPos
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.mixin.water_occlusion;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.sublevel.water_occlusion.WaterOcclusionContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Entity.class})
public abstract class EntityMixin {
    @Shadow
    private Level level;

    @Shadow
    public abstract Vec3 position();

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getEyeY();

    @Shadow
    public abstract double getZ();

    @Inject(method={"updateFluidHeightAndDoFluidPushing"}, at={@At(value="HEAD")}, cancellable=true)
    public void sable$updateFluidHeightAndDoFluidPushing(TagKey<Fluid> tagKey, double d, CallbackInfoReturnable<Boolean> cir) {
        boolean occluded = this.sable$isOccluded();
        if (occluded) {
            cir.setReturnValue((Object)false);
        }
    }

    @Unique
    private boolean sable$isOccluded() {
        WaterOcclusionContainer<?> container = WaterOcclusionContainer.getContainer(this.level);
        if (container == null) {
            return false;
        }
        return container.isOccluded(this.getBoundingBox().getCenter()) || container.isOccluded(this.position());
    }

    @WrapOperation(method={"updateSwimming"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/Entity;setSwimming(Z)V")})
    public void sable$inWaterCheck(Entity instance, boolean swimming, Operation<Void> original) {
        if (swimming && this.sable$isOccluded()) {
            original.call(new Object[]{instance, false});
            return;
        }
        original.call(new Object[]{instance, swimming});
    }

    @WrapOperation(method={"updateFluidOnEyes"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;")})
    public FluidState sable$occludeFluidOnEyes(Level instance, BlockPos pos, Operation<FluidState> original) {
        FluidState originalState = (FluidState)original.call(new Object[]{instance, pos});
        WaterOcclusionContainer<?> container = WaterOcclusionContainer.getContainer(this.level);
        if (!originalState.isEmpty() && container != null && container.isOccluded(new Vec3(this.getX(), this.getEyeY(), this.getZ()))) {
            return Fluids.EMPTY.defaultFluidState();
        }
        return originalState;
    }
}
