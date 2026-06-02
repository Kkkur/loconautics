/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.StringRepresentable
 */
package dev.simulated_team.simulated.api.sound;

import java.util.Locale;
import net.minecraft.util.StringRepresentable;

public static enum SoundFile.Type implements StringRepresentable
{
    FILE,
    EVENT;


    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
