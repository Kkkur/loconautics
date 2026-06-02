/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.builders.ItemBuilder
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.providers.RegistrateRecipeProvider
 *  com.tterrag.registrate.util.DataIngredient
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  com.tterrag.registrate.util.nullness.NonNullBiConsumer
 *  com.tterrag.registrate.util.nullness.NonNullConsumer
 *  com.tterrag.registrate.util.nullness.NonNullFunction
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.data.recipes.ShapedRecipeBuilder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.IronBarsBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.WoodType
 *  net.minecraft.world.level.material.MapColor
 *  net.neoforged.neoforge.client.model.generators.BlockModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ConfiguredModel
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 *  net.neoforged.neoforge.common.Tags$Blocks
 *  net.neoforged.neoforge.common.Tags$Items
 */
package com.simibubi.create.foundation.data;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.palettes.ConnectedGlassBlock;
import com.simibubi.create.content.decoration.palettes.ConnectedGlassPaneBlock;
import com.simibubi.create.content.decoration.palettes.GlassPaneBlock;
import com.simibubi.create.content.decoration.palettes.WindowBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import com.simibubi.create.foundation.block.connected.GlassPaneCTBehaviour;
import com.simibubi.create.foundation.block.connected.HorizontalCTBehaviour;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.Tags;

public class WindowGen {
    private static final CreateRegistrate REGISTRATE = Create.registrate();

    private static BlockBehaviour.Properties glassProperties(BlockBehaviour.Properties p) {
        return p.isValidSpawn(WindowGen::never).isRedstoneConductor(WindowGen::never).isSuffocating(WindowGen::never).isViewBlocking(WindowGen::never);
    }

    private static boolean never(BlockState p_235436_0_, BlockGetter p_235436_1_, BlockPos p_235436_2_) {
        return false;
    }

    private static Boolean never(BlockState p_235427_0_, BlockGetter p_235427_1_, BlockPos p_235427_2_, EntityType<?> p_235427_3_) {
        return false;
    }

    public static BlockEntry<WindowBlock> woodenWindowBlock(WoodType woodType, Block planksBlock) {
        return WindowGen.woodenWindowBlock(woodType, planksBlock, () -> RenderType::cutoutMipped, false);
    }

    public static BlockBuilder<WindowBlock, CreateRegistrate> randomisedWindowBlock(String name, Supplier<? extends ItemLike> ingredient, Supplier<Supplier<RenderType>> renderType, boolean translucent, Supplier<MapColor> color) {
        ResourceLocation end_texture = Create.asResource(WindowGen.palettesDir() + name + "_end");
        ResourceLocation side_texture = Create.asResource(WindowGen.palettesDir() + name);
        Function<Integer, ResourceLocation> ends = i -> Create.asResource(WindowGen.palettesDir() + name + "_" + i + "_end");
        return (BlockBuilder)WindowGen.windowBlock(name, ingredient, null, renderType, translucent, (NonNullFunction<String, ResourceLocation>)((NonNullFunction)n -> end_texture), (NonNullFunction<String, ResourceLocation>)((NonNullFunction)n -> side_texture), color).blockstate((c, p) -> p.simpleBlock((Block)c.get(), ConfiguredModel.builder().modelFile((ModelFile)p.models().cubeColumn(c.getName() + "_1", side_texture, (ResourceLocation)ends.apply(1))).nextModel().modelFile((ModelFile)p.models().cubeColumn(c.getName() + "_2", side_texture, (ResourceLocation)ends.apply(2))).nextModel().modelFile((ModelFile)p.models().cubeColumn(c.getName() + "_3", side_texture, (ResourceLocation)ends.apply(3))).nextModel().modelFile((ModelFile)p.models().cubeColumn(c.getName() + "_4", side_texture, (ResourceLocation)ends.apply(4))).build())).item().model((c, p) -> p.cubeColumn(c.getName(), side_texture, (ResourceLocation)ends.apply(1))).build();
    }

    public static BlockEntry<WindowBlock> customWindowBlock(String name, Supplier<? extends ItemLike> ingredient, Supplier<CTSpriteShiftEntry> ct, Supplier<Supplier<RenderType>> renderType, boolean translucent, Supplier<MapColor> color) {
        NonNullFunction end_texture = n -> Create.asResource(WindowGen.palettesDir() + name + "_end");
        NonNullFunction side_texture = n -> Create.asResource(WindowGen.palettesDir() + n);
        return WindowGen.windowBlock(name, ingredient, ct, renderType, translucent, (NonNullFunction<String, ResourceLocation>)end_texture, (NonNullFunction<String, ResourceLocation>)side_texture, color).register();
    }

    public static BlockEntry<WindowBlock> woodenWindowBlock(WoodType woodType, Block planksBlock, Supplier<Supplier<RenderType>> renderType, boolean translucent) {
        String woodName = woodType.name();
        String name = woodName + "_window";
        NonNullFunction end_texture = $ -> ResourceLocation.withDefaultNamespace((String)("block/" + woodName + "_planks"));
        NonNullFunction side_texture = n -> Create.asResource(WindowGen.palettesDir() + n);
        return WindowGen.windowBlock(name, () -> planksBlock, () -> AllSpriteShifts.getWoodenWindow(woodType), renderType, translucent, (NonNullFunction<String, ResourceLocation>)end_texture, (NonNullFunction<String, ResourceLocation>)side_texture, () -> ((Block)planksBlock).defaultMapColor()).register();
    }

    public static BlockBuilder<WindowBlock, CreateRegistrate> windowBlock(String name, Supplier<? extends ItemLike> ingredient, Supplier<CTSpriteShiftEntry> ct, Supplier<Supplier<RenderType>> renderType, boolean translucent, NonNullFunction<String, ResourceLocation> endTexture, NonNullFunction<String, ResourceLocation> sideTexture, Supplier<MapColor> color) {
        return ((BlockBuilder)REGISTRATE.block(name, p -> new WindowBlock((BlockBehaviour.Properties)p, translucent)).onRegister((NonNullConsumer)(ct == null ? $ -> {} : CreateRegistrate.connectedTextures(() -> new HorizontalCTBehaviour((CTSpriteShiftEntry)((Object)((Object)ct.get()))))))).addLayer(renderType).recipe((c, p) -> ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.BUILDING_BLOCKS, (ItemLike)((ItemLike)c.get()), (int)2).pattern(" # ").pattern("#X#").define(Character.valueOf('#'), (ItemLike)ingredient.get()).define(Character.valueOf('X'), DataIngredient.tag((TagKey)Tags.Items.GLASS_BLOCKS_COLORLESS).toVanilla()).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)((ItemLike)ingredient.get()))).save((RecipeOutput)p)).initialProperties(() -> Blocks.GLASS).properties(WindowGen::glassProperties).properties(p -> p.mapColor((MapColor)color.get())).loot((t, g) -> t.dropWhenSilkTouch((Block)g)).blockstate((c, p) -> p.simpleBlock((Block)c.get(), (ModelFile)p.models().cubeColumn(c.getName(), (ResourceLocation)sideTexture.apply((Object)c.getName()), (ResourceLocation)endTexture.apply((Object)c.getName())))).tag(new TagKey[]{BlockTags.IMPERMEABLE}).simpleItem();
    }

    public static BlockEntry<ConnectedGlassBlock> framedGlass(String name, Supplier<ConnectedTextureBehaviour> behaviour) {
        return ((BlockBuilder)((BlockBuilder)REGISTRATE.block(name, ConnectedGlassBlock::new).onRegister(CreateRegistrate.connectedTextures(behaviour))).addLayer(() -> RenderType::cutout).initialProperties(() -> Blocks.GLASS).properties(WindowGen::glassProperties).loot((t, g) -> t.dropWhenSilkTouch((Block)g)).recipe((c, p) -> p.stonecutting(DataIngredient.tag((TagKey)Tags.Items.GLASS_BLOCKS_COLORLESS), RecipeCategory.BUILDING_BLOCKS, () -> ((DataGenContext)c).get())).blockstate((c, p) -> BlockStateGen.cubeAll(c, p, "palettes/", "framed_glass")).tag(new TagKey[]{Tags.Blocks.GLASS_BLOCKS_COLORLESS, BlockTags.IMPERMEABLE}).item().tag(new TagKey[]{Tags.Items.GLASS_BLOCKS_COLORLESS}).model((c, p) -> p.cubeColumn(c.getName(), p.modLoc(WindowGen.palettesDir() + c.getName()), p.modLoc("block/palettes/framed_glass"))).build()).register();
    }

    public static BlockEntry<ConnectedGlassPaneBlock> framedGlassPane(String name, Supplier<? extends Block> parent, Supplier<CTSpriteShiftEntry> ctshift) {
        ResourceLocation sideTexture = Create.asResource(WindowGen.palettesDir() + "framed_glass");
        ResourceLocation itemSideTexture = Create.asResource(WindowGen.palettesDir() + name);
        ResourceLocation topTexture = Create.asResource(WindowGen.palettesDir() + "framed_glass_pane_top");
        Supplier<Supplier<RenderType>> renderType = () -> RenderType::cutoutMipped;
        return WindowGen.connectedGlassPane(name, parent, ctshift, sideTexture, itemSideTexture, topTexture, renderType, true).register();
    }

    public static BlockBuilder<ConnectedGlassPaneBlock, CreateRegistrate> customWindowPane(String name, Supplier<? extends Block> parent, Supplier<CTSpriteShiftEntry> ctshift, Supplier<Supplier<RenderType>> renderType) {
        ResourceLocation topTexture = Create.asResource(WindowGen.palettesDir() + name + "_pane_top");
        ResourceLocation sideTexture = Create.asResource(WindowGen.palettesDir() + name);
        return WindowGen.connectedGlassPane(name, parent, ctshift, sideTexture, sideTexture, topTexture, renderType, false);
    }

    public static BlockEntry<ConnectedGlassPaneBlock> woodenWindowPane(WoodType woodType, Supplier<? extends Block> parent) {
        return WindowGen.woodenWindowPane(woodType, parent, () -> RenderType::cutoutMipped);
    }

    public static BlockEntry<ConnectedGlassPaneBlock> woodenWindowPane(WoodType woodType, Supplier<? extends Block> parent, Supplier<Supplier<RenderType>> renderType) {
        String woodName = woodType.name();
        String name = woodName + "_window";
        ResourceLocation topTexture = ResourceLocation.withDefaultNamespace((String)("block/" + woodName + "_planks"));
        ResourceLocation sideTexture = Create.asResource(WindowGen.palettesDir() + name);
        return WindowGen.connectedGlassPane(name, parent, () -> AllSpriteShifts.getWoodenWindow(woodType), sideTexture, sideTexture, topTexture, renderType, false).register();
    }

    public static BlockEntry<GlassPaneBlock> standardGlassPane(String name, Supplier<? extends Block> parent, ResourceLocation sideTexture, ResourceLocation topTexture, Supplier<Supplier<RenderType>> renderType) {
        NonNullBiConsumer stateProvider = (c, p) -> p.paneBlock((IronBarsBlock)c.get(), sideTexture, topTexture);
        return WindowGen.glassPane(name, parent, sideTexture, topTexture, GlassPaneBlock::new, renderType, $ -> {}, stateProvider, true).register();
    }

    private static BlockBuilder<ConnectedGlassPaneBlock, CreateRegistrate> connectedGlassPane(String name, Supplier<? extends Block> parent, Supplier<CTSpriteShiftEntry> ctshift, ResourceLocation sideTexture, ResourceLocation itemSideTexture, ResourceLocation topTexture, Supplier<Supplier<RenderType>> renderType, boolean colorless) {
        NonNullConsumer<? super Block> connectedTextures = ctshift == null ? $ -> {} : CreateRegistrate.connectedTextures(() -> new GlassPaneCTBehaviour((CTSpriteShiftEntry)((Object)((Object)ctshift.get()))));
        String CGPparents = "block/connected_glass_pane/";
        String prefix = name + "_pane_";
        Function<RegistrateBlockstateProvider, ModelFile> post = WindowGen.getPaneModelProvider(CGPparents, prefix, "post", sideTexture, topTexture);
        Function<RegistrateBlockstateProvider, ModelFile> side = WindowGen.getPaneModelProvider(CGPparents, prefix, "side", sideTexture, topTexture);
        Function<RegistrateBlockstateProvider, ModelFile> sideAlt = WindowGen.getPaneModelProvider(CGPparents, prefix, "side_alt", sideTexture, topTexture);
        Function<RegistrateBlockstateProvider, ModelFile> noSide = WindowGen.getPaneModelProvider(CGPparents, prefix, "noside", sideTexture, topTexture);
        Function<RegistrateBlockstateProvider, ModelFile> noSideAlt = WindowGen.getPaneModelProvider(CGPparents, prefix, "noside_alt", sideTexture, topTexture);
        NonNullBiConsumer stateProvider = (c, p) -> p.paneBlock((IronBarsBlock)c.get(), (ModelFile)post.apply((RegistrateBlockstateProvider)p), (ModelFile)side.apply((RegistrateBlockstateProvider)p), (ModelFile)sideAlt.apply((RegistrateBlockstateProvider)p), (ModelFile)noSide.apply((RegistrateBlockstateProvider)p), (ModelFile)noSideAlt.apply((RegistrateBlockstateProvider)p));
        return WindowGen.glassPane(name, parent, itemSideTexture, topTexture, ConnectedGlassPaneBlock::new, renderType, connectedTextures, stateProvider, colorless);
    }

    private static Function<RegistrateBlockstateProvider, ModelFile> getPaneModelProvider(String CGPparents, String prefix, String partial, ResourceLocation sideTexture, ResourceLocation topTexture) {
        return p -> ((BlockModelBuilder)((BlockModelBuilder)p.models().withExistingParent(prefix + partial, Create.asResource(CGPparents + partial))).texture("pane", sideTexture)).texture("edge", topTexture);
    }

    private static <G extends GlassPaneBlock> BlockBuilder<G, CreateRegistrate> glassPane(String name, Supplier<? extends Block> parent, ResourceLocation sideTexture, ResourceLocation topTexture, NonNullFunction<BlockBehaviour.Properties, G> factory, Supplier<Supplier<RenderType>> renderType, NonNullConsumer<? super G> connectedTextures, NonNullBiConsumer<DataGenContext<Block, G>, RegistrateBlockstateProvider> stateProvider, boolean colorless) {
        name = (String)name + "_pane";
        ItemBuilder itemBuilder = ((BlockBuilder)REGISTRATE.block((String)name, factory).onRegister(connectedTextures)).addLayer(renderType).initialProperties(() -> Blocks.GLASS_PANE).properties(p -> p.mapColor(((Block)parent.get()).defaultMapColor())).blockstate(stateProvider).recipe((c, p) -> {
            ShapedRecipeBuilder.shaped((RecipeCategory)RecipeCategory.BUILDING_BLOCKS, (ItemLike)((ItemLike)c.get()), (int)16).pattern("###").pattern("###").define(Character.valueOf('#'), (ItemLike)parent.get()).unlockedBy("has_ingredient", RegistrateRecipeProvider.has((ItemLike)((ItemLike)parent.get()))).save((RecipeOutput)p);
            if (colorless) {
                p.stonecutting(DataIngredient.tag((TagKey)Tags.Items.GLASS_PANES_COLORLESS), RecipeCategory.BUILDING_BLOCKS, () -> ((DataGenContext)c).get());
            }
        }).loot((t, g) -> t.dropWhenSilkTouch((Block)g)).item();
        if (colorless) {
            itemBuilder.tag(new TagKey[]{Tags.Items.GLASS_PANES, Tags.Items.GLASS_PANES_COLORLESS});
        } else {
            itemBuilder.tag(new TagKey[]{Tags.Items.GLASS_PANES});
        }
        BlockBuilder blockBuilder = (BlockBuilder)itemBuilder.model((c, p) -> p.generated((NonNullSupplier)c, new ResourceLocation[]{sideTexture})).build();
        if (colorless) {
            blockBuilder.tag(new TagKey[]{Tags.Blocks.GLASS_PANES, Tags.Blocks.GLASS_PANES_COLORLESS});
        } else {
            blockBuilder.tag(new TagKey[]{Tags.Blocks.GLASS_PANES});
        }
        return blockBuilder;
    }

    private static String palettesDir() {
        return "block/palettes/";
    }
}
