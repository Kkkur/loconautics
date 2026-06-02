/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.api.data.recipe;

import net.minecraft.resources.ResourceLocation;

public interface DatagenMod {
    default public ResourceLocation asResource(String id) {
        return ResourceLocation.fromNamespaceAndPath((String)this.getId(), (String)id);
    }

    default public String recipeId(String id) {
        return "compat/" + this.getId() + "/" + id;
    }

    public String getId();

    default public ResourceLocation ingotOf(String type) {
        return ResourceLocation.fromNamespaceAndPath((String)this.getId(), (String)(this.reversedMetalPrefix() ? "ingot_" + type : type + "_ingot"));
    }

    default public ResourceLocation nuggetOf(String type) {
        return ResourceLocation.fromNamespaceAndPath((String)this.getId(), (String)(this.reversedMetalPrefix() ? "nugget_" + type : type + "_nugget"));
    }

    default public ResourceLocation oreOf(String type) {
        return ResourceLocation.fromNamespaceAndPath((String)this.getId(), (String)(this.reversedMetalPrefix() ? "ore_" + type : type + "_ore"));
    }

    default public ResourceLocation deepslateOreOf(String type) {
        return ResourceLocation.fromNamespaceAndPath((String)this.getId(), (String)(this.reversedMetalPrefix() ? "deepslate_ore_" + type : "deepslate_" + type + "_ore"));
    }

    default public boolean reversedMetalPrefix() {
        return false;
    }

    default public boolean strippedIsSuffix() {
        return false;
    }

    default public boolean omitWoodSuffix() {
        return false;
    }
}
