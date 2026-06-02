/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.content.trains.schedule.condition;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleDataEntry;
import com.simibubi.create.foundation.codec.CreateStreamCodecs;
import java.util.function.Supplier;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public abstract class ScheduleWaitCondition
extends ScheduleDataEntry {
    public static final StreamCodec<RegistryFriendlyByteBuf, ScheduleWaitCondition> STREAM_CODEC = CreateStreamCodecs.ofLegacyNbtWithRegistries(ScheduleWaitCondition::write, ScheduleWaitCondition::fromTag);

    public abstract boolean tickCompletion(Level var1, Train var2, CompoundTag var3);

    protected void requestStatusToUpdate(CompoundTag context) {
        context.putInt("StatusVersion", context.getInt("StatusVersion") + 1);
    }

    public final CompoundTag write(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        CompoundTag dataCopy = this.data.copy();
        this.writeAdditional(registries, dataCopy);
        tag.putString("Id", this.getId().toString());
        tag.put("Data", (Tag)dataCopy);
        return tag;
    }

    public static ScheduleWaitCondition fromTag(HolderLookup.Provider registries, CompoundTag tag) {
        ResourceLocation location = ResourceLocation.parse((String)tag.getString("Id"));
        Supplier supplier = null;
        for (Pair<ResourceLocation, Supplier<? extends ScheduleWaitCondition>> pair : Schedule.CONDITION_TYPES) {
            if (!((ResourceLocation)pair.getFirst()).equals((Object)location)) continue;
            supplier = (Supplier)pair.getSecond();
        }
        if (supplier == null) {
            Create.LOGGER.warn("Could not parse waiting condition type: " + String.valueOf(location));
            return null;
        }
        ScheduleWaitCondition condition = (ScheduleWaitCondition)supplier.get();
        condition.readAdditional(registries, tag);
        CompoundTag data = tag.getCompound("Data");
        condition.readAdditional(registries, data);
        condition.data = data;
        return condition;
    }

    public abstract MutableComponent getWaitingStatus(Level var1, Train var2, CompoundTag var3);
}
