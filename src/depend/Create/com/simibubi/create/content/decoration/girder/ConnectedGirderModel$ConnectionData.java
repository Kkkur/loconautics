/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 */
package com.simibubi.create.content.decoration.girder;

import java.util.Arrays;
import net.minecraft.core.Direction;

private static class ConnectedGirderModel.ConnectionData {
    boolean[] connectedFaces = new boolean[4];

    public ConnectedGirderModel.ConnectionData() {
        Arrays.fill(this.connectedFaces, false);
    }

    void setConnected(Direction face, boolean connected) {
        this.connectedFaces[face.get2DDataValue()] = connected;
    }

    boolean isConnected(Direction face) {
        return this.connectedFaces[face.get2DDataValue()];
    }
}
