/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.kinetics.clock.CuckooClockBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.source.TimeOfDayDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class StopWatchDisplaySource
extends SingleLineDisplaySource {
    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        BlockEntity blockEntity = context.getSourceBlockEntity();
        if (!(blockEntity instanceof CuckooClockBlockEntity)) {
            return TimeOfDayDisplaySource.EMPTY_TIME;
        }
        CuckooClockBlockEntity ccbe = (CuckooClockBlockEntity)blockEntity;
        if (ccbe.getSpeed() == 0.0f) {
            return TimeOfDayDisplaySource.EMPTY_TIME;
        }
        if (!context.sourceConfig().contains("StartTime")) {
            this.onSignalReset(context);
        }
        long started = context.sourceConfig().getLong("StartTime");
        long current = context.blockEntity().getLevel().getGameTime();
        int diff = (int)(current - started);
        int hours = diff / 60 / 60 / 20;
        int minutes = diff / 60 / 20 % 60;
        int seconds = diff / 20 % 60;
        MutableComponent component = Component.literal((String)((String)(hours == 0 ? "" : (hours < 10 ? " " : "") + hours + ":") + (minutes < 10 ? (hours == 0 ? " " : "0") : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds));
        return component;
    }

    @Override
    public void onSignalReset(DisplayLinkContext context) {
        context.sourceConfig().putLong("StartTime", context.blockEntity().getLevel().getGameTime());
    }

    @Override
    public int getPassiveRefreshTicks() {
        return 20;
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    protected String getFlapDisplayLayoutName(DisplayLinkContext context) {
        return "Instant";
    }

    @Override
    protected FlapDisplaySection createSectionForValue(DisplayLinkContext context, int size) {
        return new FlapDisplaySection((float)size * 7.0f, "instant", false, false);
    }

    @Override
    protected String getTranslationKey() {
        return "stop_watch";
    }
}
