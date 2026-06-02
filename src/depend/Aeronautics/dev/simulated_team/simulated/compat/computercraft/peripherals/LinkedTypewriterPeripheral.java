/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaFunction
 */
package dev.simulated_team.simulated.compat.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dev.simulated_team.simulated.compat.computercraft.peripherals.SimPeripheral;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import java.util.List;

public class LinkedTypewriterPeripheral
extends SimPeripheral<LinkedTypewriterBlockEntity> {
    public LinkedTypewriterPeripheral(LinkedTypewriterBlockEntity blockEntity) {
        super(blockEntity);
    }

    public String getType() {
        return "linked_typewriter";
    }

    @LuaFunction
    public final List<Integer> getPressedKeyCodes() {
        return ((LinkedTypewriterBlockEntity)this.blockEntity).getPressedKeys();
    }
}
