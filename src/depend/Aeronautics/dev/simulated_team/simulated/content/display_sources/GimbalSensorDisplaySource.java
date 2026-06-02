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
import dev.simulated_team.simulated.content.blocks.gimbal_sensor.GimbalSensorBlockEntity;
import dev.simulated_team.simulated.content.display_sources.AbstractNumericDisplaysource;
import dev.simulated_team.simulated.data.SimLang;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class GimbalSensorDisplaySource
extends AbstractNumericDisplaysource {
    @Override
    List<Component> getOptions() {
        return SimLang.translatedOptions("display_source.gimbal_sensor", "x_angle", "z_angle");
    }

    @Override
    String getKey() {
        return "gimbal_sensor.data";
    }

    @Override
    String getSelectionKey() {
        return "GimbalSensorSelection";
    }

    @Override
    public int getWidth() {
        return 50;
    }

    public MutableComponent provideLine(DisplayLinkContext displayLinkContext, DisplayTargetStats displayTargetStats) {
        BlockEntity blockEntity = displayLinkContext.getSourceBlockEntity();
        if (!(blockEntity instanceof GimbalSensorBlockEntity)) {
            return ZERO.copy();
        }
        GimbalSensorBlockEntity be = (GimbalSensorBlockEntity)blockEntity;
        switch (displayLinkContext.sourceConfig().getInt(this.getSelectionKey())) {
            case 0: {
                return SimLang.number(Math.toDegrees(be.getXAngle())).component();
            }
            case 1: {
                return SimLang.number(Math.toDegrees(be.getZAngle())).component();
            }
        }
        return ZERO.copy();
    }
}
