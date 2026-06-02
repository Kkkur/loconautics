/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.LuaException
 *  dan200.computercraft.api.lua.LuaFunction
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.compat.computercraft.implementation.peripherals;

import com.simibubi.create.Create;
import com.simibubi.create.compat.computercraft.events.ComputerEvent;
import com.simibubi.create.compat.computercraft.events.SignalStateChangeEvent;
import com.simibubi.create.compat.computercraft.implementation.CreateLuaTable;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.signal.SignalBlock;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalEdgeGroup;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.data.Iterate;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public class SignalPeripheral
extends SyncedPeripheral<SignalBlockEntity> {
    public SignalPeripheral(SignalBlockEntity blockEntity) {
        super(blockEntity);
    }

    @LuaFunction
    public final String getState() {
        return ((SignalBlockEntity)this.blockEntity).getState().toString();
    }

    @LuaFunction
    public final boolean isForcedRed() {
        return (Boolean)((SignalBlockEntity)this.blockEntity).getBlockState().getValue((Property)SignalBlock.POWERED);
    }

    @LuaFunction(mainThread=true)
    public final void setForcedRed(boolean powered) {
        Level level = ((SignalBlockEntity)this.blockEntity).getLevel();
        if (level != null) {
            level.setBlock(((SignalBlockEntity)this.blockEntity).getBlockPos(), (BlockState)((SignalBlockEntity)this.blockEntity).getBlockState().setValue((Property)SignalBlock.POWERED, (Comparable)Boolean.valueOf(powered)), 2);
        }
    }

    @LuaFunction
    public final CreateLuaTable listBlockingTrainNames() throws LuaException {
        SignalBoundary signal = ((SignalBlockEntity)this.blockEntity).getSignal();
        if (signal == null) {
            throw new LuaException("no signal");
        }
        CreateLuaTable trainList = new CreateLuaTable();
        int trainCounter = 1;
        for (boolean current : Iterate.trueAndFalse) {
            Map set = (Map)signal.blockEntities.get(current);
            if (!set.containsKey(((SignalBlockEntity)this.blockEntity).getBlockPos())) continue;
            UUID group = (UUID)signal.groups.get(current);
            Map<UUID, SignalEdgeGroup> signalEdgeGroups = Create.RAILWAYS.signalEdgeGroups;
            SignalEdgeGroup signalEdgeGroup = signalEdgeGroups.get(group);
            for (Train train : signalEdgeGroup.trains) {
                trainList.put(trainCounter, train.name.getString());
                ++trainCounter;
            }
        }
        return trainList;
    }

    @LuaFunction
    public final String getSignalType() throws LuaException {
        SignalBoundary signal = ((SignalBlockEntity)this.blockEntity).getSignal();
        if (signal != null) {
            return signal.getTypeFor(((SignalBlockEntity)this.blockEntity).getBlockPos()).toString();
        }
        throw new LuaException("no signal");
    }

    @LuaFunction(mainThread=true)
    public final void cycleSignalType() throws LuaException {
        SignalBoundary signal = ((SignalBlockEntity)this.blockEntity).getSignal();
        if (signal == null) {
            throw new LuaException("no signal");
        }
        signal.cycleSignalType(((SignalBlockEntity)this.blockEntity).getBlockPos());
    }

    @Override
    public void prepareComputerEvent(@NotNull ComputerEvent event) {
        if (event instanceof SignalStateChangeEvent) {
            SignalStateChangeEvent ssce = (SignalStateChangeEvent)event;
            this.queueEvent("train_signal_state_change", ssce.state.toString());
        }
    }

    @NotNull
    public String getType() {
        return "Create_Signal";
    }
}
