/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package com.simibubi.create.content.trains.schedule.condition;

import com.simibubi.create.foundation.utility.CreateLang;
import java.util.List;
import net.minecraft.network.chat.Component;

public static enum TimedWaitCondition.TimeUnit {
    TICKS(1, "t", "generic.unit.ticks"),
    SECONDS(20, "s", "generic.unit.seconds"),
    MINUTES(1200, "min", "generic.unit.minutes");

    public int ticksPer;
    public String suffix;
    public String key;

    private TimedWaitCondition.TimeUnit(int ticksPer, String suffix, String key) {
        this.ticksPer = ticksPer;
        this.suffix = suffix;
        this.key = key;
    }

    public static List<Component> translatedOptions() {
        return CreateLang.translatedOptions(null, TimedWaitCondition.TimeUnit.TICKS.key, TimedWaitCondition.TimeUnit.SECONDS.key, TimedWaitCondition.TimeUnit.MINUTES.key);
    }
}
