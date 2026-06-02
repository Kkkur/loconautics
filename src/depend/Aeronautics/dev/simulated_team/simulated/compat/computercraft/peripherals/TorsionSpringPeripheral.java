/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaFunction
 */
package dev.simulated_team.simulated.compat.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dev.simulated_team.simulated.compat.computercraft.peripherals.SimPeripheral;
import dev.simulated_team.simulated.content.blocks.torsion_spring.TorsionSpringBlockEntity;

public class TorsionSpringPeripheral
extends SimPeripheral<TorsionSpringBlockEntity> {
    public TorsionSpringPeripheral(TorsionSpringBlockEntity blockEntity) {
        super(blockEntity);
    }

    public String getType() {
        return "torsion_spring";
    }

    @LuaFunction
    public void setLimit(int limit) {
        if (((TorsionSpringBlockEntity)this.blockEntity).isSpringStatic()) {
            ((TorsionSpringBlockEntity)this.blockEntity).angleInput.setValue(limit);
        }
    }

    @LuaFunction
    public float getAngle() {
        return ((TorsionSpringBlockEntity)this.blockEntity).getAngle();
    }

    @LuaFunction
    public double getAngleRad() {
        return Math.toRadians(((TorsionSpringBlockEntity)this.blockEntity).getAngle());
    }

    @LuaFunction
    public int getLimit() {
        return ((TorsionSpringBlockEntity)this.blockEntity).angleInput.getValue();
    }

    @LuaFunction
    public boolean isRunning() {
        return !((TorsionSpringBlockEntity)this.blockEntity).isSpringStatic();
    }
}
