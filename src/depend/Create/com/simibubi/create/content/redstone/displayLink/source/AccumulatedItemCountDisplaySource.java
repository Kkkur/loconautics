/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.NumericSingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.state.properties.Property;

public class AccumulatedItemCountDisplaySource
extends NumericSingleLineDisplaySource {
    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        return Component.literal((String)String.valueOf(context.sourceConfig().getInt("Collected")));
    }

    public void itemReceived(DisplayLinkBlockEntity be, int amount) {
        if (be.getBlockState().getOptionalValue((Property)DisplayLinkBlock.POWERED).orElse(true).booleanValue()) {
            return;
        }
        int collected = be.getSourceConfig().getInt("Collected");
        be.getSourceConfig().putInt("Collected", collected + amount);
        be.updateGatheredData();
    }

    @Override
    protected String getTranslationKey() {
        return "accumulate_items";
    }

    @Override
    public int getPassiveRefreshTicks() {
        return 200;
    }

    @Override
    public void onSignalReset(DisplayLinkContext context) {
        context.sourceConfig().remove("Collected");
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}
