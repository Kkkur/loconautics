/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.util.StringRepresentable
 */
package com.simibubi.create.content.kinetics.gauge;

import net.createmod.catnip.lang.Lang;
import net.minecraft.util.StringRepresentable;

public static enum GaugeBlock.Type implements StringRepresentable
{
    SPEED,
    STRESS;


    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }
}
