/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
 *  net.neoforged.neoforge.fluids.FluidType
 *  net.neoforged.neoforge.fluids.FluidType$Properties
 */
package com.simibubi.create.foundation.data;

import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;

static class CreateRegistrate.1
extends FluidType {
    final /* synthetic */ ResourceLocation val$stillTexture;
    final /* synthetic */ ResourceLocation val$flowingTexture;

    CreateRegistrate.1(FluidType.Properties arg0, ResourceLocation resourceLocation, ResourceLocation resourceLocation2) {
        this.val$stillTexture = resourceLocation;
        this.val$flowingTexture = resourceLocation2;
        super(arg0);
    }

    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions(){

            public ResourceLocation getStillTexture() {
                return val$stillTexture;
            }

            public ResourceLocation getFlowingTexture() {
                return val$flowingTexture;
            }
        });
    }
}
