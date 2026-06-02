/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.equipment.extendoGrip.ExtendoGripRenderHandler
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.simulated_team.simulated.mixin.handle;

import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripRenderHandler;
import dev.simulated_team.simulated.index.SimClickInteractions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ExtendoGripRenderHandler.class})
public class ExtendoGripRenderHandlerMixin {
    @Inject(method={"tick"}, at={@At(value="FIELD", target="Lcom/simibubi/create/content/equipment/extendoGrip/ExtendoGripRenderHandler;pose:Ldev/engine_room/flywheel/lib/model/baked/PartialModel;", ordinal=0)})
    private static void handleAnimate(CallbackInfo ci) {
        if (SimClickInteractions.HANDLE_HANDLER.isActive()) {
            ExtendoGripRenderHandler.mainHandAnimation = 0.95f;
        }
    }
}
