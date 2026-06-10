package com.lycoris.loconautics.content.transmission;

import com.simibubi.create.content.kinetics.transmission.SplitShaftRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/**
 * Transmission Renderer — immediate-mode (TESR) renderer for the Transmission block.
 *
 * <p>Extends {@link SplitShaftRenderer} directly. The parent already handles
 * everything needed:
 * <ul>
 *   <li>Iterates both half-shafts along the rotation axis (from the AXIS blockstate
 *       property provided by {@code AbstractEncasedShaftBlock}).</li>
 *   <li>Calls {@code blockEntity.getRotationSpeedModifier(direction)} per half to
 *       compute each half's display speed: {@code angle *= modifier}.</li>
 *   <li>Input half: modifier = 1.0 → spins at input network speed.</li>
 *   <li>Output half: modifier = targetRPM / |inputSpeed| → spins at targetRPM.</li>
 *   <li>Disengaged (modifier = 0.0): output half is stopped.</li>
 *   <li>Reversed (directionActive, modifier negative): output half spins opposite to input.</li>
 * </ul>
 *
 * <p>No manual facing lookup, no manual angle computation, no {@code getGeneratedSpeed()}
 * call — all handled by the parent via {@code getRotationSpeedModifier} on
 * {@link TransmissionBlockEntity}. There is no {@code FACING} blockstate property;
 * the axis comes from {@code AXIS} inherited from {@code AbstractEncasedShaftBlock}.
 */
public class TransmissionRenderer extends SplitShaftRenderer {

    public TransmissionRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
}