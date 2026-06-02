/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 */
package dev.ryanhcode.sable.sublevel.water_occlusion;

import dev.ryanhcode.sable.SableClient;
import dev.ryanhcode.sable.render.region.SimpleCulledRenderRegion;
import dev.ryanhcode.sable.sublevel.water_occlusion.WaterOcclusionContainer;
import dev.ryanhcode.sable.sublevel.water_occlusion.WaterOcclusionRegion;
import dev.ryanhcode.sable.util.BoundedBitVolume3i;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ClientWaterOcclusionContainer
extends WaterOcclusionContainer<ClientWaterOcclusionRegion> {
    public ClientWaterOcclusionContainer(Level level) {
        super(level);
    }

    public static ClientWaterOcclusionContainer create(Level level) {
        return new ClientWaterOcclusionContainer(level);
    }

    @Override
    public void removeRegion(WaterOcclusionRegion region) {
        this.regions.remove(region);
        SableClient.WATER_OCCLUSION_RENDERER.removeRegion(((ClientWaterOcclusionRegion)region).renderRegion);
    }

    @Override
    public ClientWaterOcclusionRegion addRegion(BoundedBitVolume3i bitSet) {
        ClientWaterOcclusionRegion region = new ClientWaterOcclusionRegion(bitSet);
        this.regions.add(region);
        BoundedBitVolume3i volume = region.getVolume();
        List<BlockPos> blocks = BlockPos.betweenClosedStream((BlockPos)volume.getMinBlockPos(), (BlockPos)volume.getMaxBlockPos()).map(BlockPos::immutable).filter(x -> volume.getOccupied(x.getX(), x.getY(), x.getZ())).toList();
        region.renderRegion = SableClient.WATER_OCCLUSION_RENDERER.addRegion(blocks);
        return region;
    }

    protected static class ClientWaterOcclusionRegion
    extends WaterOcclusionRegion {
        private SimpleCulledRenderRegion renderRegion;

        public ClientWaterOcclusionRegion(BoundedBitVolume3i bitSet) {
            super(bitSet);
        }
    }
}
