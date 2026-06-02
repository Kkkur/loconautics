/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaFunction
 */
package dev.simulated_team.simulated.compat.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dev.simulated_team.simulated.compat.computercraft.peripherals.SimPeripheral;
import dev.simulated_team.simulated.content.blocks.altitude_sensor.AltitudeSensorBlockEntity;

public class AltitudeSensorPeripheral
extends SimPeripheral<AltitudeSensorBlockEntity> {
    public AltitudeSensorPeripheral(AltitudeSensorBlockEntity blockEntity) {
        super(blockEntity);
    }

    public String getType() {
        return "altitude_sensor";
    }

    @LuaFunction
    public float getHeight() {
        return ((AltitudeSensorBlockEntity)this.blockEntity).getWorldHeight();
    }

    @LuaFunction
    public double getAirPressure() {
        return ((AltitudeSensorBlockEntity)this.blockEntity).getAirPressure();
    }
}
