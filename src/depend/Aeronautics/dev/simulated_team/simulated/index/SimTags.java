/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.AllTags
 *  com.simibubi.create.AllTags$AllBlockTags
 *  com.simibubi.create.foundation.data.TagGen$CreateTagsProvider
 *  com.tterrag.registrate.providers.ProviderType
 *  com.tterrag.registrate.providers.RegistrateTagsProvider
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.saveddata.maps.MapDecorationType
 */
package dev.simulated_team.simulated.index;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.simulated_team.simulated.Simulated;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;

public class SimTags {
    public static final Map<String, TagKey<Item>> DYE_MAP = new HashMap<String, TagKey<Item>>();

    public static void addGenerators() {
        Blocks.addGenerators();
        Items.addGenerators();
    }

    public static void register() {
    }

    static {
        for (DyeColor color : DyeColor.values()) {
            DYE_MAP.put(color.getName(), (TagKey<Item>)AllTags.commonItemTag((String)("dyes/" + color.getName())));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> NON_MOVABLE = Blocks.create("non_movable");
        public static final TagKey<Block> SUPER_LIGHT = Blocks.create("sable", "super_light");
        public static final TagKey<Block> LIGHT = Blocks.create("sable", "light");
        public static final TagKey<Block> DIODE = Blocks.create("sable", "diode");
        public static final TagKey<Block> AIRTIGHT = Blocks.create("airtight");
        public static final TagKey<Block> NAMEPLATE_BLOCKS = Blocks.create("nameplate_blocks");
        public static final TagKey<Block> SYMMETRIC_SAILS = Blocks.create("symmetric_sails");
        public static final TagKey<Block> HANDLES = Blocks.create("handles");

        private static TagKey<Block> create(String path) {
            return TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)Simulated.path(path));
        }

        private static TagKey<Block> create(String namespace, String path) {
            return TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)namespace, (String)path));
        }

        protected static void addGenerators() {
            Simulated.getRegistrate().addDataGenerator(ProviderType.BLOCK_TAGS, Blocks::genBlockTags);
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

    public static class Items {
        public static final TagKey<Item> STONE = AllTags.commonItemTag((String)"stones");
        public static final TagKey<Item> REDSTONE_DUST = AllTags.commonItemTag((String)"dusts/redstone");
        public static final TagKey<Item> SLIME_BALLS = AllTags.commonItemTag((String)"slime_balls");
        public static final TagKey<Item> AMETHYST_SHARDS = AllTags.commonItemTag((String)"gems/amethyst");
        public static final TagKey<Item> NAMEPLATE_ITEMS = Items.create("nameplate_items");
        public static final TagKey<Item> ROTATE_WITH_NAV_ARROW = Items.create("rotate_with_nav_arrow");
        public static final TagKey<Item> DESTROYS_ROPE = Items.create("destroys_rope");
        public static final TagKey<Item> MERGING_GLUE = Items.create("merging_glue");
        public static final TagKey<Item> LASER_POINTER_LENS = Items.create("laser_point_lens");
        public static final TagKey<Item> LASER_POINTER_RAINBOW = Items.create("laser_point_rainbow");
        public static final TagKey<Item> HANDLE_VARIANTS = Items.create("handle_variants");
        public static final TagKey<Item> SPRING_ADJUSTER = Items.create("spring_adjuster");

        private static TagKey<Item> create(String path) {
            return TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)Simulated.path(path));
        }

        public static void addGenerators() {
            Simulated.getRegistrate().addDataGenerator(ProviderType.ITEM_TAGS, Items::genItemTags);
        }

        private static void genItemTags(RegistrateTagsProvider<Item> provIn) {
            TagGen.CreateTagsProvider prov = new TagGen.CreateTagsProvider(provIn, Item::builtInRegistryHolder);
            prov.tag(ROTATE_WITH_NAV_ARROW).add((Object[])new Item[]{net.minecraft.world.item.Items.COMPASS, net.minecraft.world.item.Items.RECOVERY_COMPASS}).addOptional(ResourceLocation.fromNamespaceAndPath((String)"naturescompass", (String)"naturescompass"));
            prov.tag(ROTATE_WITH_NAV_ARROW).addOptional(ResourceLocation.fromNamespaceAndPath((String)"explorerscompass", (String)"explorerscompass"));
            prov.tag(DESTROYS_ROPE).add((Object)net.minecraft.world.item.Items.SHEARS).add((Object)AllItems.WRENCH.asItem());
            prov.tag(MERGING_GLUE).addTag(SLIME_BALLS);
            prov.tag(LASER_POINTER_LENS).addTag(AMETHYST_SHARDS);
            prov.tag(LASER_POINTER_RAINBOW).add((Object)net.minecraft.world.item.Items.NETHER_STAR);
            prov.tag(SPRING_ADJUSTER).add((Object)AllItems.IRON_SHEET.asItem());
        }
    }

    public static class Misc {
        public static final TagKey<MapDecorationType> NAV_TABLE_FINDABLE = TagKey.create((ResourceKey)Registries.MAP_DECORATION_TYPE, (ResourceLocation)Simulated.path("nav_table_findable"));
        public static final TagKey<EntityType<?>> ARMOR_STAND_IGNORE = TagKey.create((ResourceKey)Registries.ENTITY_TYPE, (ResourceLocation)Simulated.path("armor_stand_ignore"));
        public static final TagKey<EntityType<?>> LASER_BLACKLIST = TagKey.create((ResourceKey)Registries.ENTITY_TYPE, (ResourceLocation)Simulated.path("laser_entity_blacklist"));
    }
}
