/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.StringRepresentable
 */
package dev.simulated_team.simulated.content.blocks.auger_shaft;

import java.util.Locale;
import net.minecraft.util.StringRepresentable;

public static enum AugerShaftBlock.BarrelSection implements StringRepresentable
{
    FRONT,
    MIDDLE,
    END,
    SINGLE;


    public String getSerializedName() {
        return this.toString().toLowerCase(Locale.ROOT);
    }
}
