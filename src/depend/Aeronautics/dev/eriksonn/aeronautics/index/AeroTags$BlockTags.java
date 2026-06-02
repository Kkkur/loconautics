/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.foundation.data.TagGen$CreateTagsProvider
 *  com.tterrag.registrate.providers.RegistrateTagsProvider
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.neoforged.neoforge.common.Tags$Blocks
 */
package dev.eriksonn.aeronautics.index;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.eriksonn.aeronautics.Aeronautics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

public static class AeroTags.BlockTags {
    public static final TagKey<Block> AIRTIGHT = AeroTags.BlockTags.create("airtight");
    public static final TagKey<Block> ENVELOPE = AeroTags.BlockTags.create("envelope");
    public static final TagKey<Block> LEVITITE = AeroTags.BlockTags.create("levitite");
    public static final TagKey<Block> LEVITITE_BREAKABLE = AeroTags.BlockTags.create("levitite_breakable");
    public static final TagKey<Block> LEVITITE_CATALYZER = AeroTags.BlockTags.create("levitite_catalyzer");
    public static final TagKey<Block> LEVITITE_SOUL_CATALYZER = AeroTags.BlockTags.create("levitite_soul_catalyzer");
    public static final TagKey<Block> LEVITITE_ADJACENT_CATALYZER = AeroTags.BlockTags.create("levitite_adjacent_catalyzer");
    public static final TagKey<Block> LEVITITE_ADJACENT_SOUL_CATALYZER = AeroTags.BlockTags.create("levitite_adjacent_soul_catalyzer");

    private static TagKey<Block> create(String path) {
        return TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)Aeronautics.path(path));
    }

    private static void genBlockTags(RegistrateTagsProvider<Block> provIn) {
        TagGen.CreateTagsProvider prov = new TagGen.CreateTagsProvider(provIn, Block::builtInRegistryHolder);
        prov.tag(AIRTIGHT).addTag(BlockTags.WOOL);
        prov.tag(BlockTags.DAMPENS_VIBRATIONS).addTag(ENVELOPE);
        prov.tag(LEVITITE_BREAKABLE).add((Object[])new Block[]{Blocks.CLAY, Blocks.MUD, Blocks.PACKED_MUD, Blocks.COARSE_DIRT});
        prov.tag(LEVITITE_CATALYZER).add((Object[])new Block[]{Blocks.CAMPFIRE, Blocks.MAGMA_BLOCK, Blocks.TORCH, Blocks.WALL_TORCH, (Block)AllBlocks.LIT_BLAZE_BURNER.get(), Blocks.FIRE});
        prov.tag(LEVITITE_ADJACENT_CATALYZER).add((Object)Blocks.NETHERRACK).addTag(Tags.Blocks.STORAGE_BLOCKS_COAL);
        prov.tag(LEVITITE_SOUL_CATALYZER).add((Object[])new Block[]{Blocks.SOUL_CAMPFIRE, Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH, Blocks.SOUL_FIRE});
        prov.tag(LEVITITE_ADJACENT_SOUL_CATALYZER).addTag(BlockTags.SOUL_FIRE_BASE_BLOCKS);
    }
}
