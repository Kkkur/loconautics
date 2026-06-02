/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.util.StringRepresentable
 */
package com.simibubi.create.content.fluids.tank;

import net.createmod.catnip.lang.Lang;
import net.minecraft.util.StringRepresentable;

public static enum FluidTankBlock.Shape implements StringRepresentable
{
    PLAIN,
    WINDOW,
    WINDOW_NW,
    WINDOW_SW,
    WINDOW_NE,
    WINDOW_SE;


    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }
}
