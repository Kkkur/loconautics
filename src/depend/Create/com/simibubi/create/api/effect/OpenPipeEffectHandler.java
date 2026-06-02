/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.phys.AABB
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.api.effect;

import com.simibubi.create.api.registry.SimpleRegistry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;

@FunctionalInterface
public interface OpenPipeEffectHandler {
    public static final SimpleRegistry<Fluid, OpenPipeEffectHandler> REGISTRY = SimpleRegistry.create();

    public void apply(Level var1, AABB var2, FluidStack var3);
}
