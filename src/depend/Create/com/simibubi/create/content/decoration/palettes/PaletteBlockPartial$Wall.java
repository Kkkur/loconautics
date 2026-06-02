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
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.data.recipes.ShapedRecipeBuilder
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.WallBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 */
package com.simibubi.create.content.decoration.palettes;

import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.content.decoration.palettes.PaletteBlockPartial;
import com.simibubi.create.content.decoration.palettes.PaletteBlockPattern;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import java.util.Arrays;
import java.util.function.Supplier;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

private static class PaletteBlockPartial.Wall
extends PaletteBlockPartial<WallBlock> {
    public PaletteBlockPartial.Wall() {
        super("wall");
    }

    @Override
    protected WallBlock createBlock(Supplier<? extends Block> block) {
        return new WallBlock(BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)((BlockBehaviour)block.get())).forceSolidOn());
    }

    @Override
    protected ItemBuilder<BlockItem, BlockBuilder<WallBlock, CreateRegistrate>> transformItem(ItemBuilder<BlockItem, BlockBuilder<WallBlock, CreateRegistrate>> builder, String variantName, PaletteBlockPattern pattern) {
        builder.model((c, p) -> p.wallInventory(c.getName(), this.getTexture(variantName, pattern, 0)));
        return super.transformItem(builder, variantName, pattern);
    }

    @Override
    protected void generateBlockState(DataGenContext<Block, WallBlock> ctx, RegistrateBlockstateProvider prov, String variantName, PaletteBlockPattern pattern, Supplier<? extends Block> block) {
        prov.wallBlock((WallBlock)ctx.get(), pattern.createName(variantName), this.getTexture(variantName, pattern, 0));
    }

    @Override
    protected Iterable<TagKey<Block>> getBlockTags() {
        return Arrays.asList(BlockTags.WALLS);
    }

    @Override
    protected Iterable<TagKey<Item>> getItemTags() {
        return Arrays.asList(ItemTags.WALLS);
    }

    @Override
    protected void createRecipes(AllPaletteStoneTypes type, BlockEntry<? extends Block> patternBlock, DataGenContext<Block, ? extends Block> c, RegistrateRecipeProvider p) {
        RecipeCategory category = RecipeCategory.BUILDING_BLOCKS;
        p.stonecutting(DataIngredient.tag(type.materialTag), category, () -> c.get(), 1);
        DataIngredient ingredient = DataIngredient.items((ItemLike)((Block)patternBlock.get()), (ItemLike[])new Block[0]);
        ShapedRecipeBuilder.shaped((RecipeCategory)category, (ItemLike)((ItemLike)c.get()), (int)6).pattern("XXX").pattern("XXX").define(Character.valueOf('X'), ingredient.toVanilla()).unlockedBy("has_" + p.safeName(ingredient), ingredient.getCriterion(p)).save((RecipeOutput)p, p.safeId((ItemLike)c.get()));
    }
}
