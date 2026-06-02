/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.level.block.Block
 */
package com.simibubi.create.foundation.block;

import com.tterrag.registrate.util.entry.BlockEntry;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

public class DyedBlockList<T extends Block>
implements Iterable<BlockEntry<T>> {
    private static final int COLOR_AMOUNT = DyeColor.values().length;
    private final BlockEntry<?>[] values = new BlockEntry[COLOR_AMOUNT];

    public DyedBlockList(Function<DyeColor, BlockEntry<? extends T>> filler) {
        for (DyeColor color : DyeColor.values()) {
            this.values[color.ordinal()] = filler.apply(color);
        }
    }

    public BlockEntry<T> get(DyeColor color) {
        return this.values[color.ordinal()];
    }

    public boolean contains(Block block) {
        for (BlockEntry<?> entry : this.values) {
            if (!entry.is((Object)block)) continue;
            return true;
        }
        return false;
    }

    public BlockEntry<T>[] toArray() {
        return Arrays.copyOf(this.values, this.values.length);
    }

    @Override
    public Iterator<BlockEntry<T>> iterator() {
        return new Iterator<BlockEntry<T>>(){
            private int index = 0;

            @Override
            public boolean hasNext() {
                return this.index < DyedBlockList.this.values.length;
            }

            @Override
            public BlockEntry<T> next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return DyedBlockList.this.values[this.index++];
            }
        };
    }
}
