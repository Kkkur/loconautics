/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  net.minecraft.core.SectionPos
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.neoforge.compatibility.flywheel;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.core.SectionPos;
import org.jetbrains.annotations.Nullable;

public final class SableLightLut {
    public final Layer<Layer<Layer<IntLayer>>> indices = new Layer();

    public void add(int scene, long position, int index) {
        int x = SectionPos.x((long)position);
        int y = SectionPos.y((long)position);
        int z = SectionPos.z((long)position);
        this.indices.computeIfAbsent(scene, Layer::new).computeIfAbsent(y, Layer::new).computeIfAbsent(x, IntLayer::new).set(z, index + 1);
    }

    public void prune() {
        this.indices.prune(scene -> scene.prune(middle -> middle.prune(IntLayer::prune)));
    }

    public void remove(int scene, long section) {
        int x = SectionPos.x((long)section);
        int y = SectionPos.y((long)section);
        int z = SectionPos.z((long)section);
        Layer<Layer<IntLayer>> first = this.indices.get(scene);
        if (first == null) {
            return;
        }
        Layer<IntLayer> second = first.get(y);
        if (second == null) {
            return;
        }
        IntLayer third = second.get(x);
        if (third == null) {
            return;
        }
        third.clear(z);
    }

    public IntArrayList flatten() {
        IntArrayList out = new IntArrayList();
        this.indices.fillLut(out, (sceneIndices, lut1) -> sceneIndices.fillLut((IntArrayList)lut1, (yIndices, lut2) -> yIndices.fillLut((IntArrayList)lut2, IntLayer::fillLut)));
        return out;
    }

    public static final class Layer<T> {
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

        public boolean prune(Prune<T> inner) {
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

    public static final class IntLayer {
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

    @FunctionalInterface
    public static interface Prune<T> {
        public boolean prune(T var1);
    }
}
