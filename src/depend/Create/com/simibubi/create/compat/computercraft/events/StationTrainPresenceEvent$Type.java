/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.compat.computercraft.events;

public static enum StationTrainPresenceEvent.Type {
    IMMINENT("train_imminent"),
    ARRIVAL("train_arrival"),
    DEPARTURE("train_departure");

    public final String name;

    private StationTrainPresenceEvent.Type(String name) {
        this.name = name;
    }
}
