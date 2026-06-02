/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.PercentOrProgressBarDisplaySource;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class RedstonePowerDisplaySource
extends PercentOrProgressBarDisplaySource {
    @Override
    protected String getTranslationKey() {
        return "redstone_power";
    }

    @Override
    protected MutableComponent formatNumeric(DisplayLinkContext context, Float currentLevel) {
        return Component.literal((String)String.valueOf((int)(currentLevel.floatValue() * 15.0f)));
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    protected Float getProgress(DisplayLinkContext context) {
        BlockState blockState = context.level().getBlockState(context.getSourcePos());
        return Float.valueOf((float)Math.max(context.level().getDirectSignalTo(context.getSourcePos()), blockState.getOptionalValue((Property)BlockStateProperties.POWER).orElse(0)) / 15.0f);
    }

    @Override
    protected boolean progressBarActive(DisplayLinkContext context) {
        return context.sourceConfig().getInt("Mode") != 0;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine) {
            return;
        }
        builder.addSelectionScrollInput(0, 120, (si, l) -> si.forOptions(CreateLang.translatedOptions("display_source.redstone_power", "number", "progress_bar")).titled(CreateLang.translateDirect("display_source.redstone_power.display", new Object[0])), "Mode");
    }
}
