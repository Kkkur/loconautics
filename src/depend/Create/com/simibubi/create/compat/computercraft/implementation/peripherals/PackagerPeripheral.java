/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaException
 *  dan200.computercraft.api.lua.LuaFunction
 *  dan200.computercraft.api.peripheral.IComputerAccess
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.IItemHandler
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.compat.computercraft.implementation.peripherals;

import com.simibubi.create.compat.computercraft.events.ComputerEvent;
import com.simibubi.create.compat.computercraft.events.PackageEvent;
import com.simibubi.create.compat.computercraft.implementation.ComputerUtil;
import com.simibubi.create.compat.computercraft.implementation.luaObjects.PackageLuaObject;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import java.util.Map;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class PackagerPeripheral
extends SyncedPeripheral<PackagerBlockEntity> {
    public PackagerPeripheral(PackagerBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        super.attach(computer);
        ((PackagerBlockEntity)this.blockEntity).hasCustomComputerAddress = false;
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        super.detach(computer);
        ((PackagerBlockEntity)this.blockEntity).hasCustomComputerAddress = false;
    }

    @LuaFunction(mainThread=true)
    public final boolean makePackage() {
        if (!((PackagerBlockEntity)this.blockEntity).heldBox.isEmpty()) {
            return false;
        }
        ((PackagerBlockEntity)this.blockEntity).activate();
        return !((PackagerBlockEntity)this.blockEntity).heldBox.isEmpty();
    }

    @LuaFunction(mainThread=true)
    public Map<Integer, Map<String, ?>> list() {
        return ComputerUtil.list((IItemHandler)((PackagerBlockEntity)this.blockEntity).targetInventory.getInventory());
    }

    @LuaFunction(mainThread=true)
    public Map<String, ?> getItemDetail(int slot) throws LuaException {
        return ComputerUtil.getItemDetail((IItemHandler)((PackagerBlockEntity)this.blockEntity).targetInventory.getInventory(), slot);
    }

    @LuaFunction(mainThread=true)
    public final String getAddress() {
        ((PackagerBlockEntity)this.blockEntity).updateSignAddress();
        return ((PackagerBlockEntity)this.blockEntity).signBasedAddress;
    }

    @LuaFunction(mainThread=true)
    public final void setAddress(Optional<String> argument) {
        if (argument.isPresent()) {
            ((PackagerBlockEntity)this.blockEntity).customComputerAddress = argument.get();
            ((PackagerBlockEntity)this.blockEntity).signBasedAddress = argument.get();
            ((PackagerBlockEntity)this.blockEntity).hasCustomComputerAddress = true;
        } else {
            ((PackagerBlockEntity)this.blockEntity).customComputerAddress = "";
            ((PackagerBlockEntity)this.blockEntity).hasCustomComputerAddress = false;
        }
    }

    @LuaFunction(mainThread=true)
    public final PackageLuaObject getPackage() {
        ItemStack box = ((PackagerBlockEntity)this.blockEntity).heldBox;
        if (box.isEmpty()) {
            return null;
        }
        return new PackageLuaObject((PackagerBlockEntity)this.blockEntity, box);
    }

    @Override
    public void prepareComputerEvent(@NotNull ComputerEvent event) {
        if (event instanceof PackageEvent) {
            PackageEvent pe = (PackageEvent)event;
            this.queueEvent(pe.status, new PackageLuaObject((PackagerBlockEntity)this.blockEntity, pe.box));
        }
    }

    @NotNull
    public String getType() {
        return "Create_Packager";
    }
}
