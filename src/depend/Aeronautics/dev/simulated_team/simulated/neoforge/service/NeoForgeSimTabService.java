/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.CreativeModeTab
 */
package dev.simulated_team.simulated.neoforge.service;

import dev.simulated_team.simulated.neoforge.SimulatedNeoForge;
import dev.simulated_team.simulated.service.SimTabService;
import net.minecraft.world.item.CreativeModeTab;

public class NeoForgeSimTabService
implements SimTabService {
    @Override
    public CreativeModeTab getCreativeTab() {
        return SimulatedNeoForge.TAB;
    }
}
