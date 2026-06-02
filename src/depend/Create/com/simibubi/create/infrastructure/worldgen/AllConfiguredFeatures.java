/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.data.worldgen.BootstrapContext
 *  net.minecraft.data.worldgen.features.FeatureUtils
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.feature.ConfiguredFeature
 *  net.minecraft.world.level.levelgen.feature.Feature
 *  net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration
 *  net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
 *  net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration$TargetBlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest
 *  net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest
 */
package com.simibubi.create.infrastructure.worldgen;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.infrastructure.worldgen.AllFeatures;
import com.simibubi.create.infrastructure.worldgen.AllLayerPatterns;
import com.simibubi.create.infrastructure.worldgen.LayerPattern;
import com.simibubi.create.infrastructure.worldgen.LayeredOreConfiguration;
import com.simibubi.create.infrastructure.worldgen.LayeredOreFeature;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

public class AllConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> ZINC_ORE = AllConfiguredFeatures.key("zinc_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STRIATED_ORES_OVERWORLD = AllConfiguredFeatures.key("striated_ores_overworld");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STRIATED_ORES_NETHER = AllConfiguredFeatures.key("striated_ores_nether");

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String name) {
        return ResourceKey.create((ResourceKey)Registries.CONFIGURED_FEATURE, (ResourceLocation)Create.asResource(name));
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
        TagMatchTest stoneOreReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        TagMatchTest deepslateOreReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        List<OreConfiguration.TargetBlockState> zincTargetStates = List.of(OreConfiguration.target((RuleTest)stoneOreReplaceables, (BlockState)((Block)AllBlocks.ZINC_ORE.get()).defaultBlockState()), OreConfiguration.target((RuleTest)deepslateOreReplaceables, (BlockState)((Block)AllBlocks.DEEPSLATE_ZINC_ORE.get()).defaultBlockState()));
        FeatureUtils.register(ctx, ZINC_ORE, (Feature)Feature.ORE, (FeatureConfiguration)new OreConfiguration(zincTargetStates, 12));
        List<LayerPattern> overworldLayerPatterns = List.of((LayerPattern)AllLayerPatterns.SCORIA.get(), (LayerPattern)AllLayerPatterns.CINNABAR.get(), (LayerPattern)AllLayerPatterns.MAGNETITE.get(), (LayerPattern)AllLayerPatterns.MALACHITE.get(), (LayerPattern)AllLayerPatterns.LIMESTONE.get(), (LayerPattern)AllLayerPatterns.OCHRESTONE.get());
        FeatureUtils.register(ctx, STRIATED_ORES_OVERWORLD, (Feature)((LayeredOreFeature)((Object)AllFeatures.LAYERED_ORE.get())), (FeatureConfiguration)new LayeredOreConfiguration(overworldLayerPatterns, 32, 0.0f));
        List<LayerPattern> netherLayerPatterns = List.of((LayerPattern)AllLayerPatterns.SCORIA_NETHER.get(), (LayerPattern)AllLayerPatterns.SCORCHIA_NETHER.get());
        FeatureUtils.register(ctx, STRIATED_ORES_NETHER, (Feature)((LayeredOreFeature)((Object)AllFeatures.LAYERED_ORE.get())), (FeatureConfiguration)new LayeredOreConfiguration(netherLayerPatterns, 32, 0.0f));
    }
}
