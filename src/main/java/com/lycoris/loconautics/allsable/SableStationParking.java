package com.lycoris.loconautics.allsable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.lycoris.loconautics.allsable.SableTrain.StationState;

import com.simibubi.create.content.trains.station.GlobalStation;

/**
 * Sable-side mirror of {@link GlobalStation}'s train slot ({@code reserveFor} / {@code cancelReservation} /
 * {@code trainDeparted} / {@code getPresentTrain}): Create's slot only accepts a Create {@code Train}, so sable
 * trains reserve among themselves here with the same semantics — the approaching train nearest the marker holds
 * the slot, and a parked holder is the station's present train. Consumers: {@link SableTrainDriver} (approach /
 * park / depart), the {@code StationBlockEntityMixin} (drives the station block's "train present" display state),
 * and {@link SableTrainDisassembler} (which train to disassemble).
 */
public final class SableStationParking {

    private SableStationParking() {
    }

    /** Station id → the sable train currently holding its reservation (approaching or parked). */
    private static final Map<UUID, UUID> RESERVATIONS = new HashMap<>();

    private static SableTrain reservationHolder(GlobalStation station) {
        UUID trainId = RESERVATIONS.get(station.id);
        if (trainId == null) {
            return null;
        }
        SableTrain holder = SableTrainRegistry.get(trainId);
        if (holder == null) {
            RESERVATIONS.remove(station.id); // holder despawned
        }
        return holder;
    }

    /** Mirrors {@link GlobalStation#reserveFor}: the approaching train nearest to the marker wins the slot. */
    public static void reserveFor(SableTrain train, GlobalStation station) {
        if (station == null) {
            return;
        }
        SableTrain holder = reservationHolder(station);
        if (holder == null || holder.distanceToStation() > train.distanceToStation()) {
            RESERVATIONS.put(station.id, train.id());
        }
    }

    /** Mirrors {@link GlobalStation#cancelReservation} / {@code trainDeparted}. */
    public static void cancelReservation(SableTrain train, GlobalStation station) {
        if (station == null) {
            return;
        }
        if (train.id().equals(RESERVATIONS.get(station.id))) {
            RESERVATIONS.remove(station.id);
        }
    }

    /** Mirrors {@link GlobalStation#getPresentTrain} for sable trains: the reservation holder parked at it. */
    public static SableTrain presentTrain(GlobalStation station) {
        SableTrain holder = reservationHolder(station);
        if (holder == null || holder.currentStation() != station
                || holder.stationState() != StationState.STOPPED) {
            return null;
        }
        return holder;
    }

    /** {@code Navigation.findNearestApproachable}'s occupancy check: skip stations another train is parked at —
     *  a real Create train ({@link GlobalStation#getPresentTrain}) or another parked sable train. */
    public static boolean occupiedByAnother(SableTrain self, GlobalStation station) {
        if (station.getPresentTrain() != null) {
            return true;
        }
        SableTrain present = presentTrain(station);
        return present != null && present != self;
    }

    /** Re-seats a restored train's reservation (a parked train must hold its station's slot to be "present"
     *  again after a restart — the in-memory map does not survive one). */
    public static void restoreParked(SableTrain train, GlobalStation station) {
        if (station == null) {
            return;
        }
        RESERVATIONS.put(station.id, train.id());
    }
}
