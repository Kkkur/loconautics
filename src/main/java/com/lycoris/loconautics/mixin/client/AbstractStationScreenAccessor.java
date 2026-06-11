package com.lycoris.loconautics.mixin.client;

import com.simibubi.create.content.trains.station.AbstractStationScreen;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractStationScreen.class, remap = false)
// remap=false: targets a Create (mod) class whose fields are not Minecraft-remapped.
public interface AbstractStationScreenAccessor {
    @Accessor("blockEntity")
    StationBlockEntity loconautics$getBlockEntity();

    @Accessor("station")
    GlobalStation loconautics$getStation();
}
