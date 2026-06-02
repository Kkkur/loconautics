/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.simulated_team.simulated.service;

import dev.simulated_team.simulated.service.ServiceUtil;
import net.minecraft.world.level.block.state.BlockState;

public interface SimAssemblyService {
    public static final SimAssemblyService INSTANCE = ServiceUtil.load(SimAssemblyService.class);

    public boolean canStickTo(BlockState var1, BlockState var2);
}
