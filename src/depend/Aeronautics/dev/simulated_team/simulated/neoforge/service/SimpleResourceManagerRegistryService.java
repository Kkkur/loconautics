/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 */
package dev.simulated_team.simulated.neoforge.service;

import dev.simulated_team.simulated.api.SimpleResourceManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public class SimpleResourceManagerRegistryService
implements SimpleResourceManager.Registry {
    public static final List<PreparableReloadListener> LISTENERS = new ArrayList<PreparableReloadListener>();

    @Override
    public void registerListener(PreparableReloadListener listener) {
        LISTENERS.add(listener);
    }
}
