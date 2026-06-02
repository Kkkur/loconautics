/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.CreativeModeTab
 */
package dev.simulated_team.simulated.service;

import dev.simulated_team.simulated.service.ServiceUtil;
import net.minecraft.world.item.CreativeModeTab;

public interface SimTabService {
    public static final SimTabService INSTANCE = ServiceUtil.load(SimTabService.class);

    public CreativeModeTab getCreativeTab();
}
