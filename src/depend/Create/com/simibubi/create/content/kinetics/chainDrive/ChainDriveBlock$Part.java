/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.util.StringRepresentable
 */
package com.simibubi.create.content.kinetics.chainDrive;

import net.createmod.catnip.lang.Lang;
import net.minecraft.util.StringRepresentable;

public static enum ChainDriveBlock.Part implements StringRepresentable
{
    START,
    MIDDLE,
    END,
    NONE;


    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }
}
