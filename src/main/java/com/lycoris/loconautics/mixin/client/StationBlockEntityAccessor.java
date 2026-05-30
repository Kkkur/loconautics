package com.lycoris.loconautics.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.content.trains.station.StationBlockEntity;

/**
 * Exposes Create's package-private {@code StationBlockEntity.bogeyCount} so the assembly screen
 * mixin can grey out our button exactly like the vanilla assemble button (active only when there
 * are bogeys to assemble).
 */
@Mixin(StationBlockEntity.class)
public interface StationBlockEntityAccessor {

    @Accessor("bogeyCount")
    int loconautics$getBogeyCount();
}
