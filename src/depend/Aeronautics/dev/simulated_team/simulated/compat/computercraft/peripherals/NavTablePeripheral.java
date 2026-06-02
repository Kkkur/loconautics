/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaFunction
 */
package dev.simulated_team.simulated.compat.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dev.simulated_team.simulated.compat.computercraft.peripherals.SimPeripheral;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;

public class NavTablePeripheral
extends SimPeripheral<NavTableBlockEntity> {
    public NavTablePeripheral(NavTableBlockEntity blockEntity) {
        super(blockEntity);
    }

    public String getType() {
        return "navigation_table";
    }

    @LuaFunction
    public Float getRelativeAngle() {
        return Float.valueOf(((NavTableBlockEntity)this.blockEntity).getRelativeAngle());
    }

    @LuaFunction
    public double getRelativeAngleRad() {
        return Math.toRadians(((NavTableBlockEntity)this.blockEntity).getRelativeAngle());
    }
}
