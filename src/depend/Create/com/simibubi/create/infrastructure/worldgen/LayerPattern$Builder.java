/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.infrastructure.worldgen;

import com.simibubi.create.infrastructure.worldgen.LayerPattern;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public static class LayerPattern.Builder {
    private final List<LayerPattern.Layer> layers = new ArrayList<LayerPattern.Layer>();
    private boolean netherMode;

    public LayerPattern.Builder inNether() {
        this.netherMode = true;
        return this;
    }

    public LayerPattern.Builder layer(Consumer<@NotNull LayerPattern.Layer.Builder> builder) {
        LayerPattern.Layer.Builder layerBuilder = new LayerPattern.Layer.Builder();
        layerBuilder.netherMode = this.netherMode;
        builder.accept(layerBuilder);
        this.layers.add(layerBuilder.build());
        return this;
    }

    public LayerPattern build() {
        return new LayerPattern(this.layers);
    }
}
