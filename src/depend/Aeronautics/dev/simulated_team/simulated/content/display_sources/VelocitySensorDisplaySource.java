/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.redstone.displayLink.DisplayLinkContext
 *  com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.content.display_sources;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import dev.simulated_team.simulated.content.blocks.velocity_sensor.VelocitySensorBlockEntity;
import dev.simulated_team.simulated.content.display_sources.AbstractNumericDisplaysource;
import dev.simulated_team.simulated.data.SimLang;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class VelocitySensorDisplaySource
extends AbstractNumericDisplaysource {
    @Override
    List<Component> getOptions() {
        return SimLang.translatedOptions("display_source.velocity_sensor", "speed");
    }

    @Override
    String getKey() {
        return "velocity_sensor.data";
    }

    @Override
    String getSelectionKey() {
        return "VeclotySensorSelection";
    }

    @Override
    public int getWidth() {
        return 90;
    }

    protected MutableComponent provideLine(DisplayLinkContext displayLinkContext, DisplayTargetStats displayTargetStats) {
        BlockEntity blockEntity = displayLinkContext.getSourceBlockEntity();
        if (!(blockEntity instanceof VelocitySensorBlockEntity)) {
            return ZERO.copy();
        }
        VelocitySensorBlockEntity vbe = (VelocitySensorBlockEntity)blockEntity;
        if (displayLinkContext.sourceConfig().getInt(this.getSelectionKey()) == 0) {
            return SimLang.number(vbe.getAdjustedVelocity()).text(" m/s").component();
        }
        return ZERO.copy();
    }
}
