/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaFunction
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.compat.computercraft.implementation.peripherals;

import com.simibubi.create.Create;
import com.simibubi.create.compat.computercraft.events.ComputerEvent;
import com.simibubi.create.compat.computercraft.events.TrainPassEvent;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.observer.TrackObserverBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrackObserverPeripheral
extends SyncedPeripheral<TrackObserverBlockEntity> {
    public TrackObserverPeripheral(TrackObserverBlockEntity blockEntity) {
        super(blockEntity);
    }

    @LuaFunction
    public boolean isTrainPassing() {
        return Create.RAILWAYS.trains.containsKey(((TrackObserverBlockEntity)this.blockEntity).passingTrainUUID);
    }

    @LuaFunction
    @Nullable
    public String getPassingTrainName() {
        Train train = Create.RAILWAYS.trains.get(((TrackObserverBlockEntity)this.blockEntity).passingTrainUUID);
        return train == null ? null : train.name.getString();
    }

    @Override
    public void prepareComputerEvent(@NotNull ComputerEvent event) {
        if (event instanceof TrainPassEvent) {
            TrainPassEvent tpe = (TrainPassEvent)event;
            this.queueEvent(tpe.passing ? "train_passing" : "train_passed", tpe.train.name.getString());
        }
    }

    @NotNull
    public String getType() {
        return "Create_TrainObserver";
    }
}
