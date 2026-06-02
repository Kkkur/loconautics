/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.foundation.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;

public class CreateCopperStairBlock
extends StairBlock {
    public static final MapCodec<StairBlock> CODEC = RecordCodecBuilder.mapCodec(i -> i.group((App)CreateCopperStairBlock.propertiesCodec()).apply((Applicative)i, CreateCopperStairBlock::new));

    public CreateCopperStairBlock(BlockBehaviour.Properties properties) {
        super(Blocks.AIR.defaultBlockState(), properties);
    }

    public float getExplosionResistance() {
        return this.explosionResistance;
    }

    @NotNull
    public MapCodec<? extends StairBlock> codec() {
        return CODEC;
    }
}
