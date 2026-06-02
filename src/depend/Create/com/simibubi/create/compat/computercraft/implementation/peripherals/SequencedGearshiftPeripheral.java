/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.lua.IArguments
 *  dan200.computercraft.api.lua.LuaException
 *  dan200.computercraft.api.lua.LuaFunction
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.compat.computercraft.implementation.peripherals;

import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.content.kinetics.transmission.sequencer.Instruction;
import com.simibubi.create.content.kinetics.transmission.sequencer.InstructionSpeedModifiers;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import org.jetbrains.annotations.NotNull;

public class SequencedGearshiftPeripheral
extends SyncedPeripheral<SequencedGearshiftBlockEntity> {
    public SequencedGearshiftPeripheral(SequencedGearshiftBlockEntity blockEntity) {
        super(blockEntity);
    }

    @LuaFunction(mainThread=true)
    public final void rotate(IArguments arguments) throws LuaException {
        this.runInstruction(arguments, SequencerInstructions.TURN_ANGLE);
    }

    @LuaFunction(mainThread=true)
    public final void move(IArguments arguments) throws LuaException {
        this.runInstruction(arguments, SequencerInstructions.TURN_DISTANCE);
    }

    @LuaFunction
    public final boolean isRunning() {
        return !((SequencedGearshiftBlockEntity)this.blockEntity).isIdle();
    }

    private void runInstruction(IArguments arguments, SequencerInstructions instructionType) throws LuaException {
        int speedModifier = arguments.count() > 1 ? arguments.getInt(1) : 1;
        ((SequencedGearshiftBlockEntity)this.blockEntity).getInstructions().clear();
        ((SequencedGearshiftBlockEntity)this.blockEntity).getInstructions().add(new Instruction(instructionType, InstructionSpeedModifiers.getByModifier(speedModifier), Math.abs(arguments.getInt(0))));
        ((SequencedGearshiftBlockEntity)this.blockEntity).getInstructions().add(new Instruction(SequencerInstructions.END));
        ((SequencedGearshiftBlockEntity)this.blockEntity).run(0);
    }

    @NotNull
    public String getType() {
        return "Create_SequencedGearshift";
    }
}
