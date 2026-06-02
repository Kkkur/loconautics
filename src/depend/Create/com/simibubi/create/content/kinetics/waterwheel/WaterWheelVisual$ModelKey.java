/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.waterwheel;

import com.simibubi.create.content.kinetics.waterwheel.WaterWheelRenderer;
import net.minecraft.world.level.block.state.BlockState;

public record WaterWheelVisual.ModelKey(WaterWheelRenderer.Variant variant, BlockState material) {
}
