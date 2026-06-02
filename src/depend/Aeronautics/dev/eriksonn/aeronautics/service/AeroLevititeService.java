/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.simulated_team.simulated.service.ServiceUtil
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.material.Fluid
 */
package dev.eriksonn.aeronautics.service;

import dev.simulated_team.simulated.service.ServiceUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public interface AeroLevititeService {
    public static final AeroLevititeService INSTANCE = (AeroLevititeService)ServiceUtil.load(AeroLevititeService.class);

    public Item getBucket();

    public Fluid getFluid();
}
