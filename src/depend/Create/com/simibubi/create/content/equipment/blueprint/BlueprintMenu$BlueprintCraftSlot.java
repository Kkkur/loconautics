/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket
 *  net.minecraft.server.level.ServerPlayer
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.SlotItemHandler
 */
package com.simibubi.create.content.equipment.blueprint;

import com.simibubi.create.content.equipment.blueprint.BlueprintEntity;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

class BlueprintMenu.BlueprintCraftSlot
extends SlotItemHandler {
    private int index;

    public BlueprintMenu.BlueprintCraftSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.index = index;
    }

    public void setChanged() {
        super.setChanged();
        if (this.index == 9 && this.hasItem() && !((BlueprintEntity.BlueprintSection)BlueprintMenu.this.contentHolder).getBlueprintWorld().isClientSide) {
            ((BlueprintEntity.BlueprintSection)BlueprintMenu.this.contentHolder).inferredIcon = false;
            ServerPlayer serverplayerentity = (ServerPlayer)BlueprintMenu.this.player;
            serverplayerentity.connection.send((Packet)new ClientboundContainerSetSlotPacket(BlueprintMenu.this.containerId, BlueprintMenu.this.incrementStateId(), 45, this.getItem()));
        }
        if (this.index < 9) {
            BlueprintMenu.this.onCraftMatrixChanged();
        }
    }
}
