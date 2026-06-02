/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaFunction
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.compat.computercraft.implementation.peripherals;

import com.simibubi.create.compat.computercraft.events.ComputerEvent;
import com.simibubi.create.compat.computercraft.events.KineticsChangeEvent;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.content.kinetics.gauge.SpeedGaugeBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import org.jetbrains.annotations.NotNull;

public class SpeedGaugePeripheral
extends SyncedPeripheral<SpeedGaugeBlockEntity> {
    public SpeedGaugePeripheral(SpeedGaugeBlockEntity blockEntity) {
        super(blockEntity);
    }

    @LuaFunction
    public final float getSpeed() {
        return ((SpeedGaugeBlockEntity)this.blockEntity).getSpeed();
    }

    @Override
    public void prepareComputerEvent(@NotNull ComputerEvent event) {
        if (event instanceof KineticsChangeEvent) {
            KineticsChangeEvent kce = (KineticsChangeEvent)event;
            this.queueEvent("speed_change", Float.valueOf(kce.overStressed ? 0.0f : kce.speed));
        }
    }

    @NotNull
    public String getType() {
        return "Create_Speedometer";
    }
}
