/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.chunk.ChunkSource
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.lighting.LevelLightEngine
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.mixin.plot.lighting;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={LevelChunk.class})
public class LevelChunkMixin {
    @Shadow
    @Final
    private Level level;

    @Redirect(method={"setBlockState"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/chunk/ChunkSource;getLightEngine()Lnet/minecraft/world/level/lighting/LevelLightEngine;"))
    public LevelLightEngine sable$getLightEngine(ChunkSource instance) {
        LevelChunk chunk;
        LevelPlot plot;
        SubLevelContainer container = SubLevelContainer.getContainer(this.level);
        if (container != null && this.level instanceof ServerLevel && (plot = container.getPlot((chunk = (LevelChunk)this).getPos())) != null) {
            return plot.getLightEngine();
        }
        return instance.getLightEngine();
    }
}
