/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create;

import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.registry.CreateRegistries;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class AllTags {
    @Deprecated(since="6.0.7", forRemoval=true)
    @ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
    public static <T> TagKey<T> optionalTag(Registry<T> registry, ResourceLocation id) {
        return TagKey.create((ResourceKey)registry.key(), (ResourceLocation)id);
    }

    @Deprecated(since="6.0.7", forRemoval=true)
    @ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
    public static <T> TagKey<T> commonTag(Registry<T> registry, String path) {
        return AllTags.optionalTag(registry, ResourceLocation.fromNamespaceAndPath((String)"c", (String)path));
    }

    @Deprecated(since="6.0.7", forRemoval=true)
    @ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
    public static TagKey<Block> commonBlockTag(String path) {
        return AllTags.commonTag(BuiltInRegistries.BLOCK, path);
    }

    @Deprecated(since="6.0.7", forRemoval=true)
    @ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
    public static TagKey<Item> commonItemTag(String path) {
        return AllTags.commonTag(BuiltInRegistries.ITEM, path);
    }

    @Deprecated(since="6.0.7", forRemoval=true)
    @ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
    public static TagKey<Fluid> commonFluidTag(String path) {
        return AllTags.commonTag(BuiltInRegistries.FLUID, path);
    }

    public static enum AllMountedItemStorageTypeTags {
        INTERNAL,
        FUEL_BLACKLIST;

        public final TagKey<MountedItemStorageType<?>> tag;

        private AllMountedItemStorageTypeTags() {
            this(NameSpace.MOD);
        }

        private AllMountedItemStorageTypeTags(NameSpace namespace) {
            this(namespace, null);
        }

        private AllMountedItemStorageTypeTags(NameSpace namespace, String pathOverride) {
            this.tag = TagKey.create(CreateRegistries.MOUNTED_ITEM_STORAGE_TYPE, (ResourceLocation)namespace.id(this, pathOverride));
        }

        public boolean matches(MountedItemStorage storage) {
            return this.matches(storage.type);
        }

        public boolean matches(MountedItemStorageType<?> type) {
            return type.is(this.tag);
        }
    }

    public static enum AllContraptionTypeTags {
        OPENS_CONTROLS,
        REQUIRES_VEHICLE_FOR_RENDER;

        public final TagKey<ContraptionType> tag;

        private AllContraptionTypeTags() {
            this(NameSpace.MOD);
        }

        private AllContraptionTypeTags(NameSpace namespace) {
            this(namespace, null);
        }

        private AllContraptionTypeTags(NameSpace namespace, String pathOverride) {
            this.tag = TagKey.create(CreateRegistries.CONTRAPTION_TYPE, (ResourceLocation)namespace.id(this, pathOverride));
        }

        public boolean matches(ContraptionType type) {
            return type.is(this.tag);
        }
    }

    public static enum AllRecipeSerializerTags {
        AUTOMATION_IGNORE;

        public final TagKey<RecipeSerializer<?>> tag;

        private AllRecipeSerializerTags() {
            this(NameSpace.MOD);
        }

        private AllRecipeSerializerTags(NameSpace namespace) {
            this(namespace, null);
        }

        private AllRecipeSerializerTags(NameSpace namespace, String pathOverride) {
            this.tag = TagKey.create((ResourceKey)Registries.RECIPE_SERIALIZER, (ResourceLocation)namespace.id(this, pathOverride));
        }

        public boolean matches(RecipeSerializer<?> recipeSerializer) {
            ResourceKey key = (ResourceKey)BuiltInRegistries.RECIPE_SERIALIZER.getResourceKey(recipeSerializer).orElseThrow();
            return ((Holder.Reference)BuiltInRegistries.RECIPE_SERIALIZER.getHolder(key).orElseThrow()).is(this.tag);
        }
    }

    public static enum AllEntityTags {
        BLAZE_BURNER_CAPTURABLE,
        IGNORE_SEAT;

        public final TagKey<EntityType<?>> tag;

        private AllEntityTags() {
            this(NameSpace.MOD);
        }

        private AllEntityTags(NameSpace namespace) {
            this(namespace, null);
        }

        private AllEntityTags(NameSpace namespace, String pathOverride) {
            this.tag = TagKey.create((ResourceKey)Registries.ENTITY_TYPE, (ResourceLocation)namespace.id(this, pathOverride));
        }

        public boolean matches(EntityType<?> type) {
            return type.is(this.tag);
        }

        public boolean matches(Entity entity) {
            return this.matches(entity.getType());
        }
    }

    public static enum AllFluidTags {
        BOTTOMLESS_ALLOW(NameSpace.MOD, "bottomless/allow"),
        BOTTOMLESS_DENY(NameSpace.MOD, "bottomless/deny"),
        FAN_PROCESSING_CATALYSTS_BLASTING(NameSpace.MOD, "fan_processing_catalysts/blasting"),
        FAN_PROCESSING_CATALYSTS_HAUNTING(NameSpace.MOD, "fan_processing_catalysts/haunting"),
        FAN_PROCESSING_CATALYSTS_SMOKING(NameSpace.MOD, "fan_processing_catalysts/smoking"),
        FAN_PROCESSING_CATALYSTS_SPLASHING(NameSpace.MOD, "fan_processing_catalysts/splashing"),
        TEA(NameSpace.COMMON),
        CHOCOLATE(NameSpace.COMMON),
        CREOSOTE(NameSpace.COMMON);

        public final TagKey<Fluid> tag;

        private AllFluidTags() {
            this(NameSpace.MOD);
        }

        private AllFluidTags(NameSpace namespace) {
            this(namespace, null);
        }

        private AllFluidTags(NameSpace namespace, String pathOverride) {
            this.tag = TagKey.create((ResourceKey)Registries.FLUID, (ResourceLocation)namespace.id(this, pathOverride));
        }

        public boolean matches(Fluid fluid) {
            return fluid.is(this.tag);
        }

        public boolean matches(FluidState state) {
            return state.is(this.tag);
        }
    }

    public static enum AllItemTags {
        BLAZE_BURNER_FUEL_REGULAR(NameSpace.MOD, "blaze_burner_fuel/regular"),
        BLAZE_BURNER_FUEL_SPECIAL(NameSpace.MOD, "blaze_burner_fuel/special"),
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
        OBSIDIAN_DUST(NameSpace.COMMON, "dusts/obsidian"),
        PLATES(NameSpace.COMMON),
        OBSIDIAN_PLATES(NameSpace.COMMON, "plates/obsidian"),
        CARDBOARD_PLATES(NameSpace.COMMON, "plates/cardboard"),
        ALLURITE(NameSpace.MOD, "stone_types/galosphere/allurite"),
        AMETHYST(NameSpace.MOD, "stone_types/galosphere/amethyst"),
        LUMIERE(NameSpace.MOD, "stone_types/galosphere/lumiere"),
        CERTUS_QUARTZ(NameSpace.COMMON, "gems/certus_quartz"),
        AMETRINE_ORES(NameSpace.COMMON, "ores/ametrine"),
        ANTHRACITE_ORES(NameSpace.COMMON, "ores/anthracite"),
        EMERALDITE_ORES(NameSpace.COMMON, "ores/emeraldite"),
        LIGNITE_ORES(NameSpace.COMMON, "ores/lignite"),
        CARDBOARD_STORAGE_BLOCKS(NameSpace.COMMON, "storage_blocks/cardboard"),
        ANDESITE_ALLOY_STORAGE_BLOCKS(NameSpace.COMMON, "storage_blocks/andesite_alloy"),
        CHOCOLATE_BUCKETS(NameSpace.COMMON, "buckets/chocolate"),
        HONEY_BUCKETS(NameSpace.COMMON, "buckets/honey"),
        FOODS_CHOCOLATE(NameSpace.COMMON, "foods/chocolate"),
        DRINKS_TEA(NameSpace.COMMON, "drinks/tea"),
        FLOURS(NameSpace.COMMON),
        WHEAT_FLOURS(NameSpace.COMMON, "flours/wheat"),
        FOODS_DOUGH_WHEAT(NameSpace.COMMON, "foods/dough/wheat"),
        UA_CORAL(NameSpace.MOD, "upgrade_aquatic/coral"),
        CURIOS_HEAD(NameSpace.CURIOS, "head");

        public final TagKey<Item> tag;

        private AllItemTags() {
            this(NameSpace.MOD);
        }

        private AllItemTags(NameSpace namespace) {
            this(namespace, null);
        }

        private AllItemTags(NameSpace namespace, String pathOverride) {
            this.tag = TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)namespace.id(this, pathOverride));
        }

        public boolean matches(Item item) {
            return item.builtInRegistryHolder().is(this.tag);
        }

        public boolean matches(ItemStack stack) {
            return stack.is(this.tag);
        }
    }

    public static enum AllBlockTags {
        BRITTLE,
        CASING,
        COPYCAT_ALLOW,
        COPYCAT_DENY,
        FAN_PROCESSING_CATALYSTS_BLASTING(NameSpace.MOD, "fan_processing_catalysts/blasting"),
        FAN_PROCESSING_CATALYSTS_HAUNTING(NameSpace.MOD, "fan_processing_catalysts/haunting"),
        FAN_PROCESSING_CATALYSTS_SMOKING(NameSpace.MOD, "fan_processing_catalysts/smoking"),
        FAN_PROCESSING_CATALYSTS_SPLASHING(NameSpace.MOD, "fan_processing_catalysts/splashing"),
        FAN_TRANSPARENT,
        GIRDABLE_TRACKS,
        MOVABLE_EMPTY_COLLIDER,
        NON_MOVABLE,
        NON_BREAKABLE,
        PASSIVE_BOILER_HEATERS,
        SAFE_NBT,
        SEATS,
        POSTBOXES,
        TABLE_CLOTHS,
        TOOLBOXES,
        TRACKS,
        TREE_ATTACHMENTS,
        VALVE_HANDLES,
        WINDMILL_SAILS,
        WRENCH_PICKUP,
        CHEST_MOUNTED_STORAGE,
        SIMPLE_MOUNTED_STORAGE,
        FALLBACK_MOUNTED_STORAGE_BLACKLIST,
        ROOTS,
        SUGAR_CANE_VARIANTS,
        NON_HARVESTABLE,
        SINGLE_BLOCK_INVENTORIES,
        PLOUGH_WHITELIST,
        PLOUGH_BLACKLIST,
        CARDBOARD_STORAGE_BLOCKS(NameSpace.COMMON, "storage_blocks/cardboard"),
        ANDESITE_ALLOY_STORAGE_BLOCKS(NameSpace.COMMON, "storage_blocks/andesite_alloy"),
        CORALS,
        SLIMY_LOGS(NameSpace.TIC),
        NON_DOUBLE_DOOR(NameSpace.QUARK);

        public final TagKey<Block> tag;

        private AllBlockTags() {
            this(NameSpace.MOD);
        }

        private AllBlockTags(NameSpace namespace) {
            this(namespace, null);
        }

        private AllBlockTags(NameSpace namespace, String pathOverride) {
            this.tag = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)namespace.id(this, pathOverride));
        }

        public boolean matches(Block block) {
            return block.builtInRegistryHolder().is(this.tag);
        }

        public boolean matches(ItemStack stack) {
            BlockItem blockItem;
            Item item;
            return stack != null && (item = stack.getItem()) instanceof BlockItem && this.matches((blockItem = (BlockItem)item).getBlock());
        }

        public boolean matches(BlockState state) {
            return state.is(this.tag);
        }
    }

    public static enum NameSpace {
        MOD("create"),
        COMMON("c"),
        TIC("tconstruct"),
        QUARK("quark"),
        GS("galosphere"),
        CURIOS("curios");

        public final String id;

        private NameSpace(String id) {
            this.id = id;
        }

        public ResourceLocation id(String path) {
            return ResourceLocation.fromNamespaceAndPath((String)this.id, (String)path);
        }

        public ResourceLocation id(Enum<?> entry, @Nullable String pathOverride) {
            return this.id(pathOverride != null ? pathOverride : Lang.asId((String)entry.name()));
        }
    }
}
