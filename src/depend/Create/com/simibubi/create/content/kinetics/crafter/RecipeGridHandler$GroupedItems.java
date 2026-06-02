/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.Pointing
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.item.ItemStack
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.content.kinetics.crafter;

import java.util.HashMap;
import java.util.Map;
import net.createmod.catnip.math.Pointing;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

public static class RecipeGridHandler.GroupedItems {
    Map<Pair<Integer, Integer>, ItemStack> grid = new HashMap<Pair<Integer, Integer>, ItemStack>();
    int minX;
    int minY;
    int maxX;
    int maxY;
    int width;
    int height;
    boolean statsReady;

    public RecipeGridHandler.GroupedItems() {
    }

    public RecipeGridHandler.GroupedItems(ItemStack stack) {
        this.grid.put((Pair<Integer, Integer>)Pair.of((Object)0, (Object)0), stack);
    }

    public void mergeOnto(RecipeGridHandler.GroupedItems other, Pointing pointing) {
        int xOffset;
        int n = pointing == Pointing.LEFT ? 1 : (xOffset = pointing == Pointing.RIGHT ? -1 : 0);
        int yOffset = pointing == Pointing.DOWN ? 1 : (pointing == Pointing.UP ? -1 : 0);
        this.grid.forEach((pair, stack) -> other.grid.put((Pair<Integer, Integer>)Pair.of((Object)((Integer)pair.getKey() + xOffset), (Object)((Integer)pair.getValue() + yOffset)), (ItemStack)stack));
        other.statsReady = false;
    }

    public void write(CompoundTag nbt, HolderLookup.Provider registries) {
        ListTag gridNBT = new ListTag();
        this.grid.forEach((pair, stack) -> {
            CompoundTag entry = new CompoundTag();
            entry.putInt("x", ((Integer)pair.getKey()).intValue());
            entry.putInt("y", ((Integer)pair.getValue()).intValue());
            entry.put("item", stack.saveOptional(registries));
            gridNBT.add((Object)entry);
        });
        nbt.put("Grid", (Tag)gridNBT);
    }

    public static RecipeGridHandler.GroupedItems read(CompoundTag nbt, HolderLookup.Provider registries) {
        RecipeGridHandler.GroupedItems items = new RecipeGridHandler.GroupedItems();
        ListTag gridNBT = nbt.getList("Grid", 10);
        gridNBT.forEach(inbt -> {
            CompoundTag entry = (CompoundTag)inbt;
            int x = entry.getInt("x");
            int y = entry.getInt("y");
            ItemStack stack = ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)entry.getCompound("item"));
            items.grid.put((Pair<Integer, Integer>)Pair.of((Object)x, (Object)y), stack);
        });
        return items;
    }

    public void calcStats() {
        if (this.statsReady) {
            return;
        }
        this.statsReady = true;
        this.minX = 0;
        this.minY = 0;
        this.maxX = 0;
        this.maxY = 0;
        for (Pair<Integer, Integer> pair : this.grid.keySet()) {
            int x = (Integer)pair.getKey();
            int y = (Integer)pair.getValue();
            this.minX = Math.min(this.minX, x);
            this.minY = Math.min(this.minY, y);
            this.maxX = Math.max(this.maxX, x);
            this.maxY = Math.max(this.maxY, y);
        }
        this.width = this.maxX - this.minX + 1;
        this.height = this.maxY - this.minY + 1;
    }

    public boolean onlyEmptyItems() {
        for (ItemStack stack : this.grid.values()) {
            if (stack.isEmpty()) continue;
            return false;
        }
        return true;
    }
}
