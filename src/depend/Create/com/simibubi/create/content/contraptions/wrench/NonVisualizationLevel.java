/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.visualization.VisualizationLevel
 *  net.createmod.catnip.levelWrappers.WrappedLevel
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.content.contraptions.wrench;

import dev.engine_room.flywheel.api.visualization.VisualizationLevel;
import net.createmod.catnip.levelWrappers.WrappedLevel;
import net.minecraft.world.level.Level;

public class NonVisualizationLevel
extends WrappedLevel
implements VisualizationLevel {
    public NonVisualizationLevel(Level level) {
        super(level);
    }

    public boolean supportsVisualization() {
        return false;
    }
}
