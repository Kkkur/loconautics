/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
 *  net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration$TargetBlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest
 *  net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest
 */
package com.simibubi.create.infrastructure.worldgen;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.infrastructure.worldgen.LayerPattern;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.data.Couple;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

public static class LayerPattern.Layer.Builder {
    private static final RuleTest STONE_ORE_REPLACEABLES = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
    private static final RuleTest DEEPSLATE_ORE_REPLACEABLES = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
    private static final RuleTest NETHER_ORE_REPLACEABLES = new TagMatchTest(BlockTags.BASE_STONE_NETHER);
    private final List<List<OreConfiguration.TargetBlockState>> targets = new ArrayList<List<OreConfiguration.TargetBlockState>>();
    private int minSize = 1;
    private int maxSize = 1;
    private int weight = 1;
    private boolean netherMode;

    public LayerPattern.Layer.Builder block(NonNullSupplier<? extends Block> block) {
        return this.block((Block)block.get());
    }

    public LayerPattern.Layer.Builder passiveBlock() {
        return this.blocks(Blocks.STONE.defaultBlockState(), Blocks.DEEPSLATE.defaultBlockState());
    }

    public LayerPattern.Layer.Builder block(Block block) {
        if (this.netherMode) {
            this.targets.add((List<OreConfiguration.TargetBlockState>)ImmutableList.of((Object)OreConfiguration.target((RuleTest)NETHER_ORE_REPLACEABLES, (BlockState)block.defaultBlockState())));
            return this;
        }
        return this.blocks(block.defaultBlockState(), block.defaultBlockState());
    }

    public LayerPattern.Layer.Builder blocks(Block block, Block deepblock) {
        return this.blocks(block.defaultBlockState(), deepblock.defaultBlockState());
    }

    public LayerPattern.Layer.Builder blocks(Couple<NonNullSupplier<? extends Block>> blocksByDepth) {
        return this.blocks(((Block)((NonNullSupplier)blocksByDepth.getFirst()).get()).defaultBlockState(), ((Block)((NonNullSupplier)blocksByDepth.getSecond()).get()).defaultBlockState());
    }

    private LayerPattern.Layer.Builder blocks(BlockState stone, BlockState deepslate) {
        this.targets.add((List<OreConfiguration.TargetBlockState>)ImmutableList.of((Object)OreConfiguration.target((RuleTest)STONE_ORE_REPLACEABLES, (BlockState)stone), (Object)OreConfiguration.target((RuleTest)DEEPSLATE_ORE_REPLACEABLES, (BlockState)deepslate)));
        return this;
    }

    public LayerPattern.Layer.Builder weight(int weight) {
        this.weight = weight;
        return this;
    }

    public LayerPattern.Layer.Builder size(int min, int max) {
        this.minSize = min;
        this.maxSize = max;
        return this;
    }

    public LayerPattern.Layer build() {
        return new LayerPattern.Layer(this.targets, this.minSize, this.maxSize, this.weight);
    }
}
