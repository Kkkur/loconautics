/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.item.ItemStack
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import com.simibubi.create.content.logistics.box.PackageItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableInt;

public class ChainConveyorRoutingTable {
    public static final int ENTRY_TIMEOUT = 100;
    public static final int PORT_ENTRY_TIMEOUT = 20;
    public List<RoutingTableEntry> entriesByDistance = new ArrayList<RoutingTableEntry>();
    public int lastUpdate;
    public boolean changed;

    public void tick() {
        this.entriesByDistance.forEach(RoutingTableEntry::tick);
        this.changed |= this.entriesByDistance.removeIf(RoutingTableEntry::invalid);
        ++this.lastUpdate;
    }

    public boolean shouldAdvertise() {
        return this.changed || this.lastUpdate > 80;
    }

    public void receivePortInfo(String filter, BlockPos connection) {
        this.insert(new RoutingTableEntry(filter, "*".equals(filter) ? 1000 : 0, connection, new MutableInt(20), true));
    }

    public BlockPos getExitFor(ItemStack box) {
        for (RoutingTableEntry entry : this.entriesByDistance) {
            if (!PackageItem.matchAddress(box, entry.port())) continue;
            return entry.nextConnection();
        }
        return BlockPos.ZERO;
    }

    public void advertiseTo(BlockPos connection, ChainConveyorRoutingTable otherTable) {
        BlockPos backConnection = connection.multiply(-1);
        for (RoutingTableEntry entry : this.entriesByDistance) {
            if (!entry.endOfRoute() && connection.equals((Object)entry.nextConnection())) continue;
            otherTable.insert(entry.copyForNeighbour(connection));
        }
        otherTable.entriesByDistance.removeIf(e -> e.timeout().intValue() < 100 && !e.endOfRoute() && backConnection.equals((Object)e.nextConnection()));
    }

    private void insert(RoutingTableEntry entry) {
        RoutingTableEntry otherEntry;
        int targetIndex = 0;
        for (int i = 0; i < this.entriesByDistance.size() && (otherEntry = this.entriesByDistance.get(i)).distance() <= entry.distance(); ++i) {
            if (otherEntry.port().equals(entry.port())) {
                if (otherEntry.distance() == entry.distance() && otherEntry.nextConnection().equals((Object)entry.nextConnection())) {
                    otherEntry.timeout.setValue(100);
                }
                return;
            }
            targetIndex = i + 1;
        }
        this.entriesByDistance.add(targetIndex, entry);
        this.changed = true;
    }

    public Collection<? extends Component> createSummary() {
        ArrayList<MutableComponent> list = new ArrayList<MutableComponent>();
        for (RoutingTableEntry entry : this.entriesByDistance) {
            list.add(Component.literal((String)("    [" + entry.distance() + "] " + entry.port())));
        }
        return list;
    }

    public record RoutingTableEntry(String port, int distance, BlockPos nextConnection, MutableInt timeout, boolean endOfRoute) {
        public void tick() {
            this.timeout.decrement();
        }

        public boolean invalid() {
            return this.timeout.intValue() <= 0;
        }

        public RoutingTableEntry copyForNeighbour(BlockPos connection) {
            return new RoutingTableEntry(this.port, this.distance + 1, connection.multiply(-1), new MutableInt(100), false);
        }
    }
}
