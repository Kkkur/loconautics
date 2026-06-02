/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.lang.LangBuilder
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface IRotate
extends IWrenchable {
    public boolean hasShaftTowards(LevelReader var1, BlockPos var2, BlockState var3, Direction var4);

    public Direction.Axis getRotationAxis(BlockState var1);

    default public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.NONE;
    }

    default public boolean hideStressImpact() {
        return false;
    }

    default public boolean showCapacityWithAnnotation() {
        return false;
    }

    public static enum SpeedLevel {
        NONE(ChatFormatting.DARK_GRAY, 0, 0),
        SLOW(ChatFormatting.GREEN, 0x22FF22, 10),
        MEDIUM(ChatFormatting.AQUA, 34047, 20),
        FAST(ChatFormatting.LIGHT_PURPLE, 0xFF55FF, 30);

        private final ChatFormatting textColor;
        private final int color;
        private final int particleSpeed;

        private SpeedLevel(ChatFormatting textColor, int color, int particleSpeed) {
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

        public static SpeedLevel of(float speed) {
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
            SpeedLevel speedLevel = SpeedLevel.of(speed);
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

    public static enum StressImpact {
        LOW(ChatFormatting.YELLOW, ChatFormatting.GREEN),
        MEDIUM(ChatFormatting.GOLD, ChatFormatting.YELLOW),
        HIGH(ChatFormatting.RED, ChatFormatting.GOLD),
        OVERSTRESSED(ChatFormatting.RED, ChatFormatting.RED);

        private final ChatFormatting absoluteColor;
        private final ChatFormatting relativeColor;

        private StressImpact(ChatFormatting absoluteColor, ChatFormatting relativeColor) {
            this.absoluteColor = absoluteColor;
            this.relativeColor = relativeColor;
        }

        public ChatFormatting getAbsoluteColor() {
            return this.absoluteColor;
        }

        public ChatFormatting getRelativeColor() {
            return this.relativeColor;
        }

        public static StressImpact of(double stressPercent) {
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
            StressImpact stressLevel = StressImpact.of(stressPercent);
            return CreateLang.text(TooltipHelper.makeProgressBar(3, Math.min(stressLevel.ordinal() + 1, 3))).translate("tooltip.stressImpact." + Lang.asId((String)stressLevel.name()), new Object[0]).text(String.format(" (%s%%) ", (int)(stressPercent * 100.0))).style(stressLevel.getRelativeColor());
        }
    }
}
