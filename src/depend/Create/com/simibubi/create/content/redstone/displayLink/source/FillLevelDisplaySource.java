/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.PercentOrProgressBarDisplaySource;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchBlockEntity;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class FillLevelDisplaySource
extends PercentOrProgressBarDisplaySource {
    @Override
    protected Float getProgress(DisplayLinkContext context) {
        BlockEntity be = context.getSourceBlockEntity();
        if (!(be instanceof ThresholdSwitchBlockEntity)) {
            return null;
        }
        ThresholdSwitchBlockEntity tsbe = (ThresholdSwitchBlockEntity)be;
        return Float.valueOf(Math.max(0.0f, (float)(tsbe.currentLevel - tsbe.currentMinLevel) / (float)(tsbe.currentMaxLevel - tsbe.currentMinLevel)));
    }

    @Override
    protected boolean progressBarActive(DisplayLinkContext context) {
        return context.sourceConfig().getInt("Mode") != 0;
    }

    @Override
    protected String getTranslationKey() {
        return "fill_level";
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine) {
            return;
        }
        builder.addSelectionScrollInput(0, 120, (si, l) -> si.forOptions(CreateLang.translatedOptions("display_source.fill_level", "percent", "progress_bar")).titled(CreateLang.translateDirect("display_source.fill_level.display", new Object[0])), "Mode");
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}
