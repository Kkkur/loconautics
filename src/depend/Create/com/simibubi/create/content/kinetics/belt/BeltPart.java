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

public enum BeltPart implements StringRepresentable
{
    START,
    MIDDLE,
    END,
    PULLEY;


    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }
}
