/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.infrastructure.worldgen;

import com.simibubi.create.infrastructure.worldgen.LayerPattern;

private record LayeredOreFeature.ResolvedLayerEntry(LayerPattern.Layer layer, float radialThresholdMultiplier, float rampStartValue) implements Comparable<LayeredOreFeature.ResolvedLayerEntry>
{
    @Override
    public int compareTo(LayeredOreFeature.ResolvedLayerEntry b) {
        return Float.compare(this.rampStartValue, b.rampStartValue);
    }
}
