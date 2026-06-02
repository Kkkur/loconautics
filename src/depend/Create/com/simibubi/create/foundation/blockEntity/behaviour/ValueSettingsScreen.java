/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.gui.AbstractSimiScreen
 *  net.createmod.catnip.gui.TextureSheetSegment
 *  net.createmod.catnip.gui.UIRenderHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec2
 *  org.jetbrains.annotations.NotNull
 *  org.lwjgl.glfw.GLFW
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllKeys;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsPacket;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueHandler;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.function.Consumer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class ValueSettingsScreen
extends AbstractSimiScreen {
    private int ticksOpen;
    private ValueSettingsBoard board;
    private int maxLabelWidth;
    private int valueBarWidth;
    private BlockPos pos;
    private ValueSettingsBehaviour.ValueSettings initialSettings;
    private ValueSettingsBehaviour.ValueSettings lastHovered = new ValueSettingsBehaviour.ValueSettings(-1, -1);
    private Consumer<ValueSettingsBehaviour.ValueSettings> onHover;
    private boolean iconMode;
    private int milestoneSize;
    private int soundCoolDown;
    private int netId;

    public ValueSettingsScreen(BlockPos pos, ValueSettingsBoard board, ValueSettingsBehaviour.ValueSettings valueSettings, Consumer<ValueSettingsBehaviour.ValueSettings> onHover, int netId) {
        this.pos = pos;
        this.board = board;
        this.initialSettings = valueSettings;
        this.onHover = onHover;
        this.netId = netId;
        this.iconMode = board.formatter() instanceof ValueSettingsFormatter.ScrollOptionSettingsFormatter;
        this.milestoneSize = this.iconMode ? 8 : 4;
    }

    protected void init() {
        int maxValue = this.board.maxValue();
        this.maxLabelWidth = 0;
        int milestoneCount = maxValue / this.board.milestoneInterval() + 1;
        int scale = maxValue > 128 ? 1 : 2;
        for (Component component : this.board.rows()) {
            this.maxLabelWidth = Math.max(this.maxLabelWidth, this.font.width((FormattedText)component));
        }
        if (this.iconMode) {
            this.maxLabelWidth = -18;
        }
        this.valueBarWidth = (maxValue + 1) * scale + 1 + milestoneCount * this.milestoneSize;
        int width = this.maxLabelWidth + 14 + (this.valueBarWidth + 10);
        int height = this.board.rows().size() * 11;
        this.setWindowSize(width, height);
        super.init();
        Vec2 coordinateOfValue = this.getCoordinateOfValue(this.initialSettings.row(), this.initialSettings.value());
        this.setCursor(coordinateOfValue);
    }

    private void setCursor(Vec2 coordinateOfValue) {
        double guiScale = this.minecraft.getWindow().getGuiScale();
        GLFW.glfwSetCursorPos((long)this.minecraft.getWindow().getWindow(), (double)((double)coordinateOfValue.x * guiScale), (double)((double)coordinateOfValue.y * guiScale));
    }

    public ValueSettingsBehaviour.ValueSettings getClosestCoordinate(int mouseX, int mouseY) {
        double diff;
        Vec2 coord;
        int row;
        int column = 0;
        boolean milestonesOnly = ValueSettingsScreen.hasShiftDown();
        double bestDiff = Double.MAX_VALUE;
        for (row = 0; row < this.board.rows().size(); ++row) {
            coord = this.getCoordinateOfValue(row, 0);
            diff = Math.abs(coord.y - (float)mouseY);
            if (bestDiff < diff) break;
            bestDiff = diff;
        }
        --row;
        bestDiff = Double.MAX_VALUE;
        while (column <= this.board.maxValue()) {
            coord = this.getCoordinateOfValue(row, milestonesOnly ? column * this.board.milestoneInterval() : column);
            diff = Math.abs(coord.x - (float)mouseX);
            if (bestDiff < diff) break;
            bestDiff = diff;
            ++column;
        }
        return new ValueSettingsBehaviour.ValueSettings(row, milestonesOnly ? Math.min(column * this.board.milestoneInterval(), this.board.maxValue()) : --column);
    }

    public Vec2 getCoordinateOfValue(int row, int column) {
        int scale = this.board.maxValue() > 128 ? 1 : 2;
        float xOut = (float)(this.guiLeft + (Math.max(1, column) - 1) / this.board.milestoneInterval() * this.milestoneSize + column * scale) + 1.5f;
        xOut += (float)(this.maxLabelWidth + 14 + 4);
        if (column % this.board.milestoneInterval() == 0) {
            xOut += (float)(this.milestoneSize / 2);
        }
        if (column > 0) {
            xOut += (float)this.milestoneSize;
        }
        float yOut = (float)this.guiTop + ((float)row + 0.5f) * 11.0f - 0.5f;
        return new Vec2(xOut, yOut);
    }

    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = this.guiLeft;
        int y = this.guiTop;
        int milestoneCount = this.board.maxValue() / this.board.milestoneInterval() + 1;
        int scale = this.board.maxValue() > 128 ? 1 : 2;
        Component title = this.board.title();
        MutableComponent tip = CreateLang.translateDirect("gui.value_settings.release_to_confirm", Component.keybind((String)"key.use"));
        double fadeIn = Math.pow(Mth.clamp((double)((double)((float)this.ticksOpen + partialTicks) / 4.0), (double)0.0, (double)1.0), 1.0);
        int fattestLabel = Math.max(this.font.width((FormattedText)tip), this.font.width((FormattedText)title));
        if (this.iconMode) {
            for (int i = 0; i <= this.board.maxValue(); ++i) {
                fattestLabel = Math.max(fattestLabel, this.font.width((FormattedText)this.board.formatter().format(new ValueSettingsBehaviour.ValueSettings(0, i))));
            }
        }
        int fatTipOffset = Math.max(0, fattestLabel + 10 - (this.windowWidth + 13)) / 2;
        int bgWidth = Math.max(this.windowWidth + 13, fattestLabel + 10);
        int fadeInWidth = (int)((double)bgWidth * fadeIn);
        int fadeInStart = (bgWidth - fadeInWidth) / 2 - fatTipOffset;
        int additionalHeight = this.iconMode ? 46 : 33;
        int zLevel = 0;
        UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)(x - 11 + fadeInStart), (int)(y - 17), (int)fadeInWidth, (int)(this.windowHeight + additionalHeight), (int)zLevel, (TextureSheetSegment)AllGuiTextures.VALUE_SETTINGS_OUTER_BG);
        UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)(x - 10 + fadeInStart), (int)(y - 18), (int)(fadeInWidth - 2), (int)1, (int)zLevel, (TextureSheetSegment)AllGuiTextures.VALUE_SETTINGS_OUTER_BG);
        UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)(x - 10 + fadeInStart), (int)(y - 17 + this.windowHeight + additionalHeight), (int)zLevel, (int)(fadeInWidth - 2), (int)1, (TextureSheetSegment)AllGuiTextures.VALUE_SETTINGS_OUTER_BG);
        if (fadeInWidth > fattestLabel) {
            int textX = x - 11 - fatTipOffset + bgWidth / 2;
            graphics.drawString(this.font, title, textX - this.font.width((FormattedText)title) / 2, y - 14, 0xDDDDDD, false);
            graphics.drawString(this.font, (Component)tip, textX - this.font.width((FormattedText)tip) / 2, y + this.windowHeight + additionalHeight - 27, 0xDDDDDD, false);
        }
        this.renderBrassFrame(graphics, x + this.maxLabelWidth + 14, y - 3, this.valueBarWidth + 8, this.board.rows().size() * 11 + 5);
        UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)(x + this.maxLabelWidth + 17), (int)y, (int)(this.valueBarWidth + 2), (int)(this.board.rows().size() * 11 - 1), (int)zLevel, (TextureSheetSegment)AllGuiTextures.VALUE_SETTINGS_BAR_BG);
        int originalY = y;
        for (Component component : this.board.rows()) {
            int valueBarX = x + this.maxLabelWidth + 14 + 4;
            if (!this.iconMode) {
                UIRenderHelper.drawCropped((GuiGraphics)graphics, (int)(x - 4), (int)y, (int)(this.maxLabelWidth + 8), (int)11, (int)zLevel, (TextureSheetSegment)AllGuiTextures.VALUE_SETTINGS_LABEL_BG);
                for (int w = 0; w < this.valueBarWidth; w += AllGuiTextures.VALUE_SETTINGS_BAR.getWidth() - 1) {
                    UIRenderHelper.drawCropped((GuiGraphics)graphics, (int)(valueBarX + w), (int)(y + 1), (int)Math.min(AllGuiTextures.VALUE_SETTINGS_BAR.getWidth() - 1, this.valueBarWidth - w), (int)8, (int)zLevel, (TextureSheetSegment)AllGuiTextures.VALUE_SETTINGS_BAR);
                }
                graphics.drawString(this.font, component, x, y + 1, 0x442000, false);
            }
            int milestoneX = valueBarX;
            for (int milestone = 0; milestone < milestoneCount; ++milestone) {
                if (this.iconMode) {
                    AllGuiTextures.VALUE_SETTINGS_WIDE_MILESTONE.render(graphics, milestoneX, y + 1);
                } else {
                    AllGuiTextures.VALUE_SETTINGS_MILESTONE.render(graphics, milestoneX, y + 1);
                }
                milestoneX += this.milestoneSize + this.board.milestoneInterval() * scale;
            }
            y += 11;
        }
        if (!this.iconMode) {
            this.renderBrassFrame(graphics, x - 7, originalY - 3, this.maxLabelWidth + 14, this.board.rows().size() * 11 + 5);
        }
        if (this.ticksOpen < 1) {
            return;
        }
        ValueSettingsBehaviour.ValueSettings closest = this.getClosestCoordinate(mouseX, mouseY);
        if (!closest.equals(this.lastHovered)) {
            this.onHover.accept(closest);
            if (this.soundCoolDown == 0) {
                float pitch = (float)closest.value() / (float)this.board.maxValue();
                pitch = Mth.lerp((float)pitch, (float)1.15f, (float)1.5f);
                this.minecraft.getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)AllSoundEvents.SCROLL_VALUE.getMainEvent(), (float)pitch, (float)0.25f));
                ScrollValueHandler.wrenchCog.bump(3, (double)(-(closest.value() - this.lastHovered.value()) * 10));
                this.soundCoolDown = 1;
            }
        }
        this.lastHovered = closest;
        Vec2 coordinate = this.getCoordinateOfValue(closest.row(), closest.value());
        MutableComponent cursorText = this.board.formatter().format(closest);
        AllIcons cursorIcon = null;
        ValueSettingsFormatter valueSettingsFormatter = this.board.formatter();
        if (valueSettingsFormatter instanceof ValueSettingsFormatter.ScrollOptionSettingsFormatter) {
            ValueSettingsFormatter.ScrollOptionSettingsFormatter sosf = (ValueSettingsFormatter.ScrollOptionSettingsFormatter)valueSettingsFormatter;
            cursorIcon = sosf.getIcon(closest);
        }
        int cursorWidth = (cursorIcon != null ? 16 : this.font.width((FormattedText)cursorText)) / 2 * 2 + 3;
        int cursorX = (int)coordinate.x - cursorWidth / 2;
        int cursorY = (int)coordinate.y - 7;
        if (cursorIcon != null) {
            AllGuiTextures.VALUE_SETTINGS_CURSOR_ICON.render(graphics, cursorX - 2, cursorY - 3);
            RenderSystem.setShaderColor((float)0.265625f, (float)0.125f, (float)0.0f, (float)1.0f);
            cursorIcon.render(graphics, cursorX + 1, cursorY - 1);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            if (fadeInWidth > fattestLabel) {
                graphics.drawString(this.font, (Component)cursorText, x - 11 - fatTipOffset + (bgWidth - this.font.width((FormattedText)cursorText)) / 2, originalY + this.windowHeight + additionalHeight - 40, 16505981, false);
            }
            return;
        }
        AllGuiTextures.VALUE_SETTINGS_CURSOR_LEFT.render(graphics, cursorX - 3, cursorY);
        UIRenderHelper.drawCropped((GuiGraphics)graphics, (int)cursorX, (int)cursorY, (int)cursorWidth, (int)14, (int)zLevel, (TextureSheetSegment)AllGuiTextures.VALUE_SETTINGS_CURSOR);
        AllGuiTextures.VALUE_SETTINGS_CURSOR_RIGHT.render(graphics, cursorX + cursorWidth, cursorY);
        graphics.drawString(this.font, (Component)cursorText, cursorX + 2, cursorY + 3, 0x442000, false);
    }

    protected void renderBrassFrame(GuiGraphics graphics, int x, int y, int w, int h) {
        AllGuiTextures.BRASS_FRAME_TL.render(graphics, x, y);
        AllGuiTextures.BRASS_FRAME_TR.render(graphics, x + w - 4, y);
        AllGuiTextures.BRASS_FRAME_BL.render(graphics, x, y + h - 4);
        AllGuiTextures.BRASS_FRAME_BR.render(graphics, x + w - 4, y + h - 4);
        int zLevel = 0;
        if (h > 8) {
            UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)x, (int)(y + 4), (int)3, (int)(h - 8), (int)zLevel, (TextureSheetSegment)AllGuiTextures.BRASS_FRAME_LEFT);
            UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)(x + w - 3), (int)(y + 4), (int)3, (int)(h - 8), (int)zLevel, (TextureSheetSegment)AllGuiTextures.BRASS_FRAME_RIGHT);
        }
        if (w > 8) {
            UIRenderHelper.drawCropped((GuiGraphics)graphics, (int)(x + 4), (int)y, (int)(w - 8), (int)3, (int)zLevel, (TextureSheetSegment)AllGuiTextures.BRASS_FRAME_TOP);
            UIRenderHelper.drawCropped((GuiGraphics)graphics, (int)(x + 4), (int)(y + h - 3), (int)(w - 8), (int)3, (int)zLevel, (TextureSheetSegment)AllGuiTextures.BRASS_FRAME_BOTTOM);
        }
    }

    public void renderBackground(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        int a = (int)(80.0f * Math.min(1.0f, ((float)this.ticksOpen + AnimationTickHolder.getPartialTicks()) / 20.0f)) << 24;
        graphics.fillGradient(0, 0, this.width, this.height, 0x101010 | a, 0x101010 | a);
    }

    public void tick() {
        ++this.ticksOpen;
        if (this.soundCoolDown > 0) {
            --this.soundCoolDown;
        }
        super.tick();
    }

    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        ValueSettingsBehaviour.ValueSettings closest = this.getClosestCoordinate((int)pMouseX, (int)pMouseY);
        int column = closest.value() + (int)Math.signum(pScrollY) * (ValueSettingsScreen.hasShiftDown() ? this.board.milestoneInterval() : 1);
        if ((column = Mth.clamp((int)column, (int)0, (int)this.board.maxValue())) == closest.value()) {
            return false;
        }
        this.setCursor(this.getCoordinateOfValue(closest.row(), column));
        return true;
    }

    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.minecraft.options.keyUse.matches(pKeyCode, pScanCode)) {
            Window window = this.minecraft.getWindow();
            double x = this.minecraft.mouseHandler.xpos() * (double)window.getGuiScaledWidth() / (double)window.getScreenWidth();
            double y = this.minecraft.mouseHandler.ypos() * (double)window.getGuiScaledHeight() / (double)window.getScreenHeight();
            this.saveAndClose(x, y);
            return true;
        }
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (this.minecraft.options.keyUse.matchesMouse(pButton)) {
            this.saveAndClose(pMouseX, pMouseY);
            return true;
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    protected void saveAndClose(double pMouseX, double pMouseY) {
        ValueSettingsBehaviour.ValueSettings closest = this.getClosestCoordinate((int)pMouseX, (int)pMouseY);
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ValueSettingsPacket(this.pos, closest.row(), closest.value(), null, null, Direction.UP, AllKeys.ctrlDown(), this.netId));
        this.onClose();
    }

    public void onClose() {
        super.onClose();
    }
}
