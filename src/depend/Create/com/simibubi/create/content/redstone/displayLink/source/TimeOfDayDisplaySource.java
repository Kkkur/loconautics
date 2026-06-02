/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.kinetics.clock.CuckooClockBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class TimeOfDayDisplaySource
extends SingleLineDisplaySource {
    public static final MutableComponent EMPTY_TIME = Component.literal((String)"--:--");

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        Level level = context.level();
        if (!(level instanceof ServerLevel)) {
            return EMPTY_TIME;
        }
        ServerLevel sLevel = (ServerLevel)level;
        BlockEntity blockEntity = context.getSourceBlockEntity();
        if (!(blockEntity instanceof CuckooClockBlockEntity)) {
            return EMPTY_TIME;
        }
        CuckooClockBlockEntity ccbe = (CuckooClockBlockEntity)blockEntity;
        if (ccbe.getSpeed() == 0.0f) {
            return EMPTY_TIME;
        }
        boolean c12 = context.sourceConfig().getInt("Cycle") == 0;
        boolean isNatural = sLevel.dimensionType().natural();
        int dayTime = (int)(sLevel.getDayTime() % 24000L);
        int hours = (dayTime / 1000 + 6) % 24;
        int minutes = dayTime % 1000 * 60 / 1000;
        MutableComponent suffix = CreateLang.translateDirect("generic.daytime." + (hours > 11 ? "pm" : "am"), new Object[0]);
        minutes = minutes / 5 * 5;
        if (c12 && (hours %= 12) == 0) {
            hours = 12;
        }
        if (!isNatural) {
            hours = sLevel.random.nextInt(70) + 24;
            minutes = sLevel.random.nextInt(40) + 60;
        }
        MutableComponent component = Component.literal((String)((hours < 10 ? " " : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes + (c12 ? " " : "")));
        return c12 ? component.append((Component)suffix) : component;
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
        return "time_of_day";
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine) {
            return;
        }
        builder.addSelectionScrollInput(0, 60, (si, l) -> si.forOptions(CreateLang.translatedOptions("display_source.time", "12_hour", "24_hour")).titled(CreateLang.translateDirect("display_source.time.format", new Object[0])), "Cycle");
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }
}
