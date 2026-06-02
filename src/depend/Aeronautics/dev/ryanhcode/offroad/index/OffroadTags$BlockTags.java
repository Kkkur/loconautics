/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.data.TagGen$CreateTagsProvider
 *  com.tterrag.registrate.providers.RegistrateTagsProvider
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.block.Block
 */
package dev.ryanhcode.offroad.index;

import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.ryanhcode.offroad.Offroad;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public static class OffroadTags.BlockTags {
    private static TagKey<Block> create(String path) {
        return TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)Offroad.path(path));
    }

    private static void genBlockTags(RegistrateTagsProvider<Block> provIn) {
        TagGen.CreateTagsProvider prov = new TagGen.CreateTagsProvider(provIn, Block::builtInRegistryHolder);
    }
}
