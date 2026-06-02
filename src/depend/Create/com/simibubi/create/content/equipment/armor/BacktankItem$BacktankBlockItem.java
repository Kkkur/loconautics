/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.level.block.Block
 */
package com.simibubi.create.content.equipment.armor;

import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public static class BacktankItem.BacktankBlockItem
extends BlockItem {
    private final Supplier<Item> actualItem;

    public BacktankItem.BacktankBlockItem(Block block, Supplier<Item> actualItem, Item.Properties properties) {
        super(block, properties);
        this.actualItem = actualItem;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public Item getActualItem() {
        return this.actualItem.get();
    }
}
