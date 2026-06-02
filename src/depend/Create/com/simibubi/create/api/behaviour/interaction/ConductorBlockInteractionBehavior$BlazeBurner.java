/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.api.behaviour.interaction;

import com.simibubi.create.api.behaviour.interaction.ConductorBlockInteractionBehavior;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.world.level.block.state.BlockState;

public static class ConductorBlockInteractionBehavior.BlazeBurner
extends ConductorBlockInteractionBehavior {
    @Override
    public boolean isValidConductor(BlockState state) {
        return state.getValue(BlazeBurnerBlock.HEAT_LEVEL) != BlazeBurnerBlock.HeatLevel.NONE;
    }
}
