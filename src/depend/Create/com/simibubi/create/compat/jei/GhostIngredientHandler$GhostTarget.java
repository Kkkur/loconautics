/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.gui.handlers.IGhostIngredientHandler$Target
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.compat.jei;

import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import com.simibubi.create.foundation.gui.menu.GhostItemSubmitPacket;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

private static class GhostIngredientHandler.GhostTarget<I, T extends GhostItemMenu<?>>
implements IGhostIngredientHandler.Target<I> {
    private final Rect2i area;
    private final AbstractSimiContainerScreen<T> gui;
    private final int slotIndex;
    private final boolean isAttributeFilter;

    public GhostIngredientHandler.GhostTarget(AbstractSimiContainerScreen<T> gui, int slotIndex, boolean isAttributeFilter) {
        this.gui = gui;
        this.slotIndex = slotIndex;
        this.isAttributeFilter = isAttributeFilter;
        Slot slot = (Slot)((GhostItemMenu)gui.getMenu()).slots.get(slotIndex + 36);
        this.area = new Rect2i(gui.getGuiLeft() + slot.x, gui.getGuiTop() + slot.y, 16, 16);
    }

    public Rect2i getArea() {
        return this.area;
    }

    public void accept(I ingredient) {
        ItemStack stack = ((ItemStack)ingredient).copy();
        stack.setCount(1);
        ((GhostItemMenu)this.gui.getMenu()).ghostInventory.setStackInSlot(this.slotIndex, stack);
        if (this.isAttributeFilter) {
            return;
        }
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new GhostItemSubmitPacket(stack, this.slotIndex));
    }
}
