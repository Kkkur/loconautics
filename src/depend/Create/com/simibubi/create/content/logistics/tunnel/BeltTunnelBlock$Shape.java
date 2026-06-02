/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.util.StringRepresentable
 */
package com.simibubi.create.content.logistics.tunnel;

import net.createmod.catnip.lang.Lang;
import net.minecraft.util.StringRepresentable;

public static enum BeltTunnelBlock.Shape implements StringRepresentable
{
    STRAIGHT,
    WINDOW,
    CLOSED,
    T_LEFT,
    T_RIGHT,
    CROSS;


    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }
}
