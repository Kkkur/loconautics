/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.StringRepresentable
 */
package com.bearing.linearbearing;

import net.minecraft.util.StringRepresentable;

public static enum AndesiteLampBlock.LampMode implements StringRepresentable
{
    NORMAL("normal"),
    INVERTED("inverted");

    private final String name;

    private AndesiteLampBlock.LampMode(String name) {
        this.name = name;
    }

    public String getSerializedName() {
        return this.name;
    }
}
