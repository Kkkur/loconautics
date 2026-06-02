/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.nbt.CompoundTag
 */
package com.simibubi.create.content.kinetics.transmission.sequencer;

import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;

public record SequencedGearshiftBlockEntity.SequenceContext(SequencerInstructions instruction, double relativeValue) {
    public static SequencedGearshiftBlockEntity.SequenceContext fromGearshift(SequencerInstructions instruction, double kineticSpeed, int absoluteValue) {
        return instruction.needsPropagation() ? new SequencedGearshiftBlockEntity.SequenceContext(instruction, kineticSpeed == 0.0 ? 0.0 : (double)absoluteValue / kineticSpeed) : null;
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

    public static SequencedGearshiftBlockEntity.SequenceContext fromNBT(CompoundTag nbt) {
        if (nbt.isEmpty()) {
            return null;
        }
        return new SequencedGearshiftBlockEntity.SequenceContext((SequencerInstructions)NBTHelper.readEnum((CompoundTag)nbt, (String)"Mode", SequencerInstructions.class), nbt.getDouble("Value"));
    }
}
