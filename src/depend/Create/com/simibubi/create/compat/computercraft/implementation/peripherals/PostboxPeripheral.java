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
import com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlockEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class PostboxPeripheral
extends SyncedPeripheral<PostboxBlockEntity> {
    public PostboxPeripheral(PostboxBlockEntity blockEntity) {
        super(blockEntity);
    }

    @LuaFunction(mainThread=true)
    public final void setAddress(String address) throws LuaException {
        ((PostboxBlockEntity)this.blockEntity).addressFilter = address;
        ((PostboxBlockEntity)this.blockEntity).filterChanged();
        ((PostboxBlockEntity)this.blockEntity).notifyUpdate();
    }

    @LuaFunction(mainThread=true)
    public final String getAddress() throws LuaException {
        return ((PostboxBlockEntity)this.blockEntity).addressFilter;
    }

    @LuaFunction(mainThread=true)
    public Map<Integer, Map<String, ?>> list() {
        return ComputerUtil.list((IItemHandler)((PostboxBlockEntity)this.blockEntity).inventory);
    }

    @LuaFunction(mainThread=true)
    public Map<String, ?> getItemDetail(int slot) throws LuaException {
        return ComputerUtil.getItemDetail((IItemHandler)((PostboxBlockEntity)this.blockEntity).inventory, slot);
    }

    @LuaFunction(mainThread=true)
    public final String getConfiguration() throws LuaException {
        if (((PostboxBlockEntity)this.blockEntity).target == null) {
            return null;
        }
        if (((PostboxBlockEntity)this.blockEntity).acceptsPackages) {
            return "send_recieve";
        }
        return "send";
    }

    @LuaFunction(mainThread=true)
    public final boolean setConfiguration(String config) throws LuaException {
        if (((PostboxBlockEntity)this.blockEntity).target == null) {
            return false;
        }
        if (config.equals("send_recieve")) {
            ((PostboxBlockEntity)this.blockEntity).acceptsPackages = true;
            ((PostboxBlockEntity)this.blockEntity).filterChanged();
            ((PostboxBlockEntity)this.blockEntity).notifyUpdate();
            return true;
        }
        if (config.equals("send")) {
            ((PostboxBlockEntity)this.blockEntity).acceptsPackages = false;
            ((PostboxBlockEntity)this.blockEntity).filterChanged();
            ((PostboxBlockEntity)this.blockEntity).notifyUpdate();
            return true;
        }
        throw new LuaException("Unknown configuration: \"" + config + "\" Possible configurations are: \"send_recieve\" and \"send\".");
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
        return "Create_Postbox";
    }
}
