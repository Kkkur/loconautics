/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaException
 *  dan200.computercraft.api.lua.LuaFunction
 *  net.neoforged.neoforge.items.IItemHandler
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.compat.computercraft.implementation.peripherals;

import com.simibubi.create.compat.computercraft.events.ComputerEvent;
import com.simibubi.create.compat.computercraft.events.PackageEvent;
import com.simibubi.create.compat.computercraft.implementation.ComputerUtil;
import com.simibubi.create.compat.computercraft.implementation.luaObjects.PackageLuaObject;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class FrogportPeripheral
extends SyncedPeripheral<FrogportBlockEntity> {
    public FrogportPeripheral(FrogportBlockEntity blockEntity) {
        super(blockEntity);
    }

    @LuaFunction(mainThread=true)
    public final void setAddress(String address) throws LuaException {
        ((FrogportBlockEntity)this.blockEntity).addressFilter = address;
        ((FrogportBlockEntity)this.blockEntity).filterChanged();
        ((FrogportBlockEntity)this.blockEntity).notifyUpdate();
    }

    @LuaFunction(mainThread=true)
    public final String getAddress() throws LuaException {
        return ((FrogportBlockEntity)this.blockEntity).addressFilter;
    }

    @LuaFunction(mainThread=true)
    public final String getConfiguration() throws LuaException {
        if (((FrogportBlockEntity)this.blockEntity).target == null) {
            return null;
        }
        if (((FrogportBlockEntity)this.blockEntity).acceptsPackages) {
            return "send_recieve";
        }
        return "send";
    }

    @LuaFunction(mainThread=true)
    public final boolean setConfiguration(String config) throws LuaException {
        if (((FrogportBlockEntity)this.blockEntity).target == null) {
            return false;
        }
        if (config.equals("send_recieve")) {
            ((FrogportBlockEntity)this.blockEntity).acceptsPackages = true;
            ((FrogportBlockEntity)this.blockEntity).filterChanged();
            ((FrogportBlockEntity)this.blockEntity).notifyUpdate();
            return true;
        }
        if (config.equals("send")) {
            ((FrogportBlockEntity)this.blockEntity).acceptsPackages = false;
            ((FrogportBlockEntity)this.blockEntity).filterChanged();
            ((FrogportBlockEntity)this.blockEntity).notifyUpdate();
            return true;
        }
        throw new LuaException("Unknown configuration: \"" + config + "\" Possible configurations are: \"send_recieve\" and \"send\".");
    }

    @LuaFunction(mainThread=true)
    public Map<Integer, Map<String, ?>> list() {
        return ComputerUtil.list((IItemHandler)((FrogportBlockEntity)this.blockEntity).inventory);
    }

    @LuaFunction(mainThread=true)
    public Map<String, ?> getItemDetail(int slot) throws LuaException {
        return ComputerUtil.getItemDetail((IItemHandler)((FrogportBlockEntity)this.blockEntity).inventory, slot);
    }

    @Override
    public void prepareComputerEvent(@NotNull ComputerEvent event) {
        if (event instanceof PackageEvent) {
            PackageEvent pe = (PackageEvent)event;
            this.queueEvent(pe.status, new PackageLuaObject(null, pe.box));
        }
    }

    @NotNull
    public String getType() {
        return "Create_Frogport";
    }
}
