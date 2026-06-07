package com.lycoris.loconautics.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;

import net.minecraft.core.Direction;

/**
 * Exposes Create's package-private fields on StationBlockEntity.
 *
 * <p>NOTE: this interface is in the client mixin list for historical reasons but Mixin
 * applies it on both sides (the interface itself is always visible). The @Accessor methods
 * are woven into the class bytecode regardless of which list the mixin is in, so
 * server-side code can safely cast StationBlockEntity to this interface and call them.
 *
 * <p>If this ever causes issues, move the entry from "client" to "mixins" in
 * loconautics.mixins.json — the code does not need any other change.
 */
@Mixin(StationBlockEntity.class)
public interface StationBlockEntityAccessor {

    @Accessor("bogeyCount")
    int loconautics$getBogeyCount();

    @Accessor("assemblyDirection")
    Direction loconautics$getAssemblyDirection();

    @Accessor("assemblyLength")
    int loconautics$getAssemblyLength();

    /** The track-targeting behaviour that points to the actual track block being assembled on. */
    @Accessor("edgePoint")
    TrackTargetingBehaviour<GlobalStation> loconautics$getEdgePoint();

    /**
     * The last assembly exception Create stored on this station block entity.
     * Set this to show an error message in the AssemblyScreen without sending chat.
     */
    @Accessor("lastException")
    void loconautics$setLastException(AssemblyException exception);

    /** Index of the carriage that failed (-1 = no specific carriage). */
    @Accessor("failedCarriageIndex")
    void loconautics$setFailedCarriageIndex(int index);
}