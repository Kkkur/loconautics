/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.phys.shapes.DiscreteVoxelShape
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.ryanhcode.sable.mixinhelpers.voxel_shape_iteration;

import java.util.Arrays;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class LongArrayDiscreteVoxelShape
extends DiscreteVoxelShape {
    private static final int ADDRESS_BITS_PER_WORD = 6;
    private static final int BITS_PER_WORD = 64;
    private static final long WORD_MASK = -1L;
    private final long[] baseWords;
    private final long[] words;

    public LongArrayDiscreteVoxelShape(DiscreteVoxelShape shape, int xSize, int ySize, int zSize) {
        super(xSize, ySize, zSize);
        this.words = new long[LongArrayDiscreteVoxelShape.wordIndex(xSize * ySize * zSize - 1) + 1];
        for (int x = 0; x < xSize; ++x) {
            for (int y = 0; y < ySize; ++y) {
                for (int z = 0; z < zSize; ++z) {
                    if (!shape.isFull(x, y, z)) continue;
                    int bitIndex = this.getIndex(x, y, z);
                    int n = LongArrayDiscreteVoxelShape.wordIndex(bitIndex);
                    this.words[n] = this.words[n] | 1L << bitIndex;
                }
            }
        }
        this.baseWords = Arrays.copyOf(this.words, this.words.length);
    }

    private static int wordIndex(int bitIndex) {
        return bitIndex >> 6;
    }

    private int getIndex(int x, int y, int z) {
        return (x * this.ySize + y) * this.zSize + z;
    }

    private int nextClearBit(int fromIndex) {
        int u = LongArrayDiscreteVoxelShape.wordIndex(fromIndex);
        long word = (this.words[u] ^ 0xFFFFFFFFFFFFFFFFL) & -1L << fromIndex;
        while (word == 0L) {
            if (++u == this.words.length) {
                return this.words.length * 64;
            }
            word = this.words[u] ^ 0xFFFFFFFFFFFFFFFFL;
        }
        return u * 64 + Long.numberOfTrailingZeros(word);
    }

    private void clear(int fromIndex, int toIndex) {
        int startWordIndex = LongArrayDiscreteVoxelShape.wordIndex(fromIndex);
        int endWordIndex = LongArrayDiscreteVoxelShape.wordIndex(toIndex - 1);
        if (endWordIndex >= this.words.length) {
            toIndex = 64 * (this.words.length - 1) + (64 - Long.numberOfLeadingZeros(this.words[this.words.length - 1]));
            endWordIndex = this.words.length - 1;
        }
        long firstWordMask = -1L << fromIndex;
        long lastWordMask = -1L >>> -toIndex;
        if (startWordIndex == endWordIndex) {
            int n = startWordIndex;
            this.words[n] = this.words[n] & (firstWordMask & lastWordMask ^ 0xFFFFFFFFFFFFFFFFL);
        } else {
            int n = startWordIndex;
            this.words[n] = this.words[n] & (firstWordMask ^ 0xFFFFFFFFFFFFFFFFL);
            for (int i = startWordIndex + 1; i < endWordIndex; ++i) {
                this.words[i] = 0L;
            }
            int n2 = endWordIndex;
            this.words[n2] = this.words[n2] & (lastWordMask ^ 0xFFFFFFFFFFFFFFFFL);
        }
    }

    public void reset() {
        System.arraycopy(this.baseWords, 0, this.words, 0, this.words.length);
    }

    public boolean isZStripFull(int i, int j, int k, int l) {
        return k < this.xSize && l < this.ySize && this.nextClearBit(this.getIndex(k, l, i)) >= this.getIndex(k, l, j);
    }

    public boolean isXZRectangleFull(int i, int j, int k, int l, int m) {
        for (int n = i; n < j; ++n) {
            if (this.isZStripFull(k, l, n, m)) continue;
            return false;
        }
        return true;
    }

    public void clearZStrip(int i, int j, int k, int l) {
        this.clear(this.getIndex(k, l, i), this.getIndex(k, l, j));
    }

    public boolean isFull(int x, int y, int z) {
        int bitIndex = this.getIndex(x, y, z);
        int wordIndex = LongArrayDiscreteVoxelShape.wordIndex(bitIndex);
        return wordIndex < this.words.length && (this.words[wordIndex] & 1L << bitIndex) != 0L;
    }

    public void fill(int i, int j, int k) {
        throw new UnsupportedOperationException();
    }

    public int firstFull(Direction.Axis axis) {
        throw new UnsupportedOperationException();
    }

    public int lastFull(Direction.Axis axis) {
        throw new UnsupportedOperationException();
    }
}
