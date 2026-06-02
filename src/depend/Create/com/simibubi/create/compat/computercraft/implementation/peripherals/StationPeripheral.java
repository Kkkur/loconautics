/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.IArguments
 *  dan200.computercraft.api.lua.LuaException
 *  dan200.computercraft.api.lua.LuaFunction
 *  dan200.computercraft.api.lua.MethodResult
 *  net.createmod.catnip.data.Glob
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.ByteTag
 *  net.minecraft.nbt.CollectionTag
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.DoubleTag
 *  net.minecraft.nbt.IntTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NumericTag
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.compat.computercraft.implementation.peripherals;

import com.simibubi.create.compat.computercraft.events.ComputerEvent;
import com.simibubi.create.compat.computercraft.events.StationTrainPresenceEvent;
import com.simibubi.create.compat.computercraft.implementation.CreateLuaTable;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.station.TrainEditPacket;
import com.simibubi.create.foundation.utility.StringHelper;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.PatternSyntaxException;
import net.createmod.catnip.data.Glob;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StationPeripheral
extends SyncedPeripheral<StationBlockEntity> {
    public StationPeripheral(StationBlockEntity blockEntity) {
        super(blockEntity);
    }

    @LuaFunction(mainThread=true)
    public final void assemble() throws LuaException {
        if (!((StationBlockEntity)this.blockEntity).isAssembling()) {
            throw new LuaException("station must be in assembly mode");
        }
        ((StationBlockEntity)this.blockEntity).assemble(null);
        if (((StationBlockEntity)this.blockEntity).getStation() == null || ((StationBlockEntity)this.blockEntity).getStation().getPresentTrain() == null) {
            throw new LuaException("failed to assemble train");
        }
        if (!((StationBlockEntity)this.blockEntity).exitAssemblyMode()) {
            throw new LuaException("failed to exit assembly mode");
        }
    }

    @LuaFunction(mainThread=true)
    public final void disassemble() throws LuaException {
        if (((StationBlockEntity)this.blockEntity).isAssembling()) {
            throw new LuaException("station must not be in assembly mode");
        }
        this.getTrainOrThrow();
        if (!((StationBlockEntity)this.blockEntity).enterAssemblyMode(null)) {
            throw new LuaException("could not disassemble train");
        }
    }

    @LuaFunction(mainThread=true)
    public final void setAssemblyMode(boolean assemblyMode) throws LuaException {
        if (assemblyMode) {
            if (!((StationBlockEntity)this.blockEntity).enterAssemblyMode(null)) {
                throw new LuaException("failed to enter assembly mode");
            }
        } else if (!((StationBlockEntity)this.blockEntity).exitAssemblyMode()) {
            throw new LuaException("failed to exit assembly mode");
        }
    }

    @LuaFunction
    public final boolean isInAssemblyMode() {
        return ((StationBlockEntity)this.blockEntity).isAssembling();
    }

    @LuaFunction
    public final String getStationName() throws LuaException {
        GlobalStation station = ((StationBlockEntity)this.blockEntity).getStation();
        if (station == null) {
            throw new LuaException("station is not connected to a track");
        }
        return station.name;
    }

    @LuaFunction(mainThread=true)
    public final void setStationName(String name) throws LuaException {
        if (!((StationBlockEntity)this.blockEntity).updateName(name)) {
            throw new LuaException("could not set station name");
        }
    }

    @LuaFunction
    public final boolean isTrainPresent() throws LuaException {
        GlobalStation station = ((StationBlockEntity)this.blockEntity).getStation();
        if (station == null) {
            throw new LuaException("station is not connected to a track");
        }
        return station.getPresentTrain() != null;
    }

    @LuaFunction
    public final boolean isTrainImminent() throws LuaException {
        GlobalStation station = ((StationBlockEntity)this.blockEntity).getStation();
        if (station == null) {
            throw new LuaException("station is not connected to a track");
        }
        return station.getImminentTrain() != null;
    }

    @LuaFunction
    public final boolean isTrainEnroute() throws LuaException {
        GlobalStation station = ((StationBlockEntity)this.blockEntity).getStation();
        if (station == null) {
            throw new LuaException("station is not connected to a track");
        }
        return station.getNearestTrain() != null;
    }

    @LuaFunction
    public final String getTrainName() throws LuaException {
        Train train = this.getTrainOrThrow();
        return train.name.getString();
    }

    @LuaFunction(mainThread=true)
    public final void setTrainName(String name) throws LuaException {
        Train train = this.getTrainOrThrow();
        train.name = Component.literal((String)name);
        CatnipServices.NETWORK.sendToAllClients((CustomPacketPayload)new TrainEditPacket.TrainEditReturnPacket(train.id, name, train.icon.getId(), train.mapColorIndex));
    }

    @LuaFunction
    public final boolean hasSchedule() throws LuaException {
        Train train = this.getTrainOrThrow();
        return train.runtime.getSchedule() != null;
    }

    @LuaFunction
    public final CreateLuaTable getSchedule() throws LuaException {
        Train train = this.getTrainOrThrow();
        Schedule schedule = train.runtime.getSchedule();
        if (schedule == null) {
            throw new LuaException("train doesn't have a schedule");
        }
        return StationPeripheral.fromCompoundTag(schedule.write((HolderLookup.Provider)((StationBlockEntity)this.blockEntity).getLevel().registryAccess()));
    }

    @LuaFunction(mainThread=true)
    public final void setSchedule(IArguments arguments) throws LuaException {
        Train train = this.getTrainOrThrow();
        Schedule schedule = Schedule.fromTag((HolderLookup.Provider)((StationBlockEntity)this.blockEntity).getLevel().registryAccess(), StationPeripheral.toCompoundTag(new CreateLuaTable(arguments.getTable(0))));
        if (schedule.entries.isEmpty()) {
            throw new LuaException("Schedule must have at least one entry");
        }
        boolean autoSchedule = train.runtime.getSchedule() == null || train.runtime.isAutoSchedule;
        train.runtime.setSchedule(schedule, autoSchedule);
    }

    private Pair<@Nullable DiscoveredPath, @NotNull Boolean> findPath(String destinationFilter) throws LuaException {
        DiscoveredPath best;
        Train train = this.getTrainOrThrow();
        String regex = Glob.toRegexPattern((String)destinationFilter, (String)"");
        boolean anyMatch = false;
        ArrayList<GlobalStation> validStations = new ArrayList<GlobalStation>();
        try {
            for (GlobalStation globalStation : train.graph.getPoints(EdgePointType.STATION)) {
                if (!globalStation.name.matches(regex)) continue;
                anyMatch = true;
                validStations.add(globalStation);
            }
        }
        catch (PatternSyntaxException patternSyntaxException) {
            // empty catch block
        }
        if ((best = train.navigation.findPathTo(validStations, Double.MAX_VALUE)) == null) {
            return Pair.of(null, (Object)anyMatch);
        }
        return Pair.of((Object)best, (Object)true);
    }

    @LuaFunction
    public MethodResult canTrainReach(String destinationFilter) throws LuaException {
        Pair<@Nullable DiscoveredPath, @NotNull Boolean> path = this.findPath(destinationFilter);
        if (path.getFirst() != null) {
            return MethodResult.of((Object[])new Object[]{true, null});
        }
        return MethodResult.of((Object[])new Object[]{false, (Boolean)path.getSecond() != false ? "cannot-reach" : "no-target"});
    }

    @LuaFunction
    public MethodResult distanceTo(String destinationFilter) throws LuaException {
        Pair<@Nullable DiscoveredPath, @NotNull Boolean> path = this.findPath(destinationFilter);
        if (path.getFirst() != null) {
            return MethodResult.of((Object[])new Object[]{((DiscoveredPath)path.getFirst()).distance, null});
        }
        return MethodResult.of((Object[])new Object[]{null, (Boolean)path.getSecond() != false ? "cannot-reach" : "no-target"});
    }

    @NotNull
    private Train getTrainOrThrow() throws LuaException {
        GlobalStation station = ((StationBlockEntity)this.blockEntity).getStation();
        if (station == null) {
            throw new LuaException("station is not connected to a track");
        }
        Train train = station.getPresentTrain();
        if (train == null) {
            throw new LuaException("there is no train present");
        }
        return train;
    }

    @NotNull
    private static CreateLuaTable fromCompoundTag(CompoundTag tag) throws LuaException {
        return (CreateLuaTable)StationPeripheral.fromNBTTag(null, (Tag)tag);
    }

    @NotNull
    private static Object fromNBTTag(@Nullable String key, Tag tag) throws LuaException {
        byte type = tag.getId();
        if (type == 1 && key != null && key.equals("Count")) {
            return ((NumericTag)tag).getAsByte();
        }
        if (type == 1) {
            return ((NumericTag)tag).getAsByte() != 0;
        }
        if (type == 2 || type == 3 || type == 4) {
            return ((NumericTag)tag).getAsLong();
        }
        if (type == 5 || type == 6) {
            return ((NumericTag)tag).getAsDouble();
        }
        if (type == 8) {
            return tag.getAsString();
        }
        if (type == 9 || type == 7 || type == 11 || type == 12) {
            CreateLuaTable list = new CreateLuaTable();
            CollectionTag listTag = (CollectionTag)tag;
            for (int i = 0; i < listTag.size(); ++i) {
                list.put(i + 1, StationPeripheral.fromNBTTag(null, (Tag)listTag.get(i)));
            }
            return list;
        }
        if (type == 10) {
            CreateLuaTable table = new CreateLuaTable();
            CompoundTag compoundTag = (CompoundTag)tag;
            for (String compoundKey : compoundTag.getAllKeys()) {
                table.put(StringHelper.camelCaseToSnakeCase(compoundKey), StationPeripheral.fromNBTTag(compoundKey, compoundTag.get(compoundKey)));
            }
            return table;
        }
        throw new LuaException("unknown tag type " + tag.getType().getName());
    }

    @NotNull
    private static CompoundTag toCompoundTag(CreateLuaTable table) throws LuaException {
        return (CompoundTag)StationPeripheral.toNBTTag(null, table.getMap());
    }

    @NotNull
    private static Tag toNBTTag(@Nullable String key, Object value) throws LuaException {
        Map v;
        if (value instanceof Boolean) {
            Boolean v2 = (Boolean)value;
            return ByteTag.valueOf((boolean)v2);
        }
        if (value instanceof Byte || key != null && key.equals("count")) {
            return ByteTag.valueOf((byte)((Number)value).byteValue());
        }
        if (value instanceof Number) {
            Number v3 = (Number)value;
            if ((double)v3.intValue() == v3.doubleValue()) {
                return IntTag.valueOf((int)v3.intValue());
            }
            return DoubleTag.valueOf((double)v3.doubleValue());
        }
        if (value instanceof String) {
            String v4 = (String)value;
            return StringTag.valueOf((String)v4);
        }
        if (value instanceof Map && (v = (Map)value).containsKey(1.0)) {
            ListTag list = new ListTag();
            for (double i = 1.0; i <= (double)v.size(); i += 1.0) {
                if (v.get(i) == null) continue;
                list.add((Object)StationPeripheral.toNBTTag(null, v.get(i)));
            }
            return list;
        }
        if (value instanceof Map) {
            Map v5 = (Map)value;
            CompoundTag compound = new CompoundTag();
            for (Object objectKey : v5.keySet()) {
                if (!(objectKey instanceof String)) {
                    throw new LuaException("table key is not of type string");
                }
                String compoundKey = (String)objectKey;
                compound.put(compoundKey.equals("id") && v5.containsKey("count") ? "id" : StringHelper.snakeCaseToCamelCase(compoundKey), StationPeripheral.toNBTTag(compoundKey, v5.get(compoundKey)));
            }
            return compound;
        }
        throw new LuaException("unknown object type " + value.getClass().getName());
    }

    @Override
    public void prepareComputerEvent(@NotNull ComputerEvent event) {
        if (event instanceof StationTrainPresenceEvent) {
            StationTrainPresenceEvent stpe = (StationTrainPresenceEvent)event;
            this.queueEvent(stpe.type.name, stpe.train.name.getString());
        }
    }

    @NotNull
    public String getType() {
        return "Create_Station";
    }
}
