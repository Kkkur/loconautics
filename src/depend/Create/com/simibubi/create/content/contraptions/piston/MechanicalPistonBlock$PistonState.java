/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.util.StringRepresentable
 */
package com.simibubi.create.content.contraptions.piston;

import net.createmod.catnip.lang.Lang;
import net.minecraft.util.StringRepresentable;

public static enum MechanicalPistonBlock.PistonState implements StringRepresentable
{
    RETRACTED,
    MOVING,
    EXTENDED;


    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }
}
