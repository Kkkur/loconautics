/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.LangBuilder
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.kinetics.gauge.StressGaugeBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.PercentOrProgressBarDisplaySource;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class KineticStressDisplaySource
extends PercentOrProgressBarDisplaySource {
    @Override
    protected MutableComponent formatNumeric(DisplayLinkContext context, Float currentLevel) {
        int mode = this.getMode(context);
        if (mode == 1) {
            return super.formatNumeric(context, currentLevel);
        }
        LangBuilder builder = CreateLang.number(currentLevel.floatValue());
        if (context.getTargetBlockEntity() instanceof FlapDisplayBlockEntity) {
            builder.space();
        }
        return builder.translate("generic.unit.stress", new Object[0]).component();
    }

    private int getMode(DisplayLinkContext context) {
        return context.sourceConfig().getInt("Mode");
    }

    @Override
    protected Float getProgress(DisplayLinkContext context) {
        BlockEntity blockEntity = context.getSourceBlockEntity();
        if (!(blockEntity instanceof StressGaugeBlockEntity)) {
            return null;
        }
        StressGaugeBlockEntity stressGauge = (StressGaugeBlockEntity)blockEntity;
        float capacity = stressGauge.getNetworkCapacity();
        float stress = stressGauge.getNetworkStress();
        if (capacity == 0.0f) {
            return Float.valueOf(0.0f);
        }
        return switch (this.getMode(context)) {
            case 0, 1 -> Float.valueOf(stress / capacity);
            case 2 -> Float.valueOf(stress);
            case 3 -> Float.valueOf(capacity);
            case 4 -> Float.valueOf(capacity - stress);
            default -> Float.valueOf(0.0f);
        };
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    protected boolean progressBarActive(DisplayLinkContext context) {
        return this.getMode(context) == 0;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine) {
            return;
        }
        builder.addSelectionScrollInput(0, 120, (si, l) -> si.forOptions(CreateLang.translatedOptions("display_source.kinetic_stress", "progress_bar", "percent", "current", "max", "remaining")).titled(CreateLang.translateDirect("display_source.kinetic_stress.display", new Object[0])), "Mode");
    }

    @Override
    protected String getTranslationKey() {
        return "kinetic_stress";
    }
}
