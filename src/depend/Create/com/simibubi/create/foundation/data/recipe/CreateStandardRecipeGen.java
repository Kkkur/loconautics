/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  com.tterrag.registrate.util.entry.ItemEntry
 *  com.tterrag.registrate.util.entry.ItemProviderEntry
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.advancements.Advancement$Builder
 *  net.minecraft.advancements.AdvancementHolder
 *  net.minecraft.advancements.critereon.ItemPredicate
 *  net.minecraft.advancements.critereon.ItemPredicate$Builder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.data.recipes.ShapedRecipeBuilder
 *  net.minecraft.data.recipes.ShapelessRecipeBuilder
 *  net.minecraft.data.recipes.SimpleCookingRecipeBuilder
 *  net.minecraft.data.recipes.SmithingTransformRecipeBuilder
 *  net.minecraft.data.recipes.SpecialRecipeBuilder
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.AbstractCookingRecipe
 *  net.minecraft.world.item.crafting.AbstractCookingRecipe$Factory
 *  net.minecraft.world.item.crafting.BlastingRecipe
 *  net.minecraft.world.item.crafting.CampfireCookingRecipe
 *  net.minecraft.world.item.crafting.CraftingBookCategory
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.crafting.SmeltingRecipe
 *  net.minecraft.world.item.crafting.SmokingRecipe
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.neoforged.neoforge.common.Tags$Items
 *  net.neoforged.neoforge.common.conditions.ICondition
 *  net.neoforged.neoforge.common.conditions.ModLoadedCondition
 *  net.neoforged.neoforge.common.conditions.NotCondition
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.data.recipe;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider;
import com.simibubi.create.content.decoration.palettes.AllPaletteBlocks;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.content.equipment.toolbox.ToolboxDyeingRecipe;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import com.simibubi.create.foundation.data.recipe.Mods;
import com.simibubi.create.foundation.mixin.accessor.MappedRegistryAccessor;
import com.simibubi.create.foundation.recipe.ItemCopyingRecipe;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.annotation.ParametersAreNonnullByDefault;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import org.jetbrains.annotations.Nullable;

public final class CreateStandardRecipeGen
extends BaseRecipeProvider {
    final List<BaseRecipeProvider.GeneratedRecipe> all = new ArrayList<BaseRecipeProvider.GeneratedRecipe>();
    private Marker MATERIALS = this.enterFolder("materials");
    BaseRecipeProvider.GeneratedRecipe RAW_ZINC = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.RAW_ZINC).returns(9).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllBlocks.RAW_ZINC_BLOCK.get())).viaShapeless(b -> b.requires((ItemLike)AllBlocks.RAW_ZINC_BLOCK.get()));
    BaseRecipeProvider.GeneratedRecipe RAW_ZINC_BLOCK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.RAW_ZINC_BLOCK).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllItems.RAW_ZINC.get())).viaShaped(b -> b.define(Character.valueOf('C'), (ItemLike)AllItems.RAW_ZINC.get()).pattern("CCC").pattern("CCC").pattern("CCC"));
    BaseRecipeProvider.GeneratedRecipe COPPER_NUGGET = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.COPPER_NUGGET).returns(9).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::copper)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.copper()));
    BaseRecipeProvider.GeneratedRecipe COPPER_INGOT = this.create((Supplier<ItemLike>)((Supplier)() -> Items.COPPER_INGOT)).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::copperNugget)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.copperNugget()).pattern("CCC").pattern("CCC").pattern("CCC"));
    BaseRecipeProvider.GeneratedRecipe ANDESITE_ALLOY_FROM_BLOCK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.ANDESITE_ALLOY).withSuffix("_from_block").returns(9).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShapeless(b -> b.requires((ItemLike)AllBlocks.ANDESITE_ALLOY_BLOCK.get()));
    BaseRecipeProvider.GeneratedRecipe ANDESITE_ALLOY_BLOCK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.ANDESITE_ALLOY_BLOCK).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteAlloy()).pattern("CCC").pattern("CCC").pattern("CCC"));
    BaseRecipeProvider.GeneratedRecipe EXPERIENCE_FROM_BLOCK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.EXP_NUGGET).withSuffix("_from_block").returns(9).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllItems.EXP_NUGGET.get())).viaShapeless(b -> b.requires((ItemLike)AllBlocks.EXPERIENCE_BLOCK.get()));
    BaseRecipeProvider.GeneratedRecipe EXPERIENCE_BLOCK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.EXPERIENCE_BLOCK).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllItems.EXP_NUGGET.get())).viaShaped(b -> b.define(Character.valueOf('C'), (ItemLike)AllItems.EXP_NUGGET.get()).pattern("CCC").pattern("CCC").pattern("CCC"));
    BaseRecipeProvider.GeneratedRecipe CARDBOARD_BLOCK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.CARDBOARD_BLOCK).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cardboard)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.cardboard()).pattern("CC").pattern("CC"));
    BaseRecipeProvider.GeneratedRecipe BOUND_CARDBOARD_BLOCK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.BOUND_CARDBOARD_BLOCK).returns(1).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cardboard)).viaShapeless(b -> b.requires((ItemLike)AllBlocks.CARDBOARD_BLOCK.get()).requires(Tags.Items.STRINGS));
    BaseRecipeProvider.GeneratedRecipe CARDBOARD_FROM_BLOCK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.CARDBOARD).withSuffix("_from_block").returns(4).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cardboard)).viaShapeless(b -> b.requires((ItemLike)AllBlocks.CARDBOARD_BLOCK.get()));
    BaseRecipeProvider.GeneratedRecipe CARDBOARD_FROM_BOUND_BLOCK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.CARDBOARD).withSuffix("_from_bound_block").returns(4).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cardboard)).viaShapeless(b -> b.requires((ItemLike)AllBlocks.BOUND_CARDBOARD_BLOCK.get()));
    BaseRecipeProvider.GeneratedRecipe BRASS_COMPACTING = this.metalCompacting((List<ItemProviderEntry<? extends ItemLike, ? extends ItemLike>>)ImmutableList.of(AllItems.BRASS_NUGGET, AllItems.BRASS_INGOT, AllBlocks.BRASS_BLOCK), (List<Supplier<TagKey<Item>>>)ImmutableList.of(CreateRecipeProvider.I::brassNugget, CreateRecipeProvider.I::brass, CreateRecipeProvider.I::brassBlock));
    BaseRecipeProvider.GeneratedRecipe ZINC_COMPACTING = this.metalCompacting((List<ItemProviderEntry<? extends ItemLike, ? extends ItemLike>>)ImmutableList.of(AllItems.ZINC_NUGGET, AllItems.ZINC_INGOT, AllBlocks.ZINC_BLOCK), (List<Supplier<TagKey<Item>>>)ImmutableList.of(CreateRecipeProvider.I::zincNugget, CreateRecipeProvider.I::zinc, CreateRecipeProvider.I::zincBlock));
    BaseRecipeProvider.GeneratedRecipe ROSE_QUARTZ_CYCLE = this.conversionCycle((List<ItemProviderEntry<? extends ItemLike, ? extends ItemLike>>)ImmutableList.of(AllBlocks.ROSE_QUARTZ_TILES, AllBlocks.SMALL_ROSE_QUARTZ_TILES));
    BaseRecipeProvider.GeneratedRecipe ANDESITE_ALLOY = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.ANDESITE_ALLOY).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::iron)).viaShaped(b -> b.define(Character.valueOf('A'), (ItemLike)Blocks.ANDESITE).define(Character.valueOf('B'), Tags.Items.NUGGETS_IRON).pattern("BA").pattern("AB"));
    BaseRecipeProvider.GeneratedRecipe ANDESITE_ALLOY_FROM_ZINC = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.ANDESITE_ALLOY).withSuffix("_from_zinc").unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::zinc)).viaShaped(b -> b.define(Character.valueOf('A'), (ItemLike)Blocks.ANDESITE).define(Character.valueOf('B'), CreateRecipeProvider.I.zincNugget()).pattern("BA").pattern("AB"));
    BaseRecipeProvider.GeneratedRecipe ELECTRON_TUBE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.ELECTRON_TUBE).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllItems.ROSE_QUARTZ.get())).viaShaped(b -> b.define(Character.valueOf('L'), (ItemLike)AllItems.POLISHED_ROSE_QUARTZ.get()).define(Character.valueOf('N'), CreateRecipeProvider.I.ironSheet()).pattern("L").pattern("N"));
    BaseRecipeProvider.GeneratedRecipe TRANSMITTER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.TRANSMITTER).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::copper)).viaShaped(b -> b.define(Character.valueOf('L'), CreateRecipeProvider.I.copperSheet()).define(Character.valueOf('N'), (ItemLike)Items.LIGHTNING_ROD).define(Character.valueOf('R'), CreateRecipeProvider.I.redstone()).pattern(" N ").pattern("LLL").pattern(" R "));
    BaseRecipeProvider.GeneratedRecipe ROSE_QUARTZ = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.ROSE_QUARTZ).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> Items.REDSTONE)).viaShapeless(b -> b.requires(Tags.Items.GEMS_QUARTZ).requires(Ingredient.of(CreateRecipeProvider.I.redstone()), 8));
    BaseRecipeProvider.GeneratedRecipe SAND_PAPER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.SAND_PAPER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> Items.PAPER)).viaShapeless(b -> b.requires((ItemLike)Items.PAPER).requires(Tags.Items.SANDS_COLORLESS));
    BaseRecipeProvider.GeneratedRecipe RED_SAND_PAPER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.RED_SAND_PAPER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> Items.PAPER)).viaShapeless(b -> b.requires((ItemLike)Items.PAPER).requires(Tags.Items.SANDS_RED));
    private Marker CURIOSITIES = this.enterFolder("curiosities");
    BaseRecipeProvider.GeneratedRecipe TOOLBOX = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.TOOLBOXES.get(DyeColor.BROWN)).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::goldSheet)).viaShaped(b -> b.define(Character.valueOf('S'), CreateRecipeProvider.I.goldSheet()).define(Character.valueOf('C'), CreateRecipeProvider.I.cog()).define(Character.valueOf('W'), Tags.Items.CHESTS_WOODEN).define(Character.valueOf('L'), Tags.Items.LEATHERS).pattern(" C ").pattern("SWS").pattern(" L "));
    BaseRecipeProvider.GeneratedRecipe TOOLBOX_DYEING = this.createSpecial(ToolboxDyeingRecipe::new, "crafting", "toolbox_dyeing");
    BaseRecipeProvider.GeneratedRecipe ITEM_COPYING = this.createSpecial(ItemCopyingRecipe::new, "crafting", "item_copying");
    BaseRecipeProvider.GeneratedRecipe MINECART_COUPLING = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.MINECART_COUPLING).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('E'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('O'), CreateRecipeProvider.I.ironSheet()).pattern("  E").pattern(" O ").pattern("E  "));
    BaseRecipeProvider.GeneratedRecipe PECULIAR_BELL = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.PECULIAR_BELL).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::brass)).viaShaped(b -> b.define(Character.valueOf('I'), CreateRecipeProvider.I.brassBlock()).define(Character.valueOf('P'), CreateRecipeProvider.I.brassSheet()).pattern("I").pattern("P"));
    BaseRecipeProvider.GeneratedRecipe CAKE = this.create((Supplier<ItemLike>)((Supplier)() -> Items.CAKE)).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)() -> Tags.Items.FOODS_DOUGH)).viaShaped(b -> b.define(Character.valueOf('E'), Tags.Items.EGGS).define(Character.valueOf('S'), (ItemLike)Items.SUGAR).define(Character.valueOf('P'), Tags.Items.FOODS_DOUGH).define(Character.valueOf('M'), () -> Items.MILK_BUCKET).pattern(" M ").pattern("SES").pattern(" P "));
    private Marker KINETICS = this.enterFolder("kinetics");
    BaseRecipeProvider.GeneratedRecipe BASIN = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.BASIN).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteAlloy()).pattern("A A").pattern("AAA"));
    BaseRecipeProvider.GeneratedRecipe GOGGLES = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.GOGGLES).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('G'), Tags.Items.GLASS_BLOCKS).define(Character.valueOf('P'), CreateRecipeProvider.I.goldSheet()).define(Character.valueOf('S'), Tags.Items.STRINGS).pattern(" S ").pattern("GPG"));
    BaseRecipeProvider.GeneratedRecipe WRENCH = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.WRENCH).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('G'), CreateRecipeProvider.I.goldSheet()).define(Character.valueOf('P'), CreateRecipeProvider.I.cog()).define(Character.valueOf('S'), Tags.Items.RODS_WOODEN).pattern("GG").pattern("GP").pattern(" S"));
    BaseRecipeProvider.GeneratedRecipe FILTER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.FILTER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('S'), ItemTags.WOOL).define(Character.valueOf('A'), Tags.Items.NUGGETS_IRON).pattern("ASA"));
    BaseRecipeProvider.GeneratedRecipe ATTRIBUTE_FILTER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.ATTRIBUTE_FILTER).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::brass)).viaShaped(b -> b.define(Character.valueOf('S'), ItemTags.WOOL).define(Character.valueOf('A'), CreateRecipeProvider.I.brassNugget()).pattern("ASA"));
    BaseRecipeProvider.GeneratedRecipe PACKAGE_FILTER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.PACKAGE_FILTER).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::zinc)).viaShaped(b -> b.define(Character.valueOf('S'), ItemTags.WOOL).define(Character.valueOf('A'), CreateRecipeProvider.I.zincNugget()).pattern("ASA"));
    BaseRecipeProvider.GeneratedRecipe BRASS_HAND = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.BRASS_HAND).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::brass)).viaShaped(b -> b.define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('B'), CreateRecipeProvider.I.brassSheet()).pattern(" A ").pattern("BBB").pattern(" B "));
    BaseRecipeProvider.GeneratedRecipe SUPER_GLUE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.SUPER_GLUE).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::ironSheet)).viaShaped(b -> b.define(Character.valueOf('A'), Tags.Items.SLIMEBALLS).define(Character.valueOf('S'), CreateRecipeProvider.I.ironSheet()).define(Character.valueOf('N'), Tags.Items.NUGGETS_IRON).pattern("AS").pattern("NA"));
    BaseRecipeProvider.GeneratedRecipe CRAFTER_SLOT_COVER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.CRAFTER_SLOT_COVER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllBlocks.MECHANICAL_CRAFTER.get())).viaShaped(b -> b.define(Character.valueOf('A'), CreateRecipeProvider.I.brassNugget()).pattern("AAA"));
    BaseRecipeProvider.GeneratedRecipe COGWHEEL = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.COGWHEEL).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.shaft()).requires(CreateRecipeProvider.I.planks()));
    BaseRecipeProvider.GeneratedRecipe LARGE_COGWHEEL = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.LARGE_COGWHEEL).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.shaft()).requires(CreateRecipeProvider.I.planks()).requires(CreateRecipeProvider.I.planks()));
    BaseRecipeProvider.GeneratedRecipe LARGE_COGWHEEL_FROM_LITTLE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.LARGE_COGWHEEL).withSuffix("_from_little").unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.cog()).requires(CreateRecipeProvider.I.planks()));
    BaseRecipeProvider.GeneratedRecipe WATER_WHEEL = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.WATER_WHEEL).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('S'), CreateRecipeProvider.I.planks()).define(Character.valueOf('C'), CreateRecipeProvider.I.shaft()).pattern("SSS").pattern("SCS").pattern("SSS"));
    BaseRecipeProvider.GeneratedRecipe LARGE_WATER_WHEEL = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.LARGE_WATER_WHEEL).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllBlocks.WATER_WHEEL.get())).viaShaped(b -> b.define(Character.valueOf('S'), CreateRecipeProvider.I.planks()).define(Character.valueOf('C'), (ItemLike)AllBlocks.WATER_WHEEL.get()).pattern("SSS").pattern("SCS").pattern("SSS"));
    BaseRecipeProvider.GeneratedRecipe SHAFT = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.SHAFT).returns(8).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteAlloy()).pattern("A").pattern("A"));
    BaseRecipeProvider.GeneratedRecipe MECHANICAL_PRESS = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.MECHANICAL_PRESS).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteCasing()).define(Character.valueOf('S'), CreateRecipeProvider.I.shaft()).define(Character.valueOf('I'), Tags.Items.STORAGE_BLOCKS_IRON).pattern("S").pattern("C").pattern("I"));
    BaseRecipeProvider.GeneratedRecipe MILLSTONE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.MILLSTONE).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.cog()).define(Character.valueOf('S'), CreateRecipeProvider.I.andesiteCasing()).define(Character.valueOf('I'), CreateRecipeProvider.I.stone()).pattern("C").pattern("S").pattern("I"));
    BaseRecipeProvider.GeneratedRecipe MECHANICAL_PISTON = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.MECHANICAL_PISTON).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShaped(b -> b.define(Character.valueOf('B'), ItemTags.WOODEN_SLABS).define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteCasing()).define(Character.valueOf('I'), (ItemLike)AllBlocks.PISTON_EXTENSION_POLE.get()).pattern("B").pattern("C").pattern("I"));
    BaseRecipeProvider.GeneratedRecipe STICKY_MECHANICAL_PISTON = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.STICKY_MECHANICAL_PISTON).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('S'), Tags.Items.SLIMEBALLS).define(Character.valueOf('P'), (ItemLike)AllBlocks.MECHANICAL_PISTON.get()).pattern("S").pattern("P"));
    BaseRecipeProvider.GeneratedRecipe TURNTABLE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.TURNTABLE).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('S'), CreateRecipeProvider.I.shaft()).define(Character.valueOf('P'), ItemTags.WOODEN_SLABS).pattern("P").pattern("S"));
    BaseRecipeProvider.GeneratedRecipe PISTON_EXTENSION_POLE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.PISTON_EXTENSION_POLE).returns(8).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('P'), ItemTags.PLANKS).pattern("P").pattern("A").pattern("P"));
    BaseRecipeProvider.GeneratedRecipe GANTRY_PINION = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.GANTRY_CARRIAGE).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShaped(b -> b.define(Character.valueOf('B'), ItemTags.WOODEN_SLABS).define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteCasing()).define(Character.valueOf('I'), CreateRecipeProvider.I.cog()).pattern("B").pattern("C").pattern("I"));
    BaseRecipeProvider.GeneratedRecipe GANTRY_SHAFT = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.GANTRY_SHAFT).returns(8).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('R'), CreateRecipeProvider.I.redstone()).pattern("A").pattern("R").pattern("A"));
    BaseRecipeProvider.GeneratedRecipe PLACARD = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.PLACARD).returns(1).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)() -> CreateRecipeProvider.I.brass())).viaShapeless(b -> b.requires((ItemLike)Items.ITEM_FRAME).requires(CreateRecipeProvider.I.brassSheet()));
    BaseRecipeProvider.GeneratedRecipe TRAIN_DOOR = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.TRAIN_DOOR).returns(1).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> CreateRecipeProvider.I.railwayCasing())).viaShapeless(b -> b.requires(ItemTags.WOODEN_DOORS).requires(CreateRecipeProvider.I.railwayCasing()));
    BaseRecipeProvider.GeneratedRecipe ANDESITE_DOOR = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.ANDESITE_DOOR).returns(1).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> CreateRecipeProvider.I.andesiteCasing())).viaShapeless(b -> b.requires(ItemTags.WOODEN_DOORS).requires(CreateRecipeProvider.I.andesiteCasing()));
    BaseRecipeProvider.GeneratedRecipe BRASS_DOOR = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.BRASS_DOOR).returns(1).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> CreateRecipeProvider.I.brassCasing())).viaShapeless(b -> b.requires(ItemTags.WOODEN_DOORS).requires(CreateRecipeProvider.I.brassCasing()));
    BaseRecipeProvider.GeneratedRecipe COPPER_DOOR = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.COPPER_DOOR).returns(1).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> CreateRecipeProvider.I.copperCasing())).viaShapeless(b -> b.requires(ItemTags.WOODEN_DOORS).requires(CreateRecipeProvider.I.copperCasing()));
    BaseRecipeProvider.GeneratedRecipe TRAIN_TRAPDOOR = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.TRAIN_TRAPDOOR).returns(1).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> CreateRecipeProvider.I.railwayCasing())).viaShapeless(b -> b.requires(ItemTags.WOODEN_TRAPDOORS).requires(CreateRecipeProvider.I.railwayCasing()));
    BaseRecipeProvider.GeneratedRecipe FRAMED_GLASS_DOOR = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.FRAMED_GLASS_DOOR).returns(1).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllPaletteBlocks.FRAMED_GLASS.get())).viaShapeless(b -> b.requires(ItemTags.WOODEN_DOORS).requires((ItemLike)AllPaletteBlocks.FRAMED_GLASS.get()));
    BaseRecipeProvider.GeneratedRecipe FRAMED_GLASS_TRAPDOOR = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.FRAMED_GLASS_TRAPDOOR).returns(1).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllPaletteBlocks.FRAMED_GLASS.get())).viaShapeless(b -> b.requires(ItemTags.WOODEN_TRAPDOORS).requires((ItemLike)AllPaletteBlocks.FRAMED_GLASS.get()));
    BaseRecipeProvider.GeneratedRecipe ANALOG_LEVER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.ANALOG_LEVER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('S'), CreateRecipeProvider.I.andesiteCasing()).define(Character.valueOf('P'), Tags.Items.RODS_WOODEN).pattern("P").pattern("S"));
    BaseRecipeProvider.GeneratedRecipe ROSE_QUARTZ_LAMP = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.ROSE_QUARTZ_LAMP).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::zinc)).viaShapeless(b -> b.requires((ItemLike)AllItems.POLISHED_ROSE_QUARTZ.get()).requires(CreateRecipeProvider.I.redstone()).requires(CreateRecipeProvider.I.zinc()));
    BaseRecipeProvider.GeneratedRecipe BELT_CONNECTOR = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.BELT_CONNECTOR).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('D'), (ItemLike)Items.DRIED_KELP).pattern("DDD").pattern("DDD"));
    BaseRecipeProvider.GeneratedRecipe ADJUSTABLE_PULLEY = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.ADJUSTABLE_CHAIN_GEARSHIFT).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::electronTube)).viaShapeless(b -> b.requires((ItemLike)AllBlocks.ENCASED_CHAIN_DRIVE.get()).requires(CreateRecipeProvider.I.electronTube()));
    BaseRecipeProvider.GeneratedRecipe CART_ASSEMBLER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.CART_ASSEMBLER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('L'), ItemTags.LOGS).define(Character.valueOf('R'), CreateRecipeProvider.I.redstone()).define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteAlloy()).pattern("CRC").pattern("L L"));
    BaseRecipeProvider.GeneratedRecipe CONTROLLER_RAIL = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.CONTROLLER_RAIL).returns(6).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> Items.POWERED_RAIL)).viaShaped(b -> b.define(Character.valueOf('A'), CreateRecipeProvider.I.gold()).define(Character.valueOf('E'), CreateRecipeProvider.I.electronTube()).define(Character.valueOf('S'), Tags.Items.RODS_WOODEN).pattern("A A").pattern("ASA").pattern("AEA"));
    BaseRecipeProvider.GeneratedRecipe HAND_CRANK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.HAND_CRANK).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('C'), ItemTags.PLANKS).pattern("CCC").pattern("  A"));
    BaseRecipeProvider.GeneratedRecipe COPPER_VALVE_HANDLE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.COPPER_VALVE_HANDLE).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::copper)).viaShaped(b -> b.define(Character.valueOf('S'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('C'), CreateRecipeProvider.I.copperSheet()).pattern("CCC").pattern(" S "));
    BaseRecipeProvider.GeneratedRecipe COPPER_VALVE_HANDLE_FROM_OTHER_HANDLES = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.COPPER_VALVE_HANDLE).withSuffix("_from_others").unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::copper)).viaShapeless(b -> b.requires(AllTags.AllItemTags.VALVE_HANDLES.tag));
    BaseRecipeProvider.GeneratedRecipe NOZZLE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.NOZZLE).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllBlocks.ENCASED_FAN.get())).viaShaped(b -> b.define(Character.valueOf('S'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('C'), ItemTags.WOOL).pattern(" S ").pattern(" C ").pattern("SSS"));
    BaseRecipeProvider.GeneratedRecipe PROPELLER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.PROPELLER).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::ironSheet)).viaShaped(b -> b.define(Character.valueOf('S'), CreateRecipeProvider.I.ironSheet()).define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteAlloy()).pattern(" S ").pattern("SCS").pattern(" S "));
    BaseRecipeProvider.GeneratedRecipe WHISK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.WHISK).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::ironSheet)).viaShaped(b -> b.define(Character.valueOf('S'), CreateRecipeProvider.I.ironSheet()).define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteAlloy()).pattern(" C ").pattern("SCS").pattern("SSS"));
    BaseRecipeProvider.GeneratedRecipe ENCASED_FAN = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.ENCASED_FAN).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::ironSheet)).viaShaped(b -> b.define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteCasing()).define(Character.valueOf('S'), CreateRecipeProvider.I.shaft()).define(Character.valueOf('P'), (ItemLike)AllItems.PROPELLER.get()).pattern("S").pattern("A").pattern("P"));
    BaseRecipeProvider.GeneratedRecipe CUCKOO_CLOCK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.CUCKOO_CLOCK).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('S'), ItemTags.PLANKS).define(Character.valueOf('A'), (ItemLike)Items.CLOCK).define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteCasing()).pattern("S").pattern("C").pattern("A"));
    BaseRecipeProvider.GeneratedRecipe MECHANICAL_CRAFTER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.MECHANICAL_CRAFTER).returns(3).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::brassCasing)).viaShaped(b -> b.define(Character.valueOf('B'), CreateRecipeProvider.I.electronTube()).define(Character.valueOf('R'), (ItemLike)Blocks.CRAFTING_TABLE).define(Character.valueOf('C'), CreateRecipeProvider.I.brassCasing()).pattern("B").pattern("C").pattern("R"));
    BaseRecipeProvider.GeneratedRecipe WINDMILL_BEARING = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.WINDMILL_BEARING).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('B'), ItemTags.WOODEN_SLABS).define(Character.valueOf('C'), CreateRecipeProvider.I.stone()).define(Character.valueOf('I'), CreateRecipeProvider.I.shaft()).pattern("B").pattern("C").pattern("I"));
    BaseRecipeProvider.GeneratedRecipe MECHANICAL_BEARING = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.MECHANICAL_BEARING).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShaped(b -> b.define(Character.valueOf('B'), ItemTags.WOODEN_SLABS).define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteCasing()).define(Character.valueOf('I'), CreateRecipeProvider.I.shaft()).pattern("B").pattern("C").pattern("I"));
    BaseRecipeProvider.GeneratedRecipe CLOCKWORK_BEARING = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.CLOCKWORK_BEARING).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::brassCasing)).viaShaped(b -> b.define(Character.valueOf('S'), CreateRecipeProvider.I.electronTube()).define(Character.valueOf('B'), CreateRecipeProvider.I.woodSlab()).define(Character.valueOf('C'), CreateRecipeProvider.I.brassCasing()).pattern("B").pattern("C").pattern("S"));
    BaseRecipeProvider.GeneratedRecipe WOODEN_BRACKET = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.WOODEN_BRACKET).returns(4).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('S'), Tags.Items.RODS_WOODEN).define(Character.valueOf('P'), CreateRecipeProvider.I.planks()).define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteAlloy()).pattern("SSS").pattern("PCP"));
    BaseRecipeProvider.GeneratedRecipe METAL_BRACKET = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.METAL_BRACKET).returns(4).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('S'), Tags.Items.NUGGETS_IRON).define(Character.valueOf('P'), CreateRecipeProvider.I.iron()).define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteAlloy()).pattern("SSS").pattern("PCP"));
    BaseRecipeProvider.GeneratedRecipe METAL_GIRDER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.METAL_GIRDER).returns(8).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('P'), CreateRecipeProvider.I.ironSheet()).define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteAlloy()).pattern("PPP").pattern("CCC"));
    BaseRecipeProvider.GeneratedRecipe DISPLAY_BOARD = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.DISPLAY_BOARD).returns(2).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('A'), CreateRecipeProvider.I.electronTube()).define(Character.valueOf('P'), CreateRecipeProvider.I.andesiteAlloy()).pattern("PAP"));
    BaseRecipeProvider.GeneratedRecipe STEAM_WHISTLE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.STEAM_WHISTLE).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::copper)).viaShaped(b -> b.define(Character.valueOf('P'), CreateRecipeProvider.I.goldSheet()).define(Character.valueOf('C'), CreateRecipeProvider.I.copper()).pattern("P").pattern("C"));
    BaseRecipeProvider.GeneratedRecipe STEAM_ENGINE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.STEAM_ENGINE).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::copper)).viaShaped(b -> b.define(Character.valueOf('P'), CreateRecipeProvider.I.goldSheet()).define(Character.valueOf('C'), CreateRecipeProvider.I.copperBlock()).define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteAlloy()).pattern("P").pattern("A").pattern("C"));
    BaseRecipeProvider.GeneratedRecipe FLUID_PIPE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.FLUID_PIPE).returns(4).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::copper)).viaShaped(b -> b.define(Character.valueOf('S'), CreateRecipeProvider.I.copperSheet()).define(Character.valueOf('C'), CreateRecipeProvider.I.copper()).pattern("SCS"));
    BaseRecipeProvider.GeneratedRecipe FLUID_PIPE_2 = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.FLUID_PIPE).withSuffix("_vertical").returns(4).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::copper)).viaShaped(b -> b.define(Character.valueOf('S'), CreateRecipeProvider.I.copperSheet()).define(Character.valueOf('C'), CreateRecipeProvider.I.copper()).pattern("S").pattern("C").pattern("S"));
    BaseRecipeProvider.GeneratedRecipe MECHANICAL_PUMP = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.MECHANICAL_PUMP).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::copper)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.cog()).requires((ItemLike)AllBlocks.FLUID_PIPE.get()));
    BaseRecipeProvider.GeneratedRecipe SMART_FLUID_PIPE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.SMART_FLUID_PIPE).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::copper)).viaShaped(b -> b.define(Character.valueOf('P'), CreateRecipeProvider.I.electronTube()).define(Character.valueOf('S'), (ItemLike)AllBlocks.FLUID_PIPE.get()).define(Character.valueOf('I'), CreateRecipeProvider.I.brassSheet()).pattern("I").pattern("S").pattern("P"));
    BaseRecipeProvider.GeneratedRecipe FLUID_VALVE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.FLUID_VALVE).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::copper)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.ironSheet()).requires((ItemLike)AllBlocks.FLUID_PIPE.get()));
    BaseRecipeProvider.GeneratedRecipe SPOUT = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.SPOUT).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::copperCasing)).viaShaped(b -> b.define(Character.valueOf('T'), CreateRecipeProvider.I.copperCasing()).define(Character.valueOf('P'), (ItemLike)Items.DRIED_KELP).pattern("T").pattern("P"));
    BaseRecipeProvider.GeneratedRecipe ITEM_DRAIN = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.ITEM_DRAIN).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::copperCasing)).viaShaped(b -> b.define(Character.valueOf('P'), (ItemLike)Blocks.IRON_BARS).define(Character.valueOf('S'), CreateRecipeProvider.I.copperCasing()).pattern("P").pattern("S"));
    BaseRecipeProvider.GeneratedRecipe FLUID_TANK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.FLUID_TANK).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)() -> Tags.Items.BARRELS_WOODEN)).viaShaped(b -> b.define(Character.valueOf('B'), CreateRecipeProvider.I.copperSheet()).define(Character.valueOf('C'), Tags.Items.BARRELS_WOODEN).pattern("B").pattern("C").pattern("B"));
    BaseRecipeProvider.GeneratedRecipe ITEM_VAULT = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.ITEM_VAULT).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)() -> Tags.Items.BARRELS_WOODEN)).viaShaped(b -> b.define(Character.valueOf('B'), CreateRecipeProvider.I.ironSheet()).define(Character.valueOf('C'), Tags.Items.BARRELS_WOODEN).pattern("B").pattern("C").pattern("B"));
    BaseRecipeProvider.GeneratedRecipe TRAIN_SIGNAL = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.TRACK_SIGNAL).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::railwayCasing)).returns(4).viaShapeless(b -> b.requires(CreateRecipeProvider.I.railwayCasing()).requires(CreateRecipeProvider.I.electronTube()));
    BaseRecipeProvider.GeneratedRecipe TRAIN_OBSERVER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.TRACK_OBSERVER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::railwayCasing)).returns(2).viaShapeless(b -> b.requires(CreateRecipeProvider.I.railwayCasing()).requires(ItemTags.WOODEN_PRESSURE_PLATES));
    BaseRecipeProvider.GeneratedRecipe TRAIN_OBSERVER_2 = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.TRACK_OBSERVER).withSuffix("_from_other_plates").unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::railwayCasing)).returns(2).viaShapeless(b -> b.requires(CreateRecipeProvider.I.railwayCasing()).requires(Ingredient.of((ItemLike[])new ItemLike[]{Items.STONE_PRESSURE_PLATE, Items.POLISHED_BLACKSTONE_PRESSURE_PLATE, Items.HEAVY_WEIGHTED_PRESSURE_PLATE, Items.LIGHT_WEIGHTED_PRESSURE_PLATE})));
    BaseRecipeProvider.GeneratedRecipe TRAIN_SCHEDULE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.SCHEDULE).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::sturdySheet)).returns(4).viaShapeless(b -> b.requires(CreateRecipeProvider.I.sturdySheet()).requires((ItemLike)Items.PAPER));
    BaseRecipeProvider.GeneratedRecipe TRAIN_STATION = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.TRACK_STATION).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::railwayCasing)).returns(2).viaShapeless(b -> b.requires(CreateRecipeProvider.I.railwayCasing()).requires((ItemLike)Items.COMPASS));
    BaseRecipeProvider.GeneratedRecipe TRAIN_CONTROLS = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.TRAIN_CONTROLS).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::railwayCasing)).viaShaped(b -> b.define(Character.valueOf('I'), CreateRecipeProvider.I.precisionMechanism()).define(Character.valueOf('B'), (ItemLike)Items.LEVER).define(Character.valueOf('C'), CreateRecipeProvider.I.railwayCasing()).pattern("B").pattern("C").pattern("I"));
    BaseRecipeProvider.GeneratedRecipe DEPLOYER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.DEPLOYER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::electronTube)).viaShaped(b -> b.define(Character.valueOf('I'), (ItemLike)AllItems.BRASS_HAND.get()).define(Character.valueOf('B'), CreateRecipeProvider.I.electronTube()).define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteCasing()).pattern("B").pattern("C").pattern("I"));
    BaseRecipeProvider.GeneratedRecipe PORTABLE_STORAGE_INTERFACE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.PORTABLE_STORAGE_INTERFACE).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.andesiteCasing()).requires((ItemLike)AllBlocks.CHUTE.get()));
    BaseRecipeProvider.GeneratedRecipe PORTABLE_FLUID_INTERFACE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.PORTABLE_FLUID_INTERFACE).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::copperCasing)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.copperCasing()).requires((ItemLike)AllBlocks.CHUTE.get()));
    BaseRecipeProvider.GeneratedRecipe ROPE_PULLEY = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.ROPE_PULLEY).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('B'), CreateRecipeProvider.I.andesiteCasing()).define(Character.valueOf('C'), ItemTags.WOOL).define(Character.valueOf('I'), CreateRecipeProvider.I.ironSheet()).pattern("B").pattern("C").pattern("I"));
    BaseRecipeProvider.GeneratedRecipe HOSE_PULLEY = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.HOSE_PULLEY).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::copper)).viaShaped(b -> b.define(Character.valueOf('B'), CreateRecipeProvider.I.copperCasing()).define(Character.valueOf('C'), (ItemLike)Items.DRIED_KELP_BLOCK).define(Character.valueOf('I'), CreateRecipeProvider.I.copperSheet()).pattern("B").pattern("C").pattern("I"));
    BaseRecipeProvider.GeneratedRecipe ELEVATOR_PULLEY = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.ELEVATOR_PULLEY).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::brass)).viaShaped(b -> b.define(Character.valueOf('B'), CreateRecipeProvider.I.brassCasing()).define(Character.valueOf('C'), (ItemLike)Items.DRIED_KELP_BLOCK).define(Character.valueOf('I'), CreateRecipeProvider.I.ironSheet()).pattern("B").pattern("C").pattern("I"));
    BaseRecipeProvider.GeneratedRecipe CONTRAPTION_CONTROLS = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.CONTRAPTION_CONTROLS).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('B'), ItemTags.BUTTONS).define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteCasing()).define(Character.valueOf('I'), CreateRecipeProvider.I.electronTube()).pattern("B").pattern("C").pattern("I"));
    BaseRecipeProvider.GeneratedRecipe EMPTY_BLAZE_BURNER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.EMPTY_BLAZE_BURNER).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::iron)).viaShaped(b -> b.define(Character.valueOf('A'), Tags.Items.NETHERRACKS).define(Character.valueOf('I'), CreateRecipeProvider.I.ironSheet()).pattern(" I ").pattern("IAI").pattern(" I "));
    BaseRecipeProvider.GeneratedRecipe CHUTE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.CHUTE).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).returns(4).viaShaped(b -> b.define(Character.valueOf('A'), CreateRecipeProvider.I.ironSheet()).define(Character.valueOf('I'), CreateRecipeProvider.I.iron()).pattern("A").pattern("I").pattern("A"));
    BaseRecipeProvider.GeneratedRecipe SMART_CHUTE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.SMART_CHUTE).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllBlocks.CHUTE.get())).viaShaped(b -> b.define(Character.valueOf('P'), CreateRecipeProvider.I.electronTube()).define(Character.valueOf('S'), (ItemLike)AllBlocks.CHUTE.get()).define(Character.valueOf('I'), CreateRecipeProvider.I.brassSheet()).pattern("I").pattern("S").pattern("P"));
    BaseRecipeProvider.GeneratedRecipe DEPOT = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.DEPOT).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.andesiteAlloy()).requires(CreateRecipeProvider.I.andesiteCasing()));
    BaseRecipeProvider.GeneratedRecipe WEIGHTED_EJECTOR = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.WEIGHTED_EJECTOR).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShaped(b -> b.define(Character.valueOf('A'), CreateRecipeProvider.I.goldSheet()).define(Character.valueOf('D'), (ItemLike)AllBlocks.DEPOT.get()).define(Character.valueOf('I'), CreateRecipeProvider.I.cog()).pattern("A").pattern("D").pattern("I"));
    BaseRecipeProvider.GeneratedRecipe MECHANICAL_ARM = this.create((Supplier<ItemLike>)((Supplier)() -> AllBlocks.MECHANICAL_ARM.get())).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::brassCasing)).returns(1).viaShaped(b -> b.define(Character.valueOf('L'), CreateRecipeProvider.I.brassSheet()).define(Character.valueOf('I'), CreateRecipeProvider.I.precisionMechanism()).define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('C'), CreateRecipeProvider.I.brassCasing()).pattern("LLA").pattern("L  ").pattern("IC "));
    BaseRecipeProvider.GeneratedRecipe MECHANICAL_MIXER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.MECHANICAL_MIXER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteCasing()).define(Character.valueOf('S'), CreateRecipeProvider.I.cog()).define(Character.valueOf('I'), (ItemLike)AllItems.WHISK.get()).pattern("S").pattern("C").pattern("I"));
    BaseRecipeProvider.GeneratedRecipe CLUTCH = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.CLUTCH).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.andesiteCasing()).requires(CreateRecipeProvider.I.shaft()).requires(CreateRecipeProvider.I.redstone()));
    BaseRecipeProvider.GeneratedRecipe GEARSHIFT = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.GEARSHIFT).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.andesiteCasing()).requires(CreateRecipeProvider.I.cog()).requires(CreateRecipeProvider.I.redstone()));
    BaseRecipeProvider.GeneratedRecipe SAIL = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.SAIL).returns(2).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('W'), ItemTags.WOOL).define(Character.valueOf('S'), Tags.Items.RODS_WOODEN).define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteAlloy()).pattern("WS").pattern("SA"));
    BaseRecipeProvider.GeneratedRecipe SAIL_CYCLE = this.conversionCycle((List<ItemProviderEntry<? extends ItemLike, ? extends ItemLike>>)ImmutableList.of(AllBlocks.SAIL_FRAME, AllBlocks.SAIL));
    BaseRecipeProvider.GeneratedRecipe RADIAL_CHASIS = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.RADIAL_CHASSIS).returns(3).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('P'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('L'), ItemTags.LOGS).pattern(" L ").pattern("PLP").pattern(" L "));
    BaseRecipeProvider.GeneratedRecipe LINEAR_CHASIS = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.LINEAR_CHASSIS).returns(3).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('P'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('L'), ItemTags.LOGS).pattern(" P ").pattern("LLL").pattern(" P "));
    BaseRecipeProvider.GeneratedRecipe LINEAR_CHASSIS_CYCLE = this.conversionCycle((List<ItemProviderEntry<? extends ItemLike, ? extends ItemLike>>)ImmutableList.of(AllBlocks.LINEAR_CHASSIS, AllBlocks.SECONDARY_LINEAR_CHASSIS));
    BaseRecipeProvider.GeneratedRecipe STICKER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.STICKER).returns(1).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('I'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('C'), Tags.Items.COBBLESTONES).define(Character.valueOf('R'), CreateRecipeProvider.I.redstone()).define(Character.valueOf('S'), Tags.Items.SLIMEBALLS).pattern("ISI").pattern("CRC"));
    BaseRecipeProvider.GeneratedRecipe MINECART = this.create((Supplier<ItemLike>)((Supplier)() -> Items.MINECART)).withSuffix("_from_contraption_cart").unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllBlocks.CART_ASSEMBLER.get())).viaShapeless(b -> b.requires((ItemLike)AllItems.MINECART_CONTRAPTION.get()));
    BaseRecipeProvider.GeneratedRecipe FURNACE_MINECART = this.create((Supplier<ItemLike>)((Supplier)() -> Items.FURNACE_MINECART)).withSuffix("_from_contraption_cart").unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllBlocks.CART_ASSEMBLER.get())).viaShapeless(b -> b.requires((ItemLike)AllItems.FURNACE_MINECART_CONTRAPTION.get()));
    BaseRecipeProvider.GeneratedRecipe GEARBOX = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.GEARBOX).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cog)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.cog()).define(Character.valueOf('B'), CreateRecipeProvider.I.andesiteCasing()).pattern(" C ").pattern("CBC").pattern(" C "));
    BaseRecipeProvider.GeneratedRecipe VERTICAL_GEARBOX = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.VERTICAL_GEARBOX).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cog)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.cog()).define(Character.valueOf('B'), CreateRecipeProvider.I.andesiteCasing()).pattern("C C").pattern(" B ").pattern("C C"));
    BaseRecipeProvider.GeneratedRecipe GEARBOX_CYCLE = this.conversionCycle((List<ItemProviderEntry<? extends ItemLike, ? extends ItemLike>>)ImmutableList.of(AllBlocks.GEARBOX, AllItems.VERTICAL_GEARBOX));
    BaseRecipeProvider.GeneratedRecipe MYSTERIOUS_CUCKOO_CLOCK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.MYSTERIOUS_CUCKOO_CLOCK).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllBlocks.CUCKOO_CLOCK.get())).viaShaped(b -> b.define(Character.valueOf('C'), Tags.Items.GUNPOWDERS).define(Character.valueOf('B'), (ItemLike)AllBlocks.CUCKOO_CLOCK.get()).pattern(" C ").pattern("CBC").pattern(" C "));
    BaseRecipeProvider.GeneratedRecipe ENCASED_CHAIN_DRIVE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.ENCASED_CHAIN_DRIVE).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.andesiteCasing()).requires(CreateRecipeProvider.I.ironNugget()).requires(CreateRecipeProvider.I.ironNugget()).requires(CreateRecipeProvider.I.ironNugget()));
    BaseRecipeProvider.GeneratedRecipe ENCASED_CHAIN_DRIVE_ZINC = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.ENCASED_CHAIN_DRIVE).withSuffix("_from_zinc").unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.andesiteCasing()).requires(CreateRecipeProvider.I.zincNugget()).requires(CreateRecipeProvider.I.zincNugget()).requires(CreateRecipeProvider.I.zincNugget()));
    BaseRecipeProvider.GeneratedRecipe FLYWHEEL = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.FLYWHEEL).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::brass)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.brass()).define(Character.valueOf('A'), CreateRecipeProvider.I.shaft()).pattern("CCC").pattern("CAC").pattern("CCC"));
    BaseRecipeProvider.GeneratedRecipe SPEEDOMETER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.SPEEDOMETER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('C'), (ItemLike)Items.COMPASS).define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteCasing()).pattern("C").pattern("A"));
    BaseRecipeProvider.GeneratedRecipe GAUGE_CYCLE = this.conversionCycle((List<ItemProviderEntry<? extends ItemLike, ? extends ItemLike>>)ImmutableList.of(AllBlocks.SPEEDOMETER, AllBlocks.STRESSOMETER));
    BaseRecipeProvider.GeneratedRecipe ROTATION_SPEED_CONTROLLER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.ROTATION_SPEED_CONTROLLER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::brassCasing)).viaShaped(b -> b.define(Character.valueOf('B'), CreateRecipeProvider.I.precisionMechanism()).define(Character.valueOf('C'), CreateRecipeProvider.I.brassCasing()).pattern("B").pattern("C"));
    BaseRecipeProvider.GeneratedRecipe NIXIE_TUBE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.ORANGE_NIXIE_TUBE).returns(4).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::brassCasing)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.electronTube()).requires(CreateRecipeProvider.I.electronTube()));
    BaseRecipeProvider.GeneratedRecipe MECHANICAL_SAW = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.MECHANICAL_SAW).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteCasing()).define(Character.valueOf('A'), CreateRecipeProvider.I.ironSheet()).define(Character.valueOf('I'), CreateRecipeProvider.I.iron()).pattern(" A ").pattern("AIA").pattern(" C "));
    BaseRecipeProvider.GeneratedRecipe MECHANICAL_HARVESTER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.MECHANICAL_HARVESTER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteCasing()).define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('I'), CreateRecipeProvider.I.ironSheet()).pattern("AIA").pattern("AIA").pattern(" C "));
    BaseRecipeProvider.GeneratedRecipe MECHANICAL_PLOUGH = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.MECHANICAL_PLOUGH).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteCasing()).define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('I'), CreateRecipeProvider.I.ironSheet()).pattern("III").pattern("AAA").pattern(" C "));
    BaseRecipeProvider.GeneratedRecipe MECHANICAL_ROLLER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.MECHANICAL_ROLLER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteCasing()).define(Character.valueOf('A'), CreateRecipeProvider.I.electronTube()).define(Character.valueOf('I'), (ItemLike)AllBlocks.CRUSHING_WHEEL.get()).pattern("A").pattern("C").pattern("I"));
    BaseRecipeProvider.GeneratedRecipe MECHANICAL_DRILL = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.MECHANICAL_DRILL).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteCasing()).define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('I'), CreateRecipeProvider.I.iron()).pattern(" A ").pattern("AIA").pattern(" C "));
    BaseRecipeProvider.GeneratedRecipe CHAIN_CONVEYOR = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.CHAIN_CONVEYOR).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).returns(2).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteCasing()).define(Character.valueOf('A'), CreateRecipeProvider.I.largeCog()).pattern(" C ").pattern("CAC").pattern(" C "));
    BaseRecipeProvider.GeneratedRecipe SEQUENCED_GEARSHIFT = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.SEQUENCED_GEARSHIFT).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::brassCasing)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.brassCasing()).requires(CreateRecipeProvider.I.cog()).requires(CreateRecipeProvider.I.electronTube()));
    private Marker LOGISTICS = this.enterFolder("logistics");
    BaseRecipeProvider.GeneratedRecipe REDSTONE_CONTACT = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.REDSTONE_CONTACT).returns(2).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::brassCasing)).viaShaped(b -> b.define(Character.valueOf('W'), CreateRecipeProvider.I.redstone()).define(Character.valueOf('C'), (ItemLike)Blocks.COBBLESTONE).define(Character.valueOf('S'), CreateRecipeProvider.I.ironSheet()).pattern(" S ").pattern("CWC").pattern("CCC"));
    BaseRecipeProvider.GeneratedRecipe ANDESITE_FUNNEL = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.ANDESITE_FUNNEL).returns(2).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('K'), (ItemLike)Items.DRIED_KELP).pattern("A").pattern("K"));
    BaseRecipeProvider.GeneratedRecipe BRASS_FUNNEL = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.BRASS_FUNNEL).returns(2).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::brass)).viaShaped(b -> b.define(Character.valueOf('A'), CreateRecipeProvider.I.brass()).define(Character.valueOf('K'), (ItemLike)Items.DRIED_KELP).define(Character.valueOf('E'), CreateRecipeProvider.I.electronTube()).pattern("E").pattern("A").pattern("K"));
    BaseRecipeProvider.GeneratedRecipe ANDESITE_TUNNEL = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.ANDESITE_TUNNEL).returns(2).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('K'), (ItemLike)Items.DRIED_KELP).pattern("AA").pattern("KK"));
    BaseRecipeProvider.GeneratedRecipe BRASS_TUNNEL = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.BRASS_TUNNEL).returns(2).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::brass)).viaShaped(b -> b.define(Character.valueOf('A'), CreateRecipeProvider.I.brass()).define(Character.valueOf('K'), (ItemLike)Items.DRIED_KELP).define(Character.valueOf('E'), CreateRecipeProvider.I.electronTube()).pattern("E ").pattern("AA").pattern("KK"));
    BaseRecipeProvider.GeneratedRecipe SMART_OBSERVER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.SMART_OBSERVER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::brassCasing)).viaShaped(b -> b.define(Character.valueOf('B'), CreateRecipeProvider.I.brassCasing()).define(Character.valueOf('R'), CreateRecipeProvider.I.electronTube()).define(Character.valueOf('I'), (ItemLike)Blocks.OBSERVER).pattern("R").pattern("B").pattern("I"));
    BaseRecipeProvider.GeneratedRecipe THRESHOLD_SWITCH = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.THRESHOLD_SWITCH).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::brassCasing)).viaShaped(b -> b.define(Character.valueOf('B'), CreateRecipeProvider.I.brassCasing()).define(Character.valueOf('R'), CreateRecipeProvider.I.electronTube()).define(Character.valueOf('I'), (ItemLike)Blocks.COMPARATOR).pattern("R").pattern("B").pattern("I"));
    BaseRecipeProvider.GeneratedRecipe PULSE_EXTENDER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.PULSE_EXTENDER).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::redstone)).viaShaped(b -> b.define(Character.valueOf('T'), (ItemLike)Blocks.REDSTONE_TORCH).define(Character.valueOf('C'), CreateRecipeProvider.I.brassSheet()).define(Character.valueOf('R'), CreateRecipeProvider.I.redstone()).define(Character.valueOf('S'), CreateRecipeProvider.I.stone()).pattern("  T").pattern("RCT").pattern("SSS"));
    BaseRecipeProvider.GeneratedRecipe PULSE_REPEATER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.PULSE_REPEATER).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::redstone)).viaShaped(b -> b.define(Character.valueOf('T'), (ItemLike)Blocks.REDSTONE_TORCH).define(Character.valueOf('C'), CreateRecipeProvider.I.brassSheet()).define(Character.valueOf('R'), CreateRecipeProvider.I.redstone()).define(Character.valueOf('S'), CreateRecipeProvider.I.stone()).pattern("RCT").pattern("SSS"));
    BaseRecipeProvider.GeneratedRecipe PULSE_TIMER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.PULSE_TIMER).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::redstone)).viaShaped(b -> b.define(Character.valueOf('T'), (ItemLike)Blocks.REDSTONE_TORCH).define(Character.valueOf('C'), CreateRecipeProvider.I.brassSheet()).define(Character.valueOf('R'), (ItemLike)Items.AMETHYST_SHARD).define(Character.valueOf('S'), CreateRecipeProvider.I.stone()).pattern("RCT").pattern("SSS"));
    BaseRecipeProvider.GeneratedRecipe POWERED_TOGGLE_LATCH = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.POWERED_TOGGLE_LATCH).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::redstone)).viaShaped(b -> b.define(Character.valueOf('T'), (ItemLike)Blocks.REDSTONE_TORCH).define(Character.valueOf('C'), (ItemLike)Blocks.LEVER).define(Character.valueOf('S'), CreateRecipeProvider.I.stone()).pattern(" T ").pattern(" C ").pattern("SSS"));
    BaseRecipeProvider.GeneratedRecipe POWERED_LATCH = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.POWERED_LATCH).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::redstone)).viaShaped(b -> b.define(Character.valueOf('T'), (ItemLike)Blocks.REDSTONE_TORCH).define(Character.valueOf('C'), (ItemLike)Blocks.LEVER).define(Character.valueOf('R'), CreateRecipeProvider.I.redstone()).define(Character.valueOf('S'), CreateRecipeProvider.I.stone()).pattern(" T ").pattern("RCR").pattern("SSS"));
    BaseRecipeProvider.GeneratedRecipe REDSTONE_LINK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.REDSTONE_LINK).returns(2).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShaped(b -> b.define(Character.valueOf('C'), AllItems.TRANSMITTER).define(Character.valueOf('S'), CreateRecipeProvider.I.andesiteCasing()).pattern("C").pattern("S"));
    BaseRecipeProvider.GeneratedRecipe ITEM_HATCH = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.ITEM_HATCH).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.andesiteAlloy()).requires((ItemLike)Items.IRON_TRAPDOOR));
    BaseRecipeProvider.GeneratedRecipe PACKAGER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.PACKAGER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cardboard)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.iron()).define(Character.valueOf('A'), AllBlocks.CARDBOARD_BLOCK).define(Character.valueOf('R'), CreateRecipeProvider.I.redstone()).pattern(" C ").pattern("CAC").pattern("RCR"));
    BaseRecipeProvider.GeneratedRecipe PACKAGER_CYCLE = this.conversionCycle((List<ItemProviderEntry<? extends ItemLike, ? extends ItemLike>>)ImmutableList.of(AllBlocks.PACKAGER, AllBlocks.REPACKAGER));
    BaseRecipeProvider.GeneratedRecipe PACKAGE_FROGPORT = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.PACKAGE_FROGPORT).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cardboard)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('B'), Tags.Items.SLIMEBALLS).define(Character.valueOf('A'), CreateRecipeProvider.I.vault()).pattern("B").pattern("A").pattern("C"));
    BaseRecipeProvider.GeneratedRecipe STOCK_LINK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.STOCK_LINK).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cardboard)).viaShaped(b -> b.define(Character.valueOf('C'), (ItemLike)AllItems.TRANSMITTER.get()).define(Character.valueOf('B'), CreateRecipeProvider.I.vault()).pattern("C").pattern("B"));
    BaseRecipeProvider.GeneratedRecipe STOCK_TICKER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.STOCK_TICKER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cardboard)).viaShaped(b -> b.define(Character.valueOf('C'), Tags.Items.GLASS_BLOCKS).define(Character.valueOf('B'), CreateRecipeProvider.I.gold()).define(Character.valueOf('A'), CreateRecipeProvider.I.stockLink()).pattern("C").pattern("A").pattern("B"));
    BaseRecipeProvider.GeneratedRecipe REDSTONE_REQUESTER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.REDSTONE_REQUESTER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cardboard)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.redstone()).define(Character.valueOf('B'), CreateRecipeProvider.I.iron()).define(Character.valueOf('A'), CreateRecipeProvider.I.stockLink()).pattern("C").pattern("A").pattern("B"));
    BaseRecipeProvider.GeneratedRecipe FACTORY_GAUGE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.FACTORY_GAUGE).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::stockLink)).returns(2).viaShapeless(b -> b.requires(CreateRecipeProvider.I.stockLink()).requires(CreateRecipeProvider.I.precisionMechanism()));
    BaseRecipeProvider.GeneratedRecipe DESK_BELL = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.DESK_BELL).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteCasing)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.andesiteCasing()).requires(CreateRecipeProvider.I.goldSheet()));
    BaseRecipeProvider.GeneratedRecipe LOGISTICS_LINK_CLEAR = this.clearData((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.STOCK_LINK);
    BaseRecipeProvider.GeneratedRecipe STOCK_TICKER_CLEAR = this.clearData((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.STOCK_TICKER);
    BaseRecipeProvider.GeneratedRecipe REDSTONE_REQUESTER_CLEAR = this.clearData((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.REDSTONE_REQUESTER);
    BaseRecipeProvider.GeneratedRecipe FACTORY_PANEL_CLEAR = this.clearData((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.FACTORY_GAUGE);
    BaseRecipeProvider.GeneratedRecipe DISPLAY_LINK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.DISPLAY_LINK).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::brassCasing)).viaShaped(b -> b.define(Character.valueOf('C'), (ItemLike)AllItems.TRANSMITTER.get()).define(Character.valueOf('S'), CreateRecipeProvider.I.brassCasing()).pattern("C").pattern("S"));
    private Marker SCHEMATICS = this.enterFolder("schematics");
    BaseRecipeProvider.GeneratedRecipe SCHEMATIC_TABLE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.SCHEMATIC_TABLE).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllItems.EMPTY_SCHEMATIC.get())).viaShaped(b -> b.define(Character.valueOf('W'), ItemTags.WOODEN_SLABS).define(Character.valueOf('S'), (ItemLike)Blocks.SMOOTH_STONE).pattern("WWW").pattern(" S ").pattern(" S "));
    BaseRecipeProvider.GeneratedRecipe SCHEMATICANNON = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.SCHEMATICANNON).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllItems.EMPTY_SCHEMATIC.get())).viaShaped(b -> b.define(Character.valueOf('L'), ItemTags.LOGS).define(Character.valueOf('D'), (ItemLike)Blocks.DISPENSER).define(Character.valueOf('S'), (ItemLike)Blocks.SMOOTH_STONE).define(Character.valueOf('I'), (ItemLike)Blocks.IRON_BLOCK).pattern(" I ").pattern("LIL").pattern("SDS"));
    BaseRecipeProvider.GeneratedRecipe EMPTY_SCHEMATIC = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.EMPTY_SCHEMATIC).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> Items.PAPER)).viaShapeless(b -> b.requires((ItemLike)Items.PAPER).requires(Tags.Items.DYES_LIGHT_BLUE));
    BaseRecipeProvider.GeneratedRecipe SCHEMATIC_AND_QUILL = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.SCHEMATIC_AND_QUILL).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> Items.PAPER)).viaShapeless(b -> b.requires((ItemLike)AllItems.EMPTY_SCHEMATIC.get()).requires(Tags.Items.FEATHERS));
    private Marker PALETTES = this.enterFolder("palettes");
    BaseRecipeProvider.GeneratedRecipe SCORCHIA = this.create((Supplier<ItemLike>)((Supplier)() -> AllPaletteStoneTypes.SCORCHIA.getBaseBlock().get())).returns(8).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllPaletteStoneTypes.SCORIA.getBaseBlock().get())).viaShaped(b -> b.define(Character.valueOf('#'), (ItemLike)AllPaletteStoneTypes.SCORIA.getBaseBlock().get()).define(Character.valueOf('D'), Tags.Items.DYES_BLACK).pattern("###").pattern("#D#").pattern("###"));
    private Marker APPLIANCES = this.enterFolder("appliances");
    BaseRecipeProvider.GeneratedRecipe DOUGH = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.DOUGH).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::wheatFlour)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.wheatFlour()).requires((ItemLike)Items.WATER_BUCKET));
    BaseRecipeProvider.GeneratedRecipe CHAIN_FROM_ZINC = this.create((Supplier<ItemLike>)((Supplier)() -> Items.CHAIN)).withSuffix("_from_zinc").unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::zinc)).viaShaped(b -> b.define(Character.valueOf('C'), CreateRecipeProvider.I.zinc()).define(Character.valueOf('S'), CreateRecipeProvider.I.zincNugget()).pattern("S").pattern("C").pattern("S"));
    BaseRecipeProvider.GeneratedRecipe CLIPBOARD = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.CLIPBOARD).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::andesiteAlloy)).viaShaped(b -> b.define(Character.valueOf('G'), CreateRecipeProvider.I.planks()).define(Character.valueOf('P'), (ItemLike)Items.PAPER).define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteAlloy()).pattern("A").pattern("P").pattern("G"));
    BaseRecipeProvider.GeneratedRecipe CLIPBOARD_CLEAR = this.clearData((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllBlocks.CLIPBOARD);
    BaseRecipeProvider.GeneratedRecipe SCHEDULE_CLEAR = this.clearData((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.SCHEDULE);
    BaseRecipeProvider.GeneratedRecipe FILTER_CLEAR = this.clearData((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.FILTER);
    BaseRecipeProvider.GeneratedRecipe ATTRIBUTE_FILTER_CLEAR = this.clearData((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.ATTRIBUTE_FILTER);
    BaseRecipeProvider.GeneratedRecipe PACKAGE_FILTER_CLEAR = this.clearData((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.PACKAGE_FILTER);
    BaseRecipeProvider.GeneratedRecipe CARDBOARD_SWORD = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.CARDBOARD_SWORD).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cardboard)).viaShaped(b -> b.define(Character.valueOf('P'), CreateRecipeProvider.I.cardboard()).define(Character.valueOf('S'), Tags.Items.RODS_WOODEN).pattern("P").pattern("P").pattern("S"));
    BaseRecipeProvider.GeneratedRecipe CARDBOARD_HELMET = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.CARDBOARD_HELMET).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cardboard)).viaShaped(b -> b.define(Character.valueOf('P'), CreateRecipeProvider.I.cardboard()).pattern("PPP").pattern("P P"));
    BaseRecipeProvider.GeneratedRecipe CARDBOARD_CHESTPLATE = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.CARDBOARD_CHESTPLATE).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cardboard)).viaShaped(b -> b.define(Character.valueOf('P'), CreateRecipeProvider.I.cardboard()).pattern("P P").pattern("PPP").pattern("PPP"));
    BaseRecipeProvider.GeneratedRecipe CARDBOARD_LEGGINGS = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.CARDBOARD_LEGGINGS).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cardboard)).viaShaped(b -> b.define(Character.valueOf('P'), CreateRecipeProvider.I.cardboard()).pattern("PPP").pattern("P P").pattern("P P"));
    BaseRecipeProvider.GeneratedRecipe CARDBOARD_BOOTS = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.CARDBOARD_BOOTS).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cardboard)).viaShaped(b -> b.define(Character.valueOf('P'), CreateRecipeProvider.I.cardboard()).pattern("P P").pattern("P P"));
    BaseRecipeProvider.GeneratedRecipe DIVING_HELMET = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.COPPER_DIVING_HELMET).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::copper)).viaShaped(b -> b.define(Character.valueOf('G'), Tags.Items.GLASS_BLOCKS).define(Character.valueOf('P'), CreateRecipeProvider.I.copper()).pattern("PPP").pattern("PGP"));
    BaseRecipeProvider.GeneratedRecipe COPPER_BACKTANK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.COPPER_BACKTANK).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::copper)).viaShaped(b -> b.define(Character.valueOf('G'), CreateRecipeProvider.I.shaft()).define(Character.valueOf('A'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('B'), CreateRecipeProvider.I.copperBlock()).define(Character.valueOf('P'), CreateRecipeProvider.I.copper()).pattern("AGA").pattern("PBP").pattern(" P "));
    BaseRecipeProvider.GeneratedRecipe DIVING_BOOTS = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.COPPER_DIVING_BOOTS).unlockedByTag((Supplier<TagKey<Item>>)((Supplier)CreateRecipeProvider.I::copper)).viaShaped(b -> b.define(Character.valueOf('G'), CreateRecipeProvider.I.andesiteAlloy()).define(Character.valueOf('P'), CreateRecipeProvider.I.copper()).pattern("P P").pattern("P P").pattern("G G"));
    BaseRecipeProvider.GeneratedRecipe LINKED_CONTROLLER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.LINKED_CONTROLLER).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllBlocks.REDSTONE_LINK.get())).viaShaped(b -> b.define(Character.valueOf('S'), ItemTags.WOODEN_BUTTONS).define(Character.valueOf('P'), (ItemLike)AllBlocks.REDSTONE_LINK.get()).pattern("SSS").pattern(" P ").pattern("SSS"));
    BaseRecipeProvider.GeneratedRecipe CRAFTING_BLUEPRINT = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.CRAFTING_BLUEPRINT).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> Items.CRAFTING_TABLE)).viaShapeless(b -> b.requires((ItemLike)Items.PAINTING).requires((ItemLike)Items.CRAFTING_TABLE));
    BaseRecipeProvider.GeneratedRecipe SLIME_BALL = this.create((Supplier<ItemLike>)((Supplier)() -> Items.SLIME_BALL)).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> AllItems.DOUGH.get())).viaShapeless(b -> b.requires((ItemLike)AllItems.DOUGH.get()).requires(Tags.Items.DYES_LIME));
    BaseRecipeProvider.GeneratedRecipe BOOK = this.create((Supplier<ItemLike>)((Supplier)() -> Items.BOOK)).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cardboard)).viaShapeless(b -> b.requires(CreateRecipeProvider.I.cardboard()).requires((ItemLike)Items.PAPER).requires((ItemLike)Items.PAPER).requires((ItemLike)Items.PAPER));
    BaseRecipeProvider.GeneratedRecipe NAME_TAG = this.create((Supplier<ItemLike>)((Supplier)() -> Items.NAME_TAG)).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cardboard)).viaShapeless(b -> b.requires(Tags.Items.DYES_BLACK).requires(Tags.Items.STRINGS).requires(CreateRecipeProvider.I.cardboard()));
    BaseRecipeProvider.GeneratedRecipe ITEM_FRAME = this.create((Supplier<ItemLike>)((Supplier)() -> Items.ITEM_FRAME)).unlockedBy((Supplier<? extends ItemLike>)((Supplier)CreateRecipeProvider.I::cardboard)).viaShaped(b -> b.define(Character.valueOf('S'), Tags.Items.RODS_WOODEN).define(Character.valueOf('P'), CreateRecipeProvider.I.cardboard()).pattern("SSS").pattern("SPS").pattern("SSS"));
    BaseRecipeProvider.GeneratedRecipe TREE_FERTILIZER = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.TREE_FERTILIZER).returns(2).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> Items.BONE_MEAL)).viaShapeless(b -> b.requires(Ingredient.of((TagKey)ItemTags.SMALL_FLOWERS), 2).requires(Ingredient.of((ItemLike[])new ItemLike[]{Items.HORN_CORAL, Items.BRAIN_CORAL, Items.TUBE_CORAL, Items.BUBBLE_CORAL, Items.FIRE_CORAL})).requires((ItemLike)Items.BONE_MEAL));
    BaseRecipeProvider.GeneratedRecipe NETHERITE_DIVING_HELMET = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.NETHERITE_DIVING_HELMET).viaNetheriteSmithing((Supplier<? extends Item>)((Supplier)() -> AllItems.COPPER_DIVING_HELMET.get()), (Supplier<Ingredient>)((Supplier)CreateRecipeProvider.I::netherite));
    BaseRecipeProvider.GeneratedRecipe NETHERITE_BACKTANK = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.NETHERITE_BACKTANK).viaNetheriteSmithing((Supplier<? extends Item>)((Supplier)() -> AllItems.COPPER_BACKTANK.get()), (Supplier<Ingredient>)((Supplier)CreateRecipeProvider.I::netherite));
    BaseRecipeProvider.GeneratedRecipe NETHERITE_DIVING_BOOTS = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.NETHERITE_DIVING_BOOTS).viaNetheriteSmithing((Supplier<? extends Item>)((Supplier)() -> AllItems.COPPER_DIVING_BOOTS.get()), (Supplier<Ingredient>)((Supplier)CreateRecipeProvider.I::netherite));
    BaseRecipeProvider.GeneratedRecipe NETHERITE_DIVING_HELMET_2 = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.NETHERITE_DIVING_HELMET).withSuffix("_from_netherite").viaNetheriteSmithing((Supplier<? extends Item>)((Supplier)() -> Items.NETHERITE_HELMET), (Supplier<Ingredient>)((Supplier)() -> Ingredient.of((ItemLike[])new ItemLike[]{(ItemLike)AllItems.COPPER_DIVING_HELMET.get()})));
    BaseRecipeProvider.GeneratedRecipe NETHERITE_BACKTANK_2 = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.NETHERITE_BACKTANK).withSuffix("_from_netherite").viaNetheriteSmithing((Supplier<? extends Item>)((Supplier)() -> Items.NETHERITE_CHESTPLATE), (Supplier<Ingredient>)((Supplier)() -> Ingredient.of((ItemLike[])new ItemLike[]{(ItemLike)AllItems.COPPER_BACKTANK.get()})));
    BaseRecipeProvider.GeneratedRecipe NETHERITE_DIVING_BOOTS_2 = this.create((ItemProviderEntry<? extends ItemLike, ? extends ItemLike>)AllItems.NETHERITE_DIVING_BOOTS).withSuffix("_from_netherite").viaNetheriteSmithing((Supplier<? extends Item>)((Supplier)() -> Items.NETHERITE_BOOTS), (Supplier<Ingredient>)((Supplier)() -> Ingredient.of((ItemLike[])new ItemLike[]{(ItemLike)AllItems.COPPER_DIVING_BOOTS.get()})));
    private Marker COOKING = this.enterFolder("/");
    BaseRecipeProvider.GeneratedRecipe DOUGH_TO_BREAD = this.create((Supplier<ItemLike>)((Supplier)() -> Items.BREAD)).viaCooking((Supplier<? extends ItemLike>)((Supplier)() -> AllItems.DOUGH.get())).inSmoker();
    BaseRecipeProvider.GeneratedRecipe SOUL_SAND = this.create((Supplier<ItemLike>)((Supplier)() -> AllPaletteStoneTypes.SCORIA.getBaseBlock().get())).viaCooking((Supplier<? extends ItemLike>)((Supplier)() -> Blocks.SOUL_SAND)).inFurnace();
    BaseRecipeProvider.GeneratedRecipe FRAMED_GLASS = this.recycleGlass(AllPaletteBlocks.FRAMED_GLASS);
    BaseRecipeProvider.GeneratedRecipe TILED_GLASS = this.recycleGlass(AllPaletteBlocks.TILED_GLASS);
    BaseRecipeProvider.GeneratedRecipe VERTICAL_FRAMED_GLASS = this.recycleGlass(AllPaletteBlocks.VERTICAL_FRAMED_GLASS);
    BaseRecipeProvider.GeneratedRecipe HORIZONTAL_FRAMED_GLASS = this.recycleGlass(AllPaletteBlocks.HORIZONTAL_FRAMED_GLASS);
    BaseRecipeProvider.GeneratedRecipe FRAMED_GLASS_PANE = this.recycleGlassPane(AllPaletteBlocks.FRAMED_GLASS_PANE);
    BaseRecipeProvider.GeneratedRecipe TILED_GLASS_PANE = this.recycleGlassPane(AllPaletteBlocks.TILED_GLASS_PANE);
    BaseRecipeProvider.GeneratedRecipe VERTICAL_FRAMED_GLASS_PANE = this.recycleGlassPane(AllPaletteBlocks.VERTICAL_FRAMED_GLASS_PANE);
    BaseRecipeProvider.GeneratedRecipe HORIZONTAL_FRAMED_GLASS_PANE = this.recycleGlassPane(AllPaletteBlocks.HORIZONTAL_FRAMED_GLASS_PANE);
    BaseRecipeProvider.GeneratedRecipe CRUSHED_IRON = this.blastCrushedMetal((Supplier<? extends ItemLike>)((Supplier)() -> Items.IRON_INGOT), (Supplier<? extends ItemLike>)((Supplier)() -> AllItems.CRUSHED_IRON.get()));
    BaseRecipeProvider.GeneratedRecipe CRUSHED_GOLD = this.blastCrushedMetal((Supplier<? extends ItemLike>)((Supplier)() -> Items.GOLD_INGOT), (Supplier<? extends ItemLike>)((Supplier)() -> AllItems.CRUSHED_GOLD.get()));
    BaseRecipeProvider.GeneratedRecipe CRUSHED_COPPER = this.blastCrushedMetal((Supplier<? extends ItemLike>)((Supplier)() -> Items.COPPER_INGOT), (Supplier<? extends ItemLike>)((Supplier)() -> AllItems.CRUSHED_COPPER.get()));
    BaseRecipeProvider.GeneratedRecipe CRUSHED_ZINC = this.blastCrushedMetal((Supplier<? extends ItemLike>)((Supplier)() -> AllItems.ZINC_INGOT.get()), (Supplier<? extends ItemLike>)((Supplier)() -> AllItems.CRUSHED_ZINC.get()));
    BaseRecipeProvider.GeneratedRecipe CRUSHED_OSMIUM = this.blastModdedCrushedMetal(AllItems.CRUSHED_OSMIUM, CommonMetal.OSMIUM);
    BaseRecipeProvider.GeneratedRecipe CRUSHED_PLATINUM = this.blastModdedCrushedMetal(AllItems.CRUSHED_PLATINUM, CommonMetal.PLATINUM);
    BaseRecipeProvider.GeneratedRecipe CRUSHED_SILVER = this.blastModdedCrushedMetal(AllItems.CRUSHED_SILVER, CommonMetal.SILVER);
    BaseRecipeProvider.GeneratedRecipe CRUSHED_TIN = this.blastModdedCrushedMetal(AllItems.CRUSHED_TIN, CommonMetal.TIN);
    BaseRecipeProvider.GeneratedRecipe CRUSHED_LEAD = this.blastModdedCrushedMetal(AllItems.CRUSHED_LEAD, CommonMetal.LEAD);
    BaseRecipeProvider.GeneratedRecipe CRUSHED_QUICKSILVER = this.blastModdedCrushedMetal(AllItems.CRUSHED_QUICKSILVER, CommonMetal.QUICKSILVER);
    BaseRecipeProvider.GeneratedRecipe CRUSHED_BAUXITE = this.blastModdedCrushedMetal(AllItems.CRUSHED_BAUXITE, CommonMetal.ALUMINUM);
    BaseRecipeProvider.GeneratedRecipe CRUSHED_URANIUM = this.blastModdedCrushedMetal(AllItems.CRUSHED_URANIUM, CommonMetal.URANIUM);
    BaseRecipeProvider.GeneratedRecipe CRUSHED_NICKEL = this.blastModdedCrushedMetal(AllItems.CRUSHED_NICKEL, CommonMetal.NICKEL);
    BaseRecipeProvider.GeneratedRecipe ZINC_ORE = this.create((Supplier<ItemLike>)((Supplier)() -> AllItems.ZINC_INGOT.get())).withSuffix("_from_ore").viaCookingTag((Supplier<TagKey<Item>>)((Supplier)() -> CommonMetal.ZINC.ores.items())).rewardXP(1.0f).inBlastFurnace();
    BaseRecipeProvider.GeneratedRecipe RAW_ZINC_ORE = this.create((Supplier<ItemLike>)((Supplier)() -> AllItems.ZINC_INGOT.get())).withSuffix("_from_raw_ore").viaCookingTag((Supplier<TagKey<Item>>)((Supplier)() -> CommonMetal.ZINC.rawOres)).rewardXP(0.7f).inBlastFurnace();
    BaseRecipeProvider.GeneratedRecipe UA_TREE_FERTILIZER = this.create((Supplier<ItemLike>)((Supplier)() -> AllItems.TREE_FERTILIZER.get())).returns(2).unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> Items.BONE_MEAL)).whenModLoaded(Mods.UA.getId()).viaShapeless(b -> b.requires(Ingredient.of((TagKey)ItemTags.SMALL_FLOWERS), 2).requires(AllTags.AllItemTags.UA_CORAL.tag).requires((ItemLike)Items.BONE_MEAL));
    String currentFolder = "";

    Marker enterFolder(String folder) {
        this.currentFolder = folder;
        return new Marker();
    }

    GeneratedRecipeBuilder create(Supplier<ItemLike> result) {
        return new GeneratedRecipeBuilder(this.currentFolder, result);
    }

    GeneratedRecipeBuilder create(ResourceLocation result) {
        return new GeneratedRecipeBuilder(this.currentFolder, result);
    }

    GeneratedRecipeBuilder create(ItemProviderEntry<? extends ItemLike, ? extends ItemLike> result) {
        return this.create((Supplier<ItemLike>)((Supplier)() -> result.get()));
    }

    BaseRecipeProvider.GeneratedRecipe createSpecial(Function<CraftingBookCategory, Recipe<?>> builder, String recipeType, String path) {
        ResourceLocation location = Create.asResource(recipeType + "/" + this.currentFolder + "/" + path);
        return this.register(consumer -> {
            SpecialRecipeBuilder b = SpecialRecipeBuilder.special((Function)builder);
            b.save(consumer, location.toString());
        });
    }

    BaseRecipeProvider.GeneratedRecipe blastCrushedMetal(Supplier<? extends ItemLike> result, Supplier<? extends ItemLike> ingredient) {
        return this.create((Supplier<ItemLike>)((Supplier)() -> result.get())).withSuffix("_from_crushed").viaCooking(ingredient).rewardXP(0.1f).inBlastFurnace();
    }

    BaseRecipeProvider.GeneratedRecipe blastModdedCrushedMetal(ItemEntry<? extends Item> ingredient, CommonMetal metal) {
        for (Mods mod : metal.mods) {
            String metalName = metal.getName(mod);
            ResourceLocation ingot = mod.ingotOf(metalName);
            String modId = mod.getId();
            this.create(ingot).withSuffix("_compat_" + modId).whenModLoaded(modId).viaCooking((Supplier<? extends ItemLike>)((Supplier)() -> ingredient.get())).rewardXP(0.1f).inBlastFurnace();
        }
        return null;
    }

    BaseRecipeProvider.GeneratedRecipe recycleGlass(BlockEntry<? extends Block> ingredient) {
        return this.create((Supplier<ItemLike>)((Supplier)() -> Blocks.GLASS)).withSuffix("_from_" + ingredient.getId().getPath()).viaCooking((Supplier<? extends ItemLike>)((Supplier)() -> ingredient.get())).forDuration(50).inFurnace();
    }

    BaseRecipeProvider.GeneratedRecipe recycleGlassPane(BlockEntry<? extends Block> ingredient) {
        return this.create((Supplier<ItemLike>)((Supplier)() -> Blocks.GLASS_PANE)).withSuffix("_from_" + ingredient.getId().getPath()).viaCooking((Supplier<? extends ItemLike>)((Supplier)() -> ingredient.get())).forDuration(50).inFurnace();
    }

    BaseRecipeProvider.GeneratedRecipe metalCompacting(List<ItemProviderEntry<? extends ItemLike, ? extends ItemLike>> variants, List<Supplier<TagKey<Item>>> ingredients) {
        BaseRecipeProvider.GeneratedRecipe result = null;
        int i = 0;
        while (i + 1 < variants.size()) {
            ItemProviderEntry<? extends ItemLike, ? extends ItemLike> currentEntry = variants.get(i);
            ItemProviderEntry<? extends ItemLike, ? extends ItemLike> nextEntry = variants.get(i + 1);
            Supplier<TagKey<Item>> currentIngredient = ingredients.get(i);
            Supplier<TagKey<Item>> nextIngredient = ingredients.get(i + 1);
            result = this.create(nextEntry).withSuffix("_from_compacting").unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> currentEntry.get())).viaShaped(b -> b.pattern("###").pattern("###").pattern("###").define(Character.valueOf('#'), (TagKey)currentIngredient.get()));
            result = this.create(currentEntry).returns(9).withSuffix("_from_decompacting").unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> nextEntry.get())).viaShapeless(b -> b.requires((TagKey)nextIngredient.get()));
            ++i;
        }
        return result;
    }

    BaseRecipeProvider.GeneratedRecipe conversionCycle(List<ItemProviderEntry<? extends ItemLike, ? extends ItemLike>> cycle) {
        BaseRecipeProvider.GeneratedRecipe result = null;
        for (int i = 0; i < cycle.size(); ++i) {
            ItemProviderEntry<? extends ItemLike, ? extends ItemLike> currentEntry = cycle.get(i);
            ItemProviderEntry<? extends ItemLike, ? extends ItemLike> nextEntry = cycle.get((i + 1) % cycle.size());
            result = this.create(nextEntry).withSuffix("_from_conversion").unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> currentEntry.get())).viaShapeless(b -> b.requires((ItemLike)currentEntry.get()));
        }
        return result;
    }

    BaseRecipeProvider.GeneratedRecipe clearData(ItemProviderEntry<? extends ItemLike, ? extends ItemLike> item) {
        return this.create(item).withSuffix("_clear").unlockedBy((Supplier<? extends ItemLike>)((Supplier)() -> item.get())).viaShapeless(b -> b.requires((ItemLike)item.get()));
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        this.all.forEach(c -> c.register(output));
        Create.LOGGER.info("{} registered {} recipe{}", new Object[]{this.getName(), this.all.size(), this.all.size() == 1 ? "" : "s"});
    }

    @Override
    protected BaseRecipeProvider.GeneratedRecipe register(BaseRecipeProvider.GeneratedRecipe recipe) {
        this.all.add(recipe);
        return recipe;
    }

    public String getName() {
        return "Create's Standard Recipes";
    }

    public CreateStandardRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "create");
    }

    static class Marker {
        Marker() {
        }
    }

    class GeneratedRecipeBuilder {
        private String path;
        private String suffix;
        private Supplier<? extends ItemLike> result;
        private ResourceLocation compatDatagenOutput;
        List<ICondition> recipeConditions;
        private Supplier<ItemPredicate> unlockedBy;
        private int amount;

        private GeneratedRecipeBuilder(String path) {
            this.path = path;
            this.recipeConditions = new ArrayList<ICondition>();
            this.suffix = "";
            this.amount = 1;
        }

        public GeneratedRecipeBuilder(String path, Supplier<? extends ItemLike> result) {
            this(path);
            this.result = result;
        }

        public GeneratedRecipeBuilder(String path, ResourceLocation result) {
            this(path);
            this.compatDatagenOutput = result;
        }

        GeneratedRecipeBuilder returns(int amount) {
            this.amount = amount;
            return this;
        }

        GeneratedRecipeBuilder unlockedBy(Supplier<? extends ItemLike> item) {
            this.unlockedBy = () -> ItemPredicate.Builder.item().of(new ItemLike[]{(ItemLike)item.get()}).build();
            return this;
        }

        GeneratedRecipeBuilder unlockedByTag(Supplier<TagKey<Item>> tag) {
            this.unlockedBy = () -> ItemPredicate.Builder.item().of((TagKey)tag.get()).build();
            return this;
        }

        GeneratedRecipeBuilder whenModLoaded(String modid) {
            return this.withCondition((ICondition)new ModLoadedCondition(modid));
        }

        GeneratedRecipeBuilder whenModMissing(String modid) {
            return this.withCondition((ICondition)new NotCondition((ICondition)new ModLoadedCondition(modid)));
        }

        GeneratedRecipeBuilder withCondition(ICondition condition) {
            this.recipeConditions.add(condition);
            return this;
        }

        GeneratedRecipeBuilder withSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        BaseRecipeProvider.GeneratedRecipe viaShaped(UnaryOperator<ShapedRecipeBuilder> builder) {
            return CreateStandardRecipeGen.this.register(consumer -> {
                ShapedRecipeBuilder b = (ShapedRecipeBuilder)builder.apply(ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)this.result.get()), (int)this.amount));
                if (this.unlockedBy != null) {
                    b.unlockedBy("has_item", CreateStandardRecipeGen.inventoryTrigger((ItemPredicate[])new ItemPredicate[]{(ItemPredicate)this.unlockedBy.get()}));
                }
                b.save(consumer, this.createLocation("crafting"));
            });
        }

        BaseRecipeProvider.GeneratedRecipe viaShapeless(UnaryOperator<ShapelessRecipeBuilder> builder) {
            return CreateStandardRecipeGen.this.register(recipeOutput -> {
                ShapelessRecipeBuilder b = (ShapelessRecipeBuilder)builder.apply(ShapelessRecipeBuilder.shapeless((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)this.result.get()), (int)this.amount));
                if (this.unlockedBy != null) {
                    b.unlockedBy("has_item", CreateStandardRecipeGen.inventoryTrigger((ItemPredicate[])new ItemPredicate[]{(ItemPredicate)this.unlockedBy.get()}));
                }
                RecipeOutput conditionalOutput = recipeOutput.withConditions(this.recipeConditions.toArray(new ICondition[0]));
                b.save(conditionalOutput, this.createLocation("crafting"));
            });
        }

        BaseRecipeProvider.GeneratedRecipe viaNetheriteSmithing(Supplier<? extends Item> base, Supplier<Ingredient> upgradeMaterial) {
            return CreateStandardRecipeGen.this.register(consumer -> {
                SmithingTransformRecipeBuilder b = SmithingTransformRecipeBuilder.smithing((Ingredient)Ingredient.of((ItemLike[])new ItemLike[]{Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE}), (Ingredient)Ingredient.of((ItemLike[])new ItemLike[]{(ItemLike)base.get()}), (Ingredient)((Ingredient)upgradeMaterial.get()), (RecipeCategory)RecipeCategory.COMBAT, (Item)((ItemLike)this.result.get()).asItem());
                b.unlocks("has_item", CreateStandardRecipeGen.inventoryTrigger((ItemPredicate[])new ItemPredicate[]{ItemPredicate.Builder.item().of(new ItemLike[]{(ItemLike)base.get()}).build()}));
                b.save(consumer, this.createLocation("crafting"));
            });
        }

        private ResourceLocation createSimpleLocation(String recipeType) {
            return Create.asResource(recipeType + "/" + this.getRegistryName().getPath() + this.suffix);
        }

        private ResourceLocation createLocation(String recipeType) {
            return Create.asResource(recipeType + "/" + this.path + "/" + this.getRegistryName().getPath() + this.suffix);
        }

        private ResourceLocation getRegistryName() {
            return this.compatDatagenOutput == null ? RegisteredObjectsHelper.getKeyOrThrow((Item)((ItemLike)this.result.get()).asItem()) : this.compatDatagenOutput;
        }

        GeneratedCookingRecipeBuilder viaCooking(Supplier<? extends ItemLike> item) {
            return this.unlockedBy(item).viaCookingIngredient((Supplier<Ingredient>)((Supplier)() -> Ingredient.of((ItemLike[])new ItemLike[]{(ItemLike)item.get()})));
        }

        GeneratedCookingRecipeBuilder viaCookingTag(Supplier<TagKey<Item>> tag) {
            return this.unlockedByTag(tag).viaCookingIngredient((Supplier<Ingredient>)((Supplier)() -> Ingredient.of((TagKey)((TagKey)tag.get()))));
        }

        GeneratedCookingRecipeBuilder viaCookingIngredient(Supplier<Ingredient> ingredient) {
            return new GeneratedCookingRecipeBuilder(ingredient);
        }

        class GeneratedCookingRecipeBuilder {
            private Supplier<Ingredient> ingredient;
            private float exp;
            private int cookingTime;

            GeneratedCookingRecipeBuilder(Supplier<Ingredient> ingredient) {
                this.ingredient = ingredient;
                this.cookingTime = 200;
                this.exp = 0.0f;
            }

            GeneratedCookingRecipeBuilder forDuration(int duration) {
                this.cookingTime = duration;
                return this;
            }

            GeneratedCookingRecipeBuilder rewardXP(float xp) {
                this.exp = xp;
                return this;
            }

            BaseRecipeProvider.GeneratedRecipe inFurnace() {
                return this.inFurnace(b -> b);
            }

            BaseRecipeProvider.GeneratedRecipe inFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                return this.create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1.0f);
            }

            BaseRecipeProvider.GeneratedRecipe inSmoker() {
                return this.inSmoker(b -> b);
            }

            BaseRecipeProvider.GeneratedRecipe inSmoker(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                this.create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1.0f);
                this.create(RecipeSerializer.CAMPFIRE_COOKING_RECIPE, builder, CampfireCookingRecipe::new, 3.0f);
                return this.create(RecipeSerializer.SMOKING_RECIPE, builder, SmokingRecipe::new, 0.5f);
            }

            BaseRecipeProvider.GeneratedRecipe inBlastFurnace() {
                return this.inBlastFurnace(b -> b);
            }

            BaseRecipeProvider.GeneratedRecipe inBlastFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                this.create(RecipeSerializer.SMELTING_RECIPE, builder, SmeltingRecipe::new, 1.0f);
                return this.create(RecipeSerializer.BLASTING_RECIPE, builder, BlastingRecipe::new, 0.5f);
            }

            private <T extends AbstractCookingRecipe> BaseRecipeProvider.GeneratedRecipe create(RecipeSerializer<T> serializer, UnaryOperator<SimpleCookingRecipeBuilder> builder, AbstractCookingRecipe.Factory<T> factory, float cookingTimeModifier) {
                return CreateStandardRecipeGen.this.register(recipeOutput -> {
                    boolean isOtherMod = GeneratedRecipeBuilder.this.compatDatagenOutput != null;
                    SimpleCookingRecipeBuilder b = (SimpleCookingRecipeBuilder)builder.apply(SimpleCookingRecipeBuilder.generic((Ingredient)((Ingredient)this.ingredient.get()), (RecipeCategory)RecipeCategory.MISC, (ItemLike)(isOtherMod ? Items.DIRT : (ItemLike)GeneratedRecipeBuilder.this.result.get()), (float)this.exp, (int)((int)((float)this.cookingTime * cookingTimeModifier)), (RecipeSerializer)serializer, (AbstractCookingRecipe.Factory)factory));
                    if (GeneratedRecipeBuilder.this.unlockedBy != null) {
                        b.unlockedBy("has_item", CreateStandardRecipeGen.inventoryTrigger((ItemPredicate[])new ItemPredicate[]{(ItemPredicate)GeneratedRecipeBuilder.this.unlockedBy.get()}));
                    }
                    RecipeOutput conditionalOutput = recipeOutput.withConditions(GeneratedRecipeBuilder.this.recipeConditions.toArray(new ICondition[0]));
                    b.save((RecipeOutput)(isOtherMod ? new ModdedCookingRecipeOutput(conditionalOutput, GeneratedRecipeBuilder.this.compatDatagenOutput) : conditionalOutput), GeneratedRecipeBuilder.this.createSimpleLocation(RegisteredObjectsHelper.getKeyOrThrow((RecipeSerializer)serializer).getPath()));
                });
            }
        }
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private record ModdedCookingRecipeOutput(RecipeOutput wrapped, ResourceLocation outputOverride) implements RecipeOutput
    {
        public Advancement.Builder advancement() {
            return this.wrapped.advancement();
        }

        public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition ... conditions) {
            this.wrapped.accept(id, (Recipe)new ModdedCookingRecipeOutputShim(recipe, this.outputOverride), advancement, conditions);
        }
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private static class ModdedCookingRecipeOutputShim
    implements Recipe<RecipeInput> {
        private static final Map<RecipeType<?>, Serializer> serializers = new ConcurrentHashMap();
        private final Recipe<?> wrapped;
        private final ResourceLocation overrideID;

        private ModdedCookingRecipeOutputShim(Recipe<?> wrapped, ResourceLocation overrideID) {
            this.wrapped = wrapped;
            this.overrideID = overrideID;
        }

        public boolean matches(RecipeInput recipeInput, Level level) {
            throw new AssertionError((Object)"Only for datagen output");
        }

        public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
            throw new AssertionError((Object)"Only for datagen output");
        }

        public boolean canCraftInDimensions(int pWidth, int pHeight) {
            throw new AssertionError((Object)"Only for datagen output");
        }

        public ItemStack getResultItem(HolderLookup.Provider registries) {
            throw new AssertionError((Object)"Only for datagen output");
        }

        public RecipeSerializer<?> getSerializer() {
            return serializers.computeIfAbsent(this.getType(), t -> Serializer.create(this.wrapped));
        }

        public RecipeType<?> getType() {
            return this.wrapped.getType();
        }

        private record Serializer(MapCodec<Recipe<?>> wrappedCodec) implements RecipeSerializer<ModdedCookingRecipeOutputShim>
        {
            private static Serializer create(Recipe<?> wrapped) {
                MappedRegistryAccessor mra;
                RecipeSerializer wrappedSerializer = wrapped.getSerializer();
                Serializer serializer = new Serializer(wrappedSerializer.codec());
                Registry registry = BuiltInRegistries.RECIPE_SERIALIZER;
                if (!(registry instanceof MappedRegistryAccessor)) {
                    throw new AssertionError((Object)("ModdedCookingRecipeOutputShim will not be able to serialize without injecting into a registry. Expected BuiltInRegistries.RECIPE_SERIALIZER to be of class MappedRegistry, is of class " + String.valueOf(BuiltInRegistries.RECIPE_SERIALIZER.getClass())));
                }
                MappedRegistryAccessor mra$ = mra = (MappedRegistryAccessor)registry;
                int wrappedId = mra$.getToId().getOrDefault((Object)wrappedSerializer, -1);
                ResourceKey wrappedKey = mra$.getByValue().get(wrappedSerializer).key();
                mra$.getToId().put((Object)serializer, wrappedId);
                mra$.getByValue().put(serializer, Holder.Reference.createStandAlone(null, (ResourceKey)wrappedKey));
                return serializer;
            }

            public MapCodec<ModdedCookingRecipeOutputShim> codec() {
                return RecordCodecBuilder.mapCodec(instance -> instance.group((App)this.wrappedCodec.forGetter(i -> i.wrapped), (App)FakeItemStack.CODEC.fieldOf("result").forGetter(i -> new FakeItemStack(i.overrideID))).apply((Applicative)instance, (wrappedRecipe, fakeItemStack) -> {
                    throw new AssertionError((Object)"Only for datagen output");
                }));
            }

            public StreamCodec<RegistryFriendlyByteBuf, ModdedCookingRecipeOutputShim> streamCodec() {
                throw new AssertionError((Object)"Only for datagen output");
            }
        }

        private record FakeItemStack(ResourceLocation id) {
            public static Codec<FakeItemStack> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ResourceLocation.CODEC.fieldOf("id").forGetter(FakeItemStack::id)).apply((Applicative)instance, FakeItemStack::new));
        }
    }
}
