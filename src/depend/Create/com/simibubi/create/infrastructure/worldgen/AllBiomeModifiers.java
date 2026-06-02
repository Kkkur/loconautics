/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.HolderSet
 *  net.minecraft.core.HolderSet$Named
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.data.worldgen.BootstrapContext
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BiomeTags
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.levelgen.GenerationStep$Decoration
 *  net.minecraft.world.level.levelgen.placement.PlacedFeature
 *  net.neoforged.neoforge.common.world.BiomeModifier
 *  net.neoforged.neoforge.common.world.BiomeModifiers$AddFeaturesBiomeModifier
 *  net.neoforged.neoforge.registries.NeoForgeRegistries$Keys
 */
package com.simibubi.create.infrastructure.worldgen;

import com.simibubi.create.Create;
import com.simibubi.create.infrastructure.worldgen.AllPlacedFeatures;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AllBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ZINC_ORE = AllBiomeModifiers.key("zinc_ore");
    public static final ResourceKey<BiomeModifier> STRIATED_ORES_OVERWORLD = AllBiomeModifiers.key("striated_ores_overworld");
    public static final ResourceKey<BiomeModifier> STRIATED_ORES_NETHER = AllBiomeModifiers.key("striated_ores_nether");

    private static ResourceKey<BiomeModifier> key(String name) {
        return ResourceKey.create((ResourceKey)NeoForgeRegistries.Keys.BIOME_MODIFIERS, (ResourceLocation)Create.asResource(name));
    }

    public static void bootstrap(BootstrapContext<BiomeModifier> ctx) {
        HolderGetter biomeLookup = ctx.lookup(Registries.BIOME);
        HolderSet.Named isOverworld = biomeLookup.getOrThrow(BiomeTags.IS_OVERWORLD);
        HolderSet.Named isNether = biomeLookup.getOrThrow(BiomeTags.IS_NETHER);
        HolderGetter featureLookup = ctx.lookup(Registries.PLACED_FEATURE);
        Holder.Reference zincOre = featureLookup.getOrThrow(AllPlacedFeatures.ZINC_ORE);
        Holder.Reference striatedOresOverworld = featureLookup.getOrThrow(AllPlacedFeatures.STRIATED_ORES_OVERWORLD);
        Holder.Reference striatedOresNether = featureLookup.getOrThrow(AllPlacedFeatures.STRIATED_ORES_NETHER);
        ctx.register(ZINC_ORE, (Object)AllBiomeModifiers.addOre((HolderSet<Biome>)isOverworld, (Holder<PlacedFeature>)zincOre));
        ctx.register(STRIATED_ORES_OVERWORLD, (Object)AllBiomeModifiers.addOre((HolderSet<Biome>)isOverworld, (Holder<PlacedFeature>)striatedOresOverworld));
        ctx.register(STRIATED_ORES_NETHER, (Object)AllBiomeModifiers.addOre((HolderSet<Biome>)isNether, (Holder<PlacedFeature>)striatedOresNether));
    }

    private static BiomeModifiers.AddFeaturesBiomeModifier addOre(HolderSet<Biome> biomes, Holder<PlacedFeature> feature) {
        return new BiomeModifiers.AddFeaturesBiomeModifier(biomes, (HolderSet)HolderSet.direct((Holder[])new Holder[]{feature}), GenerationStep.Decoration.UNDERGROUND_ORES);
    }
}
