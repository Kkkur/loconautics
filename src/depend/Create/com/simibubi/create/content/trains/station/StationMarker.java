/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.saveddata.maps.MapDecoration
 */
package com.simibubi.create.content.trains.station;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllMapDecorationTypes;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.Objects;
import java.util.Optional;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.maps.MapDecoration;

public class StationMarker {
    private final BlockPos source;
    private final BlockPos target;
    private final Component name;
    private final String id;

    public StationMarker(BlockPos source, BlockPos target, Component name) {
        this.source = source;
        this.target = target;
        this.name = name;
        this.id = "create:station-" + target.getX() + "," + target.getY() + "," + target.getZ();
    }

    public static StationMarker load(CompoundTag tag, HolderLookup.Provider registries) {
        BlockPos source = NBTHelper.readBlockPos((CompoundTag)tag, (String)"source");
        BlockPos target = NBTHelper.readBlockPos((CompoundTag)tag, (String)"target");
        MutableComponent name = Component.Serializer.fromJson((String)tag.getString("name"), (HolderLookup.Provider)registries);
        if (name == null) {
            name = CommonComponents.EMPTY;
        }
        return new StationMarker(source, target, (Component)name);
    }

    public static StationMarker fromWorld(BlockGetter level, BlockPos pos) {
        Optional stationOption = AllBlockEntityTypes.TRACK_STATION.get(level, pos);
        if (stationOption.isEmpty() || ((StationBlockEntity)stationOption.get()).getStation() == null) {
            return null;
        }
        String name = ((StationBlockEntity)stationOption.get()).getStation().name;
        return new StationMarker(pos, BlockEntityBehaviour.get((BlockEntity)stationOption.get(), TrackTargetingBehaviour.TYPE).getPositionForMapMarker(), (Component)Component.literal((String)name));
    }

    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("source", NbtUtils.writeBlockPos((BlockPos)this.source));
        tag.put("target", NbtUtils.writeBlockPos((BlockPos)this.target));
        tag.putString("name", Component.Serializer.toJson((Component)this.name, (HolderLookup.Provider)registries));
        return tag;
    }

    public BlockPos getSource() {
        return this.source;
    }

    public BlockPos getTarget() {
        return this.target;
    }

    public Component getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        StationMarker that = (StationMarker)o;
        if (!this.target.equals((Object)that.target)) {
            return false;
        }
        return this.name.equals((Object)that.name);
    }

    public int hashCode() {
        return Objects.hash(this.target, this.name);
    }

    public static MapDecoration createStationDecoration(byte x, byte y, Optional<Component> name) {
        return new MapDecoration(AllMapDecorationTypes.STATION_MAP_DECORATION, x, y, 0, name);
    }
}
