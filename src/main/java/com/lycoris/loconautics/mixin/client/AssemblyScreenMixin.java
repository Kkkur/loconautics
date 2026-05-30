package com.lycoris.loconautics.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.lycoris.loconautics.client.screen.SableModeButton;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.station.AbstractStationScreen;
import com.simibubi.create.content.trains.station.AssemblyScreen;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.gui.widget.IconButton;

/**
 * Adds the "Assemble as Physics Train" button to Create's train assembly screen.
 *
 * <p>By extending {@link AbstractStationScreen} (the real superclass of {@link AssemblyScreen}),
 * the mixin gets direct access to the inherited {@code guiLeft}/{@code guiTop}/{@code background}/
 * {@code blockEntity}/{@code displayedTrain} members and to {@code addRenderableWidget}, without
 * needing @Shadow stubs. The dummy constructor is required for compilation and is stripped by
 * Mixin at runtime.
 *
 * <p>The button is greyed out (inactive) exactly like the vanilla assemble button: active only
 * when there are bogeys to assemble (or a train is present).
 */
@Mixin(AssemblyScreen.class)
public abstract class AssemblyScreenMixin extends AbstractStationScreen {

    @Unique
    private IconButton loconautics$physicsButton;

    private AssemblyScreenMixin(StationBlockEntity be, GlobalStation station) {
        super(be, station);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void loconautics$addPhysicsButton(CallbackInfo ci) {
        int x = this.guiLeft;
        int y = this.guiTop;
        int by = y + this.background.getHeight() - 24;

        // Sits left of the vanilla quit (x+73) / assemble (x+94) buttons, with a gap so it does
        // not touch the cancel ("X") button.
        this.loconautics$physicsButton = SableModeButton.create(x + 48, by, this.blockEntity.getBlockPos());
        this.loconautics$physicsButton.active = false; // greyed until there is something to assemble
        this.addRenderableWidget(this.loconautics$physicsButton);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void loconautics$updateButtonState(CallbackInfo ci) {
        if (this.loconautics$physicsButton == null) {
            return;
        }
        Train train = this.displayedTrain != null ? this.displayedTrain.get() : null;
        int bogeyCount = ((StationBlockEntityAccessor) this.blockEntity).loconautics$getBogeyCount();
        this.loconautics$physicsButton.active = bogeyCount > 0 || train != null;
    }
}
