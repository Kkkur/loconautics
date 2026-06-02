/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.content.trains.bogey;

import com.simibubi.create.content.trains.bogey.BogeySizes;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public record BogeySizes.BogeySize(ResourceLocation id, float wheelRadius) {
    public BogeySizes.BogeySize nextBySize() {
        List<BogeySizes.BogeySize> values = BogeySizes.allSortedIncreasing();
        int ordinal = values.indexOf(this);
        return values.get((ordinal + 1) % values.size());
    }
}
