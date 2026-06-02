/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllTags
 *  com.simibubi.create.foundation.data.TagGen$CreateTagsProvider
 *  com.tterrag.registrate.providers.ProviderType
 *  com.tterrag.registrate.providers.RegistrateTagsProvider
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 */
package dev.ryanhcode.offroad.data;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.ryanhcode.offroad.Offroad;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public static class OffroadTags.Blocks {
    public static final TagKey<Block> BOREHEAD_EFFECTIVE = OffroadTags.Blocks.create("borehead_effective");
    public static final TagKey<Block> BOREHEAD_SUPER_EFFECTIVE = OffroadTags.Blocks.create("borehead_super_effective");

    private static TagKey<Block> create(String path) {
        return TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)Offroad.path(path));
    }

    private static TagKey<Block> create(String namespace, String path) {
        return TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)namespace, (String)path));
    }

    protected static void addGenerators() {
        Offroad.getRegistrate().addDataGenerator(ProviderType.BLOCK_TAGS, OffroadTags.Blocks::genBlockTags);
    }

    private static void genBlockTags(RegistrateTagsProvider<Block> provIn) {
        TagGen.CreateTagsProvider prov = new TagGen.CreateTagsProvider(provIn, Block::builtInRegistryHolder);
        prov.tag(BOREHEAD_EFFECTIVE).addTag(AllTags.commonBlockTag((String)"ores"));
        prov.tag(BOREHEAD_SUPER_EFFECTIVE).add((Object)Blocks.ANCIENT_DEBRIS);
    }
}
