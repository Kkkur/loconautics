/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.peripheral.IComputerAccess
 *  dan200.computercraft.api.peripheral.IPeripheral
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.compat.computercraft.implementation.peripherals;

import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.AttachedComputerPacket;
import com.simibubi.create.compat.computercraft.events.ComputerEvent;
import com.simibubi.create.compat.computercraft.implementation.ComputerBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SyncedPeripheral<T extends SmartBlockEntity>
implements IPeripheral {
    protected final T blockEntity;
    private final List<@NotNull IComputerAccess> computers = new ArrayList<IComputerAccess>();

    public SyncedPeripheral(T blockEntity) {
        this.blockEntity = blockEntity;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void attach(@NotNull IComputerAccess computer) {
        List<IComputerAccess> list = this.computers;
        synchronized (list) {
            this.computers.add(computer);
            if (this.computers.size() == 1) {
                this.onFirstAttach();
            }
            this.updateBlockEntity();
        }
    }

    protected void onFirstAttach() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void detach(@NotNull IComputerAccess computer) {
        List<IComputerAccess> list = this.computers;
        synchronized (list) {
            this.computers.remove(computer);
            this.updateBlockEntity();
            if (this.computers.isEmpty()) {
                this.onLastDetach();
            }
        }
    }

    protected void onLastDetach() {
    }

    private void updateBlockEntity() {
        boolean hasAttachedComputer = !this.computers.isEmpty();
        ((AbstractComputerBehaviour)((SmartBlockEntity)this.blockEntity).getBehaviour(ComputerBehaviour.TYPE)).setHasAttachedComputer(hasAttachedComputer);
        CatnipServices.NETWORK.sendToAllClients((CustomPacketPayload)new AttachedComputerPacket(this.blockEntity.getBlockPos(), hasAttachedComputer));
    }

    public boolean equals(@Nullable IPeripheral other) {
        return this == other;
    }

    public void prepareComputerEvent(@NotNull ComputerEvent event) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void queueEvent(@NotNull String event, Object ... arguments) {
        Object[] sourceAndArgs = new Object[arguments.length + 1];
        System.arraycopy(arguments, 0, sourceAndArgs, 1, arguments.length);
        List<IComputerAccess> list = this.computers;
        synchronized (list) {
            for (IComputerAccess computer : this.computers) {
                sourceAndArgs[0] = computer.getAttachmentName();
                computer.queueEvent(event, sourceAndArgs);
            }
        }
    }
}
