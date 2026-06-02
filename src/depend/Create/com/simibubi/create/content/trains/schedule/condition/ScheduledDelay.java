/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.content.trains.schedule.condition;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.condition.TimedWaitCondition;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.data.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ScheduledDelay
extends TimedWaitCondition {
    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of((Object)ItemStack.EMPTY, (Object)CreateLang.translateDirect("schedule.condition.delay_short", this.formatTime(true)));
    }

    @Override
    public boolean tickCompletion(Level level, Train train, CompoundTag context) {
        int time = context.getInt("Time");
        if (time >= this.totalWaitTicks()) {
            return true;
        }
        context.putInt("Time", time + 1);
        this.requestDisplayIfNecessary(context, time);
        return false;
    }

    @Override
    public ResourceLocation getId() {
        return Create.asResource("delay");
    }
}
