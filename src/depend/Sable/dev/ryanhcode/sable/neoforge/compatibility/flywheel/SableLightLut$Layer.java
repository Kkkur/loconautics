/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.neoforge.compatibility.flywheel;

import dev.ryanhcode.sable.neoforge.compatibility.flywheel.SableLightLut;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public static final class SableLightLut.Layer<T> {
    private boolean hasBase = false;
    private int base = 0;
    private Object[] nextLayer = new Object[0];

    public void fillLut(IntArrayList lut, BiConsumer<T, IntArrayList> inner) {
        lut.add(this.base);
        lut.add(this.nextLayer.length);
        int innerIndexBase = lut.size();
        lut.size(innerIndexBase + this.nextLayer.length);
        for (int i = 0; i < this.nextLayer.length; ++i) {
            Object innerIndices = this.nextLayer[i];
            if (innerIndices == null) continue;
            int layerPosition = lut.size();
            lut.set(innerIndexBase + i, layerPosition);
            inner.accept(innerIndices, lut);
        }
    }

    public int base() {
        return this.base;
    }

    public int size() {
        return this.nextLayer.length;
    }

    @Nullable
    public T getRaw(int i) {
        if (i < 0) {
            return null;
        }
        if (i >= this.nextLayer.length) {
            return null;
        }
        return (T)this.nextLayer[i];
    }

    @Nullable
    public T get(int i) {
        if (!this.hasBase) {
            return null;
        }
        return this.getRaw(i - this.base);
    }

    public T computeIfAbsent(int i, Supplier<T> ifAbsent) {
        Object out;
        int offset;
        if (!this.hasBase) {
            this.base = i;
            this.hasBase = true;
        }
        if (i < this.base) {
            this.rebase(i);
        }
        if ((offset = i - this.base) >= this.nextLayer.length) {
            this.resize(offset + 1);
        }
        if ((out = this.nextLayer[offset]) == null) {
            this.nextLayer[offset] = out = ifAbsent.get();
        }
        return (T)out;
    }

    public boolean prune(SableLightLut.Prune<T> inner) {
        if (!this.hasBase) {
            return true;
        }
        for (int i = 0; i < this.nextLayer.length; ++i) {
            Object o = this.nextLayer[i];
            if (o == null || !inner.prune(o)) continue;
            this.nextLayer[i] = null;
        }
        int leadingZeros = this.getLeadingZeros();
        if (leadingZeros == this.nextLayer.length) {
            return true;
        }
        int trailingZeros = this.getTrailingZeros();
        if (leadingZeros == 0 && trailingZeros == 0) {
            return false;
        }
        Object[] newIndices = new Object[this.nextLayer.length - leadingZeros - trailingZeros];
        System.arraycopy(this.nextLayer, leadingZeros, newIndices, 0, newIndices.length);
        this.nextLayer = newIndices;
        this.base += leadingZeros;
        return false;
    }

    private int getLeadingZeros() {
        int out = 0;
        for (Object index : this.nextLayer) {
            if (index != null) break;
            ++out;
        }
        return out;
    }

    private int getTrailingZeros() {
        int out = 0;
        for (int i = this.nextLayer.length - 1; i >= 0 && this.nextLayer[i] == null; --i) {
            ++out;
        }
        return out;
    }

    private void resize(int length) {
        Object[] newIndices = new Object[length];
        System.arraycopy(this.nextLayer, 0, newIndices, 0, this.nextLayer.length);
        this.nextLayer = newIndices;
    }

    private void rebase(int newBase) {
        int growth = this.base - newBase;
        Object[] newIndices = new Object[this.nextLayer.length + growth];
        System.arraycopy(this.nextLayer, 0, newIndices, growth, this.nextLayer.length);
        this.nextLayer = newIndices;
        this.base = newBase;
    }
}
