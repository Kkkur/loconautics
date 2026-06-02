/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.redstone.displayLink.DisplayLinkContext
 *  com.simibubi.create.content.redstone.displayLink.source.NumericSingleLineDisplaySource
 *  com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats
 *  com.simibubi.create.foundation.gui.ModularGuiLineBuilder
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.content.display_sources;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.NumericSingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import dev.simulated_team.simulated.content.blocks.altitude_sensor.AltitudeSensorBlockEntity;
import dev.simulated_team.simulated.data.SimLang;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AltitudeSensorDisplaySource
extends NumericSingleLineDisplaySource {
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        BlockEntity blockEntity = context.getSourceBlockEntity();
        if (!(blockEntity instanceof AltitudeSensorBlockEntity)) {
            return ZERO.copy();
        }
        AltitudeSensorBlockEntity be = (AltitudeSensorBlockEntity)blockEntity;
        switch (context.sourceConfig().getInt("AltitudeSensorSelection")) {
            case 0: {
                assert (be.hasLevel());
                float airPressure = (float)be.getAirPressure() * 100.0f;
                return Component.literal((String)String.format("%.2f%%", Float.valueOf(airPressure)));
            }
            case 1: {
                return Component.literal((String)String.format("%.2f", Float.valueOf(be.getWorldHeight())));
            }
        }
        return EMPTY_LINE.copy();
    }

    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine) {
            return;
        }
        builder.addSelectionScrollInput(0, 95, (selectionScrollInput, label) -> selectionScrollInput.forOptions(SimLang.translatedOptions("display_source.altitude_sensor", "air_pressure", "height")), "AltitudeSensorSelection");
    }

    protected String getTranslationKey() {
        return "altitude_sensor.data";
    }

    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}
