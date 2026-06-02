/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.IntAttached
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  net.minecraft.world.item.crafting.CraftingInput
 *  net.minecraft.world.item.crafting.CustomRecipe
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.enchantment.ItemEnchantments
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.recipe;

import com.simibubi.create.AllRecipeTypes;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemCopyingRecipe
extends CustomRecipe {
    public ItemCopyingRecipe(CraftingBookCategory category) {
        super(category);
    }

    public boolean matches(CraftingInput input, Level level) {
        return this.copyCheck(input) != null;
    }

    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        IntAttached<ItemStack> copyCheck = this.copyCheck(input);
        if (copyCheck == null) {
            return ItemStack.EMPTY;
        }
        ItemStack itemToCopy = (ItemStack)copyCheck.getValue();
        Item item = itemToCopy.getItem();
        if (!(item instanceof SupportsItemCopying)) {
            return ItemStack.EMPTY;
        }
        SupportsItemCopying sic = (SupportsItemCopying)item;
        return sic.createCopy(itemToCopy, (Integer)copyCheck.getFirst() + 1);
    }

    @Nullable
    private IntAttached<ItemStack> copyCheck(CraftingInput input) {
        SupportsItemCopying sic;
        Item item;
        ItemStack itemInSlot;
        int j;
        ItemStack itemToCopy = ItemStack.EMPTY;
        int copyTargets = 0;
        for (j = 0; j < input.size(); ++j) {
            itemInSlot = input.getItem(j);
            if (itemInSlot.isEmpty()) continue;
            item = itemInSlot.getItem();
            if (!(item instanceof SupportsItemCopying)) {
                return null;
            }
            sic = (SupportsItemCopying)item;
            if (!sic.canCopyFromItem(itemInSlot)) continue;
            itemToCopy = itemInSlot;
            break;
        }
        if (itemToCopy.isEmpty()) {
            return null;
        }
        for (j = 0; j < input.size(); ++j) {
            itemInSlot = input.getItem(j);
            if (itemInSlot.isEmpty() || itemInSlot == itemToCopy) continue;
            if (itemToCopy.getItem() != itemInSlot.getItem()) {
                return null;
            }
            item = itemInSlot.getItem();
            if (!(item instanceof SupportsItemCopying)) {
                return null;
            }
            sic = (SupportsItemCopying)item;
            if (sic.canCopyFromItem(itemInSlot)) {
                return null;
            }
            if (!sic.canCopyToItem(itemInSlot)) {
                return null;
            }
            ++copyTargets;
        }
        if (copyTargets == 0) {
            return null;
        }
        return IntAttached.with((int)copyTargets, (Object)itemToCopy);
    }

    public RecipeSerializer<?> getSerializer() {
        return AllRecipeTypes.ITEM_COPYING.getSerializer();
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 2;
    }

    public static interface SupportsItemCopying {
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
}
