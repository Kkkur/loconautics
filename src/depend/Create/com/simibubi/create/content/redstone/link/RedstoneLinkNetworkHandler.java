/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.levelWrappers.WorldHelper
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.DyedItemColor
 *  net.minecraft.world.level.LevelAccessor
 */
package com.simibubi.create.content.redstone.link;

import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.levelWrappers.WorldHelper;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.LevelAccessor;

public class RedstoneLinkNetworkHandler {
    static final Map<LevelAccessor, Map<Couple<Frequency>, Set<IRedstoneLinkable>>> connections = new IdentityHashMap<LevelAccessor, Map<Couple<Frequency>, Set<IRedstoneLinkable>>>();
    public final AtomicInteger globalPowerVersion = new AtomicInteger();

    public void onLoadWorld(LevelAccessor world) {
        connections.put(world, new HashMap());
        Create.LOGGER.debug("Prepared Redstone Network Space for " + String.valueOf(WorldHelper.getDimensionID((LevelAccessor)world)));
    }

    public void onUnloadWorld(LevelAccessor world) {
        connections.remove(world);
        Create.LOGGER.debug("Removed Redstone Network Space for " + String.valueOf(WorldHelper.getDimensionID((LevelAccessor)world)));
    }

    public Set<IRedstoneLinkable> getNetworkOf(LevelAccessor world, IRedstoneLinkable actor) {
        Couple<Frequency> key;
        Map<Couple<Frequency>, Set<IRedstoneLinkable>> networksInWorld = this.networksIn(world);
        if (!networksInWorld.containsKey(key = actor.getNetworkKey())) {
            networksInWorld.put(key, new LinkedHashSet());
        }
        return networksInWorld.get(key);
    }

    public void addToNetwork(LevelAccessor world, IRedstoneLinkable actor) {
        this.getNetworkOf(world, actor).add(actor);
        this.updateNetworkOf(world, actor);
    }

    public void removeFromNetwork(LevelAccessor world, IRedstoneLinkable actor) {
        Set<IRedstoneLinkable> network = this.getNetworkOf(world, actor);
        network.remove(actor);
        if (network.isEmpty()) {
            this.networksIn(world).remove(actor.getNetworkKey());
            return;
        }
        this.updateNetworkOf(world, actor);
    }

    public void updateNetworkOf(LevelAccessor world, IRedstoneLinkable actor) {
        LinkBehaviour linkBehaviour;
        Set<IRedstoneLinkable> network = this.getNetworkOf(world, actor);
        this.globalPowerVersion.incrementAndGet();
        int power = 0;
        Iterator<IRedstoneLinkable> iterator = network.iterator();
        while (iterator.hasNext()) {
            IRedstoneLinkable other = iterator.next();
            if (!other.isAlive()) {
                iterator.remove();
                continue;
            }
            if (!RedstoneLinkNetworkHandler.withinRange(actor, other) || power >= 15) continue;
            power = Math.max(other.getTransmittedStrength(), power);
        }
        if (actor instanceof LinkBehaviour && (linkBehaviour = (LinkBehaviour)actor).isListening()) {
            linkBehaviour.newPosition = true;
            linkBehaviour.setReceivedStrength(power);
        }
        for (IRedstoneLinkable other : network) {
            if (other == actor || !other.isListening() || !RedstoneLinkNetworkHandler.withinRange(actor, other)) continue;
            other.setReceivedStrength(power);
        }
    }

    public static boolean withinRange(IRedstoneLinkable from, IRedstoneLinkable to) {
        if (from == to) {
            return true;
        }
        return from.getLocation().closerThan((Vec3i)to.getLocation(), (double)((Integer)AllConfigs.server().logistics.linkRange.get()).intValue());
    }

    public Map<Couple<Frequency>, Set<IRedstoneLinkable>> networksIn(LevelAccessor world) {
        if (!connections.containsKey(world)) {
            Create.LOGGER.warn("Tried to Access unprepared network space of " + String.valueOf(WorldHelper.getDimensionID((LevelAccessor)world)));
            return new HashMap<Couple<Frequency>, Set<IRedstoneLinkable>>();
        }
        return connections.get(world);
    }

    public boolean hasAnyLoadedPower(Couple<Frequency> frequency) {
        for (Map<Couple<Frequency>, Set<IRedstoneLinkable>> map : connections.values()) {
            Set<IRedstoneLinkable> set = map.get(frequency);
            if (set == null || set.isEmpty()) continue;
            for (IRedstoneLinkable link : set) {
                if (link.getTransmittedStrength() <= 0) continue;
                return true;
            }
        }
        return false;
    }

    public static class Frequency {
        public static final Frequency EMPTY = new Frequency(ItemStack.EMPTY);
        private static final Map<Item, Frequency> simpleFrequencies = new IdentityHashMap<Item, Frequency>();
        private ItemStack stack;
        private Item item;
        private int color;

        public static Frequency of(ItemStack stack) {
            if (stack.isEmpty()) {
                return EMPTY;
            }
            if (stack.getComponents().isEmpty()) {
                return simpleFrequencies.computeIfAbsent(stack.getItem(), $ -> new Frequency(stack));
            }
            return new Frequency(stack);
        }

        private Frequency(ItemStack stack) {
            this.stack = stack;
            this.item = stack.getItem();
            this.color = stack.has(DataComponents.DYED_COLOR) ? ((DyedItemColor)stack.get(DataComponents.DYED_COLOR)).rgb() : -1;
        }

        public ItemStack getStack() {
            return this.stack;
        }

        public int hashCode() {
            return this.item.hashCode() * 31 ^ this.color;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            return obj instanceof Frequency ? ((Frequency)obj).item == this.item && ((Frequency)obj).color == this.color : false;
        }
    }
}
