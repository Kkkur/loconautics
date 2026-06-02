/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.foundation.instruction.TickingInstruction
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.eriksonn.aeronautics.mixin.ponder;

import net.createmod.ponder.foundation.instruction.TickingInstruction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={TickingInstruction.class})
public interface TickingInstructionAccessor {
    @Accessor
    public void setRemainingTicks(int var1);
}
