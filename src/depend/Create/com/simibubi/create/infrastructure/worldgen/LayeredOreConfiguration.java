/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration
 */
package com.simibubi.create.infrastructure.worldgen;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.infrastructure.worldgen.LayerPattern;
import java.util.List;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class LayeredOreConfiguration
implements FeatureConfiguration {
    public static final Codec<LayeredOreConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.list(LayerPattern.CODEC).fieldOf("layer_patterns").forGetter(config -> config.layerPatterns), (App)Codec.intRange((int)0, (int)64).fieldOf("size").forGetter(config -> config.size), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("discard_chance_on_air_exposure").forGetter(config -> Float.valueOf(config.discardChanceOnAirExposure))).apply((Applicative)instance, LayeredOreConfiguration::new));
    public final List<LayerPattern> layerPatterns;
    public final int size;
    public final float discardChanceOnAirExposure;

    public LayeredOreConfiguration(List<LayerPattern> layerPatterns, int size, float discardChanceOnAirExposure) {
        this.layerPatterns = layerPatterns;
        this.size = size;
        this.discardChanceOnAirExposure = discardChanceOnAirExposure;
    }
}
