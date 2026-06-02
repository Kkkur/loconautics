/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.StringRepresentable
 */
package dev.simulated_team.simulated.content.blocks.spring;

import net.minecraft.util.StringRepresentable;

public static enum SpringBlock.Size implements StringRepresentable
{
    SMALL("small"),
    MEDIUM("medium"),
    LARGE("large");

    private static final SpringBlock.Size[] VALUES;
    private final String name;

    private SpringBlock.Size(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }

    public SpringBlock.Size cycle() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    static {
        VALUES = SpringBlock.Size.values();
    }
}
