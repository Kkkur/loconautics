/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.api.behaviour.display.DisplaySource
 *  com.simibubi.create.content.decoration.encasing.EncasingRegistry
 *  com.simibubi.create.foundation.block.DyedBlockList
 *  com.simibubi.create.foundation.block.connected.SimpleCTBehaviour
 *  com.simibubi.create.foundation.data.AssetLookup
 *  com.simibubi.create.foundation.data.BlockStateGen
 *  com.simibubi.create.foundation.data.CreateRegistrate
 *  com.simibubi.create.foundation.data.ModelGen
 *  com.simibubi.create.foundation.data.SharedProperties
 *  com.simibubi.create.foundation.data.TagGen
 *  com.simibubi.create.foundation.utility.DyeHelper
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.providers.RegistrateLangProvider
 *  com.tterrag.registrate.providers.RegistrateRecipeProvider
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  com.tterrag.registrate.util.nullness.NonNullFunction
 *  com.tterrag.registrate.util.nullness.NonNullUnaryOperator
 *  dev.ryanhcode.sable.index.SableTags
 *  dev.simulated_team.simulated.api.sound.SimSoundEntry
 *  dev.simulated_team.simulated.data.SimBlockStateGen
 *  dev.simulated_team.simulated.index.SimItems
 *  dev.simulated_team.simulated.index.sounds.SimLazySoundType
 *  dev.simulated_team.simulated.registrate.SimulatedRegistrate
 *  dev.simulated_team.simulated.registrate.simulated_tab.CreativeTabItemTransforms$VisibilityType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.data.recipes.ShapedRecipeBuilder
 *  net.minecraft.data.recipes.ShapelessRecipeBuilder
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.FireBlock
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.storage.loot.LootPool
 *  net.minecraft.world.level.storage.loot.LootPool$Builder
 *  net.minecraft.world.level.storage.loot.entries.LootItem
 *  net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer$Builder
 *  net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder
 *  net.minecraft.world.level.storage.loot.providers.number.ConstantValue
 *  net.minecraft.world.level.storage.loot.providers.number.NumberProvider
 *  net.neoforged.neoforge.client.model.generators.BlockModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ConfiguredModel
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package dev.eriksonn.aeronautics.index;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.foundation.block.DyedBlockList;
import com.simibubi.create.foundation.block.connected.SimpleCTBehaviour;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.ModelGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.simibubi.create.foundation.utility.DyeHelper;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.config.server.AeroStress;
import dev.eriksonn.aeronautics.content.blocks.hot_air.envelope.EnvelopeBlock;
import dev.eriksonn.aeronautics.content.blocks.hot_air.envelope.EnvelopeEncasedShaftBlock;
import dev.eriksonn.aeronautics.content.blocks.hot_air.hot_air_burner.HotAirBurnerBlock;
import dev.eriksonn.aeronautics.content.blocks.hot_air.steam_vent.SteamVentBlock;
import dev.eriksonn.aeronautics.content.blocks.mounted_potato_cannon.MountedPotatoCannonBlock;
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.gyroscopic_propeller_bearing.GyroscopicPropellerBearingBlock;
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing.PropellerBearingBlock;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.andesite.AndesitePropellerBlock;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.smart_propeller.SmartPropellerBlock;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.wooden.WoodenPropellerBlock;
import dev.eriksonn.aeronautics.content.components.Levitating;
import dev.eriksonn.aeronautics.data.AeroBlockStateGen;
import dev.eriksonn.aeronautics.index.AeroDataComponents;
import dev.eriksonn.aeronautics.index.AeroDisplaySources;
import dev.eriksonn.aeronautics.index.AeroSoundEvents;
import dev.eriksonn.aeronautics.index.AeroSpriteShift;
import dev.eriksonn.aeronautics.index.AeroTags;
import dev.ryanhcode.sable.index.SableTags;
import dev.simulated_team.simulated.api.sound.SimSoundEntry;
import dev.simulated_team.simulated.data.SimBlockStateGen;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.index.sounds.SimLazySoundType;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import dev.simulated_team.simulated.registrate.simulated_tab.CreativeTabItemTransforms;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class AeroBlocks {
    private static final SimulatedRegistrate REGISTRATE = Aeronautics.getRegistrate();
    public static final BlockEntry<EnvelopeBlock> WHITE_ENVELOPE_BLOCK = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("white_envelope", p -> new EnvelopeBlock((BlockBehaviour.Properties)p, DyeColor.WHITE)).lang("Hot Air Envelope").initialProperties(SharedProperties::wooden).properties(p -> p.isValidSpawn(AeroBlocks::neverSpawn)).properties(p -> p.sound((SoundType)new SimLazySoundType(1.0f, 1.0f, () -> ((SimSoundEntry)AeroSoundEvents.ENVELOPE_BREAK).event(), () -> SoundEvents.WOOL_STEP, () -> ((SimSoundEntry)AeroSoundEvents.ENVELOPE_PLACE).event(), () -> ((SimSoundEntry)AeroSoundEvents.ENVELOPE_HIT).event(), () -> SoundEvents.WOOL_FALL))).properties(p -> p.mapColor(DyeColor.WHITE)).blockstate((c, p) -> p.simpleBlock((Block)c.get(), (ModelFile)p.models().cubeAll(c.getName(), p.modLoc("block/envelope_block/envelope_" + DyeColor.WHITE.getName())))).recipe((c, p) -> ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)4).pattern("WS").pattern("SW").define(Character.valueOf('W'), DyeHelper.getWoolOfDye((DyeColor)DyeColor.WHITE)).define(Character.valueOf('S'), (ItemLike)Items.STICK).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((TagKey)ItemTags.WOOL)).save((RecipeOutput)p)).tag(new TagKey[]{AeroTags.BlockTags.AIRTIGHT}).tag(new TagKey[]{AeroTags.BlockTags.ENVELOPE}).tag(new TagKey[]{BlockTags.MINEABLE_WITH_AXE}).transform(AeroBlocks.flammable(30, 60))).item().tag(new TagKey[]{AeroTags.ItemTags.ENVELOPE}).tag(new TagKey[]{AeroTags.ItemTags.SHAFTLESS_ENVELOPE}).build()).register();
    public static final DyedBlockList<EnvelopeBlock> DYED_ENVELOPE_BLOCKS = new DyedBlockList(color -> {
        String colorName = color.getSerializedName();
        if (color == DyeColor.WHITE) {
            return WHITE_ENVELOPE_BLOCK;
        }
        return ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block(colorName + "_envelope", p -> new EnvelopeBlock((BlockBehaviour.Properties)p, (DyeColor)color)).lang(RegistrateLangProvider.toEnglishName((String)color.getName()) + " Hot Air Envelope").initialProperties(SharedProperties::wooden).properties(p -> p.isValidSpawn(AeroBlocks::neverSpawn)).properties(p -> p.sound((SoundType)new SimLazySoundType(1.0f, 1.0f, () -> ((SimSoundEntry)AeroSoundEvents.ENVELOPE_BREAK).event(), () -> SoundEvents.WOOL_STEP, () -> ((SimSoundEntry)AeroSoundEvents.ENVELOPE_PLACE).event(), () -> ((SimSoundEntry)AeroSoundEvents.ENVELOPE_HIT).event(), () -> SoundEvents.WOOL_FALL))).properties(p -> p.mapColor(color)).blockstate((c, p) -> p.simpleBlock((Block)c.get(), (ModelFile)p.models().cubeAll(c.getName(), p.modLoc("block/envelope_block/envelope_" + colorName)))).recipe((c, p) -> ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)4).pattern("WS").pattern("SW").define(Character.valueOf('W'), DyeHelper.getWoolOfDye((DyeColor)color)).define(Character.valueOf('S'), (ItemLike)Items.STICK).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((TagKey)ItemTags.WOOL)).save((RecipeOutput)p)).tag(new TagKey[]{AeroTags.BlockTags.AIRTIGHT}).tag(new TagKey[]{AeroTags.BlockTags.ENVELOPE}).tag(new TagKey[]{BlockTags.MINEABLE_WITH_AXE}).transform((NonNullFunction)CreativeTabItemTransforms.VisibilityType.SEARCH_ONLY.applyBlock())).transform(AeroBlocks.flammable(30, 60))).item().tag(new TagKey[]{AeroTags.ItemTags.ENVELOPE}).tag(new TagKey[]{AeroTags.ItemTags.SHAFTLESS_ENVELOPE}).build()).register();
    });
    public static final DyedBlockList<EnvelopeEncasedShaftBlock> ENVELOPE_ENCASED_SHAFTS = new DyedBlockList(color -> {
        String colorName = color.getSerializedName();
        return ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block(colorName + "_envelope_encased_shaft", p -> EnvelopeEncasedShaftBlock.withCanvas(p, color)).initialProperties(SharedProperties::wooden).properties(p -> p.sound(SoundType.SCAFFOLDING)).properties(BlockBehaviour.Properties::noOcclusion).properties(p -> p.sound((SoundType)new SimLazySoundType(1.0f, 1.0f, () -> ((SimSoundEntry)AeroSoundEvents.ENVELOPE_BREAK).event(), () -> SoundEvents.WOOL_STEP, () -> ((SimSoundEntry)AeroSoundEvents.ENVELOPE_PLACE).event(), () -> ((SimSoundEntry)AeroSoundEvents.ENVELOPE_HIT).event(), () -> SoundEvents.WOOL_FALL))).properties(p -> p.mapColor(color)).transform(b -> (BlockBuilder)b.transform((NonNullFunction)EncasingRegistry.addVariantTo((Supplier)AllBlocks.SHAFT)))).blockstate((c, p) -> BlockStateGen.axisBlock((DataGenContext)c, (RegistrateBlockstateProvider)p, blockState -> ((BlockModelBuilder)p.models().withExistingParent(colorName + "_envelope_encased_shaft", p.modLoc("block/envelope_encased_shaft/block"))).texture("0", p.modLoc("block/envelope_block/envelope_" + colorName)))).loot((p, b) -> p.add((Block)b, p.createSingleItemTable((ItemLike)DYED_ENVELOPE_BLOCKS.get(color)).withPool((LootPool.Builder)p.applyExplosionCondition((ItemLike)AllBlocks.SHAFT.get(), (ConditionUserBuilder)LootPool.lootPool().setRolls((NumberProvider)ConstantValue.exactly((float)1.0f)).add((LootPoolEntryContainer.Builder)LootItem.lootTableItem((ItemLike)((ItemLike)AllBlocks.SHAFT.get()))))))).tag(new TagKey[]{AeroTags.BlockTags.AIRTIGHT}).tag(new TagKey[]{AeroTags.BlockTags.ENVELOPE}).transform(TagGen.axeOnly())).transform((NonNullFunction)EncasingRegistry.addVariantTo((Supplier)AllBlocks.SHAFT))).transform((NonNullFunction)CreativeTabItemTransforms.VisibilityType.INVISIBLE.applyBlock())).item().tag(new TagKey[]{AeroTags.ItemTags.ENVELOPE}).transform(b -> (BlockBuilder)b.model(SimBlockStateGen.coloredBlockItemModel((String)("envelope_block/envelope_" + colorName), (String[])new String[]{"envelope_encased_shaft/item"})).build())).register();
    });
    public static final BlockEntry<HotAirBurnerBlock> HOT_AIR_BURNER = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("adjustable_burner", HotAirBurnerBlock::new).lang("Hot Air Burner").initialProperties(SharedProperties::stone).properties(p -> p.sound(SoundType.NETHERITE_BLOCK)).properties(BlockBehaviour.Properties::noOcclusion).properties(p -> p.lightLevel(HotAirBurnerBlock::getLightPower)).blockstate((ctx, prov) -> BlockStateGen.simpleBlock((DataGenContext)ctx, (RegistrateBlockstateProvider)prov, blockState -> prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName() + "/block_" + ((HotAirBurnerBlock.Variant)((Object)((Object)((Object)blockState.getValue(HotAirBurnerBlock.VARIANT))))).getSerializedName())))).transform((NonNullFunction)DisplaySource.displaySource(AeroDisplaySources.GAS_DISPLAY))).transform(TagGen.pickaxeOnly())).item().transform(ModelGen.customItemModel())).recipe((c, p) -> ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)1).pattern("S S").pattern("SCS").pattern("ARA").define(Character.valueOf('S'), (ItemLike)AllItems.IRON_SHEET.get()).define(Character.valueOf('A'), (ItemLike)AllItems.ANDESITE_ALLOY.get()).define(Character.valueOf('C'), AeroTags.ItemTags.BURNER_FIRE).define(Character.valueOf('R'), (ItemLike)Items.REDSTONE).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)Items.REDSTONE)).save((RecipeOutput)p)).register();
    public static final BlockEntry<SteamVentBlock> STEAM_VENT = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("steam_vent", SteamVentBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.sound(SoundType.NETHERITE_BLOCK)).properties(BlockBehaviour.Properties::noOcclusion).blockstate((ctx, prov) -> prov.horizontalBlock((Block)ctx.get(), blockState -> prov.models().getExistingFile(prov.modLoc("block/" + ctx.getName() + "/block_" + ((SteamVentBlock.Variant)((Object)((Object)((Object)blockState.getValue(SteamVentBlock.VARIANT))))).getSerializedName())))).item().transform(ModelGen.customItemModel())).transform((NonNullFunction)DisplaySource.displaySource(AeroDisplaySources.GAS_DISPLAY))).tag(new TagKey[]{BlockTags.MINEABLE_WITH_PICKAXE}).recipe((c, p) -> ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)1).pattern("G").pattern("C").define(Character.valueOf('G'), AeroTags.ItemTags.GOLD_SHEET).define(Character.valueOf('C'), (ItemLike)Blocks.COPPER_BLOCK).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)Items.COPPER_INGOT)).save((RecipeOutput)p)).register();
    public static final BlockEntry<PropellerBearingBlock> PROPELLER_BEARING = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("propeller_bearing", PropellerBearingBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.sound(SoundType.COPPER)).properties(BlockBehaviour.Properties::noOcclusion).transform(AeroStress.setImpact(2.0))).blockstate((ctx, prov) -> SimBlockStateGen.facingBlockstate((DataGenContext)ctx, (RegistrateBlockstateProvider)prov, (String)"block/propeller_bearing/block")).transform(TagGen.axeOrPickaxe())).item().transform(ModelGen.customItemModel())).recipe((c, p) -> ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)1).pattern(" A ").pattern(" S ").pattern(" B ").define(Character.valueOf('A'), ItemTags.WOODEN_SLABS).define(Character.valueOf('B'), (ItemLike)AllBlocks.BRASS_CASING.get()).define(Character.valueOf('S'), (ItemLike)AllItems.IRON_SHEET.get()).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)((ItemLike)AllBlocks.BRASS_CASING.get()))).save((RecipeOutput)p)).register();
    public static final BlockEntry<GyroscopicPropellerBearingBlock> GYROSCOPIC_PROPELLER_BEARING = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("gyroscopic_propeller_bearing", GyroscopicPropellerBearingBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.sound(SoundType.COPPER)).transform(AeroStress.setImpact(2.0))).properties(BlockBehaviour.Properties::noOcclusion).blockstate((ctx, prov) -> SimBlockStateGen.facingBlockstate((DataGenContext)ctx, (RegistrateBlockstateProvider)prov, (String)"block/gyroscopic_propeller_bearing/block")).transform(TagGen.axeOrPickaxe())).item().transform(ModelGen.customItemModel())).recipe((c, p) -> ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)1).pattern(" A ").pattern(" G ").pattern(" B ").define(Character.valueOf('A'), ItemTags.WOODEN_SLABS).define(Character.valueOf('B'), (ItemLike)AllBlocks.BRASS_CASING.get()).define(Character.valueOf('G'), (ItemLike)SimItems.GYRO_MECHANISM.get()).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)((ItemLike)AllBlocks.BRASS_CASING.get()))).save((RecipeOutput)p)).register();
    public static final BlockEntry<SmartPropellerBlock> SMART_PROPELLER = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("smart_propeller", SmartPropellerBlock::new).initialProperties(SharedProperties::softMetal).transform(TagGen.axeOrPickaxe())).transform(AeroStress.setImpact(4.0))).blockstate((ctx, prov) -> prov.getVariantBuilder((Block)ctx.getEntry()).forAllStates(state -> ConfiguredModel.builder().modelFile(AssetLookup.partialBaseModel((DataGenContext)ctx, (RegistrateBlockstateProvider)prov, (String[])new String[0])).rotationY(state.getValue((Property)BlockStateProperties.HORIZONTAL_AXIS) == Direction.Axis.X ? 90 : 0).rotationX((Boolean)state.getValue((Property)SmartPropellerBlock.CEILING) != false ? 180 : 0).build())).item().transform(ModelGen.customItemModel())).recipe((c, p) -> ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)2).pattern("P").pattern("G").pattern("B").define(Character.valueOf('P'), (ItemLike)AllItems.PROPELLER).define(Character.valueOf('G'), (ItemLike)SimItems.GYRO_MECHANISM).define(Character.valueOf('B'), (ItemLike)AllBlocks.BRASS_CASING).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)((ItemLike)SimItems.GYRO_MECHANISM.get()))).save((RecipeOutput)p)).register();
    public static final BlockEntry<AndesitePropellerBlock> ANDESITE_PROPELLER = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("andesite_propeller", AndesitePropellerBlock::new).initialProperties(SharedProperties::wooden).transform(TagGen.axeOrPickaxe())).properties(p -> p.sound(SoundType.WOOD)).transform(AeroStress.setImpact(4.0))).blockstate(BlockStateGen.directionalBlockProvider((boolean)true)).item().transform(ModelGen.customItemModel())).recipe((c, p) -> {
        ShapelessRecipeBuilder.shapeless((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)1).requires((ItemLike)WOODEN_PROPELLER.get()).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)((ItemLike)AllItems.PROPELLER.get()))).save((RecipeOutput)p, Aeronautics.path(c.getName() + "_from_andesite"));
        ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)1).pattern("P").pattern("C").pattern("S").define(Character.valueOf('P'), (ItemLike)AllItems.PROPELLER).define(Character.valueOf('C'), ItemTags.WOODEN_SLABS).define(Character.valueOf('S'), (ItemLike)AllBlocks.SHAFT).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)((ItemLike)AllItems.PROPELLER.get()))).save((RecipeOutput)p);
    }).register();
    public static final BlockEntry<WoodenPropellerBlock> WOODEN_PROPELLER = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("wooden_propeller", WoodenPropellerBlock::new).initialProperties(SharedProperties::wooden).transform(TagGen.axeOrPickaxe())).properties(p -> p.sound(SoundType.WOOD)).transform(AeroStress.setImpact(4.0))).blockstate(BlockStateGen.directionalBlockProvider((boolean)true)).recipe((c, p) -> ShapelessRecipeBuilder.shapeless((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)1).requires((ItemLike)ANDESITE_PROPELLER.get()).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)((ItemLike)AllItems.PROPELLER.get()))).save((RecipeOutput)p, Aeronautics.path(c.getName() + "_from_andesite"))).item().transform(ModelGen.customItemModel())).register();
    public static final BlockEntry<MountedPotatoCannonBlock> MOUNTED_POTATO_CANNON = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("mounted_potato_cannon", MountedPotatoCannonBlock::new).initialProperties(SharedProperties::stone).blockstate(AeroBlockStateGen::directionalPoweredAxisBlockstate).properties(BlockBehaviour.Properties::noOcclusion).transform(AeroStress.setImpact(2.0))).transform(TagGen.pickaxeOnly())).item().transform(ModelGen.customItemModel())).register();
    public static final BlockEntry<Block> LEVITITE = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("levitite", Block::new).properties(p -> p.lightLevel($ -> 10)).properties(BlockBehaviour.Properties::noLootTable).properties(p -> p.strength(7.0f, 20.0f)).properties(p -> p.sound((SoundType)new SimLazySoundType(1.0f, 1.0f, () -> ((SimSoundEntry)AeroSoundEvents.LEVITITE_BREAK).event(), () -> SoundEvents.AMETHYST_BLOCK_STEP, () -> ((SimSoundEntry)AeroSoundEvents.LEVITITE_PLACE).event(), () -> SoundEvents.AMETHYST_BLOCK_HIT, () -> SoundEvents.AMETHYST_BLOCK_FALL))).tag(new TagKey[]{BlockTags.MINEABLE_WITH_PICKAXE, AeroTags.BlockTags.LEVITITE}).onRegister(CreateRegistrate.connectedTextures(() -> new SimpleCTBehaviour(AeroSpriteShift.LEVITITE)))).tag(new TagKey[]{SableTags.ALWAYS_CHUNK_RENDERING}).item(BlockItem::new).tag(new TagKey[]{AeroTags.ItemTags.LEVITITE}).properties(p -> p.component(AeroDataComponents.LEVITATING, (Object)Levitating.LEVITITE)).build()).register();
    public static final BlockEntry<Block> PEARLESCENT_LEVITITE = ((BlockBuilder)((BlockBuilder)REGISTRATE.block("pearlescent_levitite", Block::new).properties(p -> p.lightLevel($ -> 10)).properties(BlockBehaviour.Properties::noLootTable).properties(p -> p.strength(7.0f, 20.0f)).properties(p -> p.sound((SoundType)new SimLazySoundType(1.0f, 1.0f, () -> ((SimSoundEntry)AeroSoundEvents.LEVITITE_BREAK).event(), () -> SoundEvents.AMETHYST_BLOCK_STEP, () -> ((SimSoundEntry)AeroSoundEvents.LEVITITE_PLACE).event(), () -> SoundEvents.AMETHYST_BLOCK_HIT, () -> SoundEvents.AMETHYST_BLOCK_FALL))).tag(new TagKey[]{BlockTags.MINEABLE_WITH_PICKAXE, AeroTags.BlockTags.LEVITITE}).onRegister(CreateRegistrate.connectedTextures(() -> new SimpleCTBehaviour(AeroSpriteShift.PEARLESCENT_LEVITITE)))).tag(new TagKey[]{SableTags.ALWAYS_CHUNK_RENDERING}).item(BlockItem::new).tag(new TagKey[]{AeroTags.ItemTags.LEVITITE}).properties(p -> p.component(AeroDataComponents.LEVITATING, (Object)Levitating.PEARLESCENT_LEVITITE)).build()).register();

    private static Boolean neverSpawn(BlockState state, BlockGetter blockGetter, BlockPos pos, EntityType<?> entity) {
        return false;
    }

    private static <B extends Block, R> NonNullUnaryOperator<BlockBuilder<B, R>> flammable(int encouragement, int flamability) {
        return builder -> (BlockBuilder)builder.onRegisterAfter(Registries.BLOCK, block -> ((FireBlock)Blocks.FIRE).setFlammable(block, encouragement, flamability));
    }

    public static void init() {
    }
}
