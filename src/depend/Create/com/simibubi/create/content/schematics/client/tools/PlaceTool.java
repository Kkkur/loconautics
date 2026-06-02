/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.content.schematics.client.tools;

import com.simibubi.create.content.schematics.client.tools.SchematicToolBase;

public class PlaceTool
extends SchematicToolBase {
    @Override
    public boolean handleRightClick() {
        this.schematicHandler.printInstantly();
        return true;
    }

    @Override
    public boolean handleMouseWheel(double delta) {
        return false;
    }
}
