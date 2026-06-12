package com.lycoris.loconautics.mixin;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Server-side access to a few non-public {@link StationBlockEntity} internals so the Sable-train assembler can
 * read Create's own bogey scan result and surface failures through Create's standard assembly-error channel
 * (the same {@code lastException} / {@code failedCarriageIndex} fields {@code AssemblyScreen} already renders).
 */
@Mixin(value = StationBlockEntity.class, remap = false)
public interface StationBlockEntityAccessor {

    /** Number of bogeys Create's own {@code refreshAssemblyInfo()} found along the station's rail line. */
    @Accessor("bogeyCount")
    int loconautics$getBogeyCount();

    /**
     * Invokes Create's private {@code exception(AssemblyException, int)}: sets {@code lastException} +
     * {@code failedCarriageIndex} and syncs to the client. Pass {@code (null, -1)} to clear.
     */
    @Invoker("exception")
    void loconautics$exception(AssemblyException exception, int carriage);

    /**
     * Client-side setter for Create's {@code trainPresent} flag. We drive it for parked Sable trains so Create's
     * own station-flag animation + arrival sound play exactly as they do for a Create train (a Sable train and a
     * Create train can never share a station, so this never conflicts with Create's own bookkeeping).
     */
    @Accessor("trainPresent")
    void loconautics$setTrainPresent(boolean value);

    /**
     * Client-side setter for Create's {@code trainCanDisassemble} flag. We set it for a present Sable train so
     * Create's own screen tooltip reads "disassemble train" instead of the "train not aligned" warning it shows
     * when a train is present but Create thinks it can't be disassembled.
     */
    @Accessor("trainCanDisassemble")
    void loconautics$setTrainCanDisassemble(boolean value);
}
