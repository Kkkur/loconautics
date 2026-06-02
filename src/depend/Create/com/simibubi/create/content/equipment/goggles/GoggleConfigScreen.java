/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.gui.AbstractSimiScreen
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.equipment.goggles;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class GoggleConfigScreen
extends AbstractSimiScreen {
    private int offsetX;
    private int offsetY;
    private final List<Component> tooltip;

    public GoggleConfigScreen() {
        MutableComponent componentSpacing = Component.literal((String)"    ");
        this.tooltip = new ArrayList<Component>();
        this.tooltip.add((Component)componentSpacing.plainCopy().append((Component)CreateLang.translateDirect("gui.config.overlay1", new Object[0])));
        this.tooltip.add((Component)componentSpacing.plainCopy().append((Component)CreateLang.translateDirect("gui.config.overlay2", new Object[0]).withStyle(ChatFormatting.GRAY)));
        this.tooltip.add(CommonComponents.EMPTY);
        this.tooltip.add((Component)componentSpacing.plainCopy().append((Component)CreateLang.translateDirect("gui.config.overlay3", new Object[0])));
        this.tooltip.add((Component)componentSpacing.plainCopy().append((Component)CreateLang.translateDirect("gui.config.overlay4", new Object[0])));
        this.tooltip.add(CommonComponents.EMPTY);
        this.tooltip.add((Component)componentSpacing.plainCopy().append((Component)CreateLang.translateDirect("gui.config.overlay5", new Object[0]).withStyle(ChatFormatting.GRAY)));
        this.tooltip.add((Component)componentSpacing.plainCopy().append((Component)CreateLang.translateDirect("gui.config.overlay6", new Object[0]).withStyle(ChatFormatting.GRAY)));
        this.tooltip.add(CommonComponents.EMPTY);
        this.tooltip.add((Component)componentSpacing.plainCopy().append((Component)CreateLang.translateDirect("gui.config.overlay7", new Object[0])));
        this.tooltip.add((Component)componentSpacing.plainCopy().append((Component)CreateLang.translateDirect("gui.config.overlay8", new Object[0])));
    }

    protected void init() {
        this.width = this.minecraft.getWindow().getGuiScaledWidth();
        this.height = this.minecraft.getWindow().getGuiScaledHeight();
        this.offsetX = (Integer)AllConfigs.client().overlayOffsetX.get();
        this.offsetY = (Integer)AllConfigs.client().overlayOffsetY.get();
    }

    public void removed() {
        AllConfigs.client().overlayOffsetX.set((Object)this.offsetX);
        AllConfigs.client().overlayOffsetY.set((Object)this.offsetY);
    }

    public boolean mouseClicked(double x, double y, int button) {
        this.updateOffset(x, y);
        return true;
    }

    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        this.updateOffset(p_mouseDragged_1_, p_mouseDragged_3_);
        return true;
    }

    private void updateOffset(double windowX, double windowY) {
        this.offsetX = (int)(windowX - (double)(this.width / 2));
        this.offsetY = (int)(windowY - (double)(this.height / 2));
        int titleLinesCount = 1;
        int tooltipTextWidth = 0;
        for (FormattedText formattedText : this.tooltip) {
            int textLineWidth = this.minecraft.font.width(formattedText);
            if (textLineWidth <= tooltipTextWidth) continue;
            tooltipTextWidth = textLineWidth;
        }
        int tooltipHeight = 8;
        if (this.tooltip.size() > 1) {
            tooltipHeight += (this.tooltip.size() - 1) * 10;
            if (this.tooltip.size() > titleLinesCount) {
                tooltipHeight += 2;
            }
        }
        this.offsetX = Mth.clamp((int)this.offsetX, (int)(-(this.width / 2) - 5), (int)(this.width / 2 - tooltipTextWidth - 20));
        this.offsetY = Mth.clamp((int)this.offsetY, (int)(-(this.height / 2) + 17), (int)(this.height / 2 - tooltipHeight + 5));
    }

    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int posX = this.width / 2 + this.offsetX;
        int posY = this.height / 2 + this.offsetY;
        graphics.renderComponentTooltip(this.font, this.tooltip, posX, posY);
        ItemStack item = AllItems.GOGGLES.asStack();
        GuiGameElement.of((ItemStack)item).at((float)(posX + 10), (float)(posY - 16), 450.0f).render(graphics);
    }
}
