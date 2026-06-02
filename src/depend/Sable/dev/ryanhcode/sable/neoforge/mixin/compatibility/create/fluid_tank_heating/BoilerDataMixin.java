/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.simibubi.create.content.fluids.tank.BoilerData
 *  com.simibubi.create.content.fluids.tank.FluidTankBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.fluid_tank_heating;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.fluids.tank.BoilerData;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={BoilerData.class})
public class BoilerDataMixin {
    @Shadow
    public boolean needsHeatLevelUpdate;
    @Unique
    private int sable$ticksUntilUpdate = 20;

    @Inject(method={"tick"}, at={@At(value="FIELD", target="Lcom/simibubi/create/content/fluids/tank/BoilerData;ticksUntilNextSample:I", ordinal=0)})
    public void sable$forceUpdateHeatIfDisconnected(FluidTankBlockEntity controller, CallbackInfo ci) {
        if (this.sable$ticksUntilUpdate-- <= 0) {
            this.sable$ticksUntilUpdate = 20;
            this.needsHeatLevelUpdate = true;
        }
    }

    @WrapOperation(method={"updateTemperature"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/api/boiler/BoilerHeater;findHeat(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)F")})
    public float sable$subLevelHeating(Level level, BlockPos pos, BlockState state, Operation<Float> original) {
        Float originalHeat = (Float)original.call(new Object[]{level, pos, state});
        if (originalHeat.floatValue() != -1.0f) {
            return originalHeat.floatValue();
        }
        ActiveSableCompanion helper = Sable.HELPER;
        Float gatheredHeat = helper.runIncludingSubLevels(level, pos.getCenter(), false, helper.getContaining(level, (Vec3i)pos), (subLevel, internalPos) -> {
            Float internalHeat = (Float)original.call(new Object[]{level, internalPos, level.getBlockState(internalPos)});
            if (internalHeat.floatValue() != -1.0f) {
                return internalHeat;
            }
            return null;
        });
        if (gatheredHeat != null) {
            return gatheredHeat.floatValue();
        }
        return -1.0f;
    }
}
