/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.level.block.Block
 */
package com.simibubi.create.foundation.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class UncontainableBlockItem
extends BlockItem {
    public UncontainableBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    public boolean canFitInsideContainerItems() {
        return false;
    }
}
