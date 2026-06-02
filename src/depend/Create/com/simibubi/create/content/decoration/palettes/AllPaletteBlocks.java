/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.util.DataIngredient
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.TransparentBlock
 *  net.minecraft.world.level.block.state.properties.WoodType
 *  net.minecraft.world.level.material.MapColor
 *  net.neoforged.neoforge.common.Tags$Blocks
 *  net.neoforged.neoforge.common.Tags$Items
 */
package com.simibubi.create.content.decoration.palettes;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.content.decoration.palettes.ConnectedGlassBlock;
import com.simibubi.create.content.decoration.palettes.ConnectedGlassPaneBlock;
import com.simibubi.create.content.decoration.palettes.GlassPaneBlock;
import com.simibubi.create.content.decoration.palettes.WeatheredIronWindowCTBehaviour;
import com.simibubi.create.content.decoration.palettes.WeatheredIronWindowPaneCTBehaviour;
import com.simibubi.create.content.decoration.palettes.WindowBlock;
import com.simibubi.create.foundation.block.connected.HorizontalCTBehaviour;
import com.simibubi.create.foundation.block.connected.SimpleCTBehaviour;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.WindowGen;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import java.util.function.Supplier;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.Tags;

public class AllPaletteBlocks {
    private static final CreateRegistrate REGISTRATE = Create.registrate();
    public static final BlockEntry<TransparentBlock> TILED_GLASS;
    public static final BlockEntry<ConnectedGlassBlock> FRAMED_GLASS;
    public static final BlockEntry<ConnectedGlassBlock> HORIZONTAL_FRAMED_GLASS;
    public static final BlockEntry<ConnectedGlassBlock> VERTICAL_FRAMED_GLASS;
    public static final BlockEntry<GlassPaneBlock> TILED_GLASS_PANE;
    public static final BlockEntry<ConnectedGlassPaneBlock> FRAMED_GLASS_PANE;
    public static final BlockEntry<ConnectedGlassPaneBlock> HORIZONTAL_FRAMED_GLASS_PANE;
    public static final BlockEntry<ConnectedGlassPaneBlock> VERTICAL_FRAMED_GLASS_PANE;
    public static final BlockEntry<WindowBlock> OAK_WINDOW;
    public static final BlockEntry<WindowBlock> SPRUCE_WINDOW;
    public static final BlockEntry<WindowBlock> BIRCH_WINDOW;
    public static final BlockEntry<WindowBlock> JUNGLE_WINDOW;
    public static final BlockEntry<WindowBlock> ACACIA_WINDOW;
    public static final BlockEntry<WindowBlock> DARK_OAK_WINDOW;
    public static final BlockEntry<WindowBlock> MANGROVE_WINDOW;
    public static final BlockEntry<WindowBlock> CRIMSON_WINDOW;
    public static final BlockEntry<WindowBlock> WARPED_WINDOW;
    public static final BlockEntry<WindowBlock> CHERRY_WINDOW;
    public static final BlockEntry<WindowBlock> BAMBOO_WINDOW;
    public static final BlockEntry<WindowBlock> ORNATE_IRON_WINDOW;
    public static final BlockEntry<WindowBlock> INDUSTRIAL_IRON_WINDOW;
    public static final BlockEntry<WindowBlock> WEATHERED_IRON_WINDOW;
    public static final BlockEntry<ConnectedGlassPaneBlock> OAK_WINDOW_PANE;
    public static final BlockEntry<ConnectedGlassPaneBlock> SPRUCE_WINDOW_PANE;
    public static final BlockEntry<ConnectedGlassPaneBlock> BIRCH_WINDOW_PANE;
    public static final BlockEntry<ConnectedGlassPaneBlock> JUNGLE_WINDOW_PANE;
    public static final BlockEntry<ConnectedGlassPaneBlock> ACACIA_WINDOW_PANE;
    public static final BlockEntry<ConnectedGlassPaneBlock> DARK_OAK_WINDOW_PANE;
    public static final BlockEntry<ConnectedGlassPaneBlock> MANGROVE_WINDOW_PANE;
    public static final BlockEntry<ConnectedGlassPaneBlock> CRIMSON_WINDOW_PANE;
    public static final BlockEntry<ConnectedGlassPaneBlock> WARPED_WINDOW_PANE;
    public static final BlockEntry<ConnectedGlassPaneBlock> CHERRY_WINDOW_PANE;
    public static final BlockEntry<ConnectedGlassPaneBlock> BAMBOO_WINDOW_PANE;
    public static final BlockEntry<ConnectedGlassPaneBlock> ORNATE_IRON_WINDOW_PANE;
    public static final BlockEntry<ConnectedGlassPaneBlock> INDUSTRIAL_IRON_WINDOW_PANE;
    public static final BlockEntry<ConnectedGlassPaneBlock> WEATHERED_IRON_WINDOW_PANE;

    public static void register() {
    }

    static {
        REGISTRATE.setCreativeTab(AllCreativeModeTabs.PALETTES_CREATIVE_TAB);
        TILED_GLASS = ((BlockBuilder)REGISTRATE.block("tiled_glass", TransparentBlock::new).initialProperties(() -> Blocks.GLASS).addLayer(() -> RenderType::cutout).recipe((c, p) -> p.stonecutting(DataIngredient.tag((TagKey)Tags.Items.GLASS_BLOCKS_COLORLESS), RecipeCategory.BUILDING_BLOCKS, (Supplier)c)).blockstate((c, p) -> BlockStateGen.cubeAll(c, p, "palettes/")).loot((t, g) -> t.dropWhenSilkTouch((Block)g)).tag(new TagKey[]{Tags.Blocks.GLASS_BLOCKS_COLORLESS, BlockTags.IMPERMEABLE}).item().tag(new TagKey[]{Tags.Items.GLASS_BLOCKS_COLORLESS}).build()).register();
        FRAMED_GLASS = WindowGen.framedGlass("framed_glass", () -> new SimpleCTBehaviour(AllSpriteShifts.FRAMED_GLASS));
        HORIZONTAL_FRAMED_GLASS = WindowGen.framedGlass("horizontal_framed_glass", () -> new HorizontalCTBehaviour(AllSpriteShifts.HORIZONTAL_FRAMED_GLASS, AllSpriteShifts.FRAMED_GLASS));
        VERTICAL_FRAMED_GLASS = WindowGen.framedGlass("vertical_framed_glass", () -> new HorizontalCTBehaviour(AllSpriteShifts.VERTICAL_FRAMED_GLASS));
        TILED_GLASS_PANE = WindowGen.standardGlassPane("tiled_glass", TILED_GLASS, Create.asResource("block/palettes/tiled_glass"), ResourceLocation.withDefaultNamespace((String)"block/glass_pane_top"), () -> RenderType::cutoutMipped);
        FRAMED_GLASS_PANE = WindowGen.framedGlassPane("framed_glass", FRAMED_GLASS, () -> AllSpriteShifts.FRAMED_GLASS);
        HORIZONTAL_FRAMED_GLASS_PANE = WindowGen.framedGlassPane("horizontal_framed_glass", HORIZONTAL_FRAMED_GLASS, () -> AllSpriteShifts.HORIZONTAL_FRAMED_GLASS);
        VERTICAL_FRAMED_GLASS_PANE = WindowGen.framedGlassPane("vertical_framed_glass", VERTICAL_FRAMED_GLASS, () -> AllSpriteShifts.VERTICAL_FRAMED_GLASS);
        OAK_WINDOW = WindowGen.woodenWindowBlock(WoodType.OAK, Blocks.OAK_PLANKS);
        SPRUCE_WINDOW = WindowGen.woodenWindowBlock(WoodType.SPRUCE, Blocks.SPRUCE_PLANKS);
        BIRCH_WINDOW = WindowGen.woodenWindowBlock(WoodType.BIRCH, Blocks.BIRCH_PLANKS, () -> RenderType::translucent, true);
        JUNGLE_WINDOW = WindowGen.woodenWindowBlock(WoodType.JUNGLE, Blocks.JUNGLE_PLANKS);
        ACACIA_WINDOW = WindowGen.woodenWindowBlock(WoodType.ACACIA, Blocks.ACACIA_PLANKS);
        DARK_OAK_WINDOW = WindowGen.woodenWindowBlock(WoodType.DARK_OAK, Blocks.DARK_OAK_PLANKS);
        MANGROVE_WINDOW = WindowGen.woodenWindowBlock(WoodType.MANGROVE, Blocks.MANGROVE_PLANKS);
        CRIMSON_WINDOW = WindowGen.woodenWindowBlock(WoodType.CRIMSON, Blocks.CRIMSON_PLANKS);
        WARPED_WINDOW = WindowGen.woodenWindowBlock(WoodType.WARPED, Blocks.WARPED_PLANKS);
        CHERRY_WINDOW = WindowGen.woodenWindowBlock(WoodType.CHERRY, Blocks.CHERRY_PLANKS);
        BAMBOO_WINDOW = WindowGen.woodenWindowBlock(WoodType.BAMBOO, Blocks.BAMBOO_PLANKS);
        ORNATE_IRON_WINDOW = WindowGen.customWindowBlock("ornate_iron_window", () -> Items.IRON_NUGGET, () -> AllSpriteShifts.ORNATE_IRON_WINDOW, () -> RenderType::cutout, false, () -> MapColor.TERRACOTTA_LIGHT_GRAY);
        INDUSTRIAL_IRON_WINDOW = WindowGen.customWindowBlock("industrial_iron_window", AllBlocks.INDUSTRIAL_IRON_BLOCK, () -> AllSpriteShifts.INDUSTRIAL_IRON_WINDOW, () -> RenderType::cutout, false, () -> MapColor.COLOR_GRAY);
        WEATHERED_IRON_WINDOW = ((BlockBuilder)WindowGen.randomisedWindowBlock("weathered_iron_window", AllBlocks.WEATHERED_IRON_BLOCK, () -> RenderType::translucent, true, () -> MapColor.TERRACOTTA_LIGHT_GRAY).onRegister(CreateRegistrate.connectedTextures(() -> new WeatheredIronWindowCTBehaviour()))).register();
        OAK_WINDOW_PANE = WindowGen.woodenWindowPane(WoodType.OAK, OAK_WINDOW);
        SPRUCE_WINDOW_PANE = WindowGen.woodenWindowPane(WoodType.SPRUCE, SPRUCE_WINDOW);
        BIRCH_WINDOW_PANE = WindowGen.woodenWindowPane(WoodType.BIRCH, BIRCH_WINDOW, () -> RenderType::translucent);
        JUNGLE_WINDOW_PANE = WindowGen.woodenWindowPane(WoodType.JUNGLE, JUNGLE_WINDOW);
        ACACIA_WINDOW_PANE = WindowGen.woodenWindowPane(WoodType.ACACIA, ACACIA_WINDOW);
        DARK_OAK_WINDOW_PANE = WindowGen.woodenWindowPane(WoodType.DARK_OAK, DARK_OAK_WINDOW);
        MANGROVE_WINDOW_PANE = WindowGen.woodenWindowPane(WoodType.MANGROVE, MANGROVE_WINDOW);
        CRIMSON_WINDOW_PANE = WindowGen.woodenWindowPane(WoodType.CRIMSON, CRIMSON_WINDOW);
        WARPED_WINDOW_PANE = WindowGen.woodenWindowPane(WoodType.WARPED, WARPED_WINDOW);
        CHERRY_WINDOW_PANE = WindowGen.woodenWindowPane(WoodType.CHERRY, CHERRY_WINDOW);
        BAMBOO_WINDOW_PANE = WindowGen.woodenWindowPane(WoodType.BAMBOO, BAMBOO_WINDOW);
        ORNATE_IRON_WINDOW_PANE = WindowGen.customWindowPane("ornate_iron_window", ORNATE_IRON_WINDOW, () -> AllSpriteShifts.ORNATE_IRON_WINDOW, () -> RenderType::cutoutMipped).register();
        INDUSTRIAL_IRON_WINDOW_PANE = WindowGen.customWindowPane("industrial_iron_window", INDUSTRIAL_IRON_WINDOW, () -> AllSpriteShifts.INDUSTRIAL_IRON_WINDOW, () -> RenderType::cutoutMipped).register();
        WEATHERED_IRON_WINDOW_PANE = ((BlockBuilder)WindowGen.customWindowPane("weathered_iron_window", WEATHERED_IRON_WINDOW, null, () -> RenderType::translucent).onRegister(CreateRegistrate.connectedTextures(() -> new WeatheredIronWindowPaneCTBehaviour()))).register();
        AllPaletteStoneTypes.register(REGISTRATE);
    }
}
