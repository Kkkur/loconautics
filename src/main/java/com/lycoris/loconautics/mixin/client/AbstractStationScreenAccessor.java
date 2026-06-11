package com.lycoris.loconautics.mixin.client;

import com.simibubi.create.content.trains.station.AbstractStationScreen;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractStationScreen.class, remap = false)
public interface AbstractStationScreenAccessor {
    @Accessor("blockEntity")
    StationBlockEntity loconautics$getBlockEntity();
}
