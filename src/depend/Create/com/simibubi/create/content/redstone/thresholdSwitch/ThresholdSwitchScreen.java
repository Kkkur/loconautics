/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.gui.AbstractSimiScreen
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.GuiGameElement$GuiRenderBuilder
 *  net.createmod.catnip.gui.widget.AbstractSimiWidget
 *  net.createmod.catnip.platform.CatnipServices
 *  net.createmod.ponder.foundation.ui.PonderTagScreen
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.RedstoneTorchBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.redstone.thresholdSwitch;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.redstone.thresholdSwitch.ConfigureThresholdSwitchPacket;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchBlockEntity;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.ponder.foundation.ui.PonderTagScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class ThresholdSwitchScreen
extends AbstractSimiScreen {
    private ScrollInput offBelow;
    private ScrollInput onAbove;
    private SelectionScrollInput inStacks;
    private IconButton confirmButton;
    private IconButton flipSignals;
    private final Component invertSignal = CreateLang.translateDirect("gui.threshold_switch.invert_signal", new Object[0]);
    private final ItemStack renderedItem = new ItemStack((ItemLike)AllBlocks.THRESHOLD_SWITCH.get());
    private AllGuiTextures background = AllGuiTextures.THRESHOLD_SWITCH;
    private ThresholdSwitchBlockEntity blockEntity;
    private int lastModification;

    public ThresholdSwitchScreen(ThresholdSwitchBlockEntity be) {
        super((Component)CreateLang.translateDirect("gui.threshold_switch.title", new Object[0]));
        this.blockEntity = be;
        this.lastModification = -1;
    }

    protected void init() {
        this.setWindowSize(this.background.getWidth(), this.background.getHeight());
        this.setWindowOffset(-20, 0);
        super.init();
        int x = this.guiLeft;
        int y = this.guiTop;
        this.inStacks = (SelectionScrollInput)new SelectionScrollInput(x + 100, y + 23, 52, 42).forOptions(List.of(CreateLang.translateDirect("schedule.condition.threshold.items", new Object[0]), CreateLang.translateDirect("schedule.condition.threshold.stacks", new Object[0]))).titled(CreateLang.translateDirect("schedule.condition.threshold.item_measure", new Object[0])).setState(this.blockEntity.inStacks ? 1 : 0);
        this.offBelow = new ScrollInput(x + 48, y + 47, 1, 18).withRange(this.blockEntity.getMinLevel(), this.blockEntity.getMaxLevel() + 1 - this.getValueStep()).titled(CreateLang.translateDirect("gui.threshold_switch.lower_threshold", new Object[0])).calling(state -> {
            this.lastModification = 0;
            int valueStep = this.getValueStep();
            if (this.onAbove.getState() / valueStep == 0 && state / valueStep == 0) {
                return;
            }
            if (this.onAbove.getState() / valueStep <= state / valueStep) {
                this.onAbove.setState((state + valueStep) / valueStep * valueStep);
                this.onAbove.onChanged();
            }
        }).withStepFunction(sc -> sc.shift ? 10 * this.getValueStep() : this.getValueStep()).setState(this.blockEntity.offWhenBelow);
        this.onAbove = new ScrollInput(x + 48, y + 23, 1, 18).withRange(this.blockEntity.getMinLevel() + this.getValueStep(), this.blockEntity.getMaxLevel() + 1).titled(CreateLang.translateDirect("gui.threshold_switch.upper_threshold", new Object[0])).calling(state -> {
            this.lastModification = 0;
            int valueStep = this.getValueStep();
            if (this.offBelow.getState() / valueStep == 0 && state / valueStep == 0) {
                return;
            }
            if (this.offBelow.getState() / valueStep >= state / valueStep) {
                this.offBelow.setState((state - valueStep) / valueStep * valueStep);
                this.offBelow.onChanged();
            }
        }).withStepFunction(sc -> sc.shift ? 10 * this.getValueStep() : this.getValueStep()).setState(this.blockEntity.onWhenAbove);
        this.onAbove.onChanged();
        this.offBelow.onChanged();
        this.addRenderableWidget((GuiEventListener)this.onAbove);
        this.addRenderableWidget((GuiEventListener)this.offBelow);
        this.addRenderableWidget((GuiEventListener)this.inStacks);
        this.confirmButton = new IconButton(x + this.background.getWidth() - 33, y + this.background.getHeight() - 24, AllIcons.I_CONFIRM);
        this.confirmButton.withCallback(() -> this.onClose());
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
        this.flipSignals = new IconButton(x + this.background.getWidth() - 62, y + this.background.getHeight() - 24, AllIcons.I_FLIP);
        this.flipSignals.withCallback(() -> this.send(!this.blockEntity.isInverted()));
        this.flipSignals.setToolTip(this.invertSignal);
        this.addRenderableWidget((GuiEventListener)this.flipSignals);
        this.updateInputBoxes();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int pButton) {
        int itemX = this.guiLeft + 13;
        int itemY = this.guiTop + 80;
        if (mouseX >= (double)itemX && mouseX < (double)(itemX + 16) && mouseY >= (double)itemY && mouseY < (double)(itemY + 16)) {
            ScreenOpener.open((Screen)new PonderTagScreen(AllCreatePonderTags.THRESHOLD_SWITCH_TARGETS));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, pButton);
    }

    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        boolean stacks;
        int x = this.guiLeft;
        int y = this.guiTop;
        this.background.render(graphics, x, y);
        graphics.drawString(this.font, this.title, x + this.background.getWidth() / 2 - this.font.width((FormattedText)this.title) / 2, y + 4, 5841956, false);
        ThresholdSwitchBlockEntity.ThresholdType typeOfCurrentTarget = this.blockEntity.getTypeOfCurrentTarget();
        boolean forItems = typeOfCurrentTarget == ThresholdSwitchBlockEntity.ThresholdType.ITEM;
        AllGuiTextures inputBg = forItems ? AllGuiTextures.THRESHOLD_SWITCH_ITEMCOUNT_INPUTS : AllGuiTextures.THRESHOLD_SWITCH_MISC_INPUTS;
        inputBg.render(graphics, x + 44, y + 21);
        inputBg.render(graphics, x + 44, y + 21 + 24);
        int valueStep = 1;
        boolean bl = stacks = this.inStacks.getState() == 1;
        if (typeOfCurrentTarget == ThresholdSwitchBlockEntity.ThresholdType.FLUID) {
            valueStep = 1000;
        }
        if (forItems) {
            MutableComponent suffix = this.inStacks.getState() == 0 ? CreateLang.translateDirect("schedule.condition.threshold.items", new Object[0]) : CreateLang.translateDirect("schedule.condition.threshold.stacks", new Object[0]);
            valueStep = this.inStacks.getState() == 0 ? 1 : 64;
            graphics.drawString(this.font, (Component)suffix, x + 105, y + 28, -1, true);
            graphics.drawString(this.font, (Component)suffix, x + 105, y + 28 + 24, -1, true);
        }
        graphics.drawString(this.font, (Component)Component.literal((String)("\u2265 " + String.valueOf(typeOfCurrentTarget == ThresholdSwitchBlockEntity.ThresholdType.UNSUPPORTED ? "" : (forItems ? Integer.valueOf(this.onAbove.getState() / valueStep) : this.blockEntity.format(this.onAbove.getState() / valueStep, stacks).getString())))), x + 53, y + 28, -1, true);
        graphics.drawString(this.font, (Component)Component.literal((String)("\u2264 " + String.valueOf(typeOfCurrentTarget == ThresholdSwitchBlockEntity.ThresholdType.UNSUPPORTED ? "" : (forItems ? Integer.valueOf(this.offBelow.getState() / valueStep) : this.blockEntity.format(this.offBelow.getState() / valueStep, stacks).getString())))), x + 53, y + 28 + 24, -1, true);
        ((GuiGameElement.GuiRenderBuilder)GuiGameElement.of((ItemStack)this.renderedItem).at((float)(x + this.background.getWidth() + 6), (float)(y + this.background.getHeight() - 56), -200.0f)).scale(5.0).render(graphics);
        int itemX = x + 13;
        int itemY = y + 80;
        ItemStack displayItem = this.blockEntity.getDisplayItemForScreen();
        ((GuiGameElement.GuiRenderBuilder)GuiGameElement.of((ItemStack)(displayItem.isEmpty() ? new ItemStack((ItemLike)Items.BARRIER) : displayItem)).at((float)itemX, (float)itemY, 0.0f)).render(graphics);
        int torchX = x + 23;
        int torchY = y + 24;
        boolean highlightTopRow = this.blockEntity.isInverted() ^ this.blockEntity.isPowered();
        AllGuiTextures.THRESHOLD_SWITCH_CURRENT_STATE.render(graphics, torchX - 3, torchY - 4 + (highlightTopRow ? 0 : 24));
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate((float)(torchX - 5), (float)(torchY + 14), 200.0f);
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateXDegrees(-22.5f)).rotateYDegrees(45.0f);
        for (boolean power : Iterate.trueAndFalse) {
            GuiGameElement.of((BlockState)((BlockState)Blocks.REDSTONE_TORCH.defaultBlockState().setValue((Property)RedstoneTorchBlock.LIT, (Comparable)Boolean.valueOf(this.blockEntity.isInverted() ^ power)))).scale(20.0).render(graphics);
            ms.translate(0.0f, 26.0f, 0.0f);
        }
        ms.popPose();
        if (mouseX >= itemX && mouseX < itemX + 16 && mouseY >= itemY && mouseY < itemY + 16) {
            ArrayList<Object> list = new ArrayList<Object>();
            if (displayItem.isEmpty()) {
                list.add(CreateLang.translateDirect("gui.threshold_switch.not_attached", new Object[0]));
                list.add(CreateLang.translateDirect("display_link.view_compatible", new Object[0]).withStyle(ChatFormatting.DARK_GRAY));
                graphics.renderComponentTooltip(this.font, list, mouseX, mouseY);
                return;
            }
            list.add(displayItem.getHoverName());
            if (typeOfCurrentTarget == ThresholdSwitchBlockEntity.ThresholdType.UNSUPPORTED) {
                list.add(CreateLang.translateDirect("gui.threshold_switch.incompatible", new Object[0]).withStyle(ChatFormatting.GRAY));
                list.add(CreateLang.translateDirect("display_link.view_compatible", new Object[0]).withStyle(ChatFormatting.DARK_GRAY));
                graphics.renderComponentTooltip(this.font, list, mouseX, mouseY);
                return;
            }
            CreateLang.translate("gui.threshold_switch.currently", this.blockEntity.format(this.blockEntity.currentLevel / valueStep, stacks)).style(ChatFormatting.DARK_AQUA).addTo(list);
            if (this.blockEntity.currentMinLevel / valueStep == 0) {
                CreateLang.translate("gui.threshold_switch.range_max", this.blockEntity.format(this.blockEntity.currentMaxLevel / valueStep, stacks)).style(ChatFormatting.GRAY).addTo(list);
            } else {
                CreateLang.translate("gui.threshold_switch.range", this.blockEntity.currentMinLevel / valueStep, this.blockEntity.format(this.blockEntity.currentMaxLevel / valueStep, stacks)).style(ChatFormatting.GRAY).addTo(list);
            }
            list.add(CreateLang.translateDirect("display_link.view_compatible", new Object[0]).withStyle(ChatFormatting.DARK_GRAY));
            graphics.renderComponentTooltip(this.font, list, mouseX, mouseY);
            return;
        }
        for (boolean power : Iterate.trueAndFalse) {
            int thisTorchY;
            int n = thisTorchY = power ? torchY : torchY + 26;
            if (mouseX < torchX || mouseX >= torchX + 16 || mouseY < thisTorchY || mouseY >= thisTorchY + 16) continue;
            graphics.renderComponentTooltip(this.font, List.of(CreateLang.translate(power ^ this.blockEntity.isInverted() ? "gui.threshold_switch.power_on_when" : "gui.threshold_switch.power_off_when", new Object[0]).color(AbstractSimiWidget.HEADER_RGB).component()), mouseX, mouseY);
            return;
        }
    }

    public void tick() {
        super.tick();
        if (this.lastModification >= 0) {
            ++this.lastModification;
        }
        if (this.lastModification >= 20) {
            this.lastModification = -1;
            this.send(this.blockEntity.isInverted());
        }
        if (this.inStacks == null) {
            return;
        }
        this.updateInputBoxes();
    }

    private void updateInputBoxes() {
        ThresholdSwitchBlockEntity.ThresholdType typeOfCurrentTarget = this.blockEntity.getTypeOfCurrentTarget();
        boolean forItems = typeOfCurrentTarget == ThresholdSwitchBlockEntity.ThresholdType.ITEM;
        int valueStep = this.getValueStep();
        this.inStacks.active = this.inStacks.visible = forItems;
        this.onAbove.setWidth(forItems ? 48 : 103);
        this.offBelow.setWidth(forItems ? 48 : 103);
        this.onAbove.visible = typeOfCurrentTarget != ThresholdSwitchBlockEntity.ThresholdType.UNSUPPORTED;
        this.offBelow.visible = typeOfCurrentTarget != ThresholdSwitchBlockEntity.ThresholdType.UNSUPPORTED;
        int min = this.blockEntity.currentMinLevel + valueStep;
        int max = this.blockEntity.currentMaxLevel;
        this.onAbove.withRange(min, max + 1);
        int roundedState = Mth.clamp((int)(this.onAbove.getState() / valueStep * valueStep), (int)min, (int)max);
        if (roundedState != this.onAbove.getState()) {
            this.onAbove.setState(roundedState);
            this.onAbove.onChanged();
        }
        min = this.blockEntity.currentMinLevel;
        max = this.blockEntity.currentMaxLevel - valueStep;
        this.offBelow.withRange(min, max + 1);
        roundedState = Mth.clamp((int)(this.offBelow.getState() / valueStep * valueStep), (int)min, (int)max);
        if (roundedState != this.offBelow.getState()) {
            this.offBelow.setState(roundedState);
            this.offBelow.onChanged();
        }
    }

    private int getValueStep() {
        boolean stacks = this.inStacks.getState() == 1;
        int valueStep = 1;
        if (this.blockEntity.getTypeOfCurrentTarget() == ThresholdSwitchBlockEntity.ThresholdType.FLUID) {
            valueStep = 1000;
        } else if (stacks) {
            valueStep = 64;
        }
        return valueStep;
    }

    public void removed() {
        this.send(this.blockEntity.isInverted());
    }

    protected void send(boolean invert) {
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ConfigureThresholdSwitchPacket(this.blockEntity.getBlockPos(), this.offBelow.getState(), this.onAbove.getState(), invert, this.inStacks.getState() == 1));
    }
}
