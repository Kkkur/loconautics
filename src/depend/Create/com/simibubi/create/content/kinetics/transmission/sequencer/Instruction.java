/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.kinetics.transmission.sequencer;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.InstructionSpeedModifiers;
import com.simibubi.create.content.kinetics.transmission.sequencer.OnIsPoweredResult;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Vector;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class Instruction {
    public static final StreamCodec<ByteBuf, Instruction> STREAM_CODEC = StreamCodec.composite(SequencerInstructions.STREAM_CODEC, instruction -> instruction.instruction, InstructionSpeedModifiers.STREAM_CODEC, instruction -> instruction.speedModifier, (StreamCodec)ByteBufCodecs.VAR_INT, instruction -> instruction.value, Instruction::new);
    SequencerInstructions instruction;
    InstructionSpeedModifiers speedModifier;
    int value;

    public Instruction(SequencerInstructions instruction) {
        this(instruction, 1);
    }

    public Instruction(SequencerInstructions instruction, int value) {
        this(instruction, InstructionSpeedModifiers.FORWARD, value);
    }

    public Instruction(SequencerInstructions instruction, InstructionSpeedModifiers speedModifier, int value) {
        this.instruction = instruction;
        this.speedModifier = speedModifier;
        this.value = value;
    }

    int getDuration(float currentProgress, float speed) {
        speed *= (float)this.speedModifier.value;
        speed = Math.abs(speed);
        double target = (float)this.value - currentProgress;
        switch (this.instruction) {
            case TURN_ANGLE: {
                double degreesPerTick = KineticBlockEntity.convertToAngular(speed);
                return (int)Math.ceil(target / degreesPerTick) + 2;
            }
            case TURN_DISTANCE: {
                double metersPerTick = KineticBlockEntity.convertToLinear(speed);
                return (int)Math.ceil(target / metersPerTick) + 2;
            }
            case DELAY: {
                return (int)target;
            }
            case AWAIT: {
                return -1;
            }
        }
        return 0;
    }

    float getTickProgress(float speed) {
        switch (this.instruction) {
            case TURN_ANGLE: {
                return KineticBlockEntity.convertToAngular(speed);
            }
            case TURN_DISTANCE: {
                return KineticBlockEntity.convertToLinear(speed);
            }
            case DELAY: {
                return 1.0f;
            }
        }
        return 0.0f;
    }

    int getSpeedModifier() {
        switch (this.instruction) {
            case TURN_ANGLE: 
            case TURN_DISTANCE: {
                return this.speedModifier.value;
            }
        }
        return 0;
    }

    OnIsPoweredResult onRedstonePulse() {
        return this.instruction == SequencerInstructions.AWAIT ? OnIsPoweredResult.CONTINUE : OnIsPoweredResult.NOTHING;
    }

    public static ListTag serializeAll(List<Instruction> instructions) {
        ListTag list = new ListTag();
        instructions.forEach(i -> list.add((Object)i.serialize()));
        return list;
    }

    public static Vector<Instruction> deserializeAll(ListTag list) {
        if (list.isEmpty()) {
            return Instruction.createDefault();
        }
        Vector<Instruction> instructions = new Vector<Instruction>(5);
        list.forEach(inbt -> instructions.add(Instruction.deserialize((CompoundTag)inbt)));
        return instructions;
    }

    public static Vector<Instruction> createDefault() {
        Vector<Instruction> instructions = new Vector<Instruction>(5);
        instructions.add(new Instruction(SequencerInstructions.TURN_ANGLE, 90));
        instructions.add(new Instruction(SequencerInstructions.END));
        return instructions;
    }

    CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        NBTHelper.writeEnum((CompoundTag)tag, (String)"Type", (Enum)this.instruction);
        NBTHelper.writeEnum((CompoundTag)tag, (String)"Modifier", (Enum)this.speedModifier);
        tag.putInt("Value", this.value);
        return tag;
    }

    static Instruction deserialize(CompoundTag tag) {
        Instruction instruction = new Instruction((SequencerInstructions)NBTHelper.readEnum((CompoundTag)tag, (String)"Type", SequencerInstructions.class));
        instruction.speedModifier = (InstructionSpeedModifiers)NBTHelper.readEnum((CompoundTag)tag, (String)"Modifier", InstructionSpeedModifiers.class);
        instruction.value = tag.getInt("Value");
        return instruction;
    }
}
