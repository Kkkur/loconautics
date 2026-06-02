/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import net.minecraft.core.BlockPos;
import org.apache.commons.lang3.mutable.MutableInt;

public record ChainConveyorRoutingTable.RoutingTableEntry(String port, int distance, BlockPos nextConnection, MutableInt timeout, boolean endOfRoute) {
    public void tick() {
        this.timeout.decrement();
    }

    public boolean invalid() {
        return this.timeout.intValue() <= 0;
    }

    public ChainConveyorRoutingTable.RoutingTableEntry copyForNeighbour(BlockPos connection) {
        return new ChainConveyorRoutingTable.RoutingTableEntry(this.port, this.distance + 1, connection.multiply(-1), new MutableInt(100), false);
    }
}
