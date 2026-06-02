/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.api.behaviour.display.DisplayTarget
 *  com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity
 *  com.simibubi.create.content.redstone.displayLink.DisplayLinkContext
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.simulated_team.simulated.mixin.conditional_display_target;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import dev.simulated_team.simulated.api.ConditionalDisplayTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={DisplayLinkBlockEntity.class})
public class DisplayLinkBlockEntityMixin {
    @Shadow
    public DisplayTarget activeTarget;

    @Inject(method={"updateGatheredData"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/api/behaviour/display/DisplaySource;transferData(Lcom/simibubi/create/content/redstone/displayLink/DisplayLinkContext;Lcom/simibubi/create/api/behaviour/display/DisplayTarget;I)V", shift=At.Shift.BEFORE)}, cancellable=true)
    private void simulated$stopConditionalTransfer(CallbackInfo ci, @Local(name={"context"}) DisplayLinkContext context) {
        ConditionalDisplayTarget cdt;
        DisplayTarget displayTarget = this.activeTarget;
        if (displayTarget instanceof ConditionalDisplayTarget && !(cdt = (ConditionalDisplayTarget)displayTarget).allowsWriting(context)) {
            ci.cancel();
        }
    }
}
