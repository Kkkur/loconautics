/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.math.VoxelShaper
 *  net.minecraft.util.StringRepresentable
 */
package com.simibubi.create.content.logistics.funnel;

import com.simibubi.create.AllShapes;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.util.StringRepresentable;

public static enum BeltFunnelBlock.Shape implements StringRepresentable
{
    RETRACTED(AllShapes.BELT_FUNNEL_RETRACTED),
    EXTENDED(AllShapes.BELT_FUNNEL_EXTENDED),
    PUSHING(AllShapes.BELT_FUNNEL_PERPENDICULAR),
    PULLING(AllShapes.BELT_FUNNEL_PERPENDICULAR);

    VoxelShaper shaper;

    private BeltFunnelBlock.Shape(VoxelShaper shaper) {
        this.shaper = shaper;
    }

    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }
}
