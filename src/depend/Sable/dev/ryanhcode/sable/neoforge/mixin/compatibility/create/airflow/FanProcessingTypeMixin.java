/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.simibubi.create.content.kinetics.fan.processing.FanProcessingType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.airflow;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={FanProcessingType.class})
public interface FanProcessingTypeMixin {
    @WrapMethod(method={"getAt"})
    private static FanProcessingType getAt(Level level, BlockPos pos, Operation<FanProcessingType> original) {
        ActiveSableCompanion helper = Sable.HELPER;
        return helper.runIncludingSubLevels(level, pos.getCenter(), true, helper.getContaining(level, (Vec3i)pos), (subLevel, relativePos) -> (FanProcessingType)original.call(new Object[]{level, relativePos}));
    }
}
