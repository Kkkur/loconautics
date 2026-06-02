/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.util.StringRepresentable
 */
package com.simibubi.create.content.kinetics.belt;

import net.createmod.catnip.lang.Lang;
import net.minecraft.util.StringRepresentable;

public enum BeltSlope implements StringRepresentable
{
    HORIZONTAL,
    UPWARD,
    DOWNWARD,
    VERTICAL,
    SIDEWAYS;


    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }

    public boolean isDiagonal() {
        return this == UPWARD || this == DOWNWARD;
    }
}
