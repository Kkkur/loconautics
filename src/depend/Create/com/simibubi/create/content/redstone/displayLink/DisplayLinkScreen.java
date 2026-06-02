/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.gui.AbstractSimiScreen
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.RenderElement
 *  net.createmod.catnip.gui.widget.AbstractSimiWidget
 *  net.createmod.catnip.gui.widget.ElementWidget
 *  net.createmod.catnip.platform.CatnipServices
 *  net.createmod.ponder.foundation.ui.PonderTagScreen
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Renderable
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.redstone.displayLink;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkConfigurationPacket;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.Collections;
import java.util.List;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.element.RenderElement;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.createmod.catnip.gui.widget.ElementWidget;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.ponder.foundation.ui.PonderTagScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class DisplayLinkScreen
extends AbstractSimiScreen {
    private static final ItemStack FALLBACK = new ItemStack((ItemLike)Items.BARRIER);
    private AllGuiTextures background = AllGuiTextures.DATA_GATHERER;
    private DisplayLinkBlockEntity blockEntity;
    private IconButton confirmButton;
    BlockState sourceState;
    BlockState targetState;
    List<DisplaySource> sources;
    DisplayTarget target;
    ScrollInput sourceTypeSelector;
    Label sourceTypeLabel;
    ScrollInput targetLineSelector;
    Label targetLineLabel;
    AbstractSimiWidget sourceWidget;
    AbstractSimiWidget targetWidget;
    Couple<ModularGuiLine> configWidgets;

    public DisplayLinkScreen(DisplayLinkBlockEntity be) {
        this.blockEntity = be;
        this.sources = Collections.emptyList();
        this.configWidgets = Couple.create(ModularGuiLine::new);
        this.target = null;
    }

    protected void init() {
        this.setWindowSize(this.background.getWidth(), this.background.getHeight());
        super.init();
        this.clearWidgets();
        int x = this.guiLeft;
        int y = this.guiTop;
        this.initGathererOptions();
        this.confirmButton = new IconButton(x + this.background.getWidth() - 33, y + this.background.getHeight() - 24, AllIcons.I_CONFIRM);
        this.confirmButton.withCallback(this::onClose);
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
    }

    public void tick() {
        super.tick();
        if (this.sourceState != null && this.sourceState.getBlock() != this.minecraft.level.getBlockState(this.blockEntity.getSourcePosition()).getBlock() || this.targetState != null && this.targetState.getBlock() != this.minecraft.level.getBlockState(this.blockEntity.getTargetPosition()).getBlock()) {
            this.initGathererOptions();
        }
    }

    private void initGathererOptions() {
        ClientLevel level = this.minecraft.level;
        this.sourceState = level.getBlockState(this.blockEntity.getSourcePosition());
        this.targetState = level.getBlockState(this.blockEntity.getTargetPosition());
        int x = this.guiLeft;
        int y = this.guiTop;
        Block sourceBlock = this.sourceState.getBlock();
        Block targetBlock = this.targetState.getBlock();
        ItemStack asItem = sourceBlock.getCloneItemStack((LevelReader)level, this.blockEntity.getSourcePosition(), this.sourceState);
        ItemStack sourceIcon = asItem == null || asItem.isEmpty() ? FALLBACK : asItem;
        asItem = targetBlock.getCloneItemStack((LevelReader)level, this.blockEntity.getTargetPosition(), this.targetState);
        ItemStack targetIcon = asItem == null || asItem.isEmpty() ? FALLBACK : asItem;
        this.sources = DisplaySource.getAll((LevelAccessor)level, this.blockEntity.getSourcePosition());
        this.target = DisplayTarget.get((LevelAccessor)level, this.blockEntity.getTargetPosition());
        this.removeWidget((GuiEventListener)this.targetLineSelector);
        this.removeWidget((GuiEventListener)this.targetLineLabel);
        this.removeWidget((GuiEventListener)this.sourceTypeSelector);
        this.removeWidget((GuiEventListener)this.sourceTypeLabel);
        this.removeWidget((GuiEventListener)this.sourceWidget);
        this.removeWidget((GuiEventListener)this.targetWidget);
        this.configWidgets.forEach(s -> s.forEach(this::removeWidget));
        this.targetLineSelector = null;
        this.sourceTypeSelector = null;
        if (this.target != null) {
            DisplayTargetStats stats = this.target.provideStats(new DisplayLinkContext((Level)level, this.blockEntity));
            int rows = stats.maxRows();
            int startIndex = Math.min(this.blockEntity.targetLine, rows);
            this.targetLineLabel = new Label(x + 65, y + 109, CommonComponents.EMPTY).withShadow();
            this.targetLineLabel.text = this.target.getLineOptionText(startIndex);
            if (rows > 1) {
                this.targetLineSelector = new ScrollInput(x + 61, y + 105, 135, 16).withRange(0, rows).titled(CreateLang.translateDirect("display_link.display_on", new Object[0])).inverted().calling(i -> {
                    this.targetLineLabel.text = this.target.getLineOptionText((int)i);
                }).setState(startIndex);
                this.addRenderableWidget((GuiEventListener)this.targetLineSelector);
            }
            this.addRenderableWidget((GuiEventListener)this.targetLineLabel);
        }
        this.sourceWidget = new ElementWidget(x + 37, y + 26).showingElement((RenderElement)GuiGameElement.of((ItemStack)sourceIcon)).withCallback((mX, mY) -> ScreenOpener.open((Screen)new PonderTagScreen(AllCreatePonderTags.DISPLAY_SOURCES)));
        this.sourceWidget.getToolTip().addAll(List.of(CreateLang.translateDirect("display_link.reading_from", new Object[0]), this.sourceState.getBlock().getName().withStyle(s -> s.withColor(this.sources.isEmpty() ? 16157065 : 15909229)), CreateLang.translateDirect("display_link.attached_side", new Object[0]), CreateLang.translateDirect("display_link.view_compatible", new Object[0]).withStyle(ChatFormatting.GRAY)));
        this.addRenderableWidget((GuiEventListener)this.sourceWidget);
        this.targetWidget = new ElementWidget(x + 37, y + 105).showingElement((RenderElement)GuiGameElement.of((ItemStack)targetIcon)).withCallback((mX, mY) -> ScreenOpener.open((Screen)new PonderTagScreen(AllCreatePonderTags.DISPLAY_TARGETS)));
        this.targetWidget.getToolTip().addAll(List.of(CreateLang.translateDirect("display_link.writing_to", new Object[0]), this.targetState.getBlock().getName().withStyle(s -> s.withColor(this.target == null ? 16157065 : 15909229)), CreateLang.translateDirect("display_link.targeted_location", new Object[0]), CreateLang.translateDirect("display_link.view_compatible", new Object[0]).withStyle(ChatFormatting.GRAY)));
        this.addRenderableWidget((GuiEventListener)this.targetWidget);
        if (!this.sources.isEmpty()) {
            int startIndex = Math.max(this.sources.indexOf(this.blockEntity.activeSource), 0);
            this.sourceTypeLabel = new Label(x + 65, y + 30, CommonComponents.EMPTY).withShadow();
            this.sourceTypeLabel.text = this.sources.get(startIndex).getName();
            if (this.sources.size() > 1) {
                List<Component> options = this.sources.stream().map(DisplaySource::getName).toList();
                this.sourceTypeSelector = new SelectionScrollInput(x + 61, y + 26, 135, 16).forOptions(options).writingTo(this.sourceTypeLabel).titled(CreateLang.translateDirect("display_link.information_type", new Object[0])).calling(this::initGathererSourceSubOptions).setState(startIndex);
                this.sourceTypeSelector.onChanged();
                this.addRenderableWidget((GuiEventListener)this.sourceTypeSelector);
            } else {
                this.initGathererSourceSubOptions(0);
            }
            this.addRenderableWidget((GuiEventListener)this.sourceTypeLabel);
        }
    }

    private void initGathererSourceSubOptions(int i) {
        DisplaySource source = this.sources.get(i);
        source.populateData(new DisplayLinkContext(this.blockEntity.getLevel(), this.blockEntity));
        if (this.targetLineSelector != null) {
            this.targetLineSelector.titled(source instanceof SingleLineDisplaySource ? CreateLang.translateDirect("display_link.display_on", new Object[0]) : CreateLang.translateDirect("display_link.display_on_multiline", new Object[0]));
        }
        this.configWidgets.forEach(s -> {
            s.forEach(this::removeWidget);
            s.clear();
        });
        DisplayLinkContext context = new DisplayLinkContext((Level)this.minecraft.level, this.blockEntity);
        this.configWidgets.forEachWithContext((s, first) -> source.initConfigurationWidgets(context, new ModularGuiLineBuilder(this.font, (ModularGuiLine)s, this.guiLeft + 60, this.guiTop + (first != false ? 51 : 72)), (boolean)first));
        this.configWidgets.forEach(s -> s.loadValues(this.blockEntity.getSourceConfig(), x$0 -> this.addRenderableWidget((GuiEventListener)x$0), x$0 -> {
            GuiEventListener cfr_ignored_0 = (GuiEventListener)this.addRenderableOnly((Renderable)x$0);
        }));
    }

    public void onClose() {
        super.onClose();
        CompoundTag sourceData = new CompoundTag();
        if (!this.sources.isEmpty()) {
            DisplaySource source = this.sources.get(this.sourceTypeSelector == null ? 0 : this.sourceTypeSelector.getState());
            ResourceLocation id = CreateBuiltInRegistries.DISPLAY_SOURCE.getKey((Object)source);
            if (id != null) {
                sourceData.putString("Id", id.toString());
            }
            this.configWidgets.forEach(s -> s.saveValues(sourceData));
        }
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new DisplayLinkConfigurationPacket(this.blockEntity.getBlockPos(), sourceData, this.targetLineSelector == null ? 0 : this.targetLineSelector.getState()));
    }

    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = this.guiLeft;
        int y = this.guiTop;
        this.background.render(graphics, x, y);
        MutableComponent header = CreateLang.translateDirect("display_link.title", new Object[0]);
        graphics.drawString(this.font, (Component)header, x + this.background.getWidth() / 2 - this.font.width((FormattedText)header) / 2, y + 4, 5841956, false);
        if (this.sources.isEmpty()) {
            graphics.drawString(this.font, (Component)CreateLang.translateDirect("display_link.no_source", new Object[0]), x + 65, y + 30, 0xD3D3D3);
        }
        if (this.target == null) {
            graphics.drawString(this.font, (Component)CreateLang.translateDirect("display_link.no_target", new Object[0]), x + 65, y + 109, 0xD3D3D3);
        }
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(0.0f, (float)(this.guiTop + 46), 0.0f);
        ((ModularGuiLine)this.configWidgets.getFirst()).renderWidgetBG(this.guiLeft, graphics);
        ms.translate(0.0f, 21.0f, 0.0f);
        ((ModularGuiLine)this.configWidgets.getSecond()).renderWidgetBG(this.guiLeft, graphics);
        ms.popPose();
        ms.pushPose();
        ((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)ms).pushPose().translate((float)(x + this.background.getWidth() + 4), (float)(y + this.background.getHeight() + 4), 100.0f).scale(40.0f)).rotateXDegrees(-22.0f)).rotateYDegrees(63.0f);
        GuiGameElement.of((BlockState)((BlockState)this.blockEntity.getBlockState().setValue((Property)DisplayLinkBlock.FACING, (Comparable)Direction.UP))).render(graphics);
        ms.popPose();
    }

    protected void removeWidget(GuiEventListener p_169412_) {
        if (p_169412_ != null) {
            super.removeWidget(p_169412_);
        }
    }
}
