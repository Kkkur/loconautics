/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.foundation.instruction.TickingInstruction
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.eriksonn.aeronautics.mixin.ponder;

import dev.eriksonn.aeronautics.mixinterface.TickingInstructionExtension;
import net.createmod.ponder.foundation.instruction.TickingInstruction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={TickingInstruction.class})
public class TickingInstructionMixin
implements TickingInstructionExtension {
    @Unique
    boolean aeronautics$isStopped;

    @Override
    public void aeronautics$stopInstruction() {
        this.aeronautics$isStopped = true;
    }

    @Inject(method={"isComplete"}, at={@At(value="HEAD")}, cancellable=true)
    public void isComplete(CallbackInfoReturnable<Boolean> cir) {
        if (this.aeronautics$isStopped) {
            cir.setReturnValue((Object)true);
        }
    }
}
