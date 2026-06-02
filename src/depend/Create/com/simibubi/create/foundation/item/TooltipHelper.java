/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.lang.FontHelper$Palette
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 */
package com.simibubi.create.foundation.item;

import com.google.common.base.Strings;
import com.simibubi.create.foundation.utility.CreateLang;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class TooltipHelper {
    public static final int MAX_WIDTH_PER_LINE = 200;

    public static MutableComponent holdShift(FontHelper.Palette palette, boolean highlighted) {
        return CreateLang.translateDirect("tooltip.holdForDescription", CreateLang.translateDirect("tooltip.keyShift", new Object[0]).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY);
    }

    public static void addHint(List<Component> tooltip, String hintKey, Object ... messageParams) {
        CreateLang.translate(hintKey + ".title", new Object[0]).style(ChatFormatting.GOLD).forGoggles(tooltip);
        MutableComponent hint = CreateLang.translateDirect(hintKey, new Object[0]);
        List<Component> cutComponent = TooltipHelper.cutTextComponent((Component)hint, FontHelper.Palette.GRAY_AND_WHITE);
        for (Component component : cutComponent) {
            CreateLang.builder().add(component).forGoggles(tooltip);
        }
    }

    public static String makeProgressBar(int length, int filledLength) {
        int i;
        Object bar = " ";
        int emptySpaces = length - filledLength;
        for (i = 0; i < filledLength; ++i) {
            bar = (String)bar + "\u2588";
        }
        for (i = 0; i < emptySpaces; ++i) {
            bar = (String)bar + "\u2592";
        }
        return (String)bar + " ";
    }

    public static Style styleFromColor(ChatFormatting color) {
        return Style.EMPTY.applyFormat(color);
    }

    public static Style styleFromColor(int hex) {
        return Style.EMPTY.withColor(hex);
    }

    public static List<Component> cutStringTextComponent(String s, FontHelper.Palette palette) {
        return TooltipHelper.cutTextComponent((Component)Component.literal((String)s), palette);
    }

    public static List<Component> cutTextComponent(Component c, FontHelper.Palette palette) {
        return TooltipHelper.cutTextComponent(c, palette.primary(), palette.highlight());
    }

    public static List<Component> cutStringTextComponent(String s, Style primaryStyle, Style highlightStyle) {
        return TooltipHelper.cutTextComponent((Component)Component.literal((String)s), primaryStyle, highlightStyle);
    }

    public static List<Component> cutTextComponent(Component c, Style primaryStyle, Style highlightStyle) {
        return TooltipHelper.cutTextComponent(c, primaryStyle, highlightStyle, 0);
    }

    public static List<Component> cutStringTextComponent(String c, Style primaryStyle, Style highlightStyle, int indent) {
        return TooltipHelper.cutTextComponent((Component)Component.literal((String)c), primaryStyle, highlightStyle, indent);
    }

    public static List<Component> cutTextComponent(Component c, Style primaryStyle, Style highlightStyle, int indent) {
        String s = c.getString();
        LinkedList<String> words = new LinkedList<String>();
        BreakIterator iterator = BreakIterator.getLineInstance(Minecraft.getInstance().getLocale());
        iterator.setText(s);
        int start = iterator.first();
        int end = iterator.next();
        while (end != -1) {
            String word = s.substring(start, end);
            words.add(word);
            start = end;
            end = iterator.next();
        }
        Font font = Minecraft.getInstance().font;
        LinkedList<String> lines = new LinkedList<String>();
        StringBuilder currentLine = new StringBuilder();
        int width = 0;
        for (String word : words) {
            int newWidth = font.width(word.replaceAll("_", ""));
            if (width + newWidth > 200) {
                if (width > 0) {
                    String line = currentLine.toString();
                    lines.add(line);
                    currentLine = new StringBuilder();
                    width = 0;
                } else {
                    lines.add(word);
                    continue;
                }
            }
            currentLine.append(word);
            width += newWidth;
        }
        if (width > 0) {
            lines.add(currentLine.toString());
        }
        MutableComponent lineStart = Component.literal((String)Strings.repeat((String)" ", (int)indent));
        lineStart.withStyle(primaryStyle);
        ArrayList<Component> formattedLines = new ArrayList<Component>(lines.size());
        Couple styles = Couple.create((Object)highlightStyle, (Object)primaryStyle);
        boolean currentlyHighlighted = false;
        for (String string : lines) {
            String[] split;
            MutableComponent currentComponent = lineStart.plainCopy();
            for (String part : split = string.split("_", -1)) {
                currentComponent.append((Component)Component.literal((String)part).withStyle((Style)styles.get(currentlyHighlighted)));
                currentlyHighlighted = !currentlyHighlighted;
            }
            formattedLines.add((Component)currentComponent);
            currentlyHighlighted = !currentlyHighlighted;
        }
        return formattedLines;
    }
}
