/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllTags
 *  com.simibubi.create.foundation.data.TagGen$CreateTagsProvider
 *  com.tterrag.registrate.providers.RegistrateItemTagsProvider
 *  com.tterrag.registrate.providers.RegistrateTagsProvider
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 */
package dev.eriksonn.aeronautics.index;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.eriksonn.aeronautics.Aeronautics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public static class AeroTags.ItemTags {
    public static final TagKey<Item> LEATHERS = AllTags.commonItemTag((String)"leathers");
    public static final TagKey<Item> ARMORS = AllTags.commonItemTag((String)"armors");
    public static final TagKey<Item> HEAD_ARMOR = TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)ResourceLocation.withDefaultNamespace((String)"head_armor"));
    public static final TagKey<Item> IRON_SHEET = AllTags.commonItemTag((String)"plates/iron");
    public static final TagKey<Item> GOLD_SHEET = AllTags.commonItemTag((String)"plates/gold");
    public static final TagKey<Item> MUSIC_DISCS = AllTags.commonItemTag((String)"music_discs");
    public static final TagKey<Item> ENVELOPE = AeroTags.ItemTags.create("envelope");
    public static final TagKey<Item> SHAFTLESS_ENVELOPE = AeroTags.ItemTags.create("shaftless_envelope");
    public static final TagKey<Item> LEVITITE_CATALYZER = AeroTags.ItemTags.create("levitite_catalyzer");
    public static final TagKey<Item> LEVITITE_SOUL_CATALYZER = AeroTags.ItemTags.create("levitite_soul_catalyzer");
    public static final TagKey<Item> LEVITITE_CATALYZER_NO_CONSUME = AeroTags.ItemTags.create("levitite_catalyzer_no_consume");
    public static final TagKey<Item> LEVITITE = AeroTags.ItemTags.create("levitite");
    public static final TagKey<Item> BURNER_FIRE = AeroTags.ItemTags.create("burner_fire");
    public static final TagKey<Item> CONVERTS_TO_CLOUD_SKIPPER = AeroTags.ItemTags.create("converts_to_cloud_skipper");

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
