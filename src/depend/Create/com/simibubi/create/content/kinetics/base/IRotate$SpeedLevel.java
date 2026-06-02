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

public static enum IRotate.SpeedLevel {
    NONE(ChatFormatting.DARK_GRAY, 0, 0),
    SLOW(ChatFormatting.GREEN, 0x22FF22, 10),
    MEDIUM(ChatFormatting.AQUA, 34047, 20),
    FAST(ChatFormatting.LIGHT_PURPLE, 0xFF55FF, 30);

    private final ChatFormatting textColor;
    private final int color;
    private final int particleSpeed;

    private IRotate.SpeedLevel(ChatFormatting textColor, int color, int particleSpeed) {
        this.textColor = textColor;
        this.color = color;
        this.particleSpeed = particleSpeed;
    }

    public ChatFormatting getTextColor() {
        return this.textColor;
    }

    public int getColor() {
        return this.color;
    }

    public int getParticleSpeed() {
        return this.particleSpeed;
    }

    public float getSpeedValue() {
        switch (this.ordinal()) {
            case 3: {
                return ((Double)AllConfigs.server().kinetics.fastSpeed.get()).floatValue();
            }
            case 2: {
                return ((Double)AllConfigs.server().kinetics.mediumSpeed.get()).floatValue();
            }
            case 1: {
                return 1.0f;
            }
        }
        return 0.0f;
    }

    public static IRotate.SpeedLevel of(float speed) {
        if ((double)(speed = Math.abs(speed)) >= (Double)AllConfigs.server().kinetics.fastSpeed.get()) {
            return FAST;
        }
        if ((double)speed >= (Double)AllConfigs.server().kinetics.mediumSpeed.get()) {
            return MEDIUM;
        }
        if (speed >= 1.0f) {
            return SLOW;
        }
        return NONE;
    }

    public static LangBuilder getFormattedSpeedText(float speed, boolean overstressed) {
        IRotate.SpeedLevel speedLevel = IRotate.SpeedLevel.of(speed);
        LangBuilder builder = CreateLang.text(TooltipHelper.makeProgressBar(3, speedLevel.ordinal()));
        builder.translate("tooltip.speedRequirement." + Lang.asId((String)speedLevel.name()), new Object[0]).space().text("(").add(CreateLang.number(Math.abs(speed))).space().translate("generic.unit.rpm", new Object[0]).text(")").space();
        if (overstressed) {
            builder.style(ChatFormatting.DARK_GRAY).style(ChatFormatting.STRIKETHROUGH);
        } else {
            builder.style(speedLevel.getTextColor());
        }
        return builder;
    }
}
