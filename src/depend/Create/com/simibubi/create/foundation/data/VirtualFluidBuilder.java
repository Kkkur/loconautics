/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.AbstractRegistrate
 *  com.tterrag.registrate.builders.BuilderCallback
 *  com.tterrag.registrate.builders.FluidBuilder
 *  com.tterrag.registrate.builders.FluidBuilder$FluidTypeFactory
 *  com.tterrag.registrate.util.nullness.NonNullFunction
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  net.minecraft.resources.ResourceLocation
 *  net.neoforged.neoforge.fluids.BaseFlowingFluid
 *  net.neoforged.neoforge.fluids.BaseFlowingFluid$Properties
 */
package com.simibubi.create.foundation.data;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class VirtualFluidBuilder<T extends BaseFlowingFluid, P>
extends FluidBuilder<T, P> {
    public VirtualFluidBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, ResourceLocation stillTexture, ResourceLocation flowingTexture, FluidBuilder.FluidTypeFactory typeFactory, NonNullFunction<BaseFlowingFluid.Properties, T> sourceFactory, NonNullFunction<BaseFlowingFluid.Properties, T> flowingFactory) {
        super(owner, parent, name, callback, stillTexture, flowingTexture, typeFactory, flowingFactory);
        this.source(sourceFactory);
    }

    public NonNullSupplier<T> asSupplier() {
        return () -> ((VirtualFluidBuilder)this).getEntry();
    }
}
