/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaFunction
 */
package dev.simulated_team.simulated.compat.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dev.simulated_team.simulated.compat.computercraft.peripherals.SimPeripheral;
import dev.simulated_team.simulated.content.blocks.velocity_sensor.VelocitySensorBlockEntity;

public class VelocitySensorPeripheral
extends SimPeripheral<VelocitySensorBlockEntity> {
    public VelocitySensorPeripheral(VelocitySensorBlockEntity blockEntity) {
        super(blockEntity);
    }

    public String getType() {
        return "velocity_sensor";
    }

    @LuaFunction
    public float getVelocity() {
        return ((VelocitySensorBlockEntity)this.blockEntity).getAdjustedVelocity();
    }
}
