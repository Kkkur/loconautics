/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.visualization.VisualizationHelper
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;
import net.minecraft.world.level.block.entity.BlockEntity;

private static class ArmBlockEntity.Client {
    private ArmBlockEntity.Client() {
    }

    private static void queueUpdate(BlockEntity be) {
        VisualizationHelper.queueUpdate((BlockEntity)be);
    }
}
