/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.WorldGenLevel
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.BulkSectionAccess
 *  net.minecraft.world.level.chunk.LevelChunkSection
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.levelgen.feature.Feature
 *  net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
 *  net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration$TargetBlockState
 *  net.minecraft.world.level.levelgen.synth.SimplexNoise
 */
package com.simibubi.create.infrastructure.worldgen;

import com.simibubi.create.infrastructure.worldgen.LayerPattern;
import com.simibubi.create.infrastructure.worldgen.LayeredOreConfiguration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BulkSectionAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public class LayeredOreFeature
extends Feature<LayeredOreConfiguration> {
    private static final float MAX_LAYER_DISPLACEMENT = 1.75f;
    private static final float LAYER_NOISE_FREQUENCY = 0.125f;
    private static final float MAX_RADIAL_THRESHOLD_REDUCTION = 0.25f;
    private static final float RADIAL_NOISE_FREQUENCY = 0.125f;

    public LayeredOreFeature() {
        super(LayeredOreConfiguration.CODEC);
    }

    public boolean place(FeaturePlaceContext<LayeredOreConfiguration> pContext) {
        float layerSizeTotal;
        float layerSize;
        RandomSource random = pContext.random();
        BlockPos origin = pContext.origin();
        WorldGenLevel worldGenLevel = pContext.level();
        LayeredOreConfiguration config = (LayeredOreConfiguration)pContext.config();
        List<LayerPattern> patternPool = config.layerPatterns;
        if (patternPool.isEmpty()) {
            return false;
        }
        LayerPattern layerPattern = patternPool.get(random.nextInt(patternPool.size()));
        int placedAmount = 0;
        int size = config.size + 1;
        float radius = (float)config.size * 0.5f;
        int radiusBound = Mth.ceil((float)radius) - 1;
        int x0 = origin.getX();
        int y0 = origin.getY();
        int z0 = origin.getZ();
        if (origin.getY() >= worldGenLevel.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, origin.getX(), origin.getZ())) {
            return false;
        }
        ArrayList<TemporaryLayerEntry> tempLayers = new ArrayList<TemporaryLayerEntry>();
        LayerPattern.Layer current = null;
        for (layerSizeTotal = 0.0f; layerSizeTotal < (float)size; layerSizeTotal += layerSize) {
            LayerPattern.Layer next = layerPattern.rollNext(current, random);
            layerSize = Mth.randomBetween((RandomSource)random, (float)next.minSize, (float)next.maxSize);
            tempLayers.add(new TemporaryLayerEntry(next, layerSize));
            current = next;
        }
        ArrayList<ResolvedLayerEntry> resolvedLayers = new ArrayList<ResolvedLayerEntry>(tempLayers.size());
        float cumulativeLayerSize = -(layerSizeTotal - (float)size) * random.nextFloat();
        for (TemporaryLayerEntry tempLayerEntry : tempLayers) {
            float rampStartValue = resolvedLayers.size() == 0 ? Float.NEGATIVE_INFINITY : cumulativeLayerSize * (2.0f / (float)size) - 1.0f;
            if ((cumulativeLayerSize += tempLayerEntry.size()) < 0.0f) continue;
            float radialThresholdMultiplier = Mth.randomBetween((RandomSource)random, (float)0.5f, (float)1.0f);
            resolvedLayers.add(new ResolvedLayerEntry(tempLayerEntry.layer, radialThresholdMultiplier, rampStartValue));
        }
        float gy = Mth.randomBetween((RandomSource)random, (float)-1.0f, (float)1.0f);
        gy = (float)Math.cbrt(gy);
        float xzRescale = Mth.sqrt((float)(1.0f - gy * gy));
        float theta = random.nextFloat() * ((float)Math.PI * 2);
        float gx = Mth.cos((float)theta) * xzRescale;
        float gz = Mth.sin((float)theta) * xzRescale;
        SimplexNoise layerDisplacementNoise = new SimplexNoise(random);
        SimplexNoise radiusNoise = new SimplexNoise(random);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        try (BulkSectionAccess bulkSectionAccess = new BulkSectionAccess((LevelAccessor)worldGenLevel);){
            for (int dzBlock = -radiusBound; dzBlock <= radiusBound; ++dzBlock) {
                float dz = (float)dzBlock * (1.0f / radius);
                if (dz * dz > 1.0f) continue;
                for (int dxBlock = -radiusBound; dxBlock <= radiusBound; ++dxBlock) {
                    float dx = (float)dxBlock * (1.0f / radius);
                    if (dz * dz + dx * dx > 1.0f) continue;
                    block9: for (int dyBlock = -radiusBound; dyBlock <= radiusBound; ++dyBlock) {
                        LevelChunkSection levelChunkSection;
                        float thresholdNoiseValue;
                        float dy = (float)dyBlock * (1.0f / radius);
                        float distanceSquared = dz * dz + dx * dx + dy * dy;
                        if (distanceSquared > 1.0f || worldGenLevel.isOutsideBuildHeight(y0 + dyBlock)) continue;
                        int currentX = x0 + dxBlock;
                        int currentY = y0 + dyBlock;
                        int currentZ = z0 + dzBlock;
                        float rampValue = gx * dx + gy * dy + gz * dz;
                        int layerIndex = Collections.binarySearch(resolvedLayers, new ResolvedLayerEntry(null, 0.0f, rampValue = (float)((double)rampValue + layerDisplacementNoise.getValue((double)((float)currentX * 0.125f), (double)((float)currentY * 0.125f), (double)((float)currentZ * 0.125f)) * (double)(1.75f / (float)size))));
                        if (layerIndex < 0) {
                            layerIndex = -2 - layerIndex;
                        }
                        ResolvedLayerEntry layerEntry = (ResolvedLayerEntry)resolvedLayers.get(layerIndex);
                        if (distanceSquared > layerEntry.radialThresholdMultiplier || distanceSquared > layerEntry.radialThresholdMultiplier * (thresholdNoiseValue = Mth.map((float)((float)radiusNoise.getValue((double)((float)currentX * 0.125f), (double)((float)currentY * 0.125f), (double)((float)currentZ * 0.125f))), (float)-1.0f, (float)1.0f, (float)0.75f, (float)1.0f))) continue;
                        LayerPattern.Layer layer = layerEntry.layer;
                        List<OreConfiguration.TargetBlockState> targetBlockStates = layer.rollBlock(random);
                        mutablePos.set(currentX, currentY, currentZ);
                        if (!worldGenLevel.ensureCanWrite((BlockPos)mutablePos) || (levelChunkSection = bulkSectionAccess.getSection((BlockPos)mutablePos)) == null) continue;
                        int localX = SectionPos.sectionRelative((int)currentX);
                        int localY = SectionPos.sectionRelative((int)currentY);
                        int localZ = SectionPos.sectionRelative((int)currentZ);
                        BlockState blockState = levelChunkSection.getBlockState(localX, localY, localZ);
                        for (OreConfiguration.TargetBlockState targetBlockState : targetBlockStates) {
                            if (!this.canPlaceOre(blockState, arg_0 -> ((BulkSectionAccess)bulkSectionAccess).getBlockState(arg_0), random, config, targetBlockState, mutablePos) || targetBlockState.state.isAir()) continue;
                            levelChunkSection.setBlockState(localX, localY, localZ, targetBlockState.state, false);
                            ++placedAmount;
                            continue block9;
                        }
                    }
                }
            }
        }
        return placedAmount > 0;
    }

    public boolean canPlaceOre(BlockState pState, Function<BlockPos, BlockState> pAdjacentStateAccessor, RandomSource pRandom, LayeredOreConfiguration pConfig, OreConfiguration.TargetBlockState pTargetState, BlockPos.MutableBlockPos pMatablePos) {
        if (!pTargetState.target.test(pState, pRandom)) {
            return false;
        }
        if (this.shouldSkipAirCheck(pRandom, pConfig.discardChanceOnAirExposure)) {
            return true;
        }
        return !LayeredOreFeature.isAdjacentToAir(pAdjacentStateAccessor, (BlockPos)pMatablePos);
    }

    protected boolean shouldSkipAirCheck(RandomSource pRandom, float pChance) {
        return pChance <= 0.0f ? true : (pChance >= 1.0f ? false : pRandom.nextFloat() >= pChance);
    }

    private record TemporaryLayerEntry(LayerPattern.Layer layer, float size) {
    }

    private record ResolvedLayerEntry(LayerPattern.Layer layer, float radialThresholdMultiplier, float rampStartValue) implements Comparable<ResolvedLayerEntry>
    {
        @Override
        public int compareTo(ResolvedLayerEntry b) {
            return Float.compare(this.rampStartValue, b.rampStartValue);
        }
    }
}
