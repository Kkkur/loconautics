/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.MultimapBuilder
 *  com.tterrag.registrate.util.nullness.NonNullBiConsumer
 *  com.tterrag.registrate.util.nullness.NonNullFunction
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 */
package com.simibubi.create.foundation.item.render;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class CustomItemModels {
    private final Multimap<ResourceLocation, NonNullFunction<BakedModel, ? extends BakedModel>> modelFuncs = MultimapBuilder.hashKeys().arrayListValues().build();
    private final Map<Item, NonNullFunction<BakedModel, ? extends BakedModel>> finalModelFuncs = new IdentityHashMap<Item, NonNullFunction<BakedModel, ? extends BakedModel>>();
    private boolean funcsLoaded = false;

    public void register(ResourceLocation item, NonNullFunction<BakedModel, ? extends BakedModel> func) {
        this.modelFuncs.put((Object)item, func);
    }

    public void forEach(NonNullBiConsumer<Item, NonNullFunction<BakedModel, ? extends BakedModel>> consumer) {
        this.loadEntriesIfMissing();
        this.finalModelFuncs.forEach((BiConsumer<Item, NonNullFunction<BakedModel, ? extends BakedModel>>)consumer);
    }

    private void loadEntriesIfMissing() {
        if (!this.funcsLoaded) {
            this.loadEntries();
            this.funcsLoaded = true;
        }
    }

    private void loadEntries() {
        this.finalModelFuncs.clear();
        this.modelFuncs.asMap().forEach((? super K location, ? super V funcList) -> {
            Item item = (Item)BuiltInRegistries.ITEM.get(location);
            if (item == Items.AIR) {
                return;
            }
            NonNullFunction finalFunc = null;
            for (NonNullFunction func : funcList) {
                if (finalFunc == null) {
                    finalFunc = func;
                    continue;
                }
                finalFunc = finalFunc.andThen(func);
            }
            this.finalModelFuncs.put(item, finalFunc);
        });
    }
}
