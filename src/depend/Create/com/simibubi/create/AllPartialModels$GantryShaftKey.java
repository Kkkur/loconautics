/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create;

import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlock;
import net.minecraft.resources.ResourceLocation;

public record AllPartialModels.GantryShaftKey(GantryShaftBlock.Part part, boolean powered, boolean flipped) {
    private ResourceLocation name() {
        String partName = this.part.getSerializedName();
        if (!this.flipped && !this.powered) {
            return Create.asResource("block/gantry_shaft/block_" + partName);
        }
        String flipped = this.flipped ? "_flipped" : "";
        String powered = this.powered ? "_powered" : "";
        return Create.asResource("block/gantry_shaft_" + partName + powered + flipped);
    }
}
