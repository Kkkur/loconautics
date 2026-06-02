/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.StringRepresentable
 */
package com.simibubi.create.content.redstone.nixieTube;

import net.minecraft.util.StringRepresentable;

public static enum DoubleFaceAttachedBlock.DoubleAttachFace implements StringRepresentable
{
    FLOOR("floor"),
    WALL("wall"),
    WALL_REVERSED("wall_reversed"),
    CEILING("ceiling");

    private final String name;

    private DoubleFaceAttachedBlock.DoubleAttachFace(String p_61311_) {
        this.name = p_61311_;
    }

    public String getSerializedName() {
        return this.name;
    }

    public int xRot() {
        return this == FLOOR ? 0 : (this == CEILING ? 180 : 90);
    }
}
