/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.util.StringRepresentable
 */
package com.simibubi.create.content.processing.burner;

import net.createmod.catnip.lang.Lang;
import net.minecraft.util.StringRepresentable;

public static enum LitBlazeBurnerBlock.FlameType implements StringRepresentable
{
    REGULAR,
    SOUL;


    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }
}
