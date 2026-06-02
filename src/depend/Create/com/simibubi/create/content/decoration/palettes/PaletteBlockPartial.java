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
 *  com.tterrag.registrate.util.nullness.NonnullType
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.data.recipes.ShapedRecipeBuilder
 *  net.minecraft.data.recipes.ShapelessRecipeBuilder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SlabBlock
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.WallBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.neoforged.neoforge.client.model.generators.ModelBuilder
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 */
package com.simibubi.create.content.decoration.palettes;

import com.simibubi.create.Create;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.simibubi.create.content.decoration.palettes.PaletteBlockPattern;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonnullType;
import java.util.Arrays;
import java.util.function.Supplier;
import net.createmod.catnip.lang.Lang;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public abstract class PaletteBlockPartial<B extends Block> {
    public static final PaletteBlockPartial<StairBlock> STAIR = new Stairs();
    public static final PaletteBlockPartial<SlabBlock> SLAB = new Slab(false);
    public static final PaletteBlockPartial<SlabBlock> UNIQUE_SLAB = new Slab(true);
    public static final PaletteBlockPartial<WallBlock> WALL = new Wall();
    public static final PaletteBlockPartial<?>[] ALL_PARTIALS = new PaletteBlockPartial[]{STAIR, SLAB, WALL};
    public static final PaletteBlockPartial<?>[] FOR_POLISHED = new PaletteBlockPartial[]{STAIR, UNIQUE_SLAB, WALL};
    private String name;

    private PaletteBlockPartial(String name) {
        this.name = name;
    }

    @NonnullType
    public BlockBuilder<B, CreateRegistrate> create(String variantName, PaletteBlockPattern pattern, BlockEntry<? extends Block> block, AllPaletteStoneTypes variant) {
        String patternName = Lang.nonPluralId((String)pattern.createName(variantName));
        String blockName = patternName + "_" + this.name;
        BlockBuilder blockBuilder = (BlockBuilder)Create.registrate().block(blockName, p -> this.createBlock((Supplier<Block>)block)).blockstate((c, p) -> this.generateBlockState((DataGenContext<Block, B>)c, (RegistrateBlockstateProvider)p, variantName, pattern, (Supplier<? extends Block>)((Supplier<Block>)block))).recipe((c, p) -> this.createRecipes(variant, (BlockEntry<? extends Block>)((BlockEntry<Block>)block), (DataGenContext<Block, ? extends Block>)((DataGenContext<Block, Block>)c), (RegistrateRecipeProvider)p)).transform(b -> this.transformBlock((BlockBuilder<B, CreateRegistrate>)b, variantName, pattern));
        ItemBuilder itemBuilder = (ItemBuilder)blockBuilder.item().transform(b -> this.transformItem((ItemBuilder<BlockItem, BlockBuilder<B, CreateRegistrate>>)b, variantName, pattern));
        if (this.canRecycle()) {
            itemBuilder.tag(new TagKey[]{variant.materialTag});
        }
        return (BlockBuilder)itemBuilder.build();
    }

    protected ResourceLocation getTexture(String variantName, PaletteBlockPattern pattern, int index) {
        return PaletteBlockPattern.toLocation(variantName, pattern.getTexture(index));
    }

    protected BlockBuilder<B, CreateRegistrate> transformBlock(BlockBuilder<B, CreateRegistrate> builder, String variantName, PaletteBlockPattern pattern) {
        this.getBlockTags().forEach(xva$0 -> builder.tag(new TagKey[]{xva$0}));
        return (BlockBuilder)builder.transform(TagGen.pickaxeOnly());
    }

    protected ItemBuilder<BlockItem, BlockBuilder<B, CreateRegistrate>> transformItem(ItemBuilder<BlockItem, BlockBuilder<B, CreateRegistrate>> builder, String variantName, PaletteBlockPattern pattern) {
        this.getItemTags().forEach(xva$0 -> builder.tag(new TagKey[]{xva$0}));
        return builder;
    }

    protected boolean canRecycle() {
        return true;
    }

    protected abstract Iterable<TagKey<Block>> getBlockTags();

    protected abstract Iterable<TagKey<Item>> getItemTags();

    protected abstract B createBlock(Supplier<? extends Block> var1);

    protected abstract void createRecipes(AllPaletteStoneTypes var1, BlockEntry<? extends Block> var2, DataGenContext<Block, ? extends Block> var3, RegistrateRecipeProvider var4);

    protected abstract void generateBlockState(DataGenContext<Block, B> var1, RegistrateBlockstateProvider var2, String var3, PaletteBlockPattern var4, Supplier<? extends Block> var5);

    private static class Stairs
    extends PaletteBlockPartial<StairBlock> {
        public Stairs() {
            super("stairs");
        }

        @Override
        protected StairBlock createBlock(Supplier<? extends Block> block) {
            return new StairBlock(block.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)((BlockBehaviour)block.get())));
        }

        @Override
        protected void generateBlockState(DataGenContext<Block, StairBlock> ctx, RegistrateBlockstateProvider prov, String variantName, PaletteBlockPattern pattern, Supplier<? extends Block> block) {
            prov.stairsBlock((StairBlock)ctx.get(), this.getTexture(variantName, pattern, 0));
        }

        @Override
        protected Iterable<TagKey<Block>> getBlockTags() {
            return Arrays.asList(BlockTags.STAIRS);
        }

        @Override
        protected Iterable<TagKey<Item>> getItemTags() {
            return Arrays.asList(ItemTags.STAIRS);
        }

        @Override
        protected void createRecipes(AllPaletteStoneTypes type, BlockEntry<? extends Block> patternBlock, DataGenContext<Block, ? extends Block> c, RegistrateRecipeProvider p) {
            RecipeCategory category = RecipeCategory.BUILDING_BLOCKS;
            p.stairs(DataIngredient.items((ItemLike)((Block)patternBlock.get()), (ItemLike[])new Block[0]), category, () -> c.get(), c.getName(), false);
            p.stonecutting(DataIngredient.tag(type.materialTag), category, () -> c.get(), 1);
        }
    }

    private static class Slab
    extends PaletteBlockPartial<SlabBlock> {
        private boolean customSide;

        public Slab(boolean customSide) {
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

    private static class Wall
    extends PaletteBlockPartial<WallBlock> {
        public Wall() {
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
}
