/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 */
package com.simibubi.create;

import net.minecraft.world.item.Item;

private record AllCreativeModeTabs.RegistrateDisplayItemsGenerator.ItemOrdering(Item item, Item anchor, Type type) {
    public static AllCreativeModeTabs.RegistrateDisplayItemsGenerator.ItemOrdering before(Item item, Item anchor) {
        return new AllCreativeModeTabs.RegistrateDisplayItemsGenerator.ItemOrdering(item, anchor, Type.BEFORE);
    }

    public static AllCreativeModeTabs.RegistrateDisplayItemsGenerator.ItemOrdering after(Item item, Item anchor) {
        return new AllCreativeModeTabs.RegistrateDisplayItemsGenerator.ItemOrdering(item, anchor, Type.AFTER);
    }

    public static enum Type {
        BEFORE,
        AFTER;

    }
}
