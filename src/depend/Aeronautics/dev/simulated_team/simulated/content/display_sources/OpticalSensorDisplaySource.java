/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.redstone.displayLink.DisplayLinkContext
 *  com.simibubi.create.content.redstone.displayLink.source.NumericSingleLineDisplaySource
 *  com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats
 *  com.simibubi.create.foundation.gui.ModularGuiLineBuilder
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.content.display_sources;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.NumericSingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import dev.simulated_team.simulated.content.blocks.lasers.optical_sensor.OpticalSensorBlockEntity;
import dev.simulated_team.simulated.data.SimLang;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class OpticalSensorDisplaySource
extends NumericSingleLineDisplaySource {
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        BlockEntity blockEntity = context.getSourceBlockEntity();
        if (!(blockEntity instanceof OpticalSensorBlockEntity)) {
            return ZERO.copy();
        }
        OpticalSensorBlockEntity be = (OpticalSensorBlockEntity)blockEntity;
        switch (context.sourceConfig().getInt("OpticalSensorSelection")) {
            case 0: {
                return be.hasHit() ? be.getHitBlock().getName() : SimLang.text("No Block Detected").component();
            }
            case 1: {
                if (!be.hasHit()) {
                    return SimLang.text("No Block Detected").component();
                }
                float rayDistance = be.getRayDistance();
                return SimLang.number(rayDistance).space().text("block" + (rayDistance != 1.0f ? "s" : "")).component();
            }
        }
        return ZERO.copy();
    }

    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine) {
            return;
        }
        builder.addSelectionScrollInput(0, 85, (selectionScrollInput, label) -> selectionScrollInput.forOptions(SimLang.translatedOptions("display_source.optical_sensor", "detected_block", "block_distance")), "OpticalSensorSelection");
    }

    protected String getTranslationKey() {
        return "optical_sensor.data";
    }

    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}
