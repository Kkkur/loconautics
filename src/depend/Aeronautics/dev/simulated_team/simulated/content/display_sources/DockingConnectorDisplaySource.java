/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.redstone.displayLink.DisplayLinkContext
 *  com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource
 *  com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats
 *  com.simibubi.create.foundation.gui.ModularGuiLineBuilder
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.content.display_sources;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DockingConnectorDisplaySource
extends SingleLineDisplaySource {
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        SubLevel otherSubLevel;
        BlockEntity blockEntity = context.getSourceBlockEntity();
        if (!(blockEntity instanceof DockingConnectorBlockEntity)) {
            return EMPTY_LINE.copy();
        }
        DockingConnectorBlockEntity be = (DockingConnectorBlockEntity)blockEntity;
        DockingConnectorBlockEntity otherConnector = be.getOtherConnector();
        if (otherConnector != null && (otherSubLevel = Sable.HELPER.getContaining((BlockEntity)otherConnector)) != null) {
            String name = otherSubLevel.getName();
            return name != null ? Component.literal((String)name) : EMPTY_LINE.copy();
        }
        return EMPTY_LINE.copy();
    }

    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
    }

    protected String getTranslationKey() {
        return "sublevel_name";
    }

    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}
