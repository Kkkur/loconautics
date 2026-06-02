/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 */
package dev.ryanhcode.sable.neoforge.compatibility.flywheel;

import it.unimi.dsi.fastutil.ints.IntArrayList;

public static final class SableLightLut.IntLayer {
    private boolean hasBase = false;
    private int base = 0;
    private int[] indices = new int[0];

    public void fillLut(IntArrayList lut) {
        lut.add(this.base);
        lut.add(this.indices.length);
        for (int index : this.indices) {
            lut.add(index);
        }
    }

    public int base() {
        return this.base;
    }

    public int size() {
        return this.indices.length;
    }

    public int getRaw(int i) {
        if (i < 0) {
            return 0;
        }
        if (i >= this.indices.length) {
            return 0;
        }
        return this.indices[i];
    }

    public int get(int i) {
        if (!this.hasBase) {
            return 0;
        }
        return this.getRaw(i - this.base);
    }

    public void set(int i, int index) {
        int offset;
        if (!this.hasBase) {
            this.base = i;
            this.hasBase = true;
        }
        if (i < this.base) {
            this.rebase(i);
        }
        if ((offset = i - this.base) >= this.indices.length) {
            this.resize(offset + 1);
        }
        this.indices[offset] = index;
    }

    public boolean prune() {
        if (!this.hasBase) {
            return true;
        }
        int leadingZeros = this.getLeadingZeros();
        if (leadingZeros == this.indices.length) {
            return true;
        }
        int trailingZeros = this.getTrailingZeros();
        if (leadingZeros == 0 && trailingZeros == 0) {
            return false;
        }
        int[] newIndices = new int[this.indices.length - leadingZeros - trailingZeros];
        System.arraycopy(this.indices, leadingZeros, newIndices, 0, newIndices.length);
        this.indices = newIndices;
        this.base += leadingZeros;
        return false;
    }

    private int getTrailingZeros() {
        int out = 0;
        for (int i = this.indices.length - 1; i >= 0 && this.indices[i] == 0; --i) {
            ++out;
        }
        return out;
    }

    private int getLeadingZeros() {
        int out = 0;
        for (int index : this.indices) {
            if (index != 0) break;
            ++out;
        }
        return out;
    }

    public void clear(int i) {
        if (!this.hasBase) {
            return;
        }
        if (i < this.base) {
            return;
        }
        int offset = i - this.base;
        if (offset >= this.indices.length) {
            return;
        }
        this.indices[offset] = 0;
    }

    private void resize(int length) {
        int[] newIndices = new int[length];
        System.arraycopy(this.indices, 0, newIndices, 0, this.indices.length);
        this.indices = newIndices;
    }

    private void rebase(int newBase) {
        int growth = this.base - newBase;
        int[] newIndices = new int[this.indices.length + growth];
        System.arraycopy(this.indices, 0, newIndices, growth, this.indices.length);
        this.indices = newIndices;
        this.base = newBase;
    }
}
