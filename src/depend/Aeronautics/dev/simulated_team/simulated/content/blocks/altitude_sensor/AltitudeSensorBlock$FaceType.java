/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.StringRepresentable
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.content.blocks.altitude_sensor;

import java.util.Locale;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public static enum AltitudeSensorBlock.FaceType implements StringRepresentable
{
    LINEAR,
    RADIAL;


    @NotNull
    public String getSerializedName() {
        return this.toString().toLowerCase(Locale.ROOT);
    }
}
