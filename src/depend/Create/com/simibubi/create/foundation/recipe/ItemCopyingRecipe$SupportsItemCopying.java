/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.ItemEnchantments
 */
package com.simibubi.create.foundation.recipe;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public static interface ItemCopyingRecipe.SupportsItemCopying {
    default public ItemStack createCopy(ItemStack original, int count) {
        ItemStack copyWithCount = original.copyWithCount(count);
        copyWithCount.set(DataComponents.ENCHANTMENTS, (Object)ItemEnchantments.EMPTY);
        copyWithCount.remove(DataComponents.STORED_ENCHANTMENTS);
        return copyWithCount;
    }

    default public boolean canCopyFromItem(ItemStack item) {
        return item.has(this.getComponentType());
    }

    default public boolean canCopyToItem(ItemStack item) {
        return !item.has(this.getComponentType());
    }

    public DataComponentType<?> getComponentType();
}
