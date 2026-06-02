/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create;

import com.simibubi.create.AllTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public static enum AllTags.AllItemTags {
    BLAZE_BURNER_FUEL_REGULAR(AllTags.NameSpace.MOD, "blaze_burner_fuel/regular"),
    BLAZE_BURNER_FUEL_SPECIAL(AllTags.NameSpace.MOD, "blaze_burner_fuel/special"),
    CASING,
    CONTRAPTION_CONTROLLED,
    CREATE_INGOTS,
    CRUSHED_RAW_MATERIALS,
    INVALID_FOR_TRACK_PAVING,
    DEPLOYABLE_DRINK,
    PRESSURIZED_AIR_SOURCES,
    SANDPAPER,
    SEATS,
    POSTBOXES,
    TABLE_CLOTHS,
    DYED_TABLE_CLOTHS,
    PULPIFIABLE,
    SLEEPERS,
    TOOLBOXES,
    PACKAGES,
    CHAIN_RIDEABLE,
    TRACKS,
    UPRIGHT_ON_BELT,
    NOT_UPRIGHT_ON_BELT,
    NOT_POTION,
    VALVE_HANDLES,
    DISPENSE_BEHAVIOR_WRAP_BLACKLIST,
    OBSIDIAN_DUST(AllTags.NameSpace.COMMON, "dusts/obsidian"),
    PLATES(AllTags.NameSpace.COMMON),
    OBSIDIAN_PLATES(AllTags.NameSpace.COMMON, "plates/obsidian"),
    CARDBOARD_PLATES(AllTags.NameSpace.COMMON, "plates/cardboard"),
    ALLURITE(AllTags.NameSpace.MOD, "stone_types/galosphere/allurite"),
    AMETHYST(AllTags.NameSpace.MOD, "stone_types/galosphere/amethyst"),
    LUMIERE(AllTags.NameSpace.MOD, "stone_types/galosphere/lumiere"),
    CERTUS_QUARTZ(AllTags.NameSpace.COMMON, "gems/certus_quartz"),
    AMETRINE_ORES(AllTags.NameSpace.COMMON, "ores/ametrine"),
    ANTHRACITE_ORES(AllTags.NameSpace.COMMON, "ores/anthracite"),
    EMERALDITE_ORES(AllTags.NameSpace.COMMON, "ores/emeraldite"),
    LIGNITE_ORES(AllTags.NameSpace.COMMON, "ores/lignite"),
    CARDBOARD_STORAGE_BLOCKS(AllTags.NameSpace.COMMON, "storage_blocks/cardboard"),
    ANDESITE_ALLOY_STORAGE_BLOCKS(AllTags.NameSpace.COMMON, "storage_blocks/andesite_alloy"),
    CHOCOLATE_BUCKETS(AllTags.NameSpace.COMMON, "buckets/chocolate"),
    HONEY_BUCKETS(AllTags.NameSpace.COMMON, "buckets/honey"),
    FOODS_CHOCOLATE(AllTags.NameSpace.COMMON, "foods/chocolate"),
    DRINKS_TEA(AllTags.NameSpace.COMMON, "drinks/tea"),
    FLOURS(AllTags.NameSpace.COMMON),
    WHEAT_FLOURS(AllTags.NameSpace.COMMON, "flours/wheat"),
    FOODS_DOUGH_WHEAT(AllTags.NameSpace.COMMON, "foods/dough/wheat"),
    UA_CORAL(AllTags.NameSpace.MOD, "upgrade_aquatic/coral"),
    CURIOS_HEAD(AllTags.NameSpace.CURIOS, "head");

    public final TagKey<Item> tag;

    private AllTags.AllItemTags() {
        this(AllTags.NameSpace.MOD);
    }

    private AllTags.AllItemTags(AllTags.NameSpace namespace) {
        this(namespace, null);
    }

    private AllTags.AllItemTags(AllTags.NameSpace namespace, String pathOverride) {
        this.tag = TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)namespace.id(this, pathOverride));
    }

    public boolean matches(Item item) {
        return item.builtInRegistryHolder().is(this.tag);
    }

    public boolean matches(ItemStack stack) {
        return stack.is(this.tag);
    }
}
