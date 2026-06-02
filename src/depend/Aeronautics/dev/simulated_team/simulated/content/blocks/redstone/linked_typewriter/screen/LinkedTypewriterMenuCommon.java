/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.gui.menu.GhostItemMenu
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen;

import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import dev.simulated_team.simulated.index.SimMenuTypes;
import dev.simulated_team.simulated.service.SimMenuService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class LinkedTypewriterMenuCommon
extends GhostItemMenu<LinkedTypewriterBlockEntity> {
    public boolean slotsActive = false;

    public LinkedTypewriterMenuCommon(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public LinkedTypewriterMenuCommon(MenuType<?> type, int id, Inventory inv, LinkedTypewriterBlockEntity be) {
        super(type, id, inv, (Object)be);
    }

    public static LinkedTypewriterMenuCommon create(int id, Inventory inv, LinkedTypewriterBlockEntity be) {
        return SimMenuService.INSTANCE.getLoaderLinkedTypewriter((MenuType)SimMenuTypes.LINKED_TYPEWRITER.get(), id, inv, be);
    }

    protected LinkedTypewriterBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        ClientLevel world = Minecraft.getInstance().level;
        BlockEntity blockEntity = world.getBlockEntity(extraData.readBlockPos());
        if (blockEntity instanceof LinkedTypewriterBlockEntity) {
            LinkedTypewriterBlockEntity linkedTypewriter = (LinkedTypewriterBlockEntity)blockEntity;
            linkedTypewriter.readClient(extraData.readNbt(), (HolderLookup.Provider)extraData.registryAccess());
            return linkedTypewriter;
        }
        return null;
    }

    protected void addPlayerSlots(int x, int y) {
        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            this.addSlot(new PlayerSlot((Container)this.playerInventory, hotbarSlot, x + hotbarSlot * 18, y + 58));
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new PlayerSlot((Container)this.playerInventory, col + row * 9 + 9, x + col * 18, y + row * 18));
            }
        }
    }

    protected void saveData(LinkedTypewriterBlockEntity be) {
    }

    protected boolean allowRepeats() {
        return false;
    }

    private class PlayerSlot
    extends Slot {
        public PlayerSlot(Container pContainer, int pIndex, int pX, int pY) {
            super(pContainer, pIndex, pX, pY);
        }

        public boolean isActive() {
            return LinkedTypewriterMenuCommon.this.slotsActive;
        }
    }
}
