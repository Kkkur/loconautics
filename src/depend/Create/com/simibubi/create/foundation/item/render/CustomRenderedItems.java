/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.world.item.Item
 *  net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
 */
package com.simibubi.create.foundation.item.render;

import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class CustomRenderedItems {
    private static final Set<Item> ITEMS = new ReferenceOpenHashSet();
    private static boolean itemsFiltered = false;

    public static void register(Item item) {
        ITEMS.add(item);
    }

    public static void forEach(Consumer<Item> consumer) {
        if (!itemsFiltered) {
            Iterator<Item> iterator = ITEMS.iterator();
            while (iterator.hasNext()) {
                Item item = iterator.next();
                if (BuiltInRegistries.ITEM.containsValue((Object)item) && IClientItemExtensions.of((Item)item).getCustomRenderer() instanceof CustomRenderedItemModelRenderer) continue;
                iterator.remove();
            }
            itemsFiltered = true;
        }
        ITEMS.forEach(consumer);
    }
}
