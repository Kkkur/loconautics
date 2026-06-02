/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.LevelAccessor
 */
package com.simibubi.create.content.trains.station;

import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.station.StationMarker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public interface StationMapData {
    public boolean toggleStation(LevelAccessor var1, BlockPos var2, StationBlockEntity var3);

    public void addStationMarker(StationMarker var1);
}
