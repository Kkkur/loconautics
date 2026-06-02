/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.StringRepresentable
 */
package com.simibubi.create.content.equipment.symmetryWand.mirror;

import net.minecraft.util.StringRepresentable;

public static enum PlaneMirror.Align implements StringRepresentable
{
    XY("xy"),
    YZ("yz");

    private final String name;

    private PlaneMirror.Align(String name) {
        this.name = name;
    }

    public String getSerializedName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }
}
