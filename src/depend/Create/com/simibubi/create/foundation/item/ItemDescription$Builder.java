/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.createmod.catnip.lang.FontHelper$Palette
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.foundation.item;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.tuple.Pair;

public static class ItemDescription.Builder {
    protected final FontHelper.Palette palette;
    protected final List<String> summary = new ArrayList<String>();
    protected final List<Pair<String, String>> behaviours = new ArrayList<Pair<String, String>>();
    protected final List<Pair<String, String>> actions = new ArrayList<Pair<String, String>>();

    public ItemDescription.Builder(FontHelper.Palette palette) {
        this.palette = palette;
    }

    public ItemDescription.Builder addSummary(String summaryLine) {
        this.summary.add(summaryLine);
        return this;
    }

    public ItemDescription.Builder addBehaviour(String condition, String behaviour) {
        this.behaviours.add((Pair<String, String>)Pair.of((Object)condition, (Object)behaviour));
        return this;
    }

    public ItemDescription.Builder addAction(String condition, String action) {
        this.actions.add((Pair<String, String>)Pair.of((Object)condition, (Object)action));
        return this;
    }

    public ItemDescription build() {
        boolean bl;
        String condition;
        ArrayList lines = new ArrayList();
        ArrayList<Object> linesOnShift = new ArrayList<Object>();
        ArrayList<Object> linesOnCtrl = new ArrayList<Object>();
        for (String string : this.summary) {
            linesOnShift.addAll(TooltipHelper.cutStringTextComponent(string, this.palette));
        }
        if (!this.behaviours.isEmpty()) {
            linesOnShift.add(CommonComponents.EMPTY);
        }
        for (Pair pair : this.behaviours) {
            condition = (String)pair.getLeft();
            String behaviour = (String)pair.getRight();
            linesOnShift.add(Component.literal((String)condition).withStyle(ChatFormatting.GRAY));
            linesOnShift.addAll(TooltipHelper.cutStringTextComponent(behaviour, this.palette.primary(), this.palette.highlight(), 1));
        }
        for (Pair pair : this.actions) {
            condition = (String)pair.getLeft();
            String action = (String)pair.getRight();
            linesOnCtrl.add(Component.literal((String)condition).withStyle(ChatFormatting.GRAY));
            linesOnCtrl.addAll(TooltipHelper.cutStringTextComponent(action, this.palette.primary(), this.palette.highlight(), 1));
        }
        boolean hasDescription = !linesOnShift.isEmpty();
        boolean bl2 = bl = !linesOnCtrl.isEmpty();
        if (hasDescription || bl) {
            String[] holdDesc = CreateLang.translateDirect("tooltip.holdForDescription", "$").getString().split("\\$");
            String[] holdCtrl = CreateLang.translateDirect("tooltip.holdForControls", "$").getString().split("\\$");
            MutableComponent keyShift = CreateLang.translateDirect("tooltip.keyShift", new Object[0]);
            MutableComponent keyCtrl = CreateLang.translateDirect("tooltip.keyCtrl", new Object[0]);
            for (List list : Arrays.asList(lines, linesOnShift, linesOnCtrl)) {
                MutableComponent tabBuilder;
                boolean ctrl;
                boolean shift = list == linesOnShift;
                boolean bl3 = ctrl = list == linesOnCtrl;
                if (holdDesc.length != 2 || holdCtrl.length != 2) {
                    list.add(0, Component.literal((String)"Invalid lang formatting!"));
                    continue;
                }
                if (bl) {
                    tabBuilder = Component.empty();
                    tabBuilder.append((Component)Component.literal((String)holdCtrl[0]).withStyle(ChatFormatting.DARK_GRAY));
                    tabBuilder.append((Component)keyCtrl.plainCopy().withStyle(ctrl ? ChatFormatting.WHITE : ChatFormatting.GRAY));
                    tabBuilder.append((Component)Component.literal((String)holdCtrl[1]).withStyle(ChatFormatting.DARK_GRAY));
                    list.add(0, tabBuilder);
                }
                if (hasDescription) {
                    tabBuilder = Component.empty();
                    tabBuilder.append((Component)Component.literal((String)holdDesc[0]).withStyle(ChatFormatting.DARK_GRAY));
                    tabBuilder.append((Component)keyShift.plainCopy().withStyle(shift ? ChatFormatting.WHITE : ChatFormatting.GRAY));
                    tabBuilder.append((Component)Component.literal((String)holdDesc[1]).withStyle(ChatFormatting.DARK_GRAY));
                    list.add(0, tabBuilder);
                }
                if (!shift && !ctrl) continue;
                list.add(hasDescription && bl ? 2 : 1, CommonComponents.EMPTY);
            }
        }
        if (!hasDescription) {
            linesOnCtrl.clear();
            linesOnShift.addAll(lines);
        }
        if (!bl) {
            linesOnCtrl.clear();
            linesOnCtrl.addAll(lines);
        }
        return new ItemDescription((ImmutableList<Component>)ImmutableList.copyOf(lines), (ImmutableList<Component>)ImmutableList.copyOf(linesOnShift), (ImmutableList<Component>)ImmutableList.copyOf(linesOnCtrl));
    }
}
