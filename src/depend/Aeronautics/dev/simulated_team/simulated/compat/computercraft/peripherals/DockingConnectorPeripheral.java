/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaFunction
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.minecraft.core.Vec3i
 */
package dev.simulated_team.simulated.compat.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.compat.computercraft.peripherals.SimPeripheral;
import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity;
import net.minecraft.core.Vec3i;

public class DockingConnectorPeripheral
extends SimPeripheral<DockingConnectorBlockEntity> {
    public DockingConnectorPeripheral(DockingConnectorBlockEntity blockEntity) {
        super(blockEntity);
    }

    public String getType() {
        return "docking_connector";
    }

    @LuaFunction
    public String getConnectedName() {
        SubLevel subLevel;
        if (((DockingConnectorBlockEntity)this.blockEntity).otherConnectorPosition != null && (subLevel = Sable.HELPER.getContaining(((DockingConnectorBlockEntity)this.blockEntity).getLevel(), (Vec3i)((DockingConnectorBlockEntity)this.blockEntity).otherConnectorPosition)) != null && subLevel.getName() != null) {
            return subLevel.getName();
        }
        return "";
    }
}
