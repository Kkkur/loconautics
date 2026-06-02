/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllTags$AllBlockTags
 *  com.simibubi.create.foundation.data.TagGen$CreateTagsProvider
 *  com.tterrag.registrate.providers.ProviderType
 *  com.tterrag.registrate.providers.RegistrateTagsProvider
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.block.Block
 */
package dev.simulated_team.simulated.index;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.simulated_team.simulated.Simulated;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public static class SimTags.Blocks {
    public static final TagKey<Block> NON_MOVABLE = SimTags.Blocks.create("non_movable");
    public static final TagKey<Block> SUPER_LIGHT = SimTags.Blocks.create("sable", "super_light");
    public static final TagKey<Block> LIGHT = SimTags.Blocks.create("sable", "light");
    public static final TagKey<Block> DIODE = SimTags.Blocks.create("sable", "diode");
    public static final TagKey<Block> AIRTIGHT = SimTags.Blocks.create("airtight");
    public static final TagKey<Block> NAMEPLATE_BLOCKS = SimTags.Blocks.create("nameplate_blocks");
    public static final TagKey<Block> SYMMETRIC_SAILS = SimTags.Blocks.create("symmetric_sails");
    public static final TagKey<Block> HANDLES = SimTags.Blocks.create("handles");

    private static TagKey<Block> create(String path) {
        return TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)Simulated.path(path));
    }

    private static TagKey<Block> create(String namespace, String path) {
        return TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)namespace, (String)path));
    }

    protected static void addGenerators() {
        Simulated.getRegistrate().addDataGenerator(ProviderType.BLOCK_TAGS, SimTags.Blocks::genBlockTags);
    }

    private static void genBlockTags(RegistrateTagsProvider<Block> provIn) {
        TagGen.CreateTagsProvider prov = new TagGen.CreateTagsProvider(provIn, Block::builtInRegistryHolder);
        prov.tag(NON_MOVABLE);
        prov.tag(SUPER_LIGHT).addTag(NAMEPLATE_BLOCKS);
        prov.tag(SUPER_LIGHT).addTag(HANDLES);
        prov.tag(AllTags.AllBlockTags.BRITTLE.tag).addTag(HANDLES);
        prov.tag(BlockTags.MINEABLE_WITH_PICKAXE).addTag(HANDLES);
        prov.tag(AllTags.AllBlockTags.SAFE_NBT.tag).addTag(NAMEPLATE_BLOCKS);
    }
}
