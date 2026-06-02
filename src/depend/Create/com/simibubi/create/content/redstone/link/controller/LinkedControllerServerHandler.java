/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.IntAttached
 *  net.createmod.catnip.data.WorldAttached
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.LevelAccessor
 */
package com.simibubi.create.content.redstone.link.controller;

import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public class LinkedControllerServerHandler {
    public static WorldAttached<Map<UUID, Collection<ManualFrequencyEntry>>> receivedInputs = new WorldAttached($ -> new HashMap());
    static final int TIMEOUT = 30;

    public static void tick(LevelAccessor world) {
        Map map = (Map)receivedInputs.get(world);
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            Collection list = (Collection)entry.getValue();
            Iterator entryIterator = list.iterator();
            while (entryIterator.hasNext()) {
                ManualFrequencyEntry manualFrequencyEntry = (ManualFrequencyEntry)entryIterator.next();
                manualFrequencyEntry.decrement();
                if (manualFrequencyEntry.isAlive()) continue;
                Create.REDSTONE_LINK_NETWORK_HANDLER.removeFromNetwork(world, manualFrequencyEntry);
                entryIterator.remove();
            }
            if (!list.isEmpty()) continue;
            iterator.remove();
        }
    }

    public static void receivePressed(LevelAccessor world, BlockPos pos, UUID uniqueID, List<Couple<RedstoneLinkNetworkHandler.Frequency>> collect, boolean pressed) {
        Map map = (Map)receivedInputs.get(world);
        Collection list = map.computeIfAbsent(uniqueID, $ -> new ArrayList());
        block0: for (Couple<RedstoneLinkNetworkHandler.Frequency> activated : collect) {
            for (ManualFrequencyEntry entry : list) {
                if (!((Couple)entry.getSecond()).equals(activated)) continue;
                if (!pressed) {
                    entry.setFirst(0);
                    continue block0;
                }
                entry.updatePosition(pos);
                continue block0;
            }
            if (!pressed) continue;
            ManualFrequencyEntry entry = new ManualFrequencyEntry(pos, activated);
            Create.REDSTONE_LINK_NETWORK_HANDLER.addToNetwork(world, entry);
            list.add(entry);
            for (IRedstoneLinkable linkable : Create.REDSTONE_LINK_NETWORK_HANDLER.getNetworkOf(world, entry)) {
                LinkBehaviour lb;
                if (!(linkable instanceof LinkBehaviour) || !(lb = (LinkBehaviour)linkable).isListening()) continue;
                AllAdvancements.LINKED_CONTROLLER.awardTo(world.getPlayerByUUID(uniqueID));
            }
        }
    }

    static class ManualFrequencyEntry
    extends IntAttached<Couple<RedstoneLinkNetworkHandler.Frequency>>
    implements IRedstoneLinkable {
        private BlockPos pos;

        public ManualFrequencyEntry(BlockPos pos, Couple<RedstoneLinkNetworkHandler.Frequency> second) {
            super(Integer.valueOf(30), second);
            this.pos = pos;
        }

        public void updatePosition(BlockPos pos) {
            this.pos = pos;
            this.setFirst(30);
        }

        @Override
        public int getTransmittedStrength() {
            return this.isAlive() ? 15 : 0;
        }

        @Override
        public boolean isAlive() {
            return (Integer)this.getFirst() > 0;
        }

        @Override
        public BlockPos getLocation() {
            return this.pos;
        }

        @Override
        public void setReceivedStrength(int power) {
        }

        @Override
        public boolean isListening() {
            return false;
        }

        @Override
        public Couple<RedstoneLinkNetworkHandler.Frequency> getNetworkKey() {
            return (Couple)this.getSecond();
        }
    }
}
