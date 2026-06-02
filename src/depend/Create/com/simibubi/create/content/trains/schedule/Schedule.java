/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 */
package com.simibubi.create.content.trains.schedule;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.condition.FluidThresholdCondition;
import com.simibubi.create.content.trains.schedule.condition.IdleCargoCondition;
import com.simibubi.create.content.trains.schedule.condition.ItemThresholdCondition;
import com.simibubi.create.content.trains.schedule.condition.PlayerPassengerCondition;
import com.simibubi.create.content.trains.schedule.condition.RedstoneLinkCondition;
import com.simibubi.create.content.trains.schedule.condition.ScheduleWaitCondition;
import com.simibubi.create.content.trains.schedule.condition.ScheduledDelay;
import com.simibubi.create.content.trains.schedule.condition.StationPoweredCondition;
import com.simibubi.create.content.trains.schedule.condition.StationUnloadedCondition;
import com.simibubi.create.content.trains.schedule.condition.TimeOfDayCondition;
import com.simibubi.create.content.trains.schedule.destination.ChangeThrottleInstruction;
import com.simibubi.create.content.trains.schedule.destination.ChangeTitleInstruction;
import com.simibubi.create.content.trains.schedule.destination.DeliverPackagesInstruction;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;
import com.simibubi.create.content.trains.schedule.destination.FetchPackagesInstruction;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class Schedule {
    public static final StreamCodec<RegistryFriendlyByteBuf, Schedule> STREAM_CODEC = StreamCodec.composite((StreamCodec)CatnipStreamCodecBuilders.list(ScheduleEntry.STREAM_CODEC), schedule -> schedule.entries, (StreamCodec)ByteBufCodecs.BOOL, schedule -> schedule.cyclic, (StreamCodec)ByteBufCodecs.VAR_INT, schedule -> schedule.savedProgress, Schedule::new);
    public static List<Pair<ResourceLocation, Supplier<? extends ScheduleInstruction>>> INSTRUCTION_TYPES = new ArrayList<Pair<ResourceLocation, Supplier<? extends ScheduleInstruction>>>();
    public static List<Pair<ResourceLocation, Supplier<? extends ScheduleWaitCondition>>> CONDITION_TYPES = new ArrayList<Pair<ResourceLocation, Supplier<? extends ScheduleWaitCondition>>>();
    public List<ScheduleEntry> entries;
    public boolean cyclic;
    public int savedProgress;

    private static void registerInstruction(String name, Supplier<? extends ScheduleInstruction> factory) {
        INSTRUCTION_TYPES.add((Pair<ResourceLocation, Supplier<? extends ScheduleInstruction>>)Pair.of((Object)Create.asResource(name), factory));
    }

    private static void registerCondition(String name, Supplier<? extends ScheduleWaitCondition> factory) {
        CONDITION_TYPES.add((Pair<ResourceLocation, Supplier<? extends ScheduleWaitCondition>>)Pair.of((Object)Create.asResource(name), factory));
    }

    public static <T> List<? extends Component> getTypeOptions(List<Pair<ResourceLocation, T>> list) {
        String langSection = list.equals(INSTRUCTION_TYPES) ? "instruction." : "condition.";
        return list.stream().map(Pair::getFirst).map(rl -> rl.getNamespace() + ".schedule." + langSection + rl.getPath()).map(key -> Component.translatable((String)key)).toList();
    }

    public Schedule() {
        this(new ArrayList<ScheduleEntry>(), true, 0);
    }

    public Schedule(List<ScheduleEntry> entries, boolean cyclic, int savedProgress) {
        this.entries = entries;
        this.cyclic = cyclic;
        this.savedProgress = savedProgress;
    }

    public CompoundTag write(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        ListTag list = NBTHelper.writeCompoundList(this.entries, t -> t.write(registries));
        tag.put("Entries", (Tag)list);
        tag.putBoolean("Cyclic", this.cyclic);
        if (this.savedProgress > 0) {
            tag.putInt("Progress", this.savedProgress);
        }
        return tag;
    }

    public static Schedule fromTag(HolderLookup.Provider registries, CompoundTag tag) {
        Schedule schedule = new Schedule();
        schedule.entries = NBTHelper.readCompoundList((ListTag)tag.getList("Entries", 10), t -> ScheduleEntry.fromTag(registries, t));
        schedule.cyclic = tag.getBoolean("Cyclic");
        if (tag.contains("Progress")) {
            schedule.savedProgress = tag.getInt("Progress");
        }
        return schedule;
    }

    static {
        Schedule.registerInstruction("destination", DestinationInstruction::new);
        Schedule.registerInstruction("package_delivery", DeliverPackagesInstruction::new);
        Schedule.registerInstruction("package_retrieval", FetchPackagesInstruction::new);
        Schedule.registerInstruction("rename", ChangeTitleInstruction::new);
        Schedule.registerInstruction("throttle", ChangeThrottleInstruction::new);
        Schedule.registerCondition("delay", ScheduledDelay::new);
        Schedule.registerCondition("time_of_day", TimeOfDayCondition::new);
        Schedule.registerCondition("fluid_threshold", FluidThresholdCondition::new);
        Schedule.registerCondition("item_threshold", ItemThresholdCondition::new);
        Schedule.registerCondition("redstone_link", RedstoneLinkCondition::new);
        Schedule.registerCondition("player_count", PlayerPassengerCondition::new);
        Schedule.registerCondition("idle", IdleCargoCondition::new);
        Schedule.registerCondition("unloaded", StationUnloadedCondition::new);
        Schedule.registerCondition("powered", StationPoweredCondition::new);
    }
}
