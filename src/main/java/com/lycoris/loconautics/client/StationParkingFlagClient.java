package com.lycoris.loconautics.client;

import java.util.HashSet;
import java.util.Set;

import com.lycoris.loconautics.allsable.SableTrainClientRegistry;
import com.lycoris.loconautics.mixin.StationBlockEntityAccessor;

import com.simibubi.create.content.trains.station.StationBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Drives Create's station-flag animation for stations where a Sable train is parked. Each client tick we push
 * {@code trainPresent = true} onto the loaded {@link StationBlockEntity} for every parked station (and {@code false}
 * when it stops being parked). Create's own client-side tick then raises/lowers the flag and plays the arrival
 * sound exactly as it does for a Create train — we don't touch the animation itself, only the flag input.
 *
 * <p>Set before the level tick (from {@code ClientTickEvent.Pre}) so Create's block-entity tick reads our value the
 * same frame.
 */
@OnlyIn(Dist.CLIENT)
public final class StationParkingFlagClient {

    /** Stations we have currently forced present, so we can clear the flag once they stop being parked. */
    private static final Set<BlockPos> FORCED = new HashSet<>();

    private StationParkingFlagClient() {
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            FORCED.clear();
            return;
        }
        Set<BlockPos> parked = SableTrainClientRegistry.parkedStations();

        for (BlockPos pos : parked) {
            if (mc.level.getBlockEntity(pos) instanceof StationBlockEntity be) {
                StationBlockEntityAccessor acc = (StationBlockEntityAccessor) be;
                acc.loconautics$setTrainPresent(true);        // raises Create's station flag
                acc.loconautics$setTrainCanDisassemble(true);  // makes Create's tooltip read "disassemble"
                FORCED.add(pos.immutable());
            }
        }
        // Clear the flags on stations that are no longer parked (and forget them).
        FORCED.removeIf(pos -> {
            if (parked.contains(pos)) {
                return false;
            }
            if (mc.level.getBlockEntity(pos) instanceof StationBlockEntity be) {
                StationBlockEntityAccessor acc = (StationBlockEntityAccessor) be;
                acc.loconautics$setTrainPresent(false);
                acc.loconautics$setTrainCanDisassemble(false);
            }
            return true;
        });
    }

    public static void clear() {
        FORCED.clear();
    }
}
