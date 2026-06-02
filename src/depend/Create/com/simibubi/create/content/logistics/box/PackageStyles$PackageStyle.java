/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.content.logistics.box;

import com.simibubi.create.Create;
import net.minecraft.resources.ResourceLocation;

public record PackageStyles.PackageStyle(String type, int width, int height, float riggingOffset, boolean rare) {
    public ResourceLocation getItemId() {
        String size = "_" + this.width + "x" + this.height;
        String id = this.type + "_package" + (String)(this.rare ? "" : size);
        return Create.asResource(id);
    }

    public ResourceLocation getRiggingModel() {
        String size = this.width + "x" + this.height;
        return Create.asResource("item/package/rigging_" + size);
    }
}
