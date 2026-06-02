/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.AllTags
 *  com.simibubi.create.foundation.data.TagGen$CreateTagsProvider
 *  com.tterrag.registrate.providers.ProviderType
 *  com.tterrag.registrate.providers.RegistrateItemTagsProvider
 *  com.tterrag.registrate.providers.RegistrateTagsProvider
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.neoforged.neoforge.common.Tags$Blocks
 */
package dev.eriksonn.aeronautics.index;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.eriksonn.aeronautics.Aeronautics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

public class AeroTags {
    public static void addGenerators() {
        Aeronautics.getRegistrate().addDataGenerator(ProviderType.BLOCK_TAGS, BlockTags::genBlockTags);
        Aeronautics.getRegistrate().addDataGenerator(ProviderType.ITEM_TAGS, ItemTags::genItemTags);
    }

    public static class ItemTags {
        public static final TagKey<Item> LEATHERS = AllTags.commonItemTag((String)"leathers");
        public static final TagKey<Item> ARMORS = AllTags.commonItemTag((String)"armors");
        public static final TagKey<Item> HEAD_ARMOR = TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)ResourceLocation.withDefaultNamespace((String)"head_armor"));
        public static final TagKey<Item> IRON_SHEET = AllTags.commonItemTag((String)"plates/iron");
        public static final TagKey<Item> GOLD_SHEET = AllTags.commonItemTag((String)"plates/gold");
        public static final TagKey<Item> MUSIC_DISCS = AllTags.commonItemTag((String)"music_discs");
        public static final TagKey<Item> ENVELOPE = ItemTags.create("envelope");
        public static final TagKey<Item> SHAFTLESS_ENVELOPE = ItemTags.create("shaftless_envelope");
        public static final TagKey<Item> LEVITITE_CATALYZER = ItemTags.create("levitite_catalyzer");
        public static final TagKey<Item> LEVITITE_SOUL_CATALYZER = ItemTags.create("levitite_soul_catalyzer");
        public static final TagKey<Item> LEVITITE_CATALYZER_NO_CONSUME = ItemTags.create("levitite_catalyzer_no_consume");
        public static final TagKey<Item> LEVITITE = ItemTags.create("levitite");
        public static final TagKey<Item> BURNER_FIRE = ItemTags.create("burner_fire");
        public static final TagKey<Item> CONVERTS_TO_CLOUD_SKIPPER = ItemTags.create("converts_to_cloud_skipper");

        private static TagKey<Item> create(String path) {
            return TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)Aeronautics.path(path));
        }

        public static void genItemTags(RegistrateItemTagsProvider provIn) {
            TagGen.CreateTagsProvider prov = new TagGen.CreateTagsProvider((RegistrateTagsProvider)provIn, Item::builtInRegistryHolder);
            prov.tag(LEVITITE_CATALYZER).add((Object[])new Item[]{Items.FLINT_AND_STEEL, Items.FIRE_CHARGE, Items.TORCH, Items.CAMPFIRE});
            prov.tag(LEVITITE_SOUL_CATALYZER).add((Object[])new Item[]{Items.SOUL_TORCH, Items.SOUL_CAMPFIRE});
            prov.tag(LEVITITE_CATALYZER_NO_CONSUME).add((Object[])new Item[]{Items.TORCH, Items.CAMPFIRE, Items.SOUL_TORCH, Items.SOUL_CAMPFIRE});
            prov.tag(BURNER_FIRE).add((Object)Items.COAL_BLOCK);
        }
    }

    public static class BlockTags {
        public static final TagKey<Block> AIRTIGHT = BlockTags.create("airtight");
        public static final TagKey<Block> ENVELOPE = BlockTags.create("envelope");
        public static final TagKey<Block> LEVITITE = BlockTags.create("levitite");
        public static final TagKey<Block> LEVITITE_BREAKABLE = BlockTags.create("levitite_breakable");
        public static final TagKey<Block> LEVITITE_CATALYZER = BlockTags.create("levitite_catalyzer");
        public static final TagKey<Block> LEVITITE_SOUL_CATALYZER = BlockTags.create("levitite_soul_catalyzer");
        public static final TagKey<Block> LEVITITE_ADJACENT_CATALYZER = BlockTags.create("levitite_adjacent_catalyzer");
        public static final TagKey<Block> LEVITITE_ADJACENT_SOUL_CATALYZER = BlockTags.create("levitite_adjacent_soul_catalyzer");

        private static TagKey<Block> create(String path) {
            return TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)Aeronautics.path(path));
        }

        private static void genBlockTags(RegistrateTagsProvider<Block> provIn) {
            TagGen.CreateTagsProvider prov = new TagGen.CreateTagsProvider(provIn, Block::builtInRegistryHolder);
            prov.tag(AIRTIGHT).addTag(net.minecraft.tags.BlockTags.WOOL);
            prov.tag(net.minecraft.tags.BlockTags.DAMPENS_VIBRATIONS).addTag(ENVELOPE);
            prov.tag(LEVITITE_BREAKABLE).add((Object[])new Block[]{Blocks.CLAY, Blocks.MUD, Blocks.PACKED_MUD, Blocks.COARSE_DIRT});
            prov.tag(LEVITITE_CATALYZER).add((Object[])new Block[]{Blocks.CAMPFIRE, Blocks.MAGMA_BLOCK, Blocks.TORCH, Blocks.WALL_TORCH, (Block)AllBlocks.LIT_BLAZE_BURNER.get(), Blocks.FIRE});
            prov.tag(LEVITITE_ADJACENT_CATALYZER).add((Object)Blocks.NETHERRACK).addTag(Tags.Blocks.STORAGE_BLOCKS_COAL);
            prov.tag(LEVITITE_SOUL_CATALYZER).add((Object[])new Block[]{Blocks.SOUL_CAMPFIRE, Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH, Blocks.SOUL_FIRE});
            prov.tag(LEVITITE_ADJACENT_SOUL_CATALYZER).addTag(net.minecraft.tags.BlockTags.SOUL_FIRE_BASE_BLOCKS);
        }
    }
}
