/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.providers.RegistrateRecipeProvider
 *  com.tterrag.registrate.util.DataIngredient
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.data.recipes.ShapelessRecipeBuilder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SlabBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.neoforged.neoforge.client.model.generators.ModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.content.decoration.palettes;

import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.content.decoration.palettes.PaletteBlockPartial;
import com.simibubi.create.content.decoration.palettes.PaletteBlockPattern;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import java.util.Arrays;
import java.util.function.Supplier;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;

private static class PaletteBlockPartial.Slab
extends PaletteBlockPartial<SlabBlock> {
    private boolean customSide;

    public PaletteBlockPartial.Slab(boolean customSide) {
        super("slab");
        this.customSide = customSide;
    }

    @Override
    protected SlabBlock createBlock(Supplier<? extends Block> block) {
        return new SlabBlock(BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)((BlockBehaviour)block.get())));
    }

    @Override
    protected boolean canRecycle() {
        return false;
    }

    @Override
    protected void generateBlockState(DataGenContext<Block, SlabBlock> ctx, RegistrateBlockstateProvider prov, String variantName, PaletteBlockPattern pattern, Supplier<? extends Block> block) {
        String name = ctx.getName();
        ResourceLocation mainTexture = this.getTexture(variantName, pattern, 0);
        ResourceLocation sideTexture = this.customSide ? this.getTexture(variantName, pattern, 1) : mainTexture;
        ModelBuilder bottom = prov.models().slab(name, sideTexture, mainTexture, mainTexture);
        ModelBuilder top = prov.models().slabTop(name + "_top", sideTexture, mainTexture, mainTexture);
        Object doubleSlab = this.customSide ? prov.models().cubeColumn(name + "_double", sideTexture, mainTexture) : prov.models().getExistingFile(prov.modLoc(pattern.createName(variantName)));
        prov.slabBlock((SlabBlock)ctx.get(), (ModelFile)bottom, (ModelFile)top, (ModelFile)doubleSlab);
    }

    @Override
    protected Iterable<TagKey<Block>> getBlockTags() {
        return Arrays.asList(BlockTags.SLABS);
    }

    @Override
    protected Iterable<TagKey<Item>> getItemTags() {
        return Arrays.asList(ItemTags.SLABS);
    }

    @Override
    protected void createRecipes(AllPaletteStoneTypes type, BlockEntry<? extends Block> patternBlock, DataGenContext<Block, ? extends Block> c, RegistrateRecipeProvider p) {
        RecipeCategory category = RecipeCategory.BUILDING_BLOCKS;
        p.slab(DataIngredient.items((ItemLike)((Block)patternBlock.get()), (ItemLike[])new Block[0]), category, () -> c.get(), c.getName(), false);
        p.stonecutting(DataIngredient.tag(type.materialTag), category, () -> c.get(), 2);
        DataIngredient ingredient = DataIngredient.items((ItemLike)((Block)c.get()), (ItemLike[])new Block[0]);
        ShapelessRecipeBuilder.shapeless((RecipeCategory)category, (ItemLike)((ItemLike)patternBlock.get())).requires(ingredient.toVanilla()).requires(ingredient.toVanilla()).unlockedBy("has_" + c.getName(), ingredient.getCriterion(p)).save((RecipeOutput)p, "create:" + c.getName() + "_recycling");
    }

    @Override
    protected BlockBuilder<SlabBlock, CreateRegistrate> transformBlock(BlockBuilder<SlabBlock, CreateRegistrate> builder, String variantName, PaletteBlockPattern pattern) {
        builder.loot((lt, block) -> lt.add((Block)block, lt.createSlabItemTable((Block)block)));
        return super.transformBlock(builder, variantName, pattern);
    }
}
