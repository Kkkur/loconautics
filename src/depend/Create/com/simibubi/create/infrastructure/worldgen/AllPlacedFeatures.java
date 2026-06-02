/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.data.worldgen.BootstrapContext
 *  net.minecraft.data.worldgen.placement.PlacementUtils
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.levelgen.VerticalAnchor
 *  net.minecraft.world.level.levelgen.placement.CountPlacement
 *  net.minecraft.world.level.levelgen.placement.HeightRangePlacement
 *  net.minecraft.world.level.levelgen.placement.InSquarePlacement
 *  net.minecraft.world.level.levelgen.placement.PlacedFeature
 *  net.minecraft.world.level.levelgen.placement.PlacementModifier
 *  net.minecraft.world.level.levelgen.placement.RarityFilter
 */
package com.simibubi.create.infrastructure.worldgen;

import com.simibubi.create.Create;
import com.simibubi.create.infrastructure.worldgen.AllConfiguredFeatures;
import com.simibubi.create.infrastructure.worldgen.ConfigPlacementFilter;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class AllPlacedFeatures {
    public static final ResourceKey<PlacedFeature> ZINC_ORE = AllPlacedFeatures.key("zinc_ore");
    public static final ResourceKey<PlacedFeature> STRIATED_ORES_OVERWORLD = AllPlacedFeatures.key("striated_ores_overworld");
    public static final ResourceKey<PlacedFeature> STRIATED_ORES_NETHER = AllPlacedFeatures.key("striated_ores_nether");

    private static ResourceKey<PlacedFeature> key(String name) {
        return ResourceKey.create((ResourceKey)Registries.PLACED_FEATURE, (ResourceLocation)Create.asResource(name));
    }

    public static void bootstrap(BootstrapContext<PlacedFeature> ctx) {
        HolderGetter featureLookup = ctx.lookup(Registries.CONFIGURED_FEATURE);
        Holder.Reference zincOre = featureLookup.getOrThrow(AllConfiguredFeatures.ZINC_ORE);
        Holder.Reference striatedOresOverworld = featureLookup.getOrThrow(AllConfiguredFeatures.STRIATED_ORES_OVERWORLD);
        Holder.Reference striatedOresNether = featureLookup.getOrThrow(AllConfiguredFeatures.STRIATED_ORES_NETHER);
        PlacementUtils.register(ctx, ZINC_ORE, (Holder)zincOre, AllPlacedFeatures.placement((PlacementModifier)CountPlacement.of((int)8), -63, 70));
        PlacementUtils.register(ctx, STRIATED_ORES_OVERWORLD, (Holder)striatedOresOverworld, AllPlacedFeatures.placement((PlacementModifier)RarityFilter.onAverageOnceEvery((int)18), -30, 70));
        PlacementUtils.register(ctx, STRIATED_ORES_NETHER, (Holder)striatedOresNether, AllPlacedFeatures.placement((PlacementModifier)RarityFilter.onAverageOnceEvery((int)18), 40, 90));
    }

    private static List<PlacementModifier> placement(PlacementModifier frequency, int minHeight, int maxHeight) {
        return List.of(frequency, InSquarePlacement.spread(), HeightRangePlacement.uniform((VerticalAnchor)VerticalAnchor.absolute((int)minHeight), (VerticalAnchor)VerticalAnchor.absolute((int)maxHeight)), ConfigPlacementFilter.INSTANCE);
    }
}
