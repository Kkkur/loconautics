/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 */
package com.simibubi.create.content.contraptions.render;

import com.simibubi.create.content.contraptions.render.ClientContraption;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

class ClientContraption.1
extends VirtualRenderWorld {
    ClientContraption.1(ClientContraption this$0, Level level, int minBuildHeight, int height, Vec3i biomeOffset, Runnable onBlockUpdated) {
        super(level, minBuildHeight, height, biomeOffset, onBlockUpdated);
    }

    public boolean supportsVisualization() {
        return VisualizationManager.supportsVisualization((LevelAccessor)this.level);
    }
}
