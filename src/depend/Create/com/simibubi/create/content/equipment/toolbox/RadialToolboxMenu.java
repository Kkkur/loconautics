/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.gui.AbstractSimiScreen
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.equipment.toolbox;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllKeys;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxDisposeAllPacket;
import com.simibubi.create.content.equipment.toolbox.ToolboxEquipPacket;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandlerClient;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.List;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.theme.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class RadialToolboxMenu
extends AbstractSimiScreen {
    private State state;
    private int ticksOpen;
    private int hoveredSlot;
    private boolean scrollMode;
    private int scrollSlot = 0;
    private List<ToolboxBlockEntity> toolboxes;
    private ToolboxBlockEntity selectedBox;
    private static final int DEPOSIT = -7;
    private static final int UNEQUIP = -5;

    public RadialToolboxMenu(List<ToolboxBlockEntity> toolboxes, State state, @Nullable ToolboxBlockEntity selectedBox) {
        this.toolboxes = toolboxes;
        this.state = state;
        this.hoveredSlot = -1;
        if (selectedBox != null) {
            this.selectedBox = selectedBox;
        }
    }

    public void prevSlot(int slot) {
        this.scrollSlot = slot;
    }

    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        boolean renderCenterSlot;
        float hoveredY;
        float fade = Mth.clamp((float)(((float)this.ticksOpen + AnimationTickHolder.getPartialTicks()) / 10.0f), (float)0.001953125f, (float)1.0f);
        this.hoveredSlot = -1;
        Window window = this.getMinecraft().getWindow();
        float hoveredX = mouseX - window.getGuiScaledWidth() / 2;
        float distance = hoveredX * hoveredX + (hoveredY = (float)(mouseY - window.getGuiScaledHeight() / 2)) * hoveredY;
        if (distance > 25.0f && distance < 10000.0f) {
            this.hoveredSlot = Mth.floor((float)(AngleHelper.deg((double)Mth.atan2((double)hoveredY, (double)hoveredX)) + 360.0f + 180.0f - 22.5f)) % 360 / 45;
        }
        boolean bl = renderCenterSlot = this.state == State.SELECT_ITEM_UNEQUIP;
        if (this.scrollMode && distance > 150.0f) {
            this.scrollMode = false;
        }
        if (renderCenterSlot && distance <= 150.0f) {
            this.hoveredSlot = -5;
        }
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate((float)(this.width / 2), (float)(this.height / 2), 0.0f);
        MutableComponent tip = null;
        if (this.state == State.DETACH) {
            tip = CreateLang.translateDirect("toolbox.outOfRange", new Object[0]);
            if (hoveredX > -20.0f && hoveredX < 20.0f && hoveredY > -80.0f && hoveredY < -20.0f) {
                this.hoveredSlot = -5;
            }
            ms.pushPose();
            AllGuiTextures.TOOLBELT_INACTIVE_SLOT.render(graphics, -12, -12);
            GuiGameElement.of((ItemStack)AllBlocks.TOOLBOXES.get(DyeColor.BROWN).asStack()).at(-9.0f, -9.0f).render(graphics);
            ms.translate(0.0f, -40.0f + 10.0f * (1.0f - fade) * (1.0f - fade), 0.0f);
            AllGuiTextures.TOOLBELT_SLOT.render(graphics, -12, -12);
            ms.translate(-0.5, 0.5, 0.0);
            AllIcons.I_DISABLE.render(graphics, -9, -9);
            ms.translate(0.5, -0.5, 0.0);
            if (!this.scrollMode && this.hoveredSlot == -5) {
                AllGuiTextures.TOOLBELT_SLOT_HIGHLIGHT.render(graphics, -13, -13);
                tip = CreateLang.translateDirect("toolbox.detach", new Object[0]).withStyle(ChatFormatting.GOLD);
            }
            ms.popPose();
        } else {
            if (hoveredX > 60.0f && hoveredX < 100.0f && hoveredY > -20.0f && hoveredY < 20.0f) {
                this.hoveredSlot = -7;
            }
            ms.pushPose();
            ms.translate(80.0f + -5.0f * (1.0f - fade) * (1.0f - fade), 0.0f, 0.0f);
            AllGuiTextures.TOOLBELT_SLOT.render(graphics, -12, -12);
            ms.translate(-0.5, 0.5, 0.0);
            AllIcons.I_TOOLBOX.render(graphics, -9, -9);
            ms.translate(0.5, -0.5, 0.0);
            if (!this.scrollMode && this.hoveredSlot == -7) {
                AllGuiTextures.TOOLBELT_SLOT_HIGHLIGHT.render(graphics, -13, -13);
                tip = CreateLang.translateDirect(this.state == State.SELECT_BOX ? "toolbox.depositAll" : "toolbox.depositBox", new Object[0]).withStyle(ChatFormatting.GOLD);
            }
            ms.popPose();
            for (int slot = 0; slot < 8; ++slot) {
                ms.pushPose();
                ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateZDegrees((float)(slot * 45 - 45))).translate(0.0f, -40.0f + 10.0f * (1.0f - fade) * (1.0f - fade), 0.0f).rotateZDegrees((float)(-slot * 45 + 45));
                ms.translate(-12.0f, -12.0f, 0.0f);
                if (this.state == State.SELECT_ITEM || this.state == State.SELECT_ITEM_UNEQUIP) {
                    ToolboxInventory inv = this.selectedBox.inventory;
                    ItemStack stackInSlot = inv.filters.get(slot);
                    if (!stackInSlot.isEmpty()) {
                        boolean empty = inv.getStackInSlot(slot * 4).isEmpty();
                        (empty ? AllGuiTextures.TOOLBELT_INACTIVE_SLOT : AllGuiTextures.TOOLBELT_SLOT).render(graphics, 0, 0);
                        GuiGameElement.of((ItemStack)stackInSlot).at(3.0f, 3.0f).render(graphics);
                        if (slot == (this.scrollMode ? this.scrollSlot : this.hoveredSlot) && !empty) {
                            AllGuiTextures.TOOLBELT_SLOT_HIGHLIGHT.render(graphics, -1, -1);
                            tip = stackInSlot.getHoverName();
                        }
                    } else {
                        AllGuiTextures.TOOLBELT_EMPTY_SLOT.render(graphics, 0, 0);
                    }
                } else if (this.state == State.SELECT_BOX) {
                    if (slot < this.toolboxes.size()) {
                        AllGuiTextures.TOOLBELT_SLOT.render(graphics, 0, 0);
                        ToolboxBlockEntity toolboxBlockEntity = this.toolboxes.get(slot);
                        GuiGameElement.of((ItemStack)AllBlocks.TOOLBOXES.get(toolboxBlockEntity.getColor()).asStack()).at(3.0f, 3.0f).render(graphics);
                        if (slot == (this.scrollMode ? this.scrollSlot : this.hoveredSlot)) {
                            AllGuiTextures.TOOLBELT_SLOT_HIGHLIGHT.render(graphics, -1, -1);
                            tip = toolboxBlockEntity.getDisplayName();
                        }
                    } else {
                        AllGuiTextures.TOOLBELT_EMPTY_SLOT.render(graphics, 0, 0);
                    }
                }
                ms.popPose();
            }
            if (renderCenterSlot) {
                ms.pushPose();
                AllGuiTextures.TOOLBELT_SLOT.render(graphics, -12, -12);
                (this.scrollMode ? AllIcons.I_REFRESH : AllIcons.I_FLIP).render(graphics, -9, -9);
                if (!this.scrollMode && -5 == this.hoveredSlot) {
                    AllGuiTextures.TOOLBELT_SLOT_HIGHLIGHT.render(graphics, -13, -13);
                    tip = CreateLang.translateDirect("toolbox.unequip", this.minecraft.player.getMainHandItem().getHoverName()).withStyle(ChatFormatting.GOLD);
                }
                ms.popPose();
            }
        }
        ms.popPose();
        if (tip != null) {
            int i1 = (int)(fade * 255.0f);
            if (i1 > 255) {
                i1 = 255;
            }
            if (i1 > 8) {
                ms.pushPose();
                ms.translate((float)(this.width / 2), (float)(this.height - 68), 0.0f);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                int k1 = 0xFFFFFF;
                int k = i1 << 24 & 0xFF000000;
                int l = this.font.width((FormattedText)tip);
                graphics.drawString(this.font, (Component)tip, Math.round((float)(-l) / 2.0f), -4, k1 | k, false);
                RenderSystem.disableBlend();
                ms.popPose();
            }
        }
    }

    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        Color color = BACKGROUND_COLOR.scaleAlpha(Math.min(1.0f, ((float)this.ticksOpen + AnimationTickHolder.getPartialTicks()) / 20.0f));
        pGuiGraphics.fillGradient(0, 0, this.width, this.height, color.getRGB(), color.getRGB());
    }

    public void tick() {
        ++this.ticksOpen;
        super.tick();
    }

    public void removed() {
        int selected;
        super.removed();
        int n = selected = this.scrollMode ? this.scrollSlot : this.hoveredSlot;
        if (selected == -7) {
            if (this.state == State.DETACH) {
                return;
            }
            if (this.state == State.SELECT_BOX) {
                this.toolboxes.forEach(be -> CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ToolboxDisposeAllPacket(be.getBlockPos())));
            } else {
                CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ToolboxDisposeAllPacket(this.selectedBox.getBlockPos()));
            }
            return;
        }
        if (this.state == State.SELECT_BOX) {
            return;
        }
        if (this.state == State.DETACH) {
            if (selected == -5) {
                CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ToolboxEquipPacket(null, selected, this.minecraft.player.getInventory().selected));
            }
            return;
        }
        if (selected == -5) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ToolboxEquipPacket(this.selectedBox.getBlockPos(), selected, this.minecraft.player.getInventory().selected));
        }
        if (selected < 0) {
            return;
        }
        ToolboxInventory inv = this.selectedBox.inventory;
        ItemStack stackInSlot = inv.filters.get(selected);
        if (stackInSlot.isEmpty()) {
            return;
        }
        if (inv.getStackInSlot(selected * 4).isEmpty()) {
            return;
        }
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ToolboxEquipPacket(this.selectedBox.getBlockPos(), selected, this.minecraft.player.getInventory().selected));
    }

    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        double hoveredY;
        Window window = this.getMinecraft().getWindow();
        double hoveredX = pMouseY - (double)(window.getGuiScaledWidth() / 2);
        double distance = hoveredX * hoveredX + (hoveredY = pMouseY - (double)(window.getGuiScaledHeight() / 2)) * hoveredY;
        if (distance <= 150.0) {
            this.scrollMode = true;
            this.scrollSlot = ((int)((double)this.scrollSlot - pScrollY) + 8) % 8;
            for (int i = 0; i < 10; ++i) {
                if (this.state == State.SELECT_ITEM || this.state == State.SELECT_ITEM_UNEQUIP) {
                    ToolboxInventory inv = this.selectedBox.inventory;
                    ItemStack stackInSlot = inv.filters.get(this.scrollSlot);
                    if (!stackInSlot.isEmpty() && !inv.getStackInSlot(this.scrollSlot * 4).isEmpty()) break;
                }
                if (this.state == State.SELECT_BOX && this.scrollSlot < this.toolboxes.size() || this.state == State.DETACH) break;
                this.scrollSlot -= Mth.sign((double)pScrollY);
                this.scrollSlot = (this.scrollSlot + 8) % 8;
            }
            return true;
        }
        return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
    }

    public boolean mouseClicked(double x, double y, int button) {
        int selected;
        int n = selected = this.scrollMode ? this.scrollSlot : this.hoveredSlot;
        if (button == 0) {
            if (selected == -7) {
                this.onClose();
                ToolboxHandlerClient.COOLDOWN = 2;
                return true;
            }
            if (this.state == State.SELECT_BOX && selected >= 0 && selected < this.toolboxes.size()) {
                this.state = State.SELECT_ITEM;
                this.selectedBox = this.toolboxes.get(selected);
                return true;
            }
            if (!(this.state != State.DETACH && this.state != State.SELECT_ITEM && this.state != State.SELECT_ITEM_UNEQUIP || selected != -5 && selected < 0)) {
                this.onClose();
                ToolboxHandlerClient.COOLDOWN = 2;
                return true;
            }
        }
        if (button == 1) {
            if (this.state == State.SELECT_ITEM && this.toolboxes.size() > 1) {
                this.state = State.SELECT_BOX;
                return true;
            }
            if (this.state == State.SELECT_ITEM_UNEQUIP && selected == -5) {
                if (this.toolboxes.size() > 1) {
                    CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ToolboxEquipPacket(this.selectedBox.getBlockPos(), selected, this.minecraft.player.getInventory().selected));
                    this.state = State.SELECT_BOX;
                    return true;
                }
                this.onClose();
                ToolboxHandlerClient.COOLDOWN = 2;
                return true;
            }
        }
        return super.mouseClicked(x, y, button);
    }

    public boolean keyPressed(int code, int scanCode, int modifiers) {
        KeyMapping[] hotbarBinds = this.minecraft.options.keyHotbarSlots;
        for (int i = 0; i < hotbarBinds.length && i < 8; ++i) {
            if (!hotbarBinds[i].matches(code, scanCode)) continue;
            if (this.state == State.SELECT_ITEM || this.state == State.SELECT_ITEM_UNEQUIP) {
                ToolboxInventory inv = this.selectedBox.inventory;
                ItemStack stackInSlot = inv.filters.get(i);
                if (stackInSlot.isEmpty() || inv.getStackInSlot(i * 4).isEmpty()) {
                    return false;
                }
            }
            if (this.state == State.SELECT_BOX && i >= this.toolboxes.size()) {
                return false;
            }
            this.scrollMode = true;
            this.scrollSlot = i;
            this.mouseClicked(0.0, 0.0, 0);
            return true;
        }
        return super.keyPressed(code, scanCode, modifiers);
    }

    public boolean keyReleased(int code, int scanCode, int modifiers) {
        InputConstants.Key mouseKey = InputConstants.getKey((int)code, (int)scanCode);
        if (AllKeys.TOOLBELT.getKeybind().isActiveAndMatches(mouseKey)) {
            this.onClose();
            return true;
        }
        return super.keyReleased(code, scanCode, modifiers);
    }

    public static enum State {
        SELECT_BOX,
        SELECT_ITEM,
        SELECT_ITEM_UNEQUIP,
        DETACH;

    }
}
