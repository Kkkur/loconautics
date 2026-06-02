/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaFunction
 */
package dev.simulated_team.simulated.compat.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dev.simulated_team.simulated.compat.computercraft.peripherals.SimPeripheral;
import dev.simulated_team.simulated.content.blocks.gimbal_sensor.GimbalSensorBlockEntity;
import java.util.List;

public class GimbalSensorPeripheral
extends SimPeripheral<GimbalSensorBlockEntity> {
    public GimbalSensorPeripheral(GimbalSensorBlockEntity blockEntity) {
        super(blockEntity);
    }

    public String getType() {
        return "gimbal_sensor";
    }

    @LuaFunction
    public List<Double> getAngles() {
        return List.of(Double.valueOf(Math.toDegrees(((GimbalSensorBlockEntity)this.blockEntity).getXAngle())), Double.valueOf(Math.toDegrees(((GimbalSensorBlockEntity)this.blockEntity).getZAngle())));
    }

    @LuaFunction
    public List<Double> getAnglesRad() {
        return List.of(Double.valueOf(((GimbalSensorBlockEntity)this.blockEntity).getXAngle()), Double.valueOf(((GimbalSensorBlockEntity)this.blockEntity).getZAngle()));
    }
}
