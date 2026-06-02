/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.Block
 */
package com.simibubi.create.foundation.data.recipe;

import com.simibubi.create.foundation.data.recipe.CommonMetal;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public record CommonMetal.ItemLikeTag(TagKey<Item> items, TagKey<Block> blocks) {
    private CommonMetal.ItemLikeTag(String path) {
        this(CommonMetal.itemTag(path), CommonMetal.blockTag(path));
    }
}
