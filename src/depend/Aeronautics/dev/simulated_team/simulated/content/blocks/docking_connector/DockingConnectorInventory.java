/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.content.blocks.docking_connector;

import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity;
import dev.simulated_team.simulated.multiloader.inventory.ContainerSlot;
import dev.simulated_team.simulated.multiloader.inventory.ItemInfoWrapper;
import dev.simulated_team.simulated.multiloader.inventory.SingleSlotContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DockingConnectorInventory
extends SingleSlotContainer {
    private final DockingConnectorBlockEntity blockEntity;
    private BlockPos connectedPos;
    private DockingConnectorInventory connectedInventory;

    public DockingConnectorInventory(DockingConnectorBlockEntity blockEntity) {
        super(64);
        this.blockEntity = blockEntity;
    }

    public void connect(BlockPos connectedPos, DockingConnectorInventory other) {
        this.connectedPos = connectedPos;
        this.connectedInventory = other;
    }

    public void disconnect() {
        this.connectedPos = null;
        this.connectedInventory = null;
    }

    @Override
    public int commonInsert(ItemInfoWrapper item, ContainerSlot slot, int amountToInsert, boolean simulate) {
        return this.canInsertItem(item) ? this.connectedInventory.slot.insertStack(item, amountToInsert, simulate) : 0;
    }

    @Override
    public boolean canInsertItem(ItemInfoWrapper info) {
        BlockEntity blockEntity;
        Level level = this.blockEntity.getLevel();
        if (level == null) {
            return false;
        }
        if (this.connectedPos == null || !((blockEntity = level.getBlockEntity(this.connectedPos)) instanceof DockingConnectorBlockEntity)) {
            return false;
        }
        DockingConnectorBlockEntity other = (DockingConnectorBlockEntity)blockEntity;
        return this.connectedInventory == other.inventory;
    }
}
