/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.foundation.data.recipe;

class Mods.Builder {
    Mods.Builder() {
    }

    Mods.Builder reverseMetalPrefix() {
        Mods.this.reversedMetalPrefix = true;
        return this;
    }

    Mods.Builder strippedWoodIsSuffix() {
        Mods.this.strippedIsSuffix = true;
        return this;
    }

    Mods.Builder omitWoodSuffix() {
        Mods.this.omitWoodSuffix = true;
        return this;
    }
}
