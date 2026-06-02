/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.PackOutput$PathProvider
 *  net.minecraft.data.PackOutput$Target
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.FireBlock
 *  net.minecraft.world.level.block.SimpleWaterloggedBlock
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.infrastructure.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CreateWikiBlockInfoProvider
implements DataProvider {
    private final PackOutput.PathProvider path;

    public CreateWikiBlockInfoProvider(PackOutput output) {
        this.path = output.createPathProvider(PackOutput.Target.DATA_PACK, ".wiki/block_info/");
    }

    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        return CompletableFuture.allOf((CompletableFuture[])BuiltInRegistries.BLOCK.stream().filter(b -> RegisteredObjectsHelper.getKeyOrThrow((Block)b).getNamespace().equals("create")).map(block -> {
            BlockState state = block.defaultBlockState();
            ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow((Block)block);
            JsonObject element = new JsonObject();
            ItemLike item = RegisteredObjectsHelper.getItemOrBlock((ResourceLocation)id);
            if (item != null) {
                element.addProperty("stackable", (Number)item.asItem().getDefaultInstance().getMaxStackSize());
            }
            element.addProperty("blast_resistance", (Number)Float.valueOf(block.getExplosionResistance()));
            element.addProperty("hardness", (Number)Float.valueOf(block.defaultDestroyTime()));
            element.addProperty("luminous", Boolean.valueOf(state.getLightEmission() > 0));
            element.addProperty("waterloggable", Boolean.valueOf(block instanceof SimpleWaterloggedBlock));
            element.addProperty("flammable", Boolean.valueOf(((FireBlock)Blocks.FIRE).getBurnOdds(state) > 0));
            element.addProperty("ignited_by_lava", Boolean.valueOf(state.ignitedByLava()));
            return DataProvider.saveStable((CachedOutput)cachedOutput, (JsonElement)element, (Path)this.path.json(id));
        }).toArray(CompletableFuture[]::new));
    }

    public String getName() {
        return "Create's Wiki Block Info Provider";
    }
}
