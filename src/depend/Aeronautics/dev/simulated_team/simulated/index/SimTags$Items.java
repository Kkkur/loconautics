/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.AllTags
 *  com.simibubi.create.foundation.data.TagGen$CreateTagsProvider
 *  com.tterrag.registrate.providers.ProviderType
 *  com.tterrag.registrate.providers.RegistrateTagsProvider
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 */
package dev.simulated_team.simulated.index;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.simulated_team.simulated.Simulated;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public static class SimTags.Items {
    public static final TagKey<Item> STONE = AllTags.commonItemTag((String)"stones");
    public static final TagKey<Item> REDSTONE_DUST = AllTags.commonItemTag((String)"dusts/redstone");
    public static final TagKey<Item> SLIME_BALLS = AllTags.commonItemTag((String)"slime_balls");
    public static final TagKey<Item> AMETHYST_SHARDS = AllTags.commonItemTag((String)"gems/amethyst");
    public static final TagKey<Item> NAMEPLATE_ITEMS = SimTags.Items.create("nameplate_items");
    public static final TagKey<Item> ROTATE_WITH_NAV_ARROW = SimTags.Items.create("rotate_with_nav_arrow");
    public static final TagKey<Item> DESTROYS_ROPE = SimTags.Items.create("destroys_rope");
    public static final TagKey<Item> MERGING_GLUE = SimTags.Items.create("merging_glue");
    public static final TagKey<Item> LASER_POINTER_LENS = SimTags.Items.create("laser_point_lens");
    public static final TagKey<Item> LASER_POINTER_RAINBOW = SimTags.Items.create("laser_point_rainbow");
    public static final TagKey<Item> HANDLE_VARIANTS = SimTags.Items.create("handle_variants");
    public static final TagKey<Item> SPRING_ADJUSTER = SimTags.Items.create("spring_adjuster");

    private static TagKey<Item> create(String path) {
        return TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)Simulated.path(path));
    }

    public static void addGenerators() {
        Simulated.getRegistrate().addDataGenerator(ProviderType.ITEM_TAGS, SimTags.Items::genItemTags);
    }

    private static void genItemTags(RegistrateTagsProvider<Item> provIn) {
        TagGen.CreateTagsProvider prov = new TagGen.CreateTagsProvider(provIn, Item::builtInRegistryHolder);
        prov.tag(ROTATE_WITH_NAV_ARROW).add((Object[])new Item[]{Items.COMPASS, Items.RECOVERY_COMPASS}).addOptional(ResourceLocation.fromNamespaceAndPath((String)"naturescompass", (String)"naturescompass"));
        prov.tag(ROTATE_WITH_NAV_ARROW).addOptional(ResourceLocation.fromNamespaceAndPath((String)"explorerscompass", (String)"explorerscompass"));
        prov.tag(DESTROYS_ROPE).add((Object)Items.SHEARS).add((Object)AllItems.WRENCH.asItem());
        prov.tag(MERGING_GLUE).addTag(SLIME_BALLS);
        prov.tag(LASER_POINTER_LENS).addTag(AMETHYST_SHARDS);
        prov.tag(LASER_POINTER_RAINBOW).add((Object)Items.NETHER_STAR);
        prov.tag(SPRING_ADJUSTER).add((Object)AllItems.IRON_SHEET.asItem());
    }
}
