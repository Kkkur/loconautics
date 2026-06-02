/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.compat.computercraft.events;

import com.simibubi.create.compat.computercraft.events.ComputerEvent;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;

public class SignalStateChangeEvent
implements ComputerEvent {
    public SignalBlockEntity.SignalState state;

    public SignalStateChangeEvent(SignalBlockEntity.SignalState state) {
        this.state = state;
    }
}
