/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.builders.ItemBuilder
 *  com.tterrag.registrate.providers.ProviderType
 *  com.tterrag.registrate.util.entry.ItemEntry
 *  com.tterrag.registrate.util.nullness.NonNullBiConsumer
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.food.FoodProperties$Builder
 *  net.minecraft.world.item.ArmorItem$Type
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.ArmorMaterials
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.item.SwordItem
 *  net.minecraft.world.item.Tier
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.neoforged.neoforge.common.Tags$Items
 */
package com.simibubi.create;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.api.data.datamaps.BlazeBurnerFuel;
import com.simibubi.create.api.registry.CreateDataMaps;
import com.simibubi.create.content.contraptions.glue.SuperGlueItem;
import com.simibubi.create.content.contraptions.minecart.MinecartCouplingItem;
import com.simibubi.create.content.contraptions.mounted.MinecartContraptionItem;
import com.simibubi.create.content.equipment.BuildersTeaItem;
import com.simibubi.create.content.equipment.TreeFertilizerItem;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import com.simibubi.create.content.equipment.armor.CardboardArmorItem;
import com.simibubi.create.content.equipment.armor.CardboardArmorStealthOverlay;
import com.simibubi.create.content.equipment.armor.DivingBootsItem;
import com.simibubi.create.content.equipment.armor.DivingHelmetItem;
import com.simibubi.create.content.equipment.armor.TrimmableArmorModelGenerator;
import com.simibubi.create.content.equipment.blueprint.BlueprintItem;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.content.equipment.goggles.GogglesModel;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItem;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryWandItem;
import com.simibubi.create.content.equipment.tool.AllToolMaterials;
import com.simibubi.create.content.equipment.tool.CardboardSwordItem;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.content.equipment.zapper.terrainzapper.WorldshaperItem;
import com.simibubi.create.content.kinetics.belt.item.BeltConnectorItem;
import com.simibubi.create.content.kinetics.gearbox.VerticalGearboxItem;
import com.simibubi.create.content.legacy.ChromaticCompoundColor;
import com.simibubi.create.content.legacy.ChromaticCompoundItem;
import com.simibubi.create.content.legacy.RefinedRadianceItem;
import com.simibubi.create.content.legacy.ShadowSteelItem;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.logistics.filter.AttributeFilterItem;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.ListFilterItem;
import com.simibubi.create.content.logistics.filter.PackageFilterItem;
import com.simibubi.create.content.logistics.tableCloth.ShoppingListItem;
import com.simibubi.create.content.materials.ExperienceNuggetItem;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.simibubi.create.content.schematics.SchematicAndQuillItem;
import com.simibubi.create.content.schematics.SchematicItem;
import com.simibubi.create.content.trains.schedule.ScheduleItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

public class AllItems {
    private static final CreateRegistrate REGISTRATE = Create.registrate();
    public static final ItemEntry<Item> WHEAT_FLOUR;
    public static final ItemEntry<Item> DOUGH;
    public static final ItemEntry<Item> CINDER_FLOUR;
    public static final ItemEntry<Item> ROSE_QUARTZ;
    public static final ItemEntry<Item> POLISHED_ROSE_QUARTZ;
    public static final ItemEntry<Item> POWDERED_OBSIDIAN;
    public static final ItemEntry<Item> STURDY_SHEET;
    public static final ItemEntry<Item> PROPELLER;
    public static final ItemEntry<Item> WHISK;
    public static final ItemEntry<Item> BRASS_HAND;
    public static final ItemEntry<Item> CRAFTER_SLOT_COVER;
    public static final ItemEntry<Item> ELECTRON_TUBE;
    public static final ItemEntry<Item> TRANSMITTER;
    public static final ItemEntry<Item> PULP;
    public static final ItemEntry<Item> CARDBOARD;
    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_PRECISION_MECHANISM;
    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_REINFORCED_SHEET;
    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_TRACK;
    public static final ItemEntry<Item> PRECISION_MECHANISM;
    public static final ItemEntry<Item> BLAZE_CAKE_BASE;
    public static final ItemEntry<Item> BLAZE_CAKE;
    public static final ItemEntry<Item> CREATIVE_BLAZE_CAKE;
    public static final ItemEntry<Item> BAR_OF_CHOCOLATE;
    public static final ItemEntry<Item> SWEET_ROLL;
    public static final ItemEntry<Item> CHOCOLATE_BERRIES;
    public static final ItemEntry<Item> HONEYED_APPLE;
    public static final ItemEntry<BuildersTeaItem> BUILDERS_TEA;
    public static final ItemEntry<CardboardSwordItem> CARDBOARD_SWORD;
    public static final ItemEntry<Item> RAW_ZINC;
    public static final ItemEntry<Item> ANDESITE_ALLOY;
    public static final ItemEntry<Item> ZINC_INGOT;
    public static final ItemEntry<Item> BRASS_INGOT;
    public static final ItemEntry<ChromaticCompoundItem> CHROMATIC_COMPOUND;
    public static final ItemEntry<ShadowSteelItem> SHADOW_STEEL;
    public static final ItemEntry<RefinedRadianceItem> REFINED_RADIANCE;
    public static final ItemEntry<Item> COPPER_NUGGET;
    public static final ItemEntry<Item> ZINC_NUGGET;
    public static final ItemEntry<Item> BRASS_NUGGET;
    public static final ItemEntry<ExperienceNuggetItem> EXP_NUGGET;
    public static final ItemEntry<Item> COPPER_SHEET;
    public static final ItemEntry<Item> BRASS_SHEET;
    public static final ItemEntry<Item> IRON_SHEET;
    public static final ItemEntry<Item> GOLDEN_SHEET;
    public static final ItemEntry<Item> CRUSHED_IRON;
    public static final ItemEntry<Item> CRUSHED_GOLD;
    public static final ItemEntry<Item> CRUSHED_COPPER;
    public static final ItemEntry<Item> CRUSHED_ZINC;
    public static final ItemEntry<TagDependentIngredientItem> CRUSHED_OSMIUM;
    public static final ItemEntry<TagDependentIngredientItem> CRUSHED_PLATINUM;
    public static final ItemEntry<TagDependentIngredientItem> CRUSHED_SILVER;
    public static final ItemEntry<TagDependentIngredientItem> CRUSHED_TIN;
    public static final ItemEntry<TagDependentIngredientItem> CRUSHED_LEAD;
    public static final ItemEntry<TagDependentIngredientItem> CRUSHED_QUICKSILVER;
    public static final ItemEntry<TagDependentIngredientItem> CRUSHED_BAUXITE;
    public static final ItemEntry<TagDependentIngredientItem> CRUSHED_URANIUM;
    public static final ItemEntry<TagDependentIngredientItem> CRUSHED_NICKEL;
    public static final ItemEntry<BeltConnectorItem> BELT_CONNECTOR;
    public static final ItemEntry<VerticalGearboxItem> VERTICAL_GEARBOX;
    public static final ItemEntry<BlazeBurnerBlockItem> EMPTY_BLAZE_BURNER;
    public static final ItemEntry<GogglesItem> GOGGLES;
    public static final ItemEntry<SuperGlueItem> SUPER_GLUE;
    public static final ItemEntry<MinecartCouplingItem> MINECART_COUPLING;
    public static final ItemEntry<BlueprintItem> CRAFTING_BLUEPRINT;
    public static final ItemEntry<BacktankItem.BacktankBlockItem> COPPER_BACKTANK_PLACEABLE;
    public static final ItemEntry<BacktankItem.BacktankBlockItem> NETHERITE_BACKTANK_PLACEABLE;
    public static final ItemEntry<? extends BacktankItem> COPPER_BACKTANK;
    public static final ItemEntry<? extends BacktankItem> NETHERITE_BACKTANK;
    public static final ItemEntry<? extends DivingHelmetItem> COPPER_DIVING_HELMET;
    public static final ItemEntry<? extends DivingHelmetItem> NETHERITE_DIVING_HELMET;
    public static final ItemEntry<? extends DivingBootsItem> COPPER_DIVING_BOOTS;
    public static final ItemEntry<? extends DivingBootsItem> NETHERITE_DIVING_BOOTS;
    public static final ItemEntry<? extends BaseArmorItem> CARDBOARD_HELMET;
    public static final ItemEntry<? extends BaseArmorItem> CARDBOARD_CHESTPLATE;
    public static final ItemEntry<? extends BaseArmorItem> CARDBOARD_LEGGINGS;
    public static final ItemEntry<? extends BaseArmorItem> CARDBOARD_BOOTS;
    public static final ItemEntry<SandPaperItem> SAND_PAPER;
    public static final ItemEntry<SandPaperItem> RED_SAND_PAPER;
    public static final ItemEntry<WrenchItem> WRENCH;
    public static final ItemEntry<MinecartContraptionItem> MINECART_CONTRAPTION;
    public static final ItemEntry<MinecartContraptionItem> FURNACE_MINECART_CONTRAPTION;
    public static final ItemEntry<MinecartContraptionItem> CHEST_MINECART_CONTRAPTION;
    public static final ItemEntry<LinkedControllerItem> LINKED_CONTROLLER;
    public static final ItemEntry<PotatoCannonItem> POTATO_CANNON;
    public static final ItemEntry<ExtendoGripItem> EXTENDO_GRIP;
    public static final ItemEntry<SymmetryWandItem> WAND_OF_SYMMETRY;
    public static final ItemEntry<WorldshaperItem> WORLDSHAPER;
    public static final ItemEntry<TreeFertilizerItem> TREE_FERTILIZER;
    public static final ItemEntry<ListFilterItem> FILTER;
    public static final ItemEntry<AttributeFilterItem> ATTRIBUTE_FILTER;
    public static final ItemEntry<PackageFilterItem> PACKAGE_FILTER;
    public static final ItemEntry<ScheduleItem> SCHEDULE;
    public static final ItemEntry<ShoppingListItem> SHOPPING_LIST;
    public static final ItemEntry<Item> EMPTY_SCHEMATIC;
    public static final ItemEntry<SchematicAndQuillItem> SCHEMATIC_AND_QUILL;
    public static final ItemEntry<SchematicItem> SCHEMATIC;

    private static ItemEntry<Item> ingredient(String name) {
        return REGISTRATE.item(name, Item::new).register();
    }

    private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
        return REGISTRATE.item(name, SequencedAssemblyItem::new).register();
    }

    @SafeVarargs
    private static ItemEntry<Item> taggedIngredient(String name, TagKey<Item> ... tags) {
        return REGISTRATE.item(name, Item::new).tag(tags).register();
    }

    private static ItemEntry<TagDependentIngredientItem> compatCrushedOre(CommonMetal metal) {
        return REGISTRATE.item("crushed_raw_" + String.valueOf((Object)metal), props -> new TagDependentIngredientItem((Item.Properties)props, metal.ores.items())).tag(new TagKey[]{AllTags.AllItemTags.CRUSHED_RAW_MATERIALS.tag}).register();
    }

    public static void register() {
    }

    static {
        REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_CREATIVE_TAB);
        WHEAT_FLOUR = AllItems.taggedIngredient("wheat_flour", AllTags.AllItemTags.FLOURS.tag, AllTags.AllItemTags.WHEAT_FLOURS.tag);
        DOUGH = AllItems.taggedIngredient("dough", Tags.Items.FOODS_DOUGH, AllTags.AllItemTags.FOODS_DOUGH_WHEAT.tag);
        CINDER_FLOUR = AllItems.ingredient("cinder_flour");
        ROSE_QUARTZ = AllItems.ingredient("rose_quartz");
        POLISHED_ROSE_QUARTZ = AllItems.ingredient("polished_rose_quartz");
        POWDERED_OBSIDIAN = AllItems.ingredient("powdered_obsidian");
        STURDY_SHEET = AllItems.taggedIngredient("sturdy_sheet", AllTags.AllItemTags.OBSIDIAN_PLATES.tag, AllTags.AllItemTags.PLATES.tag);
        PROPELLER = AllItems.ingredient("propeller");
        WHISK = AllItems.ingredient("whisk");
        BRASS_HAND = AllItems.ingredient("brass_hand");
        CRAFTER_SLOT_COVER = AllItems.ingredient("crafter_slot_cover");
        ELECTRON_TUBE = AllItems.ingredient("electron_tube");
        TRANSMITTER = AllItems.ingredient("transmitter");
        PULP = AllItems.ingredient("pulp");
        CARDBOARD = REGISTRATE.item("cardboard", Item::new).tag(new TagKey[]{AllTags.AllItemTags.CARDBOARD_PLATES.tag, AllTags.AllItemTags.PLATES.tag}).burnTime(1000).register();
        INCOMPLETE_PRECISION_MECHANISM = AllItems.sequencedIngredient("incomplete_precision_mechanism");
        INCOMPLETE_REINFORCED_SHEET = AllItems.sequencedIngredient("unprocessed_obsidian_sheet");
        INCOMPLETE_TRACK = AllItems.sequencedIngredient("incomplete_track");
        PRECISION_MECHANISM = AllItems.ingredient("precision_mechanism");
        BLAZE_CAKE_BASE = REGISTRATE.item("blaze_cake_base", Item::new).tag(new TagKey[]{AllTags.AllItemTags.UPRIGHT_ON_BELT.tag}).register();
        BLAZE_CAKE = ((ItemBuilder)REGISTRATE.item("blaze_cake", Item::new).tag(new TagKey[]{AllTags.AllItemTags.UPRIGHT_ON_BELT.tag}).dataMap(CreateDataMaps.SUPERHEATED_BLAZE_BURNER_FUELS, (Object)new BlazeBurnerFuel(3200))).burnTime(6400).register();
        CREATIVE_BLAZE_CAKE = REGISTRATE.item("creative_blaze_cake", Item::new).properties(p -> p.rarity(Rarity.EPIC)).tag(new TagKey[]{AllTags.AllItemTags.UPRIGHT_ON_BELT.tag}).burnTime(Integer.MAX_VALUE).register();
        BAR_OF_CHOCOLATE = REGISTRATE.item("bar_of_chocolate", Item::new).tag(new TagKey[]{Tags.Items.FOODS, AllTags.AllItemTags.FOODS_CHOCOLATE.tag}).properties(p -> p.food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.3f).build())).lang("Bar of Chocolate").register();
        SWEET_ROLL = REGISTRATE.item("sweet_roll", Item::new).tag(new TagKey[]{Tags.Items.FOODS}).properties(p -> p.food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.8f).build())).register();
        CHOCOLATE_BERRIES = REGISTRATE.item("chocolate_glazed_berries", Item::new).tag(new TagKey[]{Tags.Items.FOODS, Tags.Items.FOODS_BERRY}).properties(p -> p.food(new FoodProperties.Builder().nutrition(7).saturationModifier(0.8f).build())).register();
        HONEYED_APPLE = REGISTRATE.item("honeyed_apple", Item::new).tag(new TagKey[]{Tags.Items.FOODS, Tags.Items.FOODS_FRUIT}).properties(p -> p.food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.8f).build())).register();
        BUILDERS_TEA = REGISTRATE.item("builders_tea", BuildersTeaItem::new).tag(new TagKey[]{AllTags.AllItemTags.UPRIGHT_ON_BELT.tag, Tags.Items.FOODS, Tags.Items.DRINKS, AllTags.AllItemTags.DRINKS_TEA.tag}).properties(p -> p.stacksTo(16).food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.6f).alwaysEdible().effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 3600, 0, false, false, false), 1.0f).build())).lang("Builder's Tea").register();
        CARDBOARD_SWORD = REGISTRATE.item("cardboard_sword", CardboardSwordItem::new).burnTime(1000).properties(p -> p.stacksTo(1)).properties(p -> p.attributes(SwordItem.createAttributes((Tier)AllToolMaterials.CARDBOARD, (int)3, (float)1.0f))).model(AssetLookup.itemModelWithPartials()).register();
        RAW_ZINC = AllItems.taggedIngredient("raw_zinc", CommonMetal.ZINC.rawOres, Tags.Items.RAW_MATERIALS);
        ANDESITE_ALLOY = AllItems.taggedIngredient("andesite_alloy", AllTags.AllItemTags.CREATE_INGOTS.tag);
        ZINC_INGOT = AllItems.taggedIngredient("zinc_ingot", CommonMetal.ZINC.ingots, AllTags.AllItemTags.CREATE_INGOTS.tag);
        BRASS_INGOT = AllItems.taggedIngredient("brass_ingot", CommonMetal.BRASS.ingots, AllTags.AllItemTags.CREATE_INGOTS.tag);
        CHROMATIC_COMPOUND = REGISTRATE.item("chromatic_compound", ChromaticCompoundItem::new).properties(p -> p.rarity(Rarity.UNCOMMON)).model(AssetLookup.existingItemModel()).color(() -> ChromaticCompoundColor::new).register();
        SHADOW_STEEL = REGISTRATE.item("shadow_steel", ShadowSteelItem::new).properties(p -> p.rarity(Rarity.UNCOMMON)).register();
        REFINED_RADIANCE = REGISTRATE.item("refined_radiance", RefinedRadianceItem::new).properties(p -> p.rarity(Rarity.UNCOMMON)).register();
        COPPER_NUGGET = AllItems.taggedIngredient("copper_nugget", CommonMetal.COPPER.nuggets, Tags.Items.NUGGETS);
        ZINC_NUGGET = AllItems.taggedIngredient("zinc_nugget", CommonMetal.ZINC.nuggets, Tags.Items.NUGGETS);
        BRASS_NUGGET = AllItems.taggedIngredient("brass_nugget", CommonMetal.BRASS.nuggets, Tags.Items.NUGGETS);
        EXP_NUGGET = REGISTRATE.item("experience_nugget", ExperienceNuggetItem::new).tag(new TagKey[]{Tags.Items.NUGGETS}).properties(p -> p.rarity(Rarity.UNCOMMON)).lang("Nugget of Experience").register();
        COPPER_SHEET = AllItems.taggedIngredient("copper_sheet", CommonMetal.COPPER.plates, AllTags.AllItemTags.PLATES.tag);
        BRASS_SHEET = AllItems.taggedIngredient("brass_sheet", CommonMetal.BRASS.plates, AllTags.AllItemTags.PLATES.tag);
        IRON_SHEET = AllItems.taggedIngredient("iron_sheet", CommonMetal.IRON.plates, AllTags.AllItemTags.PLATES.tag);
        GOLDEN_SHEET = AllItems.taggedIngredient("golden_sheet", CommonMetal.GOLD.plates, AllTags.AllItemTags.PLATES.tag, ItemTags.PIGLIN_LOVED);
        CRUSHED_IRON = AllItems.taggedIngredient("crushed_raw_iron", AllTags.AllItemTags.CRUSHED_RAW_MATERIALS.tag);
        CRUSHED_GOLD = AllItems.taggedIngredient("crushed_raw_gold", AllTags.AllItemTags.CRUSHED_RAW_MATERIALS.tag, ItemTags.PIGLIN_LOVED);
        CRUSHED_COPPER = AllItems.taggedIngredient("crushed_raw_copper", AllTags.AllItemTags.CRUSHED_RAW_MATERIALS.tag);
        CRUSHED_ZINC = AllItems.taggedIngredient("crushed_raw_zinc", AllTags.AllItemTags.CRUSHED_RAW_MATERIALS.tag);
        CRUSHED_OSMIUM = AllItems.compatCrushedOre(CommonMetal.OSMIUM);
        CRUSHED_PLATINUM = AllItems.compatCrushedOre(CommonMetal.PLATINUM);
        CRUSHED_SILVER = AllItems.compatCrushedOre(CommonMetal.SILVER);
        CRUSHED_TIN = AllItems.compatCrushedOre(CommonMetal.TIN);
        CRUSHED_LEAD = AllItems.compatCrushedOre(CommonMetal.LEAD);
        CRUSHED_QUICKSILVER = AllItems.compatCrushedOre(CommonMetal.QUICKSILVER);
        CRUSHED_BAUXITE = AllItems.compatCrushedOre(CommonMetal.ALUMINUM);
        CRUSHED_URANIUM = AllItems.compatCrushedOre(CommonMetal.URANIUM);
        CRUSHED_NICKEL = AllItems.compatCrushedOre(CommonMetal.NICKEL);
        BELT_CONNECTOR = REGISTRATE.item("belt_connector", BeltConnectorItem::new).lang("Mechanical Belt").register();
        VERTICAL_GEARBOX = REGISTRATE.item("vertical_gearbox", VerticalGearboxItem::new).model(AssetLookup.customBlockItemModel("gearbox", "item_vertical")).register();
        EMPTY_BLAZE_BURNER = REGISTRATE.item("empty_blaze_burner", BlazeBurnerBlockItem::empty).model(AssetLookup.customBlockItemModel("blaze_burner", "block")).register();
        GOGGLES = ((ItemBuilder)REGISTRATE.item("goggles", GogglesItem::new).properties(p -> p.stacksTo(1)).onRegister(CreateRegistrate.itemModel(() -> GogglesModel::new))).lang("Engineer's Goggles").register();
        SUPER_GLUE = REGISTRATE.item("super_glue", SuperGlueItem::new).properties(p -> p.stacksTo(1).durability(99)).tag(new TagKey[]{ItemTags.DURABILITY_ENCHANTABLE}).register();
        MINECART_COUPLING = REGISTRATE.item("minecart_coupling", MinecartCouplingItem::new).register();
        CRAFTING_BLUEPRINT = REGISTRATE.item("crafting_blueprint", BlueprintItem::new).register();
        COPPER_BACKTANK_PLACEABLE = REGISTRATE.item("copper_backtank_placeable", p -> new BacktankItem.BacktankBlockItem((Block)AllBlocks.COPPER_BACKTANK.get(), () -> COPPER_BACKTANK.get(), (Item.Properties)p)).model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/barrier"))).register();
        NETHERITE_BACKTANK_PLACEABLE = REGISTRATE.item("netherite_backtank_placeable", p -> new BacktankItem.BacktankBlockItem((Block)AllBlocks.NETHERITE_BACKTANK.get(), () -> NETHERITE_BACKTANK.get(), (Item.Properties)p)).model((c, p) -> p.withExistingParent(c.getName(), p.mcLoc("item/barrier"))).register();
        COPPER_BACKTANK = REGISTRATE.item("copper_backtank", p -> new BacktankItem(AllArmorMaterials.COPPER, (Item.Properties)p, Create.asResource("copper_diving"), (Supplier<BacktankItem.BacktankBlockItem>)COPPER_BACKTANK_PLACEABLE)).model(AssetLookup.customGenericItemModel("_", "item")).tag(new TagKey[]{AllTags.AllItemTags.PRESSURIZED_AIR_SOURCES.tag}).tag(new TagKey[]{ItemTags.CHEST_ARMOR}).register();
        NETHERITE_BACKTANK = REGISTRATE.item("netherite_backtank", p -> new BacktankItem.Layered((Holder<ArmorMaterial>)ArmorMaterials.NETHERITE, (Item.Properties)p, Create.asResource("netherite_diving"), (Supplier<BacktankItem.BacktankBlockItem>)NETHERITE_BACKTANK_PLACEABLE)).model(AssetLookup.customGenericItemModel("_", "item")).properties(p -> p.fireResistant()).tag(new TagKey[]{AllTags.AllItemTags.PRESSURIZED_AIR_SOURCES.tag}).tag(new TagKey[]{ItemTags.CHEST_ARMOR}).register();
        COPPER_DIVING_HELMET = REGISTRATE.item("copper_diving_helmet", p -> new DivingHelmetItem(AllArmorMaterials.COPPER, (Item.Properties)p, Create.asResource("copper_diving"))).properties(p -> p.durability(ArmorItem.Type.HELMET.getDurability(7))).tag(new TagKey[]{ItemTags.HEAD_ARMOR}).register();
        NETHERITE_DIVING_HELMET = REGISTRATE.item("netherite_diving_helmet", p -> new DivingHelmetItem((Holder<ArmorMaterial>)ArmorMaterials.NETHERITE, (Item.Properties)p, Create.asResource("netherite_diving"))).properties(p -> p.fireResistant().durability(ArmorItem.Type.HELMET.getDurability(37))).tag(new TagKey[]{ItemTags.HEAD_ARMOR}).register();
        COPPER_DIVING_BOOTS = REGISTRATE.item("copper_diving_boots", p -> new DivingBootsItem(AllArmorMaterials.COPPER, (Item.Properties)p, Create.asResource("copper_diving"))).properties(p -> p.durability(ArmorItem.Type.BOOTS.getDurability(7))).tag(new TagKey[]{ItemTags.FOOT_ARMOR}).register();
        NETHERITE_DIVING_BOOTS = REGISTRATE.item("netherite_diving_boots", p -> new DivingBootsItem((Holder<ArmorMaterial>)ArmorMaterials.NETHERITE, (Item.Properties)p, Create.asResource("netherite_diving"))).properties(p -> p.fireResistant().durability(ArmorItem.Type.BOOTS.getDurability(37))).tag(new TagKey[]{ItemTags.FOOT_ARMOR}).register();
        CARDBOARD_HELMET = ((ItemBuilder)REGISTRATE.item("cardboard_helmet", p -> new CardboardArmorItem(ArmorItem.Type.HELMET, (Item.Properties)p)).properties(p -> p.durability(ArmorItem.Type.HELMET.getDurability(4))).tag(new TagKey[]{ItemTags.HEAD_ARMOR}).burnTime(1000).onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey((ItemLike)v, "item.create.cardboard_armor"))).model(TrimmableArmorModelGenerator::generate).clientExtension(() -> () -> new CardboardArmorStealthOverlay()).register();
        CARDBOARD_CHESTPLATE = ((ItemBuilder)REGISTRATE.item("cardboard_chestplate", p -> new CardboardArmorItem(ArmorItem.Type.CHESTPLATE, (Item.Properties)p)).properties(p -> p.durability(ArmorItem.Type.CHESTPLATE.getDurability(4))).tag(new TagKey[]{ItemTags.CHEST_ARMOR}).burnTime(1000).onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey((ItemLike)v, "item.create.cardboard_armor"))).model(TrimmableArmorModelGenerator::generate).register();
        CARDBOARD_LEGGINGS = ((ItemBuilder)REGISTRATE.item("cardboard_leggings", p -> new CardboardArmorItem(ArmorItem.Type.LEGGINGS, (Item.Properties)p)).properties(p -> p.durability(ArmorItem.Type.LEGGINGS.getDurability(4))).tag(new TagKey[]{ItemTags.LEG_ARMOR}).burnTime(1000).onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey((ItemLike)v, "item.create.cardboard_armor"))).model(TrimmableArmorModelGenerator::generate).register();
        CARDBOARD_BOOTS = ((ItemBuilder)REGISTRATE.item("cardboard_boots", p -> new CardboardArmorItem(ArmorItem.Type.BOOTS, (Item.Properties)p)).properties(p -> p.durability(ArmorItem.Type.BOOTS.getDurability(4))).tag(new TagKey[]{ItemTags.FOOT_ARMOR}).burnTime(1000).onRegisterAfter(Registries.ITEM, v -> ItemDescription.useKey((ItemLike)v, "item.create.cardboard_armor"))).model(TrimmableArmorModelGenerator::generate).register();
        SAND_PAPER = REGISTRATE.item("sand_paper", SandPaperItem::new).tag(new TagKey[]{AllTags.AllItemTags.SANDPAPER.tag}).register();
        RED_SAND_PAPER = ((ItemBuilder)REGISTRATE.item("red_sand_paper", SandPaperItem::new).tag(new TagKey[]{AllTags.AllItemTags.SANDPAPER.tag}).onRegister(s -> ItemDescription.referKey((ItemLike)s, SAND_PAPER))).register();
        WRENCH = REGISTRATE.item("wrench", WrenchItem::new).properties(p -> p.stacksTo(1)).model(AssetLookup.itemModelWithPartials()).tag(new TagKey[]{Tags.Items.TOOLS_WRENCH}).register();
        MINECART_CONTRAPTION = REGISTRATE.item("minecart_contraption", MinecartContraptionItem::rideable).register();
        FURNACE_MINECART_CONTRAPTION = REGISTRATE.item("furnace_minecart_contraption", MinecartContraptionItem::furnace).register();
        CHEST_MINECART_CONTRAPTION = REGISTRATE.item("chest_minecart_contraption", MinecartContraptionItem::chest).register();
        LINKED_CONTROLLER = REGISTRATE.item("linked_controller", LinkedControllerItem::new).properties(p -> p.stacksTo(1)).model(AssetLookup.itemModelWithPartials()).register();
        POTATO_CANNON = REGISTRATE.item("potato_cannon", PotatoCannonItem::new).properties(p -> p.durability(100)).model(AssetLookup.itemModelWithPartials()).tag(new TagKey[]{Tags.Items.ENCHANTABLES, ItemTags.DURABILITY_ENCHANTABLE, ItemTags.BOW_ENCHANTABLE}).register();
        EXTENDO_GRIP = REGISTRATE.item("extendo_grip", ExtendoGripItem::new).properties(p -> p.rarity(Rarity.UNCOMMON)).tag(new TagKey[]{ItemTags.DURABILITY_ENCHANTABLE}).model(AssetLookup.itemModelWithPartials()).register();
        WAND_OF_SYMMETRY = REGISTRATE.item("wand_of_symmetry", SymmetryWandItem::new).properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON)).model(AssetLookup.itemModelWithPartials()).register();
        WORLDSHAPER = REGISTRATE.item("handheld_worldshaper", WorldshaperItem::new).properties(p -> p.rarity(Rarity.EPIC)).lang("Creative Worldshaper").model(AssetLookup.itemModelWithPartials()).register();
        TREE_FERTILIZER = REGISTRATE.item("tree_fertilizer", TreeFertilizerItem::new).register();
        boolean rareCreated = false;
        boolean normalCreated = false;
        for (PackageStyles.PackageStyle style : PackageStyles.STYLES) {
            ItemBuilder<PackageItem, CreateRegistrate> packageItem = BuilderTransformers.packageItem(style);
            if (rareCreated && style.rare() || normalCreated && !style.rare()) {
                packageItem.setData(ProviderType.LANG, NonNullBiConsumer.noop());
            }
            rareCreated |= style.rare();
            normalCreated |= !style.rare();
            packageItem.register();
        }
        FILTER = REGISTRATE.item("filter", FilterItem::regular).lang("List Filter").register();
        ATTRIBUTE_FILTER = REGISTRATE.item("attribute_filter", FilterItem::attribute).register();
        PACKAGE_FILTER = REGISTRATE.item("package_filter", FilterItem::address).register();
        SCHEDULE = REGISTRATE.item("schedule", ScheduleItem::new).lang("Train Schedule").register();
        SHOPPING_LIST = REGISTRATE.item("shopping_list", ShoppingListItem::new).properties(p -> p.stacksTo(1)).register();
        EMPTY_SCHEMATIC = REGISTRATE.item("empty_schematic", Item::new).properties(p -> p.stacksTo(1)).register();
        SCHEMATIC_AND_QUILL = REGISTRATE.item("schematic_and_quill", SchematicAndQuillItem::new).properties(p -> p.stacksTo(1)).register();
        SCHEMATIC = REGISTRATE.item("schematic", SchematicItem::new).properties(p -> p.stacksTo(1)).register();
    }
}
