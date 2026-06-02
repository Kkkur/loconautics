/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.builders.ItemBuilder
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.ProviderType
 *  com.tterrag.registrate.providers.RegistrateRecipeProvider
 *  com.tterrag.registrate.util.DataIngredient
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.Block
 */
package com.simibubi.create.content.decoration.palettes;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.content.decoration.palettes.PaletteBlockPartial;
import com.simibubi.create.content.decoration.palettes.PaletteBlockPattern;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class PalettesVariantEntry {
    private static final CreateRegistrate REGISTRATE = Create.registrate();
    public final ImmutableList<BlockEntry<? extends Block>> registeredBlocks;
    public final ImmutableList<BlockEntry<? extends Block>> registeredPartials;

    public PalettesVariantEntry(String name, AllPaletteStoneTypes paletteStoneVariants) {
        ImmutableList.Builder registeredBlocks = ImmutableList.builder();
        ImmutableList.Builder registeredPartials = ImmutableList.builder();
        NonNullSupplier<Block> baseBlock = paletteStoneVariants.baseBlock;
        for (PaletteBlockPattern pattern : paletteStoneVariants.variantTypes) {
            TagKey<Item>[] itemTags;
            BlockBuilder builder = ((BlockBuilder)REGISTRATE.block(pattern.createName(name), pattern.getBlockFactory()).initialProperties(baseBlock).transform(TagGen.pickaxeOnly())).blockstate((arg_0, arg_1) -> ((PaletteBlockPattern.IBlockStateProvider)((PaletteBlockPattern.IBlockStateProvider)((Function)pattern.getBlockStateGenerator().apply(pattern)).apply(name))).accept(arg_0, arg_1));
            ItemBuilder itemBuilder = builder.item();
            TagKey<Block>[] blockTags = pattern.getBlockTags();
            if (blockTags != null) {
                builder.tag(blockTags);
            }
            if ((itemTags = pattern.getItemTags()) != null) {
                itemBuilder.tag(itemTags);
            }
            itemBuilder.tag(new TagKey[]{paletteStoneVariants.materialTag});
            if (pattern.isTranslucent()) {
                builder.addLayer(() -> RenderType::translucent);
            }
            pattern.createCTBehaviour(name).ifPresent(b -> builder.onRegister(CreateRegistrate.connectedTextures(b)));
            builder.recipe((c, p) -> {
                p.stonecutting(DataIngredient.tag(paletteStoneVariants.materialTag), RecipeCategory.BUILDING_BLOCKS, (Supplier)c);
                pattern.addRecipes(baseBlock, (DataGenContext<Block, ? extends Block>)c, (RegistrateRecipeProvider)p);
            });
            itemBuilder.register();
            BlockEntry block = builder.register();
            registeredBlocks.add((Object)block);
            for (PaletteBlockPartial<? extends Block> partialBlock : pattern.getPartials()) {
                registeredPartials.add((Object)partialBlock.create(name, pattern, (BlockEntry<Block>)block, paletteStoneVariants).register());
            }
        }
        REGISTRATE.addDataGenerator(ProviderType.RECIPE, p -> p.stonecutting(DataIngredient.tag(paletteStoneVariants.materialTag), RecipeCategory.BUILDING_BLOCKS, (Supplier)baseBlock));
        REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, p -> p.addTag(paletteStoneVariants.materialTag).add((Object)((Block)baseBlock.get()).asItem()));
        this.registeredBlocks = registeredBlocks.build();
        this.registeredPartials = registeredPartials.build();
    }
}
