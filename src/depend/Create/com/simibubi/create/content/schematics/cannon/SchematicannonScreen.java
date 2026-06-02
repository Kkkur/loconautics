/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.GuiGameElement$GuiRenderBuilder
 *  net.createmod.catnip.gui.element.ScreenElement
 *  net.createmod.catnip.gui.widget.AbstractSimiWidget
 *  net.createmod.catnip.lang.FontHelper$Palette
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.schematics.cannon;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.crate.CreativeCrateBlock;
import com.simibubi.create.content.schematics.cannon.ConfigureSchematicannonPacket;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import com.simibubi.create.content.schematics.cannon.SchematicannonMenu;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class SchematicannonScreen
extends AbstractSimiContainerScreen<SchematicannonMenu> {
    private static final AllGuiTextures BG_BOTTOM = AllGuiTextures.SCHEMATICANNON_BOTTOM;
    private static final AllGuiTextures BG_TOP = AllGuiTextures.SCHEMATICANNON_TOP;
    private final Component listPrinter = CreateLang.translateDirect("gui.schematicannon.listPrinter", new Object[0]);
    private final String _gunpowderLevel = "gui.schematicannon.gunpowderLevel";
    private final String _shotsRemaining = "gui.schematicannon.shotsRemaining";
    private final String _showSettings = "gui.schematicannon.showOptions";
    private final String _shotsRemainingWithBackup = "gui.schematicannon.shotsRemainingWithBackup";
    private final String _slotGunpowder = "gui.schematicannon.slot.gunpowder";
    private final String _slotListPrinter = "gui.schematicannon.slot.listPrinter";
    private final String _slotSchematic = "gui.schematicannon.slot.schematic";
    private final Component optionEnabled = CreateLang.translateDirect("gui.schematicannon.optionEnabled", new Object[0]);
    private final Component optionDisabled = CreateLang.translateDirect("gui.schematicannon.optionDisabled", new Object[0]);
    protected List<Indicator> replaceLevelIndicators;
    protected List<IconButton> replaceLevelButtons;
    protected IconButton skipMissingButton;
    protected Indicator skipMissingIndicator;
    protected IconButton skipBlockEntitiesButton;
    protected Indicator skipBlockEntitiesIndicator;
    protected IconButton playButton;
    protected Indicator playIndicator;
    protected IconButton pauseButton;
    protected Indicator pauseIndicator;
    protected IconButton resetButton;
    protected Indicator resetIndicator;
    private IconButton confirmButton;
    private IconButton showSettingsButton;
    private Indicator showSettingsIndicator;
    protected List<AbstractWidget> placementSettingWidgets;
    private final ItemStack renderedItem = AllBlocks.SCHEMATICANNON.asStack();
    private List<Rect2i> extraAreas = Collections.emptyList();

    public SchematicannonScreen(SchematicannonMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.placementSettingWidgets = new ArrayList<AbstractWidget>();
    }

    @Override
    protected void init() {
        this.setWindowSize(BG_TOP.getWidth(), BG_TOP.getHeight() + BG_BOTTOM.getHeight() + 2 + AllGuiTextures.PLAYER_INVENTORY.getHeight());
        this.setWindowOffset(-11, 0);
        super.init();
        int x = this.leftPos;
        int y = this.topPos;
        this.playButton = new IconButton(x + 75, y + 85, AllIcons.I_PLAY);
        this.playButton.withCallback(() -> this.sendOptionUpdate(ConfigureSchematicannonPacket.Option.PLAY, true));
        this.playIndicator = new Indicator(x + 75, y + 79, CommonComponents.EMPTY);
        this.pauseButton = new IconButton(x + 93, y + 85, AllIcons.I_PAUSE);
        this.pauseButton.withCallback(() -> this.sendOptionUpdate(ConfigureSchematicannonPacket.Option.PAUSE, true));
        this.pauseIndicator = new Indicator(x + 93, y + 79, CommonComponents.EMPTY);
        this.resetButton = new IconButton(x + 111, y + 85, AllIcons.I_STOP);
        this.resetButton.withCallback(() -> this.sendOptionUpdate(ConfigureSchematicannonPacket.Option.STOP, true));
        this.resetIndicator = new Indicator(x + 111, y + 79, CommonComponents.EMPTY);
        this.resetIndicator.state = Indicator.State.RED;
        this.addRenderableWidgets((GuiEventListener[])new AbstractSimiWidget[]{this.playButton, this.playIndicator, this.pauseButton, this.pauseIndicator, this.resetButton, this.resetIndicator});
        this.confirmButton = new IconButton(x + 180, y + 111, AllIcons.I_CONFIRM);
        this.confirmButton.withCallback(() -> this.minecraft.player.closeContainer());
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
        this.showSettingsButton = new IconButton(x + 8, y + 111, AllIcons.I_PLACEMENT_SETTINGS);
        this.showSettingsButton.withCallback(() -> {
            this.showSettingsIndicator.state = this.placementSettingsHidden() ? Indicator.State.GREEN : Indicator.State.OFF;
            this.initPlacementSettings();
        });
        this.showSettingsButton.setToolTip((Component)CreateLang.translateDirect("gui.schematicannon.showOptions", new Object[0]));
        this.addRenderableWidget((GuiEventListener)this.showSettingsButton);
        this.showSettingsIndicator = new Indicator(x + 9, y + 111, CommonComponents.EMPTY);
        this.extraAreas = ImmutableList.of((Object)new Rect2i(x + BG_TOP.getWidth(), y + BG_TOP.getHeight() + BG_BOTTOM.getHeight() - 62, 84, 92));
        this.tick();
    }

    private void initPlacementSettings() {
        this.removeWidgets(this.placementSettingWidgets);
        this.placementSettingWidgets.clear();
        if (this.placementSettingsHidden()) {
            return;
        }
        int x = this.leftPos;
        int y = this.topPos;
        this.replaceLevelButtons = new ArrayList<IconButton>(4);
        this.replaceLevelIndicators = new ArrayList<Indicator>(4);
        ImmutableList icons = ImmutableList.of((Object)AllIcons.I_DONT_REPLACE, (Object)AllIcons.I_REPLACE_SOLID, (Object)AllIcons.I_REPLACE_ANY, (Object)AllIcons.I_REPLACE_EMPTY);
        ImmutableList toolTips = ImmutableList.of((Object)CreateLang.translateDirect("gui.schematicannon.option.dontReplaceSolid", new Object[0]), (Object)CreateLang.translateDirect("gui.schematicannon.option.replaceWithSolid", new Object[0]), (Object)CreateLang.translateDirect("gui.schematicannon.option.replaceWithAny", new Object[0]), (Object)CreateLang.translateDirect("gui.schematicannon.option.replaceWithEmpty", new Object[0]));
        for (int i = 0; i < 4; ++i) {
            this.replaceLevelIndicators.add(new Indicator(x + 33 + i * 18, y + 111, CommonComponents.EMPTY));
            IconButton replaceLevelButton = new IconButton(x + 33 + i * 18, y + 111, (ScreenElement)icons.get(i));
            int replaceMode = i;
            replaceLevelButton.withCallback(() -> {
                if (((SchematicannonBlockEntity)((SchematicannonMenu)this.menu).contentHolder).replaceMode != replaceMode) {
                    this.sendOptionUpdate(ConfigureSchematicannonPacket.Option.values()[replaceMode], true);
                }
            });
            replaceLevelButton.setToolTip((Component)toolTips.get(i));
            this.replaceLevelButtons.add(replaceLevelButton);
        }
        this.placementSettingWidgets.addAll(this.replaceLevelButtons);
        this.skipMissingButton = new IconButton(x + 111, y + 111, AllIcons.I_SKIP_MISSING);
        this.skipMissingButton.withCallback(() -> this.sendOptionUpdate(ConfigureSchematicannonPacket.Option.SKIP_MISSING, !((SchematicannonBlockEntity)((SchematicannonMenu)this.menu).contentHolder).skipMissing));
        this.skipMissingButton.setToolTip((Component)CreateLang.translateDirect("gui.schematicannon.option.skipMissing", new Object[0]));
        this.skipMissingIndicator = new Indicator(x + 111, y + 111, CommonComponents.EMPTY);
        Collections.addAll(this.placementSettingWidgets, this.skipMissingButton);
        this.skipBlockEntitiesButton = new IconButton(x + 135, y + 111, AllIcons.I_SKIP_BLOCK_ENTITIES);
        this.skipBlockEntitiesButton.withCallback(() -> this.sendOptionUpdate(ConfigureSchematicannonPacket.Option.SKIP_BLOCK_ENTITIES, !((SchematicannonBlockEntity)((SchematicannonMenu)this.menu).contentHolder).replaceBlockEntities));
        this.skipBlockEntitiesButton.setToolTip((Component)CreateLang.translateDirect("gui.schematicannon.option.skipBlockEntities", new Object[0]));
        this.skipBlockEntitiesIndicator = new Indicator(x + 129, y + 111, CommonComponents.EMPTY);
        Collections.addAll(this.placementSettingWidgets, this.skipBlockEntitiesButton);
        this.addRenderableWidgets(this.placementSettingWidgets);
    }

    protected boolean placementSettingsHidden() {
        return this.showSettingsIndicator.state == Indicator.State.OFF;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        SchematicannonBlockEntity be = (SchematicannonBlockEntity)((SchematicannonMenu)this.menu).contentHolder;
        if (!this.placementSettingsHidden()) {
            for (int replaceMode = 0; replaceMode < this.replaceLevelButtons.size(); ++replaceMode) {
                this.replaceLevelButtons.get((int)replaceMode).green = replaceMode == be.replaceMode;
                this.replaceLevelIndicators.get((int)replaceMode).state = replaceMode == be.replaceMode ? Indicator.State.ON : Indicator.State.OFF;
            }
            this.skipMissingButton.green = be.skipMissing;
            this.skipBlockEntitiesButton.green = !be.replaceBlockEntities;
        }
        this.playIndicator.state = Indicator.State.OFF;
        this.pauseIndicator.state = Indicator.State.OFF;
        this.resetIndicator.state = Indicator.State.OFF;
        switch (be.state) {
            case PAUSED: {
                this.pauseIndicator.state = Indicator.State.YELLOW;
                this.playButton.active = true;
                this.pauseButton.active = false;
                this.resetButton.active = true;
                break;
            }
            case RUNNING: {
                this.playIndicator.state = Indicator.State.GREEN;
                this.playButton.active = false;
                this.pauseButton.active = true;
                this.resetButton.active = true;
                break;
            }
            case STOPPED: {
                this.resetIndicator.state = Indicator.State.RED;
                this.playButton.active = true;
                this.pauseButton.active = false;
                this.resetButton.active = false;
                break;
            }
        }
        this.handleTooltips();
    }

    protected void handleTooltips() {
        if (this.placementSettingsHidden()) {
            return;
        }
        for (AbstractWidget w : this.placementSettingWidgets) {
            IconButton button;
            if (!(w instanceof IconButton) || (button = (IconButton)w).getToolTip().isEmpty()) continue;
            button.setToolTip((Component)button.getToolTip().get(0));
            button.getToolTip().add(TooltipHelper.holdShift(FontHelper.Palette.BLUE, SchematicannonScreen.hasShiftDown()));
        }
        if (SchematicannonScreen.hasShiftDown()) {
            this.fillToolTip(this.skipMissingButton, this.skipMissingIndicator, "skipMissing");
            this.fillToolTip(this.skipBlockEntitiesButton, this.skipBlockEntitiesIndicator, "skipBlockEntities");
            this.fillToolTip(this.replaceLevelButtons.get(0), this.replaceLevelIndicators.get(0), "dontReplaceSolid");
            this.fillToolTip(this.replaceLevelButtons.get(1), this.replaceLevelIndicators.get(1), "replaceWithSolid");
            this.fillToolTip(this.replaceLevelButtons.get(2), this.replaceLevelIndicators.get(2), "replaceWithAny");
            this.fillToolTip(this.replaceLevelButtons.get(3), this.replaceLevelIndicators.get(3), "replaceWithEmpty");
        }
    }

    private void fillToolTip(IconButton button, Indicator indicator, String tooltipKey) {
        if (!button.isHovered()) {
            return;
        }
        boolean enabled = button.green;
        List tip = button.getToolTip();
        tip.add((enabled ? this.optionEnabled : this.optionDisabled).plainCopy().withStyle(enabled ? ChatFormatting.DARK_GREEN : ChatFormatting.RED));
        tip.addAll(TooltipHelper.cutTextComponent((Component)CreateLang.translateDirect("gui.schematicannon.option." + tooltipKey + ".description", new Object[0]), FontHelper.Palette.ALL_GRAY));
    }

    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int invX = this.getLeftOfCentered(AllGuiTextures.PLAYER_INVENTORY.getWidth());
        int invY = this.topPos + BG_TOP.getHeight() + BG_BOTTOM.getHeight() + 2;
        this.renderPlayerInventory(graphics, invX, invY);
        int x = this.leftPos;
        int y = this.topPos;
        BG_TOP.render(graphics, x, y);
        BG_BOTTOM.render(graphics, x, y + BG_TOP.getHeight());
        AllGuiTextures.SCHEMATIC_TITLE.render(graphics, x, y - 2);
        SchematicannonBlockEntity be = (SchematicannonBlockEntity)((SchematicannonMenu)this.menu).contentHolder;
        this.renderPrintingProgress(graphics, x, y, be.schematicProgress);
        float amount = (float)be.remainingFuel / (float)be.getShotsPerGunpowder();
        this.renderFuelBar(graphics, x, y, amount);
        this.renderChecklistPrinterProgress(graphics, x, y, be.bookPrintingProgress);
        if (!be.inventory.getStackInSlot(0).isEmpty()) {
            this.renderBlueprintHighlight(graphics, x, y);
        }
        ((GuiGameElement.GuiRenderBuilder)GuiGameElement.of((ItemStack)this.renderedItem).at((float)(x + BG_TOP.getWidth()), (float)(y + BG_TOP.getHeight() + BG_BOTTOM.getHeight() - 48), -200.0f)).scale(5.0).render(graphics);
        graphics.drawString(this.font, this.title, x + (BG_TOP.getWidth() - 8 - this.font.width((FormattedText)this.title)) / 2, y + 2, 0x505050, false);
        MutableComponent msg = CreateLang.translateDirect("schematicannon.status." + be.statusMsg, new Object[0]);
        int stringWidth = this.font.width((FormattedText)msg);
        if (be.missingItem != null) {
            stringWidth += 16;
            ((GuiGameElement.GuiRenderBuilder)GuiGameElement.of((ItemStack)be.missingItem).at((float)(x + 128), (float)(y + 49), 100.0f)).scale(1.0).render(graphics);
        }
        graphics.drawString(this.font, (Component)msg, x + 103 - stringWidth / 2, y + 53, 0xDDEEFF);
        if ("schematicErrored".equals(be.statusMsg)) {
            graphics.drawString(this.font, (Component)CreateLang.translateDirect("schematicannon.status.schematicErroredCheckLogs", new Object[0]), x + 103 - stringWidth / 2, y + 65, 0xDDEEFF);
        }
    }

    protected void renderBlueprintHighlight(GuiGraphics graphics, int x, int y) {
        AllGuiTextures.SCHEMATICANNON_HIGHLIGHT.render(graphics, x + 10, y + 60);
    }

    protected void renderPrintingProgress(GuiGraphics graphics, int x, int y, float progress) {
        progress = Math.min(progress, 1.0f);
        AllGuiTextures sprite = AllGuiTextures.SCHEMATICANNON_PROGRESS;
        graphics.blit(sprite.location, x + 44, y + 64, sprite.getStartX(), sprite.getStartY(), (int)((float)sprite.getWidth() * progress), sprite.getHeight());
    }

    protected void renderChecklistPrinterProgress(GuiGraphics graphics, int x, int y, float progress) {
        AllGuiTextures sprite = AllGuiTextures.SCHEMATICANNON_CHECKLIST_PROGRESS;
        graphics.blit(sprite.location, x + 154, y + 20, sprite.getStartX(), sprite.getStartY(), (int)((float)sprite.getWidth() * progress), sprite.getHeight());
    }

    protected void renderFuelBar(GuiGraphics graphics, int x, int y, float amount) {
        AllGuiTextures sprite = AllGuiTextures.SCHEMATICANNON_FUEL;
        if (((SchematicannonBlockEntity)((SchematicannonMenu)this.menu).contentHolder).hasCreativeCrate) {
            AllGuiTextures.SCHEMATICANNON_FUEL_CREATIVE.render(graphics, x + 36, y + 19);
            return;
        }
        graphics.blit(sprite.location, x + 36, y + 19, sprite.getStartX(), sprite.getStartY(), (int)((float)sprite.getWidth() * amount), sprite.getHeight());
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        SchematicannonBlockEntity be = (SchematicannonBlockEntity)((SchematicannonMenu)this.menu).contentHolder;
        int x = this.leftPos;
        int y = this.topPos;
        int fuelX = x + 36;
        int fuelY = y + 19;
        if (mouseX >= fuelX && mouseY >= fuelY && mouseX <= fuelX + AllGuiTextures.SCHEMATICANNON_FUEL.getWidth() && mouseY <= fuelY + AllGuiTextures.SCHEMATICANNON_FUEL.getHeight()) {
            List<Component> tooltip = this.getFuelLevelTooltip(be);
            graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }
        if (this.hoveredSlot != null && !this.hoveredSlot.hasItem()) {
            if (this.hoveredSlot.index == 0) {
                graphics.renderComponentTooltip(this.font, TooltipHelper.cutTextComponent((Component)CreateLang.translateDirect("gui.schematicannon.slot.schematic", new Object[0]), FontHelper.Palette.GRAY_AND_BLUE), mouseX, mouseY);
            }
            if (this.hoveredSlot.index == 2) {
                graphics.renderComponentTooltip(this.font, TooltipHelper.cutTextComponent((Component)CreateLang.translateDirect("gui.schematicannon.slot.listPrinter", new Object[0]), FontHelper.Palette.GRAY_AND_BLUE), mouseX, mouseY);
            }
            if (this.hoveredSlot.index == 4) {
                graphics.renderComponentTooltip(this.font, TooltipHelper.cutTextComponent((Component)CreateLang.translateDirect("gui.schematicannon.slot.gunpowder", new Object[0]), FontHelper.Palette.GRAY_AND_BLUE), mouseX, mouseY);
            }
        }
        if (be.missingItem != null) {
            int missingBlockX = x + 128;
            int missingBlockY = y + 49;
            if (mouseX >= missingBlockX && mouseY >= missingBlockY && mouseX <= missingBlockX + 16 && mouseY <= missingBlockY + 16) {
                graphics.renderTooltip(this.font, be.missingItem, mouseX, mouseY);
            }
        }
        int paperX = x + 112;
        int paperY = y + 19;
        if (mouseX >= paperX && mouseY >= paperY && mouseX <= paperX + 16 && mouseY <= paperY + 16) {
            graphics.renderTooltip(this.font, this.listPrinter, mouseX, mouseY);
        }
        super.renderForeground(graphics, mouseX, mouseY, partialTicks);
    }

    protected List<Component> getFuelLevelTooltip(SchematicannonBlockEntity be) {
        int shotsLeft = be.remainingFuel;
        int shotsLeftWithItems = shotsLeft + be.inventory.getStackInSlot(4).getCount() * be.getShotsPerGunpowder();
        ArrayList<Component> tooltip = new ArrayList<Component>();
        if (be.hasCreativeCrate) {
            tooltip.add((Component)CreateLang.translateDirect("gui.schematicannon.gunpowderLevel", "100"));
            tooltip.add((Component)Component.literal((String)"(").append((Component)((CreativeCrateBlock)AllBlocks.CREATIVE_CRATE.get()).getName()).append(")").withStyle(ChatFormatting.DARK_PURPLE));
            return tooltip;
        }
        int fillPercent = (int)((float)be.remainingFuel / (float)be.getShotsPerGunpowder() * 100.0f);
        tooltip.add((Component)CreateLang.translateDirect("gui.schematicannon.gunpowderLevel", fillPercent));
        tooltip.add((Component)CreateLang.translateDirect("gui.schematicannon.shotsRemaining", Component.literal((String)Integer.toString(shotsLeft)).withStyle(ChatFormatting.BLUE)).withStyle(ChatFormatting.GRAY));
        if (shotsLeftWithItems != shotsLeft) {
            tooltip.add((Component)CreateLang.translateDirect("gui.schematicannon.shotsRemainingWithBackup", Component.literal((String)Integer.toString(shotsLeftWithItems)).withStyle(ChatFormatting.BLUE)).withStyle(ChatFormatting.GRAY));
        }
        return tooltip;
    }

    protected void sendOptionUpdate(ConfigureSchematicannonPacket.Option option, boolean set) {
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ConfigureSchematicannonPacket(option, set));
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return this.extraAreas;
    }
}
