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
import dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineBlockEntity;
import dev.simulated_team.simulated.content.display_sources.AbstractNumericDisplaysource;
import dev.simulated_team.simulated.data.SimLang;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PortableEngineDisplaySource
extends AbstractNumericDisplaysource {
    @Override
    List<Component> getOptions() {
        return SimLang.translatedOptions("display_source.portable_engine", "current_burn", "total_burn");
    }

    @Override
    String getKey() {
        return "portable_engine.data";
    }

    @Override
    String getSelectionKey() {
        return "PortableEngineSelection";
    }

    protected MutableComponent provideLine(DisplayLinkContext displayLinkContext, DisplayTargetStats displayTargetStats) {
        BlockEntity blockEntity = displayLinkContext.getSourceBlockEntity();
        if (!(blockEntity instanceof PortableEngineBlockEntity)) {
            return ZERO.copy();
        }
        PortableEngineBlockEntity be = (PortableEngineBlockEntity)blockEntity;
        switch (displayLinkContext.sourceConfig().getInt(this.getSelectionKey())) {
            case 0: {
                if (be.isCurrentFuelInfinite()) {
                    return SimLang.translate("portable_engine.infinite", new Object[0]).component();
                }
                return SimLang.number((double)be.getCurrentBurnTime() / 20.0).component();
            }
            case 1: {
                if (be.isCurrentFuelInfinite()) {
                    return SimLang.translate("portable_engine.infinite", new Object[0]).component();
                }
                return SimLang.number((double)be.getTotalBurnTime() / 20.0).component();
            }
        }
        return ZERO.copy();
    }
}
