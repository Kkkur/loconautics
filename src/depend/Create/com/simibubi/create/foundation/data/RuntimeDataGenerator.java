/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Multimap
 *  com.google.gson.JsonElement
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.createmod.catnip.codecs.CatnipCodecUtils
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagEntry
 *  net.minecraft.tags.TagFile
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.ConcretePowderBlock
 *  net.neoforged.neoforge.common.conditions.WithConditions
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package com.simibubi.create.foundation.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.data.recipe.Mods;
import com.simibubi.create.foundation.mixin.accessor.ConcretePowderBlockAccessor;
import com.simibubi.create.foundation.pack.DynamicPack;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.neoforged.neoforge.common.conditions.WithConditions;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class RuntimeDataGenerator {
    private static final Pattern STRIPPED_WOODS_PREFIX_REGEX = Pattern.compile("(\\w*)??stripped_(\\w*)(_log|_wood|_stem|_hyphae|_block|(?<!_)wood)()$");
    private static final Pattern STRIPPED_WOOD_SUFFIX_REGEX = Pattern.compile("(\\w*)(_log|_wood|_stem|_hyphae|_block|(?<!_)wood)(\\w*)_stripped(\\w*)");
    private static final Pattern NON_STRIPPED_WOODS_REGEX = Pattern.compile("^(?!stripped_)([a-z_]+)(_log|_wood|_stem|_hyphae|(?<!bioshroom)_block)(([a-z_]+)(?<!_stripped))?$");
    private static final Multimap<ResourceLocation, TagEntry> TAGS = HashMultimap.create();
    private static final Object2ObjectOpenHashMap<ResourceLocation, JsonElement> JSON_FILES = new Object2ObjectOpenHashMap();
    private static final Map<ResourceLocation, ResourceLocation> MISMATCHED_WOOD_NAMES = ImmutableMap.builder().put((Object)Mods.ARS_N.asResource("blue_archwood"), (Object)Mods.ARS_N.asResource("archwood")).put((Object)Mods.DD.asResource("blooming"), (Object)Mods.DD.asResource("bloom")).build();

    public static void insertIntoPack(DynamicPack dynamicPack) {
        for (ResourceLocation resourceLocation : BuiltInRegistries.ITEM.keySet()) {
            RuntimeDataGenerator.cuttingRecipes(resourceLocation);
            RuntimeDataGenerator.washingRecipes(resourceLocation);
        }
        Create.LOGGER.info("Created {} recipes which will be injected into the game", (Object)JSON_FILES.size());
        JSON_FILES.forEach(dynamicPack::put);
        Create.LOGGER.info("Created {} tags which will be injected into the game", (Object)TAGS.size());
        for (Map.Entry entry : TAGS.asMap().entrySet()) {
            TagFile tagFile = new TagFile(new ArrayList((Collection)entry.getValue()), false);
            dynamicPack.put(((ResourceLocation)entry.getKey()).withPrefix("tags/item/"), (JsonElement)TagFile.CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)tagFile).result().orElseThrow());
        }
        JSON_FILES.clear();
        JSON_FILES.trim();
        TAGS.clear();
    }

    private static void cuttingRecipes(ResourceLocation itemId) {
        boolean hasFoundMatch;
        String path = itemId.getPath();
        Matcher match = STRIPPED_WOODS_PREFIX_REGEX.matcher(path);
        boolean strippedInPrefix = hasFoundMatch = match.find();
        if (!hasFoundMatch) {
            match = STRIPPED_WOOD_SUFFIX_REGEX.matcher(path);
            hasFoundMatch = match.find();
        }
        boolean noStrippedVariant = false;
        if (!(hasFoundMatch || BuiltInRegistries.ITEM.containsKey(itemId.withPrefix("stripped_")) || BuiltInRegistries.ITEM.containsKey(itemId.withSuffix("_stripped")))) {
            match = NON_STRIPPED_WOODS_REGEX.matcher(path);
            hasFoundMatch = match.find();
            noStrippedVariant = true;
        }
        if (hasFoundMatch) {
            int planksCount;
            String prefix = strippedInPrefix && match.group(1) != null ? match.group(1) : "";
            String suffix = !strippedInPrefix && !noStrippedVariant ? match.group(3) + match.group(4) : "";
            String type = match.group(strippedInPrefix ? 3 : 2);
            ResourceLocation matched_name = itemId.withPath(match.group(strippedInPrefix ? 2 : 1));
            ResourceLocation base = matched_name.withSuffix(type.equals("wood") ? "wood" : "");
            base = MISMATCHED_WOOD_NAMES.getOrDefault(base, base);
            ResourceLocation nonStrippedId = matched_name.withSuffix(type).withPrefix(prefix).withSuffix(suffix);
            ResourceLocation planksId = base.withSuffix("_planks");
            ResourceLocation stairsId = base.withSuffix(base.getNamespace().equals(Mods.BTN.getId()) ? "_planks_stairs" : "_stairs");
            ResourceLocation slabId = base.withSuffix(base.getNamespace().equals(Mods.BTN.getId()) ? "_planks_slab" : "_slab");
            ResourceLocation fenceId = base.withSuffix("_fence");
            ResourceLocation fenceGateId = base.withSuffix("_fence_gate");
            ResourceLocation doorId = base.withSuffix("_door");
            ResourceLocation trapdoorId = base.withSuffix("_trapdoor");
            ResourceLocation pressurePlateId = base.withSuffix("_pressure_plate");
            ResourceLocation buttonId = base.withSuffix("_button");
            ResourceLocation signId = base.withSuffix("_sign");
            int n = planksCount = type.contains("block") ? 3 : 6;
            if (!noStrippedVariant) {
                if (BuiltInRegistries.ITEM.containsKey(nonStrippedId)) {
                    RuntimeDataGenerator.simpleWoodRecipe(nonStrippedId, itemId);
                }
                RuntimeDataGenerator.simpleWoodRecipe(itemId, planksId, planksCount);
            } else if (BuiltInRegistries.ITEM.containsKey(planksId)) {
                ResourceLocation tag = Create.asResource("runtime_generated/compat/" + itemId.getNamespace() + "/" + base.getPath());
                RuntimeDataGenerator.insertIntoTag(tag, itemId);
                RuntimeDataGenerator.simpleWoodRecipe((TagKey<Item>)TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)tag), planksId, planksCount);
            }
            if (!path.contains("_wood") && !path.contains("_hyphae") && BuiltInRegistries.ITEM.containsKey(planksId)) {
                RuntimeDataGenerator.simpleWoodRecipe(planksId, stairsId);
                RuntimeDataGenerator.simpleWoodRecipe(planksId, slabId, 2);
                RuntimeDataGenerator.simpleWoodRecipe(planksId, fenceId);
                RuntimeDataGenerator.simpleWoodRecipe(planksId, fenceGateId);
                RuntimeDataGenerator.simpleWoodRecipe(planksId, doorId);
                RuntimeDataGenerator.simpleWoodRecipe(planksId, trapdoorId);
                RuntimeDataGenerator.simpleWoodRecipe(planksId, pressurePlateId);
                RuntimeDataGenerator.simpleWoodRecipe(planksId, buttonId);
                RuntimeDataGenerator.simpleWoodRecipe(planksId, signId);
            }
        }
    }

    private static void washingRecipes(ResourceLocation itemId) {
        Block block = (Block)BuiltInRegistries.BLOCK.get(itemId);
        if (block instanceof ConcretePowderBlock) {
            ConcretePowderBlock concretePowderBlock = (ConcretePowderBlock)block;
            Block concreteBlock = ((ConcretePowderBlockAccessor)concretePowderBlock).create$getConcrete();
            RuntimeDataGenerator.simpleSplashingRecipe(itemId, BuiltInRegistries.BLOCK.getKey((Object)concreteBlock));
        }
    }

    private static void insertIntoTag(ResourceLocation tag, ResourceLocation itemId) {
        if (BuiltInRegistries.ITEM.containsKey(itemId)) {
            TAGS.put((Object)tag, (Object)TagEntry.optionalElement((ResourceLocation)itemId));
        }
    }

    private static void simpleWoodRecipe(ResourceLocation inputId, ResourceLocation outputId) {
        RuntimeDataGenerator.simpleWoodRecipe(inputId, outputId, 1);
    }

    private static void simpleWoodRecipe(ResourceLocation inputId, ResourceLocation outputId, int amount) {
        if (BuiltInRegistries.ITEM.containsKey(outputId)) {
            ((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)new StandardBuilder<CuttingRecipe>(inputId.getNamespace(), CuttingRecipe::new, inputId.getPath(), outputId.getPath()).require((ItemLike)BuiltInRegistries.ITEM.get(inputId))).output((ItemLike)BuiltInRegistries.ITEM.get(outputId), amount)).duration(50)).build();
        }
    }

    private static void simpleWoodRecipe(TagKey<Item> inputTag, ResourceLocation outputId, int amount) {
        if (BuiltInRegistries.ITEM.containsKey(outputId)) {
            ((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)new StandardBuilder<CuttingRecipe>(inputTag.location().getNamespace(), CuttingRecipe::new, "tag_" + inputTag.location().getPath(), outputId.getPath()).require(inputTag)).output((ItemLike)BuiltInRegistries.ITEM.get(outputId), amount)).duration(50)).build();
        }
    }

    private static void simpleSplashingRecipe(ResourceLocation first, ResourceLocation second) {
        ((StandardProcessingRecipe.Builder)((StandardProcessingRecipe.Builder)new StandardBuilder<SplashingRecipe>(first.getNamespace(), SplashingRecipe::new, first.getPath(), second.getPath()).require((ItemLike)BuiltInRegistries.BLOCK.get(first))).output((ItemLike)BuiltInRegistries.BLOCK.get(second))).build();
    }

    private static class StandardBuilder<T extends StandardProcessingRecipe<?>>
    extends StandardProcessingRecipe.Builder<T> {
        public StandardBuilder(String modid, StandardProcessingRecipe.Factory<T> factory, String from, String to) {
            super(factory, Create.asResource("runtime_generated/compat/" + modid + "/" + from + "_to_" + to));
        }

        @Override
        public T build() {
            StandardProcessingRecipe recipe = (StandardProcessingRecipe)super.build();
            IRecipeTypeInfo recipeType = recipe.getTypeInfo();
            ResourceLocation typeId = recipeType.getId();
            if (!(recipeType.getSerializer() instanceof StandardProcessingRecipe.Serializer)) {
                throw new IllegalStateException("Cannot datagen ProcessingRecipe of type: " + String.valueOf(typeId));
            }
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath((String)this.recipeId.getNamespace(), (String)(typeId.getPath() + "/" + this.recipeId.getPath()));
            Optional serialized = CatnipCodecUtils.encode((Codec)Recipe.CONDITIONAL_CODEC, (DynamicOps)JsonOps.INSTANCE, Optional.of(new WithConditions((Object)recipe)));
            serialized.ifPresent(r -> JSON_FILES.put((Object)id.withPrefix("recipe/"), r));
            return (T)recipe;
        }
    }
}
