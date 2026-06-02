/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.lighting.LevelLightEngine
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.ryanhcode.sable.mixin.plot.lighting;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.mixinterface.plot.SubLevelContainerHolder;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={BlockAndTintGetter.class})
public interface BlockAndTintGetterMixin {
    @Shadow
    public LevelLightEngine getLightEngine();

    @Overwrite
    default public int getBrightness(LightLayer lightLayer, BlockPos blockPos) {
        LevelPlot plot;
        SubLevelContainerHolder holder;
        SubLevelContainer plotContainer;
        LevelLightEngine engine = this.getLightEngine();
        BlockAndTintGetterMixin blockAndTintGetterMixin = this;
        if (blockAndTintGetterMixin instanceof SubLevelContainerHolder && (plotContainer = (holder = (SubLevelContainerHolder)((Object)blockAndTintGetterMixin)).sable$getPlotContainer()).getLevel() instanceof ServerLevel && (plot = plotContainer.getPlot(new ChunkPos(blockPos))) != null) {
            engine = plot.getLightEngine();
        }
        return engine.getLayerListener(lightLayer).getLightValue(blockPos);
    }

    @Overwrite
    default public int getRawBrightness(BlockPos blockPos, int i) {
        LevelPlot plot;
        SubLevelContainerHolder holder;
        SubLevelContainer plotContainer;
        LevelLightEngine engine = this.getLightEngine();
        BlockAndTintGetterMixin blockAndTintGetterMixin = this;
        if (blockAndTintGetterMixin instanceof SubLevelContainerHolder && (plotContainer = (holder = (SubLevelContainerHolder)((Object)blockAndTintGetterMixin)).sable$getPlotContainer()).getLevel() instanceof ServerLevel && (plot = plotContainer.getPlot(new ChunkPos(blockPos))) != null) {
            engine = plot.getLightEngine();
        }
        return engine.getRawBrightness(blockPos, i);
    }
}
