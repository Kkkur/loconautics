/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.trains.schedule;

import com.simibubi.create.content.trains.schedule.condition.ScheduleWaitCondition;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ScheduleEntry {
    public static final StreamCodec<RegistryFriendlyByteBuf, ScheduleEntry> STREAM_CODEC = StreamCodec.composite(ScheduleInstruction.STREAM_CODEC, entry -> entry.instruction, (StreamCodec)CatnipStreamCodecBuilders.list((StreamCodec)CatnipStreamCodecBuilders.list(ScheduleWaitCondition.STREAM_CODEC)), entry -> entry.conditions, ScheduleEntry::new);
    public ScheduleInstruction instruction;
    public List<List<ScheduleWaitCondition>> conditions;

    public ScheduleEntry() {
        this.conditions = new ArrayList<List<ScheduleWaitCondition>>();
    }

    public ScheduleEntry(ScheduleInstruction instruction, List<List<ScheduleWaitCondition>> conditions) {
        this.instruction = instruction;
        this.conditions = conditions;
    }

    public ScheduleEntry clone(HolderLookup.Provider registries) {
        return ScheduleEntry.fromTag(registries, this.write(registries));
    }

    public CompoundTag write(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        ListTag outer = new ListTag();
        tag.put("Instruction", (Tag)this.instruction.write(registries));
        if (!this.instruction.supportsConditions()) {
            return tag;
        }
        for (List<ScheduleWaitCondition> column : this.conditions) {
            outer.add((Object)NBTHelper.writeCompoundList(column, t -> t.write(registries)));
        }
        tag.put("Conditions", (Tag)outer);
        return tag;
    }

    public static ScheduleEntry fromTag(HolderLookup.Provider registries, CompoundTag tag) {
        ScheduleEntry entry = new ScheduleEntry();
        entry.instruction = ScheduleInstruction.fromTag(registries, tag.getCompound("Instruction"));
        entry.conditions = new ArrayList<List<ScheduleWaitCondition>>();
        if (entry.instruction.supportsConditions()) {
            for (Tag t : tag.getList("Conditions", 9)) {
                if (!(t instanceof ListTag)) continue;
                ListTag list = (ListTag)t;
                entry.conditions.add(NBTHelper.readCompoundList((ListTag)list, conditionTag -> ScheduleWaitCondition.fromTag(registries, conditionTag)));
            }
        }
        return entry;
    }
}
