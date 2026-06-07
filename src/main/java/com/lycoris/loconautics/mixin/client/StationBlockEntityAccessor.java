package com.lycoris.loconautics.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.content.trains.station.StationBlockEntity;

import net.minecraft.core.Direction;

/**
 * Exposes Create's package-private fields on StationBlockEntity.
 */
@Mixin(StationBlockEntity.class)
public interface StationBlockEntityAccessor {

    @Accessor("bogeyCount")
    int loconautics$getBogeyCount();

    @Accessor("assemblyDirection")
    Direction loconautics$getAssemblyDirection();

    @Accessor("assemblyLength")
    int loconautics$getAssemblyLength();
}