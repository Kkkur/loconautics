/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.SignBlockEntity
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.NumericSingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class PercentOrProgressBarDisplaySource
extends NumericSingleLineDisplaySource {
    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        Float rawProgress = this.getProgress(context);
        if (rawProgress == null) {
            return EMPTY_LINE;
        }
        if (!this.progressBarActive(context)) {
            return this.formatNumeric(context, rawProgress);
        }
        String label = context.sourceConfig().getString("Label");
        int labelSize = label.isEmpty() ? 0 : label.length() + 1;
        int length = Math.min(stats.maxColumns() - labelSize, 128);
        if (context.getTargetBlockEntity() instanceof SignBlockEntity) {
            length = (int)((float)length * 6.0f / 9.0f);
        }
        if (context.getTargetBlockEntity() instanceof FlapDisplayBlockEntity) {
            length = this.sizeForWideChars(length);
        }
        float currentLevel = Mth.clamp((float)rawProgress.floatValue(), (float)0.0f, (float)1.0f);
        int filledLength = (int)(currentLevel * (float)length);
        if (length < 1) {
            return EMPTY_LINE;
        }
        int emptySpaces = length - filledLength;
        String s = "\u2588".repeat(Math.max(0, filledLength)) + "\u2592".repeat(Math.max(0, emptySpaces));
        return Component.literal((String)s);
    }

    protected MutableComponent formatNumeric(DisplayLinkContext context, Float currentLevel) {
        return Component.literal((String)(Mth.clamp((int)((int)(currentLevel.floatValue() * 100.0f)), (int)0, (int)100) + "%"));
    }

    @Nullable
    protected abstract Float getProgress(DisplayLinkContext var1);

    protected abstract boolean progressBarActive(DisplayLinkContext var1);

    @Override
    protected String getFlapDisplayLayoutName(DisplayLinkContext context) {
        return !this.progressBarActive(context) ? super.getFlapDisplayLayoutName(context) : "Progress";
    }

    @Override
    protected FlapDisplaySection createSectionForValue(DisplayLinkContext context, int size) {
        return !this.progressBarActive(context) ? super.createSectionForValue(context, size) : new FlapDisplaySection((float)size * 7.0f, "pixel", false, false).wideFlaps();
    }

    private int sizeForWideChars(int size) {
        return (int)((float)size * 7.0f / 9.0f);
    }
}
