/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.equipment.toolbox;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxDisposeAllPacket;
import com.simibubi.create.content.equipment.toolbox.ToolboxMenu;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.Collections;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ToolboxScreen
extends AbstractSimiContainerScreen<ToolboxMenu> {
    protected static final AllGuiTextures BG = AllGuiTextures.TOOLBOX;
    protected static final AllGuiTextures PLAYER = AllGuiTextures.PLAYER_INVENTORY;
    protected Slot hoveredToolboxSlot;
    private IconButton confirmButton;
    private IconButton disposeButton;
    private DyeColor color;
    private List<Rect2i> extraAreas = Collections.emptyList();

    public ToolboxScreen(ToolboxMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.init();
    }

    @Override
    protected void init() {
        this.setWindowSize(30 + BG.getWidth(), BG.getHeight() + PLAYER.getHeight() - 24);
        this.setWindowOffset(-11, 0);
        super.init();
        this.clearWidgets();
        this.color = ((ToolboxBlockEntity)((ToolboxMenu)this.menu).contentHolder).getColor();
        this.confirmButton = new IconButton(this.leftPos + 30 + BG.getWidth() - 33, this.topPos + BG.getHeight() - 24, AllIcons.I_CONFIRM);
        this.confirmButton.withCallback(() -> this.minecraft.player.closeContainer());
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
        this.disposeButton = new IconButton(this.leftPos + 30 + 81, this.topPos + 69, AllIcons.I_TOOLBOX);
        this.disposeButton.withCallback(() -> CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ToolboxDisposeAllPacket(((ToolboxBlockEntity)((ToolboxMenu)this.menu).contentHolder).getBlockPos())));
        this.disposeButton.setToolTip((Component)CreateLang.translateDirect("toolbox.depositBox", new Object[0]));
        this.addRenderableWidget((GuiEventListener)this.disposeButton);
        this.extraAreas = ImmutableList.of((Object)new Rect2i(this.leftPos + 30 + BG.getWidth(), this.topPos + BG.getHeight() - 15 - 34 - 6, 72, 68));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        ((ToolboxMenu)this.menu).renderPass = true;
        super.render(graphics, mouseX, mouseY, partialTicks);
        ((ToolboxMenu)this.menu).renderPass = false;
    }

    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int x = this.leftPos + this.imageWidth - BG.getWidth();
        int y = this.topPos;
        BG.render(graphics, x, y);
        graphics.drawString(this.font, this.title, x + 15, y + 4, 5841956, false);
        int invX = this.leftPos;
        int invY = this.topPos + this.imageHeight - PLAYER.getHeight();
        this.renderPlayerInventory(graphics, invX, invY);
        this.renderToolbox(graphics, x + BG.getWidth() + 50, y + BG.getHeight() + 12, partialTicks);
        PoseStack ms = graphics.pose();
        this.hoveredToolboxSlot = null;
        for (int compartment = 0; compartment < 8; ++compartment) {
            int baseIndex = compartment * 4;
            Slot slot = (Slot)((ToolboxMenu)this.menu).slots.get(baseIndex);
            ItemStack itemstack = slot.getItem();
            int i = slot.x + this.leftPos;
            int j = slot.y + this.topPos;
            if (itemstack.isEmpty()) {
                itemstack = ((ToolboxMenu)this.menu).getFilter(compartment);
            }
            if (!itemstack.isEmpty()) {
                int count = ((ToolboxMenu)this.menu).totalCountInCompartment(compartment);
                String s = String.valueOf(count);
                ms.pushPose();
                ms.translate(0.0f, 0.0f, 100.0f);
                RenderSystem.enableDepthTest();
                graphics.renderItem((LivingEntity)this.minecraft.player, itemstack, i, j, 0);
                graphics.renderItemDecorations(this.font, itemstack, i, j, s);
                ms.popPose();
            }
            if (!this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY)) continue;
            this.hoveredToolboxSlot = slot;
            RenderSystem.disableDepthTest();
            RenderSystem.colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)false);
            int slotColor = this.getSlotColor(baseIndex);
            graphics.fillGradient(i, j, i + 16, j + 16, slotColor, slotColor);
            RenderSystem.colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
            RenderSystem.enableDepthTest();
        }
    }

    private void renderToolbox(GuiGraphics graphics, int x, int y, float partialTicks) {
        PoseStack ms = graphics.pose();
        ((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)ms).pushPose().translate((float)x, (float)y, 100.0f).scale(50.0f)).rotateXDegrees(-22.0f)).rotateYDegrees(-202.0f);
        GuiGameElement.of((BlockState)AllBlocks.TOOLBOXES.get(this.color).getDefaultState()).render(graphics);
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).pushPose().translate(0.0f, -0.375f, 0.75f).rotateXDegrees(-105.0f * ((ToolboxBlockEntity)((ToolboxMenu)this.menu).contentHolder).lid.getValue(partialTicks))).translate(0.0f, 0.375f, -0.75f);
        GuiGameElement.of((PartialModel)AllPartialModels.TOOLBOX_LIDS.get(this.color)).render(graphics);
        ms.popPose();
        for (int offset : Iterate.zeroAndOne) {
            ms.pushPose();
            ms.translate(0.0f, (float)(-offset * 1) / 8.0f, ((ToolboxBlockEntity)((ToolboxMenu)this.menu).contentHolder).drawers.getValue(partialTicks) * -0.175f * (float)(2 - offset));
            GuiGameElement.of((PartialModel)AllPartialModels.TOOLBOX_DRAWER).render(graphics);
            ms.popPose();
        }
        ms.popPose();
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.hoveredToolboxSlot != null) {
            this.hoveredSlot = this.hoveredToolboxSlot;
        }
        super.renderForeground(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return this.extraAreas;
    }
}
