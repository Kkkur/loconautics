/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.sublevel.water_occlusion;

import dev.ryanhcode.sable.render.region.SimpleCulledRenderRegion;
import dev.ryanhcode.sable.sublevel.water_occlusion.WaterOcclusionRegion;
import dev.ryanhcode.sable.util.BoundedBitVolume3i;

protected static class ClientWaterOcclusionContainer.ClientWaterOcclusionRegion
extends WaterOcclusionRegion {
    private SimpleCulledRenderRegion renderRegion;

    public ClientWaterOcclusionContainer.ClientWaterOcclusionRegion(BoundedBitVolume3i bitSet) {
        super(bitSet);
    }
}
