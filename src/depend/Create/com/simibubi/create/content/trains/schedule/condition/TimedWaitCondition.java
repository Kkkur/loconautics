/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.ChatFormatting
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.trains.schedule.condition;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.condition.ScheduleWaitCondition;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class TimedWaitCondition
extends ScheduleWaitCondition {
    protected void requestDisplayIfNecessary(CompoundTag context, int time) {
        int ticksUntilDeparture = this.totalWaitTicks() - time;
        if (ticksUntilDeparture < 1200 && ticksUntilDeparture % 100 == 0) {
            this.requestStatusToUpdate(context);
        }
        if (ticksUntilDeparture >= 1200 && ticksUntilDeparture % 1200 == 0) {
            this.requestStatusToUpdate(context);
        }
    }

    public int totalWaitTicks() {
        return this.getValue() * this.getUnit().ticksPer;
    }

    public TimedWaitCondition() {
        this.data.putInt("Value", 5);
        this.data.putInt("TimeUnit", TimeUnit.SECONDS.ordinal());
    }

    protected Component formatTime(boolean compact) {
        if (compact) {
            return Component.literal((String)(this.getValue() + this.getUnit().suffix));
        }
        return Component.literal((String)(this.getValue() + " ")).append((Component)CreateLang.translateDirect(this.getUnit().key, new Object[0]));
    }

    @Override
    public List<Component> getTitleAs(String type) {
        return ImmutableList.of((Object)Component.translatable((String)(this.getId().getNamespace() + ".schedule." + type + "." + this.getId().getPath())), (Object)CreateLang.translateDirect("schedule.condition.for_x_time", this.formatTime(false)).withStyle(ChatFormatting.DARK_AQUA));
    }

    @Override
    public ItemStack getSecondLineIcon() {
        return new ItemStack((ItemLike)Items.REPEATER);
    }

    @Override
    public List<Component> getSecondLineTooltip(int slot) {
        return ImmutableList.of((Object)CreateLang.translateDirect("generic.duration", new Object[0]));
    }

    public int getValue() {
        return this.intData("Value");
    }

    public TimeUnit getUnit() {
        return this.enumData("TimeUnit", TimeUnit.class);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        builder.addScrollInput(0, 31, (i, l) -> {
            i.titled(CreateLang.translateDirect("generic.duration", new Object[0])).withShiftStep(15).withRange(0, 121);
            i.lockedTooltipX = -15;
            i.lockedTooltipY = 35;
        }, "Value");
        builder.addSelectionScrollInput(36, 85, (i, l) -> i.forOptions(TimeUnit.translatedOptions()).titled(CreateLang.translateDirect("generic.timeUnit", new Object[0])), "TimeUnit");
    }

    @Override
    public MutableComponent getWaitingStatus(Level level, Train train, CompoundTag tag) {
        int time = tag.getInt("Time");
        int ticksUntilDeparture = this.totalWaitTicks() - time;
        boolean showInMinutes = ticksUntilDeparture >= 1200;
        int num = (int)(showInMinutes ? Math.floor((float)ticksUntilDeparture / 1200.0f) : Math.ceil((float)ticksUntilDeparture / 100.0f) * 5.0);
        String key = "generic." + (showInMinutes ? (num == 1 ? "daytime.minute" : "unit.minutes") : (num == 1 ? "daytime.second" : "unit.seconds"));
        return CreateLang.translateDirect("schedule.condition." + this.getId().getPath() + ".status", Component.literal((String)(num + " ")).append((Component)CreateLang.translateDirect(key, new Object[0])));
    }

    public static enum TimeUnit {
        TICKS(1, "t", "generic.unit.ticks"),
        SECONDS(20, "s", "generic.unit.seconds"),
        MINUTES(1200, "min", "generic.unit.minutes");

        public int ticksPer;
        public String suffix;
        public String key;

        private TimeUnit(int ticksPer, String suffix, String key) {
            this.ticksPer = ticksPer;
            this.suffix = suffix;
            this.key = key;
        }

        public static List<Component> translatedOptions() {
            return CreateLang.translatedOptions(null, TimeUnit.TICKS.key, TimeUnit.SECONDS.key, TimeUnit.MINUTES.key);
        }
    }
}
