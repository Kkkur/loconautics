/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.redstone.displayLink.DisplayLinkContext
 *  com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource
 *  com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats
 *  com.simibubi.create.foundation.gui.ModularGuiLineBuilder
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.content.display_sources;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.NavigationTarget;
import dev.simulated_team.simulated.data.SimLang;
import java.time.Duration;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class NavigationTableDisplaySource
extends SingleLineDisplaySource {
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        BlockEntity blockEntity = context.getSourceBlockEntity();
        if (!(blockEntity instanceof NavTableBlockEntity)) {
            return EMPTY_LINE.copy();
        }
        NavTableBlockEntity be = (NavTableBlockEntity)blockEntity;
        switch (context.sourceConfig().getInt("NavTableSelection")) {
            case 0: {
                NavigationTarget navigationTarget = be.getNavTableItem();
                if (navigationTarget == null) {
                    return EMPTY_LINE.copy();
                }
                int distance = (int)navigationTarget.distanceToTarget(be);
                return Component.literal((String)String.valueOf(distance));
            }
            case 1: {
                double distance = be.distanceToTarget();
                double lastDistance = be.lastDistanceToTarget();
                double change = lastDistance - distance;
                double speed = change / 0.5;
                int totalSeconds = (int)(distance / speed);
                Duration duration = Duration.ofSeconds(totalSeconds);
                Object eta = "%2s:%2s".formatted(duration.toMinutesPart(), duration.toSecondsPart());
                if (duration.toHoursPart() > 0) {
                    eta = "%2s:".formatted(duration.toHoursPart()) + (String)eta;
                }
                if (totalSeconds < 0 || change < 0.001) {
                    return Component.literal((String)"N/A");
                }
                return Component.literal((String)((String)eta).replace(' ', '0'));
            }
        }
        return EMPTY_LINE.copy();
    }

    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine) {
            return;
        }
        builder.addSelectionScrollInput(0, 95, (selectionScrollInput, label) -> selectionScrollInput.forOptions(SimLang.translatedOptions("display_source.navigation_table", "distance", "eta_real")), "NavTableSelection");
    }

    protected String getTranslationKey() {
        return "navigation_table.data";
    }

    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}
