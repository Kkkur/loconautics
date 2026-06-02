/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Mth
 */
package com.simibubi.create.content.schematics.client.tools;

import com.simibubi.create.content.schematics.client.tools.PlacementToolBase;
import net.minecraft.util.Mth;

public class MoveVerticalTool
extends PlacementToolBase {
    @Override
    public boolean handleMouseWheel(double delta) {
        if (this.schematicHandler.isDeployed()) {
            this.schematicHandler.getTransformation().move(0, Mth.sign((double)delta), 0);
            this.schematicHandler.markDirty();
        }
        return true;
    }
}
