/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.schedule.destination;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleDataEntry;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;
import com.simibubi.create.foundation.codec.CreateStreamCodecs;
import java.util.function.Supplier;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class ScheduleInstruction
extends ScheduleDataEntry {
    public static final StreamCodec<RegistryFriendlyByteBuf, ScheduleInstruction> STREAM_CODEC = CreateStreamCodecs.ofLegacyNbtWithRegistries(ScheduleInstruction::write, ScheduleInstruction::fromTag);

    public abstract boolean supportsConditions();

    @Nullable
    public abstract DiscoveredPath start(ScheduleRuntime var1, Level var2);

    public final CompoundTag write(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        CompoundTag dataCopy = this.data.copy();
        this.writeAdditional(registries, dataCopy);
        tag.putString("Id", this.getId().toString());
        tag.put("Data", (Tag)dataCopy);
        return tag;
    }

    public static ScheduleInstruction fromTag(HolderLookup.Provider registries, CompoundTag tag) {
        ResourceLocation location = ResourceLocation.parse((String)tag.getString("Id"));
        Supplier supplier = null;
        for (Pair<ResourceLocation, Supplier<? extends ScheduleInstruction>> pair : Schedule.INSTRUCTION_TYPES) {
            if (!((ResourceLocation)pair.getFirst()).equals((Object)location)) continue;
            supplier = (Supplier)pair.getSecond();
        }
        if (supplier == null) {
            Create.LOGGER.warn("Could not parse schedule instruction type: " + String.valueOf(location));
            return new DestinationInstruction();
        }
        ScheduleInstruction scheduleDestination = (ScheduleInstruction)supplier.get();
        scheduleDestination.readAdditional(registries, tag);
        CompoundTag data = tag.getCompound("Data");
        scheduleDestination.readAdditional(registries, data);
        scheduleDestination.data = data;
        return scheduleDestination;
    }
}
