/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.sublevel.plot;

public class HeatDataChunkSection {
    public static final int SIZE = 4096;
    private final short[] data = new short[4096];

    public static int getIndex(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }

    public short get(int x, int y, int z) {
        return this.data[HeatDataChunkSection.getIndex(x, y, z)];
    }

    public void set(int x, int y, int z, short value) {
        this.data[HeatDataChunkSection.getIndex((int)x, (int)y, (int)z)] = value;
    }
}
