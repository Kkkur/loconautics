/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.levelWrappers.WorldHelper
 *  net.minecraft.world.level.LevelAccessor
 */
package com.simibubi.create.content.kinetics;

import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import java.util.HashMap;
import java.util.Map;
import net.createmod.catnip.levelWrappers.WorldHelper;
import net.minecraft.world.level.LevelAccessor;

public class TorquePropagator {
    static Map<LevelAccessor, Map<Long, KineticNetwork>> networks = new HashMap<LevelAccessor, Map<Long, KineticNetwork>>();

    public void onLoadWorld(LevelAccessor world) {
        networks.put(world, new HashMap());
        Create.LOGGER.debug("Prepared Kinetic Network Space for " + String.valueOf(WorldHelper.getDimensionID((LevelAccessor)world)));
    }

    public void onUnloadWorld(LevelAccessor world) {
        networks.remove(world);
        Create.LOGGER.debug("Removed Kinetic Network Space for " + String.valueOf(WorldHelper.getDimensionID((LevelAccessor)world)));
    }

    public KineticNetwork getOrCreateNetworkFor(KineticBlockEntity be) {
        KineticNetwork network;
        Long id = be.network;
        Map map = networks.computeIfAbsent((LevelAccessor)be.getLevel(), $ -> new HashMap());
        if (id == null) {
            return null;
        }
        if (!map.containsKey(id)) {
            network = new KineticNetwork();
            network.id = be.network;
            map.put(id, network);
        }
        network = (KineticNetwork)map.get(id);
        return network;
    }
}
