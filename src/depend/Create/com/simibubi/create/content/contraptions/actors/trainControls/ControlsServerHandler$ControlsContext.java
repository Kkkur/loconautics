/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 */
package com.simibubi.create.content.contraptions.actors.trainControls;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsServerHandler;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.core.BlockPos;

static class ControlsServerHandler.ControlsContext {
    Collection<ControlsServerHandler.ManuallyPressedKey> keys;
    AbstractContraptionEntity entity;
    BlockPos controlsLocalPos;

    public ControlsServerHandler.ControlsContext(AbstractContraptionEntity entity, BlockPos controlsPos) {
        this.entity = entity;
        this.controlsLocalPos = controlsPos;
        this.keys = new ArrayList<ControlsServerHandler.ManuallyPressedKey>();
    }
}
