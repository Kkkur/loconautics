/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.chunk.RenderChunkRegion
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.ryanhcode.sable.mixin.plot.lighting;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.mixinterface.plot.SubLevelContainerHolder;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={RenderChunkRegion.class})
public class RenderChunkRegionMixin
implements SubLevelContainerHolder {
    @Shadow
    @Final
    protected Level level;

    @Override
    public SubLevelContainer sable$getPlotContainer() {
        return SubLevelContainer.getContainer(this.level);
    }
}
