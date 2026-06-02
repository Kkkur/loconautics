/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.ChatFormatting
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.simibubi.create.content.trains.display.GlobalTrainDisplayData;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class StationSummaryDisplaySource
extends DisplaySource {
    protected static final MutableComponent UNPREDICTABLE = Component.literal((String)" ~ ");
    protected static final List<MutableComponent> EMPTY_ENTRY_4 = ImmutableList.of((Object)WHITESPACE, (Object)Component.literal((String)" . "), (Object)WHITESPACE, (Object)WHITESPACE);
    protected static final List<MutableComponent> EMPTY_ENTRY_5 = ImmutableList.of((Object)WHITESPACE, (Object)Component.literal((String)" . "), (Object)WHITESPACE, (Object)WHITESPACE, (Object)WHITESPACE);

    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        return EMPTY;
    }

    @Override
    public List<List<MutableComponent>> provideFlapDisplayText(DisplayLinkContext context, DisplayTargetStats stats) {
        String filter = context.sourceConfig().getString("Filter");
        boolean hasPlatform = filter.contains("*");
        ArrayList<List<MutableComponent>> list = new ArrayList<List<MutableComponent>>();
        GlobalTrainDisplayData.prepare(filter, stats.maxRows()).forEach(prediction -> {
            ArrayList<MutableComponent> lines = new ArrayList<MutableComponent>();
            if (prediction.ticks == -1 || prediction.ticks >= 11700) {
                lines.add(WHITESPACE);
                lines.add(UNPREDICTABLE);
            } else if (prediction.ticks < 200) {
                lines.add(WHITESPACE);
                lines.add(CreateLang.translateDirect("display_source.station_summary.now", new Object[0]));
            } else {
                int min = prediction.ticks / 1200;
                int sec = prediction.ticks / 20 % 60;
                if ((sec = Mth.ceil((float)((float)sec / 15.0f)) * 15) == 60) {
                    ++min;
                    sec = 0;
                }
                lines.add(min > 0 ? Component.literal((String)String.valueOf(min)) : WHITESPACE);
                lines.add(min > 0 ? CreateLang.translateDirect("display_source.station_summary.minutes", new Object[0]) : CreateLang.translateDirect("display_source.station_summary.seconds", sec));
            }
            lines.add(prediction.train.name.copy());
            lines.add(prediction.scheduleTitle);
            if (!hasPlatform) {
                list.add(lines);
                return;
            }
            String platform = prediction.destination;
            for (String string : filter.split("\\*")) {
                if (string.isEmpty()) continue;
                platform = platform.replace(string, "");
            }
            platform = platform.replace("*", "?");
            lines.add(Component.literal((String)platform.trim()));
            list.add(lines);
        });
        if (list.size() > 0) {
            context.blockEntity().award(AllAdvancements.DISPLAY_BOARD);
        }
        int toPad = stats.maxRows() - list.size();
        for (int padding = 0; padding < toPad; ++padding) {
            list.add(hasPlatform ? EMPTY_ENTRY_5 : EMPTY_ENTRY_4);
        }
        return list;
    }

    @Override
    public void loadFlapDisplayLayout(DisplayLinkContext context, FlapDisplayBlockEntity flapDisplay, FlapDisplayLayout layout) {
        CompoundTag conf = context.sourceConfig();
        int columnWidth = conf.getInt("NameColumn");
        int columnWidth2 = conf.getInt("PlatformColumn");
        boolean hasPlatform = conf.getString("Filter").contains("*");
        String layoutName = "StationSummary" + columnWidth + hasPlatform + columnWidth2;
        if (layout.isLayout(layoutName)) {
            return;
        }
        ArrayList<FlapDisplaySection> list = new ArrayList<FlapDisplaySection>();
        int timeWidth = 20;
        float gapSize = 8.0f;
        float platformWidth = (float)columnWidth2 * 7.0f;
        FlapDisplaySection minutes = new FlapDisplaySection(7.0f, "numeric", false, false);
        FlapDisplaySection time = new FlapDisplaySection(timeWidth, "arrival_time", true, true);
        float totalSize = (float)flapDisplay.xSize * 32.0f - 4.0f - gapSize * 2.0f;
        totalSize = totalSize - (float)timeWidth - 7.0f;
        platformWidth = Math.min(platformWidth, totalSize - gapSize);
        platformWidth = (float)((int)(platformWidth / 7.0f)) * 7.0f;
        if (hasPlatform) {
            totalSize = totalSize - gapSize - platformWidth;
        }
        if (platformWidth == 0.0f && hasPlatform) {
            totalSize += gapSize;
        }
        int trainNameWidth = (int)((float)columnWidth / 100.0f * totalSize / 7.0f);
        int destinationWidth = Math.round((1.0f - (float)columnWidth / 100.0f) * totalSize / 7.0f);
        FlapDisplaySection trainName = new FlapDisplaySection((float)trainNameWidth * 7.0f, "alphabet", false, trainNameWidth > 0);
        FlapDisplaySection destination = new FlapDisplaySection((float)destinationWidth * 7.0f, "alphabet", false, hasPlatform && destinationWidth > 0 && platformWidth > 0.0f);
        FlapDisplaySection platform = new FlapDisplaySection(platformWidth, "numeric", false, false).rightAligned();
        list.add(minutes);
        list.add(time);
        list.add(trainName);
        list.add(destination);
        if (hasPlatform) {
            list.add(platform);
        }
        layout.configure(layoutName, list);
    }

    @Override
    protected String getTranslationKey() {
        return "station_summary";
    }

    @Override
    public void populateData(DisplayLinkContext context) {
        CompoundTag conf = context.sourceConfig();
        if (!conf.contains("PlatformColumn")) {
            conf.putInt("PlatformColumn", 3);
        }
        if (!conf.contains("NameColumn")) {
            conf.putInt("NameColumn", 50);
        }
        if (conf.contains("Filter")) {
            return;
        }
        BlockEntity blockEntity = context.getSourceBlockEntity();
        if (!(blockEntity instanceof StationBlockEntity)) {
            return;
        }
        StationBlockEntity stationBe = (StationBlockEntity)blockEntity;
        GlobalStation station = stationBe.getStation();
        if (station == null) {
            return;
        }
        conf.putString("Filter", station.name);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        if (isFirstLine) {
            builder.addTextInput(0, 137, (e, t) -> {
                e.setValue("");
                t.withTooltip((List<Component>)ImmutableList.of((Object)CreateLang.translateDirect("display_source.station_summary.filter", new Object[0]).withStyle(s -> s.withColor(5476833)), (Object)CreateLang.translateDirect("gui.schedule.lmb_edit", new Object[0]).withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})));
            }, "Filter");
            return;
        }
        builder.addScrollInput(0, 32, (si, l) -> {
            si.titled(CreateLang.translateDirect("display_source.station_summary.train_name_column", new Object[0])).withRange(0, 73).withShiftStep(12);
            si.setState(50);
            l.withSuffix("%");
        }, "NameColumn");
        builder.addScrollInput(36, 22, (si, l) -> {
            si.titled(CreateLang.translateDirect("display_source.station_summary.platform_column", new Object[0])).withRange(0, 16).withShiftStep(4);
            si.setState(3);
        }, "PlatformColumn");
    }
}
