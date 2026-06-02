/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.simibubi.create.content.kinetics.RotationPropagator
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  net.minecraft.core.Direction
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.offroad.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlockEntity;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={RotationPropagator.class})
public class RotationPropagatorMixin {
    @WrapMethod(method={"getAxisModifier"})
    private static float offroad$boreheadBearingRotation(KineticBlockEntity be, Direction direction, Operation<Float> original) {
        if (be.hasSource() && be instanceof BoreheadBearingBlockEntity) {
            BoreheadBearingBlockEntity bhbe = (BoreheadBearingBlockEntity)be;
            return bhbe.handleAxisModification(direction);
        }
        return ((Float)original.call(new Object[]{be, direction})).floatValue();
    }
}
