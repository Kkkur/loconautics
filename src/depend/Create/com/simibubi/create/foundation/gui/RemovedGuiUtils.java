/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.Style
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.bus.api.Event
 *  net.neoforged.neoforge.client.ClientHooks
 *  net.neoforged.neoforge.client.event.RenderTooltipEvent$Color
 *  net.neoforged.neoforge.client.event.RenderTooltipEvent$Pre
 *  net.neoforged.neoforge.common.NeoForge
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Matrix4f
 */
package com.simibubi.create.foundation.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class RemovedGuiUtils {
    @NotNull
    private static ItemStack cachedTooltipStack = ItemStack.EMPTY;

    public static void preItemToolTip(@NotNull ItemStack stack) {
        cachedTooltipStack = stack;
    }

    public static void postItemToolTip() {
        cachedTooltipStack = ItemStack.EMPTY;
    }

    public static void drawHoveringText(GuiGraphics graphics, List<? extends FormattedText> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, Font font) {
        RemovedGuiUtils.drawHoveringText(graphics, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, -267386864, 0x505000FF, 1344798847, font);
    }

    public static void drawHoveringText(GuiGraphics graphics, List<? extends FormattedText> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, int backgroundColor, int borderColorStart, int borderColorEnd, Font font) {
        RemovedGuiUtils.drawHoveringText(cachedTooltipStack, graphics, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, backgroundColor, borderColorStart, borderColorEnd, font);
    }

    public static void drawHoveringText(@NotNull ItemStack stack, GuiGraphics graphics, List<? extends FormattedText> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, Font font) {
        RemovedGuiUtils.drawHoveringText(stack, graphics, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, -267386864, 0x505000FF, 1344798847, font);
    }

    public static void drawHoveringText(@NotNull ItemStack stack, GuiGraphics graphics, List<? extends FormattedText> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, int backgroundColor, int borderColorStart, int borderColorEnd, Font font) {
        int n;
        if (textLines.isEmpty()) {
            return;
        }
        List list = ClientHooks.gatherTooltipComponents((ItemStack)stack, textLines, (Optional)stack.getTooltipImage(), (int)mouseX, (int)screenWidth, (int)screenHeight, (Font)font);
        RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(stack, graphics, mouseX, mouseY, screenWidth, screenHeight, font, list, null);
        if (((RenderTooltipEvent.Pre)NeoForge.EVENT_BUS.post((Event)event)).isCanceled()) {
            return;
        }
        PoseStack pStack = graphics.pose();
        mouseX = event.getX();
        mouseY = event.getY();
        screenWidth = event.getScreenWidth();
        screenHeight = event.getScreenHeight();
        font = event.getFont();
        RenderSystem.disableDepthTest();
        int tooltipTextWidth = 0;
        for (FormattedText formattedText : textLines) {
            int textLineWidth = font.width(formattedText);
            if (textLineWidth <= tooltipTextWidth) continue;
            tooltipTextWidth = textLineWidth;
        }
        boolean needsWrap = false;
        boolean bl = true;
        int tooltipX = mouseX + 12;
        if (tooltipX + tooltipTextWidth + 4 > screenWidth && (tooltipX = mouseX - 16 - tooltipTextWidth) < 4) {
            tooltipTextWidth = mouseX > screenWidth / 2 ? mouseX - 12 - 8 : screenWidth - 16 - mouseX;
            needsWrap = true;
        }
        if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
            tooltipTextWidth = maxTextWidth;
            needsWrap = true;
        }
        if (needsWrap) {
            int wrappedTooltipWidth = 0;
            ArrayList<? extends FormattedText> wrappedTextLines = new ArrayList<FormattedText>();
            for (int i = 0; i < textLines.size(); ++i) {
                FormattedText textLine = textLines.get(i);
                List wrappedLine = font.getSplitter().splitLines(textLine, tooltipTextWidth, Style.EMPTY);
                if (i == 0) {
                    n = wrappedLine.size();
                }
                for (FormattedText line : wrappedLine) {
                    int lineWidth = font.width(line);
                    if (lineWidth > wrappedTooltipWidth) {
                        wrappedTooltipWidth = lineWidth;
                    }
                    wrappedTextLines.add((FormattedText)line);
                }
            }
            tooltipTextWidth = wrappedTooltipWidth;
            textLines = wrappedTextLines;
            tooltipX = mouseX > screenWidth / 2 ? mouseX - 16 - tooltipTextWidth : mouseX + 12;
        }
        int tooltipY = mouseY - 12;
        int tooltipHeight = 8;
        if (textLines.size() > 1) {
            tooltipHeight += (textLines.size() - 1) * 10;
            if (textLines.size() > n) {
                tooltipHeight += 2;
            }
        }
        if (tooltipY < 4) {
            tooltipY = 4;
        } else if (tooltipY + tooltipHeight + 4 > screenHeight) {
            tooltipY = screenHeight - tooltipHeight - 4;
        }
        int zLevel = 400;
        RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(stack, graphics, tooltipX, tooltipY, font, backgroundColor, borderColorStart, borderColorEnd, list);
        NeoForge.EVENT_BUS.post((Event)colorEvent);
        backgroundColor = colorEvent.getBackgroundStart();
        borderColorStart = colorEvent.getBorderStart();
        borderColorEnd = colorEvent.getBorderEnd();
        pStack.pushPose();
        Matrix4f mat = pStack.last().pose();
        graphics.fillGradient(tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, 400, backgroundColor, backgroundColor);
        graphics.fillGradient(tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, 400, backgroundColor, backgroundColor);
        graphics.fillGradient(tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, 400, backgroundColor, backgroundColor);
        graphics.fillGradient(tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, 400, backgroundColor, backgroundColor);
        graphics.fillGradient(tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, 400, backgroundColor, backgroundColor);
        graphics.fillGradient(tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, 400, borderColorStart, borderColorEnd);
        graphics.fillGradient(tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, 400, borderColorStart, borderColorEnd);
        graphics.fillGradient(tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, 400, borderColorStart, borderColorStart);
        graphics.fillGradient(tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, 400, borderColorEnd, borderColorEnd);
        MultiBufferSource.BufferSource renderType = graphics.bufferSource();
        pStack.translate(0.0, 0.0, 400.0);
        for (int lineNumber = 0; lineNumber < list.size(); ++lineNumber) {
            ClientTooltipComponent line = (ClientTooltipComponent)list.get(lineNumber);
            if (line != null) {
                line.renderText(font, tooltipX, tooltipY, mat, renderType);
            }
            if (lineNumber + 1 == n) {
                tooltipY += 2;
            }
            tooltipY += line == null ? 10 : line.getHeight();
        }
        renderType.endBatch();
        pStack.popPose();
        RenderSystem.enableDepthTest();
    }
}
