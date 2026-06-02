/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package dev.ryanhcode.sable.sublevel.render.fancy;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public static class SubLevelMeshBuilder.QuadMesh {
    private final IntList[] faces = new IntArrayList[DIRECTIONS.length];

    public SubLevelMeshBuilder.QuadMesh() {
        for (int i = 0; i < this.faces.length; ++i) {
            this.faces[i] = new IntArrayList();
        }
    }

    public IntList[] getFaces() {
        return this.faces;
    }
}
