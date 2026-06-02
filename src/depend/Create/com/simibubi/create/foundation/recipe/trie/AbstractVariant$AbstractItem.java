/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.foundation.recipe.trie;

import com.simibubi.create.foundation.recipe.trie.AbstractVariant;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public static final class AbstractVariant.AbstractItem
implements AbstractVariant {
    @NotNull
    private final Item item;
    private final int hashCode;

    public AbstractVariant.AbstractItem(@NotNull Item item) {
        this.item = item;
        this.hashCode = item.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof AbstractVariant.AbstractItem)) {
            return false;
        }
        AbstractVariant.AbstractItem that = (AbstractVariant.AbstractItem)o;
        return this.item == that.item;
    }

    public int hashCode() {
        return this.hashCode;
    }
}
