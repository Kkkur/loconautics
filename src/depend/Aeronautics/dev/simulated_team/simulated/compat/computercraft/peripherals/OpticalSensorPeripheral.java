/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaFunction
 *  net.minecraft.core.registries.BuiltInRegistries
 */
package dev.simulated_team.simulated.compat.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dev.simulated_team.simulated.compat.computercraft.peripherals.SimPeripheral;
import dev.simulated_team.simulated.content.blocks.lasers.optical_sensor.OpticalSensorBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;

public class OpticalSensorPeripheral
extends SimPeripheral<OpticalSensorBlockEntity> {
    public OpticalSensorPeripheral(OpticalSensorBlockEntity blockEntity) {
        super(blockEntity);
    }

    public String getType() {
        return "optical_sensor";
    }

    @LuaFunction
    public boolean hasHit() {
        return ((OpticalSensorBlockEntity)this.blockEntity).hasHit();
    }

    @LuaFunction
    public float getDistance() {
        return ((OpticalSensorBlockEntity)this.blockEntity).getHitBlockDistance();
    }

    @LuaFunction
    public String getBlock() {
        return BuiltInRegistries.BLOCK.getKey((Object)((OpticalSensorBlockEntity)this.blockEntity).getHitBlock()).toString();
    }

    @LuaFunction
    public float getRange() {
        return ((OpticalSensorBlockEntity)this.blockEntity).getLaserRange();
    }

    @LuaFunction(mainThread=true)
    public final void setRange(int blocks) {
        ((OpticalSensorBlockEntity)this.blockEntity).setRange(blocks);
    }
}
