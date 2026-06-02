/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.simibubi.create.content.kinetics.RotationPropagator
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.simulated_team.simulated.mixin.flicker_tally_removal;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={RotationPropagator.class}, priority=100000)
public class RotationPropagatorMixin {
    @WrapOperation(method={"propagateNewSource"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;getFlickerScore()I")})
    private static int removeFlicker(KineticBlockEntity instance, Operation<Integer> original) {
        return 0;
    }
}
