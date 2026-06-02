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
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 */
package com.simibubi.create.foundation.block.render;

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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class CustomBlockModels {
    private final Multimap<ResourceLocation, NonNullFunction<BakedModel, ? extends BakedModel>> modelFuncs = MultimapBuilder.hashKeys().arrayListValues().build();
    private final Map<Block, NonNullFunction<BakedModel, ? extends BakedModel>> finalModelFuncs = new IdentityHashMap<Block, NonNullFunction<BakedModel, ? extends BakedModel>>();
    private boolean funcsLoaded = false;

    public void register(ResourceLocation block, NonNullFunction<BakedModel, ? extends BakedModel> func) {
        this.modelFuncs.put((Object)block, func);
    }

    public void forEach(NonNullBiConsumer<Block, NonNullFunction<BakedModel, ? extends BakedModel>> consumer) {
        this.loadEntriesIfMissing();
        this.finalModelFuncs.forEach((BiConsumer<Block, NonNullFunction<BakedModel, ? extends BakedModel>>)consumer);
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
            Block block = (Block)BuiltInRegistries.BLOCK.get(location);
            if (block == Blocks.AIR) {
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
            this.finalModelFuncs.put(block, finalFunc);
        });
    }
}
