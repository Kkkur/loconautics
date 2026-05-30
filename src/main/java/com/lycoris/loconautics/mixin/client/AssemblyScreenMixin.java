package com.lycoris.loconautics.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.lycoris.loconautics.client.screen.SableModeButton;

import com.simibubi.create.content.trains.station.AbstractStationScreen;
import com.simibubi.create.content.trains.station.AssemblyScreen;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.station.WideIconButton;

/**
 * Adds the "Assemble as Physics Train" button to Create's train assembly screen.
 *
 * <p>By extending {@link AbstractStationScreen} (the real superclass of {@link AssemblyScreen}),
 * the mixin gets direct access to the inherited {@code guiLeft}/{@code guiTop}/{@code background}/
 * {@code blockEntity} members and to {@code addRenderableWidget}, without needing @Shadow stubs.
 * The dummy constructor is required for compilation and is stripped by Mixin at runtime.
 */
@Mixin(AssemblyScreen.class)
public abstract class AssemblyScreenMixin extends AbstractStationScreen {

    private AssemblyScreenMixin(StationBlockEntity be, GlobalStation station) {
        super(be, station);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void loconautics$addPhysicsButton(CallbackInfo ci) {
        int x = this.guiLeft;
        int y = this.guiTop;
        int by = y + this.background.getHeight() - 24;

        // Sits to the left of the vanilla quit (x+73) / assemble (x+94) buttons.
        WideIconButton button = SableModeButton.create(x + 52, by, this.blockEntity.getBlockPos());
        this.addRenderableWidget(button);
    }
}
