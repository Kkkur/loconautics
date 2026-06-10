package com.lycoris.loconautics.mixin;

import com.lycoris.loconautics.content.transmission.TransmissionBlock;
import com.lycoris.loconautics.content.transmission.TransmissionBlockEntity;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RotationPropagator.class)
public class RotationPropagatorMixin {

    /**
     * Intercepts getConveyedSpeed whenever one side is a Transmission.
     *
     * Cases:
     *   from=Transmission, to=output neighbor → return target RPM directly
     *   from=input neighbor, to=Transmission  → return input speed (plain shaft, Transmission joins input network)
     *   from=Transmission, to=input neighbor  → return 0 (don't propagate backwards out the input face)
     */
    @Inject(
            method = "getConveyedSpeed(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;" +
                    "Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;)F",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void loconautics$transmissionSpeed(KineticBlockEntity from, KineticBlockEntity to,
                                                      CallbackInfoReturnable<Float> cir) {
        // Case 1: from = Transmission → it is propagating outward to one of its neighbors
        if (from instanceof TransmissionBlockEntity tbe) {
            BlockState state = from.getBlockState();
            Direction facing = state.getValue(TransmissionBlock.FACING);
            BlockPos diff = to.getBlockPos().subtract(from.getBlockPos());
            Direction towardTarget = Direction.getNearest(diff.getX(), diff.getY(), diff.getZ());

            boolean towardTargetIsOutputFace = tbe.isOutputFace(towardTarget, facing);

            if (towardTargetIsOutputFace) {
                // Propagating toward output — return the absolute target RPM
                cir.setReturnValue(tbe.getTargetRPM());
            } else {
                // Propagating back toward input — block it; input network flows the other way
                cir.setReturnValue(0f);
            }
            return;
        }

        // Case 2: to = Transmission → a neighbor is propagating into it
        if (to instanceof TransmissionBlockEntity tbe) {
            BlockState state = to.getBlockState();
            Direction facing = state.getValue(TransmissionBlock.FACING);
            BlockPos diff = to.getBlockPos().subtract(from.getBlockPos());
            Direction towardTransmission = Direction.getNearest(diff.getX(), diff.getY(), diff.getZ());

            // towardTransmission is the face of the Transmission that `from` is on
            boolean fromIsOnOutputFace = tbe.isOutputFace(towardTransmission, facing);

            if (fromIsOnOutputFace) {
                // Something is trying to drive us from the output face — block it
                cir.setReturnValue(0f);
            }
            // else: from is on the input face — let normal shaft logic run (don't cancel)
        }
    }
}