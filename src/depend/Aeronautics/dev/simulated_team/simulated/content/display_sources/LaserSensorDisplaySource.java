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
import dev.simulated_team.simulated.content.blocks.lasers.laser_sensor.LaserSensorBlockEntity;
import dev.simulated_team.simulated.data.SimLang;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LaserSensorDisplaySource
extends NumericSingleLineDisplaySource {
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats displayTargetStats) {
        BlockEntity blockEntity = context.getSourceBlockEntity();
        if (!(blockEntity instanceof LaserSensorBlockEntity)) {
            return (MutableComponent)EMPTY.getFirst();
        }
        LaserSensorBlockEntity be = (LaserSensorBlockEntity)blockEntity;
        return be.closestHitDistance == Double.MAX_VALUE ? (MutableComponent)EMPTY.getFirst() : SimLang.number(be.closestHitDistance).space().text("block" + (be.closestHitDistance != 1.0 ? "s" : "")).component();
    }

    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine) {
            return;
        }
        builder.addSelectionScrollInput(0, 88, (selectionScrollInput, label) -> selectionScrollInput.forOptions(SimLang.translatedOptions("display_source.laser_sensor", "laser_distance")), "LaserSensorSelection");
    }

    protected String getTranslationKey() {
        return "laser_sensor.data";
    }

    protected boolean allowsLabeling(DisplayLinkContext displayLinkContext) {
        return true;
    }
}
