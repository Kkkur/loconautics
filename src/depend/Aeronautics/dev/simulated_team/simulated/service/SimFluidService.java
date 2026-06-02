/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.material.Fluid
 */
package dev.simulated_team.simulated.service;

import dev.simulated_team.simulated.service.ServiceUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public interface SimFluidService {
    public static final SimFluidService INSTANCE = ServiceUtil.load(SimFluidService.class);

    public long mbToLoaderUnits(long var1);

    public Fluid getFluidInItem(ItemStack var1);
}
