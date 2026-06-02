/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.peripheral.PeripheralCapability
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 */
package com.simibubi.create.content.kinetics.transmission.sequencer;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.Instruction;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlock;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import java.util.List;
import java.util.Vector;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class SequencedGearshiftBlockEntity
extends SplitShaftBlockEntity {
    Vector<Instruction> instructions = Instruction.createDefault();
    int currentInstruction = -1;
    int currentInstructionDuration = -1;
    float currentInstructionProgress = 0.0f;
    int timer = 0;
    boolean poweredPreviously = false;
    public AbstractComputerBehaviour computerBehaviour;

    public SequencedGearshiftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (Mods.COMPUTERCRAFT.isLoaded()) {
            event.registerBlockEntity(PeripheralCapability.get(), (BlockEntityType)AllBlockEntityTypes.SEQUENCED_GEARSHIFT.get(), (be, context) -> be.computerBehaviour.getPeripheralCapability());
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.computerBehaviour = ComputerCraftProxy.behaviour(this);
        behaviours.add(this.computerBehaviour);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isIdle()) {
            return;
        }
        if (this.level.isClientSide) {
            return;
        }
        if (this.currentInstructionDuration < 0) {
            return;
        }
        if (this.timer < this.currentInstructionDuration) {
            ++this.timer;
            this.currentInstructionProgress += this.getInstruction(this.currentInstruction).getTickProgress(this.speed);
            return;
        }
        this.run(this.currentInstruction + 1);
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        if (this.isIdle()) {
            return;
        }
        float currentSpeed = Math.abs(this.speed);
        if (Math.abs(previousSpeed) == currentSpeed) {
            return;
        }
        Instruction instruction = this.getInstruction(this.currentInstruction);
        if (instruction == null) {
            return;
        }
        if (this.getSpeed() == 0.0f) {
            this.run(-1);
        }
        this.currentInstructionDuration = instruction.getDuration(this.currentInstructionProgress, this.getTheoreticalSpeed());
        this.timer = 0;
    }

    public boolean isIdle() {
        return this.currentInstruction == -1;
    }

    public void onRedstoneUpdate(boolean isPowered, boolean isRunning) {
        if (this.computerBehaviour.hasAttachedComputer()) {
            return;
        }
        if (!this.poweredPreviously && isPowered) {
            this.risingFlank();
        }
        this.poweredPreviously = isPowered;
        if (!this.isIdle()) {
            return;
        }
        if (isPowered == isRunning) {
            return;
        }
        if (!this.level.hasNeighborSignal(this.worldPosition)) {
            this.level.setBlock(this.worldPosition, (BlockState)this.getBlockState().setValue((Property)SequencedGearshiftBlock.STATE, (Comparable)Integer.valueOf(0)), 3);
            return;
        }
        if (this.getSpeed() == 0.0f) {
            return;
        }
        this.run(0);
    }

    public void risingFlank() {
        Instruction instruction = this.getInstruction(this.currentInstruction);
        if (instruction == null) {
            return;
        }
        if (this.poweredPreviously) {
            return;
        }
        this.poweredPreviously = true;
        switch (instruction.onRedstonePulse()) {
            case CONTINUE: {
                this.run(this.currentInstruction + 1);
                break;
            }
        }
    }

    public void run(int instructionIndex) {
        Instruction instruction = this.getInstruction(instructionIndex);
        if (instruction == null || instruction.instruction == SequencerInstructions.END) {
            if (this.getModifier() != 0) {
                this.detachKinetics();
            }
            this.currentInstruction = -1;
            this.currentInstructionDuration = -1;
            this.currentInstructionProgress = 0.0f;
            this.sequenceContext = null;
            this.timer = 0;
            if (!this.level.hasNeighborSignal(this.worldPosition)) {
                this.level.setBlock(this.worldPosition, (BlockState)this.getBlockState().setValue((Property)SequencedGearshiftBlock.STATE, (Comparable)Integer.valueOf(0)), 3);
            } else {
                this.sendData();
            }
            return;
        }
        this.detachKinetics();
        this.currentInstructionDuration = instruction.getDuration(0.0f, this.getTheoreticalSpeed());
        this.currentInstruction = instructionIndex;
        this.currentInstructionProgress = 0.0f;
        this.sequenceContext = SequenceContext.fromGearshift(instruction.instruction, this.getTheoreticalSpeed() * (float)this.getModifier(), instruction.value);
        this.timer = 0;
        this.level.setBlock(this.worldPosition, (BlockState)this.getBlockState().setValue((Property)SequencedGearshiftBlock.STATE, (Comparable)Integer.valueOf(instructionIndex + 1)), 3);
    }

    public Instruction getInstruction(int instructionIndex) {
        return instructionIndex >= 0 && instructionIndex < this.instructions.size() ? this.instructions.get(instructionIndex) : null;
    }

    @Override
    protected void copySequenceContextFrom(KineticBlockEntity sourceBE) {
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("InstructionIndex", this.currentInstruction);
        compound.putInt("InstructionDuration", this.currentInstructionDuration);
        compound.putFloat("InstructionProgress", this.currentInstructionProgress);
        compound.putInt("Timer", this.timer);
        compound.putBoolean("PrevPowered", this.poweredPreviously);
        compound.put("Instructions", (Tag)Instruction.serializeAll(this.instructions));
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.currentInstruction = compound.getInt("InstructionIndex");
        this.currentInstructionDuration = compound.getInt("InstructionDuration");
        this.currentInstructionProgress = compound.getFloat("InstructionProgress");
        this.poweredPreviously = compound.getBoolean("PrevPowered");
        this.timer = compound.getInt("Timer");
        this.instructions = Instruction.deserializeAll(compound.getList("Instructions", 10));
        super.read(compound, registries, clientPacket);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.computerBehaviour.removePeripheral();
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (this.isVirtual()) {
            return 1.0f;
        }
        return !this.hasSource() || face == this.getSourceFacing() ? 1.0f : (float)this.getModifier();
    }

    public int getModifier() {
        if (this.currentInstruction >= this.instructions.size()) {
            return 0;
        }
        return this.isIdle() ? 0 : this.instructions.get(this.currentInstruction).getSpeedModifier();
    }

    public Vector<Instruction> getInstructions() {
        return this.instructions;
    }

    public record SequenceContext(SequencerInstructions instruction, double relativeValue) {
        public static SequenceContext fromGearshift(SequencerInstructions instruction, double kineticSpeed, int absoluteValue) {
            return instruction.needsPropagation() ? new SequenceContext(instruction, kineticSpeed == 0.0 ? 0.0 : (double)absoluteValue / kineticSpeed) : null;
        }

        public double getEffectiveValue(double speedAtTarget) {
            return Math.abs(this.relativeValue * speedAtTarget);
        }

        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            NBTHelper.writeEnum((CompoundTag)nbt, (String)"Mode", (Enum)this.instruction);
            nbt.putDouble("Value", this.relativeValue);
            return nbt;
        }

        public static SequenceContext fromNBT(CompoundTag nbt) {
            if (nbt.isEmpty()) {
                return null;
            }
            return new SequenceContext((SequencerInstructions)NBTHelper.readEnum((CompoundTag)nbt, (String)"Mode", SequencerInstructions.class), nbt.getDouble("Value"));
        }
    }
}
