/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.level.block.Block
 */
package com.simibubi.create.content.contraptions.bearing;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.bearing.SailBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import java.util.Map;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BlankSailBlockItem
extends BlockItem {
    public BlankSailBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {
        super.registerBlocks(blockToItemMap, item);
        for (BlockEntry<SailBlock> blockEntry : AllBlocks.DYED_SAILS) {
            blockToItemMap.put((Block)blockEntry.get(), item);
        }
    }

    public void removeFromBlockToItemMap(Map<Block, Item> blockToItemMap, Item item) {
        super.removeFromBlockToItemMap(blockToItemMap, item);
        for (BlockEntry<SailBlock> blockEntry : AllBlocks.DYED_SAILS) {
            blockToItemMap.remove(blockEntry.get());
        }
    }
}
