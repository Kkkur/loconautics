/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.kinetics.gauge.SpeedGaugeBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.NumericSingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class KineticSpeedDisplaySource
extends NumericSingleLineDisplaySource {
    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        BlockEntity blockEntity = context.getSourceBlockEntity();
        if (!(blockEntity instanceof SpeedGaugeBlockEntity)) {
            return ZERO.copy();
        }
        SpeedGaugeBlockEntity speedGauge = (SpeedGaugeBlockEntity)blockEntity;
        boolean absoluteValue = context.sourceConfig().getInt("Directional") == 0;
        float speed = absoluteValue ? Math.abs(speedGauge.getSpeed()) : speedGauge.getSpeed();
        return CreateLang.number(speed).space().translate("generic.unit.rpm", new Object[0]).component();
    }

    @Override
    protected String getTranslationKey() {
        return "kinetic_speed";
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine) {
            return;
        }
        builder.addSelectionScrollInput(0, 95, (selectionScrollInput, label) -> selectionScrollInput.forOptions(CreateLang.translatedOptions("display_source.kinetic_speed", "absolute", "directional")), "Directional");
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}
