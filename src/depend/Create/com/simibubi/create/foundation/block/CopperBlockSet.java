/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.AbstractRegistrate
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.providers.DataGenContext
 *  com.tterrag.registrate.providers.RegistrateBlockstateProvider
 *  com.tterrag.registrate.providers.RegistrateRecipeProvider
 *  com.tterrag.registrate.providers.loot.RegistrateBlockLootTables
 *  com.tterrag.registrate.util.DataIngredient
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  com.tterrag.registrate.util.nullness.NonNullBiConsumer
 *  com.tterrag.registrate.util.nullness.NonNullFunction
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.core.Holder
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.data.recipes.RecipeOutput
 *  net.minecraft.data.recipes.ShapelessRecipeBuilder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.SlabBlock
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.WeatheringCopper$WeatherState
 *  net.minecraft.world.level.block.WeatheringCopperFullBlock
 *  net.minecraft.world.level.block.WeatheringCopperSlabBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.neoforged.neoforge.client.model.generators.ModelFile
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.simibubi.create.foundation.block;

import com.simibubi.create.foundation.block.CopperRegistries;
import com.simibubi.create.foundation.block.CreateCopperStairBlock;
import com.simibubi.create.foundation.block.CreateWeatheringCopperStairBlock;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.DataIngredient;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperFullBlock;
import net.minecraft.world.level.block.WeatheringCopperSlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import org.apache.commons.lang3.ArrayUtils;

public class CopperBlockSet {
    protected static final WeatheringCopper.WeatherState[] WEATHER_STATES = WeatheringCopper.WeatherState.values();
    protected static final int WEATHER_STATE_COUNT = WEATHER_STATES.length;
    protected static final Map<WeatheringCopper.WeatherState, Supplier<Block>> BASE_BLOCKS = new EnumMap<WeatheringCopper.WeatherState, Supplier<Block>>(WeatheringCopper.WeatherState.class);
    public static final Variant<?>[] DEFAULT_VARIANTS;
    protected final String name;
    protected final String generalDirectory;
    protected final Variant<?>[] variants;
    protected final Map<Variant<?>, BlockEntry<?>[]> entries = new HashMap();
    protected final NonNullBiConsumer<DataGenContext<Block, ?>, RegistrateRecipeProvider> mainBlockRecipe;
    protected final String endTextureName;
    protected final NonNullBiConsumer<WeatheringCopper.WeatherState, Block> onRegister;

    public CopperBlockSet(AbstractRegistrate<?> registrate, String name, String endTextureName, Variant<?>[] variants) {
        this(registrate, name, endTextureName, variants, NonNullBiConsumer.noop(), "copper/", (NonNullBiConsumer<WeatheringCopper.WeatherState, Block>)NonNullBiConsumer.noop());
    }

    public CopperBlockSet(AbstractRegistrate<?> registrate, String name, String endTextureName, Variant<?>[] variants, String generalDirectory) {
        this(registrate, name, endTextureName, variants, NonNullBiConsumer.noop(), generalDirectory, (NonNullBiConsumer<WeatheringCopper.WeatherState, Block>)NonNullBiConsumer.noop());
    }

    public CopperBlockSet(AbstractRegistrate<?> registrate, String name, String endTextureName, Variant<?>[] variants, NonNullBiConsumer<DataGenContext<Block, ?>, RegistrateRecipeProvider> mainBlockRecipe) {
        this(registrate, name, endTextureName, variants, mainBlockRecipe, "copper/", (NonNullBiConsumer<WeatheringCopper.WeatherState, Block>)NonNullBiConsumer.noop());
    }

    public CopperBlockSet(AbstractRegistrate<?> registrate, String name, String endTextureName, Variant<?>[] variants, NonNullBiConsumer<DataGenContext<Block, ?>, RegistrateRecipeProvider> mainBlockRecipe, NonNullBiConsumer<WeatheringCopper.WeatherState, Block> onRegister) {
        this(registrate, name, endTextureName, variants, mainBlockRecipe, "copper/", onRegister);
    }

    public CopperBlockSet(AbstractRegistrate<?> registrate, String name, String endTextureName, Variant<?>[] variants, NonNullBiConsumer<DataGenContext<Block, ?>, RegistrateRecipeProvider> mainBlockRecipe, String generalDirectory, NonNullBiConsumer<WeatheringCopper.WeatherState, Block> onRegister) {
        this.name = name;
        this.generalDirectory = generalDirectory;
        this.endTextureName = endTextureName;
        this.variants = variants;
        this.mainBlockRecipe = mainBlockRecipe;
        this.onRegister = onRegister;
        for (boolean waxed : Iterate.falseAndTrue) {
            for (Variant<?> variant : this.variants) {
                BlockEntry[] entries = waxed ? this.entries.get(variant) : new BlockEntry[WEATHER_STATE_COUNT * 2];
                for (WeatheringCopper.WeatherState state : WEATHER_STATES) {
                    BlockEntry<?> entry;
                    int index = this.getIndex(state, waxed);
                    entries[index] = entry = this.createEntry(registrate, variant, state, waxed);
                    if (waxed) {
                        CopperRegistries.addWaxable((Holder<Block>)entries[this.getIndex(state, false)], entry);
                        continue;
                    }
                    if (state == WeatheringCopper.WeatherState.UNAFFECTED) continue;
                    CopperRegistries.addWeathering((Holder<Block>)entries[this.getIndex(WEATHER_STATES[state.ordinal() - 1], false)], entry);
                }
                if (waxed) continue;
                this.entries.put(variant, entries);
            }
        }
    }

    protected <T extends Block> BlockEntry<?> createEntry(AbstractRegistrate<?> registrate, Variant<T> variant, WeatheringCopper.WeatherState state, boolean waxed) {
        Object name = "";
        if (waxed) {
            name = (String)name + "waxed_";
        }
        name = (String)name + CopperBlockSet.getWeatherStatePrefix(state);
        name = (String)name + this.name;
        String suffix = variant.getSuffix();
        if (!suffix.equals("")) {
            name = Lang.nonPluralId((String)name);
        }
        name = (String)name + suffix;
        Supplier<Block> baseBlock = BASE_BLOCKS.get(state);
        BlockBuilder builder = ((BlockBuilder)((BlockBuilder)registrate.block((String)name, variant.getFactory(this, state, waxed)).initialProperties(() -> (Block)baseBlock.get()).loot((lt, block) -> variant.generateLootTable((RegistrateBlockLootTables)lt, (Object)block, this, state, waxed)).blockstate((ctx, prov) -> variant.generateBlockState((DataGenContext)ctx, (RegistrateBlockstateProvider)prov, this, state, waxed)).transform(TagGen.pickaxeOnly())).onRegister(block -> this.onRegister.accept((Object)state, block))).tag(new TagKey[]{BlockTags.NEEDS_STONE_TOOL}).simpleItem();
        if (variant == BlockVariant.INSTANCE && state == WeatheringCopper.WeatherState.UNAFFECTED && !waxed) {
            builder.recipe((arg_0, arg_1) -> this.mainBlockRecipe.accept(arg_0, arg_1));
        } else {
            builder.recipe((ctx, prov) -> {
                if (waxed) {
                    Block unwaxed = (Block)this.get(variant, state, false).get();
                    ShapelessRecipeBuilder.shapeless((RecipeCategory)RecipeCategory.BUILDING_BLOCKS, (ItemLike)((ItemLike)ctx.get())).requires((ItemLike)unwaxed).requires((ItemLike)Items.HONEYCOMB).unlockedBy("has_unwaxed", RegistrateRecipeProvider.has((ItemLike)unwaxed)).save((RecipeOutput)prov, ResourceLocation.fromNamespaceAndPath((String)ctx.getId().getNamespace(), (String)("crafting/" + this.generalDirectory + ctx.getName() + "_from_honeycomb")));
                }
                variant.generateRecipes(this.get(BlockVariant.INSTANCE, state, waxed), (DataGenContext)ctx, (RegistrateRecipeProvider)prov);
            });
        }
        if (variant == StairVariant.INSTANCE) {
            builder.tag(new TagKey[]{BlockTags.STAIRS});
        }
        if (variant == SlabVariant.INSTANCE) {
            builder.tag(new TagKey[]{BlockTags.SLABS});
        }
        return builder.register();
    }

    protected int getIndex(WeatheringCopper.WeatherState state, boolean waxed) {
        return state.ordinal() + (waxed ? WEATHER_STATE_COUNT : 0);
    }

    public String getName() {
        return this.name;
    }

    public String getEndTextureName() {
        return this.endTextureName;
    }

    public Variant<?>[] getVariants() {
        return this.variants;
    }

    public boolean hasVariant(Variant<?> variant) {
        return ArrayUtils.contains((Object[])this.variants, variant);
    }

    public BlockEntry<?> get(Variant<?> variant, WeatheringCopper.WeatherState state, boolean waxed) {
        BlockEntry<?>[] entries = this.entries.get(variant);
        if (entries != null) {
            return entries[this.getIndex(state, waxed)];
        }
        return null;
    }

    public BlockEntry<?> getStandard() {
        return this.get(BlockVariant.INSTANCE, WeatheringCopper.WeatherState.UNAFFECTED, false);
    }

    public static String getWeatherStatePrefix(WeatheringCopper.WeatherState state) {
        if (state != WeatheringCopper.WeatherState.UNAFFECTED) {
            return state.name().toLowerCase(Locale.ROOT) + "_";
        }
        return "";
    }

    static {
        BASE_BLOCKS.put(WeatheringCopper.WeatherState.UNAFFECTED, () -> Blocks.COPPER_BLOCK);
        BASE_BLOCKS.put(WeatheringCopper.WeatherState.EXPOSED, () -> Blocks.EXPOSED_COPPER);
        BASE_BLOCKS.put(WeatheringCopper.WeatherState.WEATHERED, () -> Blocks.WEATHERED_COPPER);
        BASE_BLOCKS.put(WeatheringCopper.WeatherState.OXIDIZED, () -> Blocks.OXIDIZED_COPPER);
        DEFAULT_VARIANTS = new Variant[]{BlockVariant.INSTANCE, SlabVariant.INSTANCE, StairVariant.INSTANCE};
    }

    public static interface Variant<T extends Block> {
        public String getSuffix();

        public NonNullFunction<BlockBehaviour.Properties, T> getFactory(CopperBlockSet var1, WeatheringCopper.WeatherState var2, boolean var3);

        default public void generateLootTable(RegistrateBlockLootTables lootTable, T block, CopperBlockSet blocks, WeatheringCopper.WeatherState state, boolean waxed) {
            lootTable.dropSelf(block);
        }

        public void generateRecipes(BlockEntry<?> var1, DataGenContext<Block, T> var2, RegistrateRecipeProvider var3);

        public void generateBlockState(DataGenContext<Block, T> var1, RegistrateBlockstateProvider var2, CopperBlockSet var3, WeatheringCopper.WeatherState var4, boolean var5);
    }

    public static class BlockVariant
    implements Variant<Block> {
        public static final BlockVariant INSTANCE = new BlockVariant();

        protected BlockVariant() {
        }

        @Override
        public String getSuffix() {
            return "";
        }

        @Override
        public NonNullFunction<BlockBehaviour.Properties, Block> getFactory(CopperBlockSet blocks, WeatheringCopper.WeatherState state, boolean waxed) {
            if (waxed) {
                return Block::new;
            }
            return p -> new WeatheringCopperFullBlock(state, p);
        }

        @Override
        public void generateBlockState(DataGenContext<Block, Block> ctx, RegistrateBlockstateProvider prov, CopperBlockSet blocks, WeatheringCopper.WeatherState state, boolean waxed) {
            Block block = (Block)ctx.get();
            String path = RegisteredObjectsHelper.getKeyOrThrow((Block)block).getPath();
            String baseLoc = "block/" + blocks.generalDirectory + CopperBlockSet.getWeatherStatePrefix(state);
            ResourceLocation texture = prov.modLoc(baseLoc + blocks.getName());
            if (Objects.equals(blocks.getName(), blocks.getEndTextureName())) {
                prov.simpleBlock(block, (ModelFile)prov.models().cubeAll(path, texture));
            } else {
                ResourceLocation endTexture = prov.modLoc(baseLoc + blocks.getEndTextureName());
                prov.simpleBlock(block, (ModelFile)prov.models().cubeColumn(path, texture, endTexture));
            }
        }

        @Override
        public void generateRecipes(BlockEntry<?> blockVariant, DataGenContext<Block, Block> ctx, RegistrateRecipeProvider prov) {
        }
    }

    public static class StairVariant
    implements Variant<StairBlock> {
        public static final StairVariant INSTANCE = new StairVariant(BlockVariant.INSTANCE);
        protected final Variant<?> parent;

        protected StairVariant(Variant<?> parent) {
            this.parent = parent;
        }

        @Override
        public String getSuffix() {
            return "_stairs";
        }

        @Override
        public NonNullFunction<BlockBehaviour.Properties, StairBlock> getFactory(CopperBlockSet blocks, WeatheringCopper.WeatherState state, boolean waxed) {
            if (!blocks.hasVariant(this.parent)) {
                throw new IllegalStateException("Cannot add StairVariant '" + String.valueOf(this) + "' without parent Variant '" + this.parent.toString() + "'!");
            }
            if (waxed) {
                return CreateCopperStairBlock::new;
            }
            return p -> new CreateWeatheringCopperStairBlock(state, (BlockBehaviour.Properties)p);
        }

        @Override
        public void generateBlockState(DataGenContext<Block, StairBlock> ctx, RegistrateBlockstateProvider prov, CopperBlockSet blocks, WeatheringCopper.WeatherState state, boolean waxed) {
            String baseLoc = "block/" + blocks.generalDirectory + CopperBlockSet.getWeatherStatePrefix(state);
            ResourceLocation texture = prov.modLoc(baseLoc + blocks.getName());
            ResourceLocation endTexture = prov.modLoc(baseLoc + blocks.getEndTextureName());
            prov.stairsBlock((StairBlock)ctx.get(), texture, endTexture, endTexture);
        }

        @Override
        public void generateRecipes(BlockEntry<?> blockVariant, DataGenContext<Block, StairBlock> ctx, RegistrateRecipeProvider prov) {
            prov.stairs(DataIngredient.items((ItemLike)((Block)blockVariant.get()), (ItemLike[])new Block[0]), RecipeCategory.BUILDING_BLOCKS, () -> ctx.get(), null, true);
        }
    }

    public static class SlabVariant
    implements Variant<SlabBlock> {
        public static final SlabVariant INSTANCE = new SlabVariant();

        protected SlabVariant() {
        }

        @Override
        public String getSuffix() {
            return "_slab";
        }

        @Override
        public NonNullFunction<BlockBehaviour.Properties, SlabBlock> getFactory(CopperBlockSet blocks, WeatheringCopper.WeatherState state, boolean waxed) {
            if (waxed) {
                return SlabBlock::new;
            }
            return p -> new WeatheringCopperSlabBlock(state, p);
        }

        @Override
        public void generateLootTable(RegistrateBlockLootTables lootTable, SlabBlock block, CopperBlockSet blocks, WeatheringCopper.WeatherState state, boolean waxed) {
            lootTable.add((Block)block, lootTable.createSlabItemTable((Block)block));
        }

        @Override
        public void generateBlockState(DataGenContext<Block, SlabBlock> ctx, RegistrateBlockstateProvider prov, CopperBlockSet blocks, WeatheringCopper.WeatherState state, boolean waxed) {
            ResourceLocation fullModel = prov.modLoc("block/" + CopperBlockSet.getWeatherStatePrefix(state) + blocks.getName());
            String baseLoc = "block/" + blocks.generalDirectory + CopperBlockSet.getWeatherStatePrefix(state);
            ResourceLocation texture = prov.modLoc(baseLoc + blocks.getName());
            ResourceLocation endTexture = prov.modLoc(baseLoc + blocks.getEndTextureName());
            prov.slabBlock((SlabBlock)ctx.get(), fullModel, texture, endTexture, endTexture);
        }

        @Override
        public void generateRecipes(BlockEntry<?> blockVariant, DataGenContext<Block, SlabBlock> ctx, RegistrateRecipeProvider prov) {
            prov.slab(DataIngredient.items((ItemLike)((Block)blockVariant.get()), (ItemLike[])new Block[0]), RecipeCategory.BUILDING_BLOCKS, () -> ctx.get(), null, true);
        }
    }
}
