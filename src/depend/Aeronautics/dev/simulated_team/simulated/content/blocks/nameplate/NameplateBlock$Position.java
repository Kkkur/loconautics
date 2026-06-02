/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.StringRepresentable
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.content.blocks.nameplate;

import java.util.Locale;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public static enum NameplateBlock.Position implements StringRepresentable
{
    SINGLE,
    LEFT,
    RIGHT,
    MIDDLE;


    @NotNull
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
