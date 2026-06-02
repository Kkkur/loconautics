/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.data.TagGen$CreateTagsProvider
 *  com.tterrag.registrate.providers.RegistrateItemTagsProvider
 *  com.tterrag.registrate.providers.RegistrateTagsProvider
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 */
package dev.ryanhcode.offroad.index;

import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.ryanhcode.offroad.Offroad;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public static class OffroadTags.ItemTags {
    private static TagKey<Item> create(String path) {
        return TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)Offroad.path(path));
    }

    public static void genItemTags(RegistrateItemTagsProvider provIn) {
        TagGen.CreateTagsProvider prov = new TagGen.CreateTagsProvider((RegistrateTagsProvider)provIn, Item::builtInRegistryHolder);
    }
}
