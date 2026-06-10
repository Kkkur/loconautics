/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.StringRepresentable
 */
package com.bearing.linearbearing;

import net.minecraft.util.StringRepresentable;

public static enum LinearBearingBlock.SliderSide implements StringRepresentable
{
    FRONT("front"),
    BACK("back");

    private final String name;

    private LinearBearingBlock.SliderSide(String name) {
        this.name = name;
    }

    public String getSerializedName() {
        return this.name;
    }
}
