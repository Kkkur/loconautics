/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.neoforged.neoforge.fluids.FluidType$Properties
 */
package dev.eriksonn.aeronautics.neoforge.content.fluids;

import dev.eriksonn.aeronautics.neoforge.content.fluids.AeroFluidType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidType;

public static interface AeroFluidType.Factory {
    public AeroFluidType create(FluidType.Properties var1, ResourceLocation var2, ResourceLocation var3);
}
