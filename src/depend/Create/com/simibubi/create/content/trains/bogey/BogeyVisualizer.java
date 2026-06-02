/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 */
package com.simibubi.create.content.trains.bogey;

import com.simibubi.create.content.trains.bogey.BogeyVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

@FunctionalInterface
public interface BogeyVisualizer {
    public BogeyVisual createVisual(VisualizationContext var1, float var2, boolean var3);
}
