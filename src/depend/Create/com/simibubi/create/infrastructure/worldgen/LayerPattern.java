/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
 *  net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration$TargetBlockState
 *  net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest
 *  net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.infrastructure.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.createmod.catnip.data.Couple;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LayerPattern {
    public static final Codec<LayerPattern> CODEC = Codec.list(Layer.CODEC).xmap(LayerPattern::new, pattern -> pattern.layers);
    public final List<Layer> layers;

    public LayerPattern(List<Layer> layers) {
        this.layers = layers;
    }

    public Layer rollNext(@Nullable Layer previous, RandomSource random) {
        int totalWeight = 0;
        for (Layer layer : this.layers) {
            if (layer == previous) continue;
            totalWeight += layer.weight;
        }
        int rolled = random.nextInt(totalWeight);
        for (Layer layer : this.layers) {
            if (layer == previous || (rolled -= layer.weight) >= 0) continue;
            return layer;
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Layer {
        public static final Codec<Layer> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.list((Codec)Codec.list((Codec)OreConfiguration.TargetBlockState.CODEC)).fieldOf("targets").forGetter(layer -> layer.targets), (App)Codec.intRange((int)0, (int)Integer.MAX_VALUE).fieldOf("min_size").forGetter(layer -> layer.minSize), (App)Codec.intRange((int)0, (int)Integer.MAX_VALUE).fieldOf("max_size").forGetter(layer -> layer.maxSize), (App)Codec.intRange((int)0, (int)Integer.MAX_VALUE).fieldOf("weight").forGetter(layer -> layer.weight)).apply((Applicative)instance, Layer::new));
        public final List<List<OreConfiguration.TargetBlockState>> targets;
        public final int minSize;
        public final int maxSize;
        public final int weight;

        public Layer(List<List<OreConfiguration.TargetBlockState>> targets, int minSize, int maxSize, int weight) {
            this.targets = targets;
            this.minSize = minSize;
            this.maxSize = maxSize;
            this.weight = weight;
        }

        public List<OreConfiguration.TargetBlockState> rollBlock(RandomSource random) {
            if (this.targets.size() == 1) {
                return this.targets.get(0);
            }
            return this.targets.get(random.nextInt(this.targets.size()));
        }

        public static class Builder {
            private static final RuleTest STONE_ORE_REPLACEABLES = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
            private static final RuleTest DEEPSLATE_ORE_REPLACEABLES = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
            private static final RuleTest NETHER_ORE_REPLACEABLES = new TagMatchTest(BlockTags.BASE_STONE_NETHER);
            private final List<List<OreConfiguration.TargetBlockState>> targets = new ArrayList<List<OreConfiguration.TargetBlockState>>();
            private int minSize = 1;
            private int maxSize = 1;
            private int weight = 1;
            private boolean netherMode;

            public Builder block(NonNullSupplier<? extends Block> block) {
                return this.block((Block)block.get());
            }

            public Builder passiveBlock() {
                return this.blocks(Blocks.STONE.defaultBlockState(), Blocks.DEEPSLATE.defaultBlockState());
            }

            public Builder block(Block block) {
                if (this.netherMode) {
                    this.targets.add((List<OreConfiguration.TargetBlockState>)ImmutableList.of((Object)OreConfiguration.target((RuleTest)NETHER_ORE_REPLACEABLES, (BlockState)block.defaultBlockState())));
                    return this;
                }
                return this.blocks(block.defaultBlockState(), block.defaultBlockState());
            }

            public Builder blocks(Block block, Block deepblock) {
                return this.blocks(block.defaultBlockState(), deepblock.defaultBlockState());
            }

            public Builder blocks(Couple<NonNullSupplier<? extends Block>> blocksByDepth) {
                return this.blocks(((Block)((NonNullSupplier)blocksByDepth.getFirst()).get()).defaultBlockState(), ((Block)((NonNullSupplier)blocksByDepth.getSecond()).get()).defaultBlockState());
            }

            private Builder blocks(BlockState stone, BlockState deepslate) {
                this.targets.add((List<OreConfiguration.TargetBlockState>)ImmutableList.of((Object)OreConfiguration.target((RuleTest)STONE_ORE_REPLACEABLES, (BlockState)stone), (Object)OreConfiguration.target((RuleTest)DEEPSLATE_ORE_REPLACEABLES, (BlockState)deepslate)));
                return this;
            }

            public Builder weight(int weight) {
                this.weight = weight;
                return this;
            }

            public Builder size(int min, int max) {
                this.minSize = min;
                this.maxSize = max;
                return this;
            }

            public Layer build() {
                return new Layer(this.targets, this.minSize, this.maxSize, this.weight);
            }
        }
    }

    public static class Builder {
        private final List<Layer> layers = new ArrayList<Layer>();
        private boolean netherMode;

        public Builder inNether() {
            this.netherMode = true;
            return this;
        }

        public Builder layer(Consumer<@NotNull Layer.Builder> builder) {
            Layer.Builder layerBuilder = new Layer.Builder();
            layerBuilder.netherMode = this.netherMode;
            builder.accept(layerBuilder);
            this.layers.add(layerBuilder.build());
            return this;
        }

        public LayerPattern build() {
            return new LayerPattern(this.layers);
        }
    }
}
