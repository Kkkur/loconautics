/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.ChangeOverTimeBlock
 *  net.minecraft.world.level.block.WeatheringCopper$WeatherState
 *  net.minecraft.world.level.block.WeatheringCopperStairBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.foundation.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperStairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

public class CreateWeatheringCopperStairBlock
extends WeatheringCopperStairBlock {
    public static final MapCodec<WeatheringCopperStairBlock> CODEC = RecordCodecBuilder.mapCodec(i -> i.group((App)WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(ChangeOverTimeBlock::getAge), (App)CreateWeatheringCopperStairBlock.propertiesCodec()).apply((Applicative)i, CreateWeatheringCopperStairBlock::new));

    public CreateWeatheringCopperStairBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties) {
        super(weatherState, Blocks.AIR.defaultBlockState(), properties);
    }

    public float getExplosionResistance() {
        return this.explosionResistance;
    }

    @NotNull
    public MapCodec<WeatheringCopperStairBlock> codec() {
        return CODEC;
    }
}
