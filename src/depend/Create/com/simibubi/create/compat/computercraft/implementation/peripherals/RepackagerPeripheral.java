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
import com.simibubi.create.compat.computercraft.events.RepackageEvent;
import com.simibubi.create.compat.computercraft.implementation.ComputerUtil;
import com.simibubi.create.compat.computercraft.implementation.luaObjects.PackageLuaObject;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.repackager.RepackagerBlockEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import java.util.Map;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class RepackagerPeripheral
extends SyncedPeripheral<RepackagerBlockEntity> {
    public RepackagerPeripheral(RepackagerBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        super.attach(computer);
        ((RepackagerBlockEntity)this.blockEntity).hasCustomComputerAddress = false;
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        super.detach(computer);
        ((RepackagerBlockEntity)this.blockEntity).hasCustomComputerAddress = false;
    }

    @LuaFunction(mainThread=true)
    public final boolean makePackage() {
        if (!((RepackagerBlockEntity)this.blockEntity).heldBox.isEmpty()) {
            return false;
        }
        ((RepackagerBlockEntity)this.blockEntity).activate();
        return !((RepackagerBlockEntity)this.blockEntity).heldBox.isEmpty();
    }

    @LuaFunction(mainThread=true)
    public Map<Integer, Map<String, ?>> list() {
        return ComputerUtil.list((IItemHandler)((RepackagerBlockEntity)this.blockEntity).targetInventory.getInventory());
    }

    @LuaFunction(mainThread=true)
    public Map<String, ?> getItemDetail(int slot) throws LuaException {
        return ComputerUtil.getItemDetail((IItemHandler)((RepackagerBlockEntity)this.blockEntity).targetInventory.getInventory(), slot);
    }

    @LuaFunction(mainThread=true)
    public final String getAddress() {
        ((RepackagerBlockEntity)this.blockEntity).updateSignAddress();
        return ((RepackagerBlockEntity)this.blockEntity).signBasedAddress;
    }

    @LuaFunction(mainThread=true)
    public final void setAddress(Optional<String> argument) {
        if (argument.isPresent()) {
            ((RepackagerBlockEntity)this.blockEntity).customComputerAddress = argument.get();
            ((RepackagerBlockEntity)this.blockEntity).signBasedAddress = argument.get();
            ((RepackagerBlockEntity)this.blockEntity).hasCustomComputerAddress = true;
        } else {
            ((RepackagerBlockEntity)this.blockEntity).customComputerAddress = "";
            ((RepackagerBlockEntity)this.blockEntity).hasCustomComputerAddress = false;
        }
    }

    @LuaFunction(mainThread=true)
    public final PackageLuaObject getPackage() {
        ItemStack box = ((RepackagerBlockEntity)this.blockEntity).heldBox;
        if (box.isEmpty()) {
            return null;
        }
        return new PackageLuaObject((PackagerBlockEntity)this.blockEntity, box);
    }

    @Override
    public void prepareComputerEvent(@NotNull ComputerEvent event) {
        if (event instanceof RepackageEvent) {
            RepackageEvent pe = (RepackageEvent)event;
            this.queueEvent("package_repackaged", new PackageLuaObject((PackagerBlockEntity)this.blockEntity, pe.box), pe.count);
        } else if (event instanceof PackageEvent) {
            PackageEvent pe = (PackageEvent)event;
            this.queueEvent(pe.status, new PackageLuaObject((PackagerBlockEntity)this.blockEntity, pe.box));
        }
    }

    @NotNull
    public String getType() {
        return "Create_Repackager";
    }
}
