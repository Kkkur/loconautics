/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.lang.LangBuilder
 *  net.minecraft.ChatFormatting
 */
package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;

public static enum IRotate.StressImpact {
    LOW(ChatFormatting.YELLOW, ChatFormatting.GREEN),
    MEDIUM(ChatFormatting.GOLD, ChatFormatting.YELLOW),
    HIGH(ChatFormatting.RED, ChatFormatting.GOLD),
    OVERSTRESSED(ChatFormatting.RED, ChatFormatting.RED);

    private final ChatFormatting absoluteColor;
    private final ChatFormatting relativeColor;

    private IRotate.StressImpact(ChatFormatting absoluteColor, ChatFormatting relativeColor) {
        this.absoluteColor = absoluteColor;
        this.relativeColor = relativeColor;
    }

    public ChatFormatting getAbsoluteColor() {
        return this.absoluteColor;
    }

    public ChatFormatting getRelativeColor() {
        return this.relativeColor;
    }

    public static IRotate.StressImpact of(double stressPercent) {
        if (stressPercent > 1.0) {
            return OVERSTRESSED;
        }
        if (stressPercent > 0.75) {
            return HIGH;
        }
        if (stressPercent > 0.5) {
            return MEDIUM;
        }
        return LOW;
    }

    public static boolean isEnabled() {
        return (Boolean)AllConfigs.server().kinetics.disableStress.get() == false;
    }

    public static LangBuilder getFormattedStressText(double stressPercent) {
        IRotate.StressImpact stressLevel = IRotate.StressImpact.of(stressPercent);
        return CreateLang.text(TooltipHelper.makeProgressBar(3, Math.min(stressLevel.ordinal() + 1, 3))).translate("tooltip.stressImpact." + Lang.asId((String)stressLevel.name()), new Object[0]).text(String.format(" (%s%%) ", (int)(stressPercent * 100.0))).style(stressLevel.getRelativeColor());
    }
}
