/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids;

import com.simibubi.create.content.fluids.PipeConnection;
import net.createmod.catnip.animation.LerpedFloat;
import net.neoforged.neoforge.fluids.FluidStack;

public class PipeConnection.Flow {
    public boolean complete;
    public boolean inbound;
    public LerpedFloat progress;
    public FluidStack fluid;

    public PipeConnection.Flow(PipeConnection this$0, boolean inbound, FluidStack fluid) {
        this.inbound = inbound;
        this.fluid = fluid;
        this.progress = LerpedFloat.linear().startWithValue(0.0);
        this.complete = false;
    }
}
