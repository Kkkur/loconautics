package com.lycoris.loconautics.content.transmission;

import com.simibubi.create.content.kinetics.transmission.SplitShaftVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

/**
 * Transmission Visual — Flywheel visual for the Transmission block.
 *
 * <p>Extends {@link SplitShaftVisual} directly. The parent class already handles
 * everything we need:
 * <ul>
 *   <li>Creates two {@code RotatingInstance}s, one per half-shaft on the rotation axis.</li>
 *   <li>Calls {@code blockEntity.getRotationSpeedModifier(direction)} per half to get
 *       each half's display speed: {@code splitSpeed = be.getSpeed() * modifier}.</li>
 *   <li>Input half: modifier = 1.0 → spins at input network speed.</li>
 *   <li>Output half: modifier = targetRPM / inputSpeed → spins at targetRPM.</li>
 *   <li>Disengaged (modifier = 0.0): output half is stopped.</li>
 *   <li>Reversed (modifier negative): output half spins opposite to input.</li>
 * </ul>
 *
 * <p>No custom rendering is needed beyond what the parent provides, so this class is
 * intentionally minimal — it exists only to satisfy the registry and any future
 * extension point.
 */
public class TransmissionVisual extends SplitShaftVisual {

    public TransmissionVisual(VisualizationContext context,
                              TransmissionBlockEntity blockEntity,
                              float partialTick) {
        super(context, blockEntity, partialTick);
    }
}