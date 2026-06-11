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
}
