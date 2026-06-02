/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 */
package com.simibubi.create.foundation.item;

import java.util.Iterator;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class TagDependentIngredientItem
extends Item {
    private TagKey<Item> tag;

    public TagDependentIngredientItem(Item.Properties properties, TagKey<Item> tag) {
        super(properties);
        this.tag = tag;
    }

    public boolean shouldHide() {
        Iterator iterator = BuiltInRegistries.ITEM.getTagOrEmpty(this.tag).iterator();
        if (iterator.hasNext()) {
            Holder ignored = (Holder)iterator.next();
            return false;
        }
        return true;
    }
}
