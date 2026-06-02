/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.compat.computercraft.events;

import com.simibubi.create.compat.computercraft.events.ComputerEvent;
import com.simibubi.create.content.trains.entity.Train;
import org.jetbrains.annotations.NotNull;

public class StationTrainPresenceEvent
implements ComputerEvent {
    public Type type;
    @NotNull
    public Train train;

    public StationTrainPresenceEvent(Type type, @NotNull Train train) {
        this.type = type;
        this.train = train;
    }

    public static enum Type {
        IMMINENT("train_imminent"),
        ARRIVAL("train_arrival"),
        DEPARTURE("train_departure");

        public final String name;

        private Type(String name) {
            this.name = name;
        }
    }
}
