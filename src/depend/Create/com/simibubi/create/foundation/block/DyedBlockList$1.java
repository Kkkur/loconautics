/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.entry.BlockEntry
 */
package com.simibubi.create.foundation.block;

import com.tterrag.registrate.util.entry.BlockEntry;
import java.util.Iterator;
import java.util.NoSuchElementException;

class DyedBlockList.1
implements Iterator<BlockEntry<T>> {
    private int index = 0;

    DyedBlockList.1() {
    }

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
}
