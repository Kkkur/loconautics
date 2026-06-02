/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.data.TagGen$CreateTagsProvider
 *  com.tterrag.registrate.providers.ProviderType
 *  com.tterrag.registrate.providers.RegistrateItemTagsProvider
 *  com.tterrag.registrate.providers.RegistrateTagsProvider
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.Block
 */
package dev.ryanhcode.offroad.index;

import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.ryanhcode.offroad.Offroad;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class OffroadTags {
    public static void addGenerators() {
        Offroad.getRegistrate().addDataGenerator(ProviderType.BLOCK_TAGS, BlockTags::genBlockTags);
        Offroad.getRegistrate().addDataGenerator(ProviderType.ITEM_TAGS, ItemTags::genItemTags);
    }

    public static class ItemTags {
        private static TagKey<Item> create(String path) {
            return TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)Offroad.path(path));
        }

        public static void genItemTags(RegistrateItemTagsProvider provIn) {
            TagGen.CreateTagsProvider prov = new TagGen.CreateTagsProvider((RegistrateTagsProvider)provIn, Item::builtInRegistryHolder);
        }
    }

    public static class BlockTags {
        private static TagKey<Block> create(String path) {
            return TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)Offroad.path(path));
        }

        private static void genBlockTags(RegistrateTagsProvider<Block> provIn) {
            TagGen.CreateTagsProvider prov = new TagGen.CreateTagsProvider(provIn, Block::builtInRegistryHolder);
        }
    }
}
