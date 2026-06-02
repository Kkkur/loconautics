/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaFunction
 */
package dev.simulated_team.simulated.compat.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dev.simulated_team.simulated.compat.computercraft.peripherals.SimPeripheral;
import dev.simulated_team.simulated.content.blocks.nameplate.NameplateBlockEntity;

public class NamePlatePeripheral
extends SimPeripheral<NameplateBlockEntity> {
    public NamePlatePeripheral(NameplateBlockEntity blockEntity) {
        super(blockEntity);
    }

    public String getType() {
        return "name_plate";
    }

    @LuaFunction
    public void setName(String newName) {
        ((NameplateBlockEntity)this.blockEntity).setName(newName, true, null);
    }

    @LuaFunction
    public String getName() {
        return ((NameplateBlockEntity)this.blockEntity).getName();
    }
}
