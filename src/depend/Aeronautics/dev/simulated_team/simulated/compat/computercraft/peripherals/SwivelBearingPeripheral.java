/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaFunction
 */
package dev.simulated_team.simulated.compat.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dev.simulated_team.simulated.compat.computercraft.peripherals.SimPeripheral;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.SwivelBearingBlockEntity;

public class SwivelBearingPeripheral
extends SimPeripheral<SwivelBearingBlockEntity> {
    public SwivelBearingPeripheral(SwivelBearingBlockEntity blockEntity) {
        super(blockEntity);
    }

    public String getType() {
        return "swivel_bearing";
    }

    @LuaFunction
    public double getTargetAngle() {
        return ((SwivelBearingBlockEntity)this.blockEntity).getTargetAngleDegrees();
    }

    @LuaFunction
    public double getTargetAngleRad() {
        return Math.toRadians(((SwivelBearingBlockEntity)this.blockEntity).getTargetAngleDegrees());
    }
}
