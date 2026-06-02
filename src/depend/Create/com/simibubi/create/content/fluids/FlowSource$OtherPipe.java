/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids;

import com.simibubi.create.content.fluids.FlowSource;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.lang.ref.WeakReference;
import java.util.function.Predicate;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;

public static class FlowSource.OtherPipe
extends FlowSource {
    WeakReference<FluidTransportBehaviour> cached;

    public FlowSource.OtherPipe(BlockFace location) {
        super(location);
    }

    @Override
    public void manageSource(Level world, BlockEntity networkBE) {
        if (this.cached != null && this.cached.get() != null && !((FluidTransportBehaviour)this.cached.get()).blockEntity.isRemoved()) {
            return;
        }
        this.cached = null;
        FluidTransportBehaviour fluidTransportBehaviour = BlockEntityBehaviour.get((BlockGetter)world, this.location.getConnectedPos(), FluidTransportBehaviour.TYPE);
        if (fluidTransportBehaviour != null) {
            this.cached = new WeakReference<FluidTransportBehaviour>(fluidTransportBehaviour);
        }
    }

    @Override
    public FluidStack provideFluid(Predicate<FluidStack> extractionPredicate) {
        if (this.cached == null || this.cached.get() == null) {
            return FluidStack.EMPTY;
        }
        FluidTransportBehaviour behaviour = (FluidTransportBehaviour)this.cached.get();
        FluidStack providedOutwardFluid = behaviour.getProvidedOutwardFluid(this.location.getOppositeFace());
        return extractionPredicate.test(providedOutwardFluid) ? providedOutwardFluid : FluidStack.EMPTY;
    }

    @Override
    public boolean isEndpoint() {
        return false;
    }
}
