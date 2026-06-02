/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.AllTags$AllBlockTags
 *  com.simibubi.create.api.behaviour.movement.MovementBehaviour
 *  com.simibubi.create.content.contraptions.actors.roller.RollerBlockItem
 *  com.simibubi.create.foundation.data.BlockStateGen
 *  com.simibubi.create.foundation.data.ModelGen
 *  com.simibubi.create.foundation.data.SharedProperties
 *  com.simibubi.create.foundation.data.TagGen
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.providers.RegistrateRecipeProvider
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  dev.simulated_team.simulated.data.SimBlockStateGen
 *  dev.simulated_team.simulated.index.SimItems
 *  dev.simulated_team.simulated.registrate.SimulatedRegistrate
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.data.recipes.ShapedRecipeBuilder
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.material.MapColor
 */
package dev.ryanhcode.offroad.index;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.actors.roller.RollerBlockItem;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.ModelGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.config.server.OffroadStress;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlock;
import dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel.RockCuttingWheelActor;
import dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel.RockCuttingWheelBlock;
import dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlock;
import dev.ryanhcode.offroad.content.components.TireLike;
import dev.ryanhcode.offroad.index.OffroadDataComponents;
import dev.simulated_team.simulated.data.SimBlockStateGen;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class OffroadBlocks {
    private static final SimulatedRegistrate REGISTRATE = Offroad.getRegistrate();
    public static final BlockEntry<BoreheadBearingBlock> BOREHEAD_BEARING_BLOCK = ((BlockBuilder)((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("borehead_bearing", BoreheadBearingBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK).requiresCorrectToolForDrops().noOcclusion()).transform(TagGen.pickaxeOnly())).properties(p -> p.mapColor(MapColor.COLOR_GRAY).noOcclusion().isRedstoneConductor((state, level, pos) -> false)).transform(TagGen.pickaxeOnly())).transform(OffroadStress.setImpact(8.0))).addLayer(() -> RenderType::cutoutMipped).blockstate(BlockStateGen.directionalAxisBlockProvider()).recipe((c, p) -> ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)1).pattern("S").pattern("G").pattern("I").define(Character.valueOf('S'), ItemTags.WOODEN_SLABS).define(Character.valueOf('G'), (ItemLike)AllBlocks.GEARBOX).define(Character.valueOf('I'), (ItemLike)AllBlocks.INDUSTRIAL_IRON_BLOCK).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)((ItemLike)AllBlocks.INDUSTRIAL_IRON_BLOCK.get()))).save((RecipeOutput)p)).item().transform(ModelGen.customItemModel())).register();
    public static final BlockEntry<RockCuttingWheelBlock> ROCK_CUTTER_BLOCK = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("rockcutting_wheel", RockCuttingWheelBlock::new).initialProperties(SharedProperties::softMetal).properties(p -> p.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK).requiresCorrectToolForDrops().noOcclusion()).transform(TagGen.pickaxeOnly())).onRegister(MovementBehaviour.movementBehaviour((MovementBehaviour)new RockCuttingWheelActor()))).properties(BlockBehaviour.Properties::noOcclusion).addLayer(() -> RenderType::cutoutMipped).blockstate(SimBlockStateGen::directionalAxisBlock).recipe((c, p) -> ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)1).pattern("C").pattern("G").pattern("S").define(Character.valueOf('C'), (ItemLike)AllBlocks.CRUSHING_WHEEL).define(Character.valueOf('G'), (ItemLike)AllBlocks.INDUSTRIAL_IRON_BLOCK).define(Character.valueOf('S'), (ItemLike)AllItems.IRON_SHEET).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)((ItemLike)AllBlocks.INDUSTRIAL_IRON_BLOCK.get()))).save((RecipeOutput)p)).item().properties(x -> x.component(OffroadDataComponents.TIRE, (Object)TireLike.ROCKCUTTING_WHEEL)).transform(ModelGen.customItemModel())).lang("Rock Cutting Wheel").register();
    public static final BlockEntry<WheelMountBlock> WHEEL_MOUNT = ((BlockBuilder)((BlockBuilder)((BlockBuilder)REGISTRATE.block("wheel_mount", WheelMountBlock::new).initialProperties(SharedProperties::stone).properties(p -> p.mapColor(MapColor.COLOR_GRAY).noOcclusion().isRedstoneConductor((state, level, pos) -> false)).transform(TagGen.axeOrPickaxe())).addLayer(() -> RenderType::cutoutMipped).tag(new TagKey[]{AllTags.AllBlockTags.SAFE_NBT.tag}).blockstate(BlockStateGen.horizontalBlockProvider((boolean)true)).transform(OffroadStress.setImpact(16.0))).item(RollerBlockItem::new).transform(ModelGen.customItemModel())).recipe((c, p) -> ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)c.get()), (int)1).pattern("C").pattern("S").pattern("P").define(Character.valueOf('C'), (ItemLike)AllBlocks.ANDESITE_CASING.get()).define(Character.valueOf('S'), (ItemLike)SimItems.SPRING.asItem()).define(Character.valueOf('P'), (ItemLike)AllItems.IRON_SHEET.asItem()).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)((ItemLike)AllBlocks.ANDESITE_CASING.get()))).save((RecipeOutput)p)).register();

    public static void init() {
    }
}
