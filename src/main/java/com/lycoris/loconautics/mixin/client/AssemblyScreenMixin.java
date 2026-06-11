package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.client.gui.SableButtonIcon;
import com.lycoris.loconautics.network.packets.AssembleSableTrainPacket;
import com.simibubi.create.content.trains.station.AssemblyScreen;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.station.StationScreen;
import com.simibubi.create.content.trains.station.WideIconButton;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AssemblyScreen.class, remap = false)
public abstract class AssemblyScreenMixin extends Screen {

    // Dummy constructor — never invoked at runtime; Mixin bypasses constructors entirely.
    protected AssemblyScreenMixin() { super(Component.empty()); }

    @Inject(method = "init", at = @At("TAIL"))
    private void loconautics$addSableTrainButton(CallbackInfo ci) {
        // Compute screen-space position using standard Screen dimensions + known background size.
        // Avoids @Shadow on inherited fields, which fail without a refmap.
        int bgWidth  = AllGuiTextures.STATION_ASSEMBLING.getWidth();
        int bgHeight = AllGuiTextures.STATION_ASSEMBLING.getHeight();
        int x  = (this.width  - bgWidth)  / 2;
        int by = (this.height - bgHeight) / 2 + bgHeight - 24;

        StationBlockEntity be = ((AbstractStationScreenAccessor) (Object) this).loconautics$getBlockEntity();

        // A wide button (26×18, like Create's own assemble button) carrying the custom 24×16 sable icon.
        // Seated to the left of Create's "cancel assembly" button (which sits at x + 73).
        WideIconButton sableButton = new WideIconButton(x + 45, by, SableButtonIcon.INSTANCE);
        sableButton.setToolTip(Component.literal("Assemble as Sable Train"));
        sableButton.withCallback(() ->
                CatnipServices.NETWORK.sendToServer(
                        (CustomPacketPayload) new AssembleSableTrainPacket(be.getBlockPos())
                )
        );
        this.addRenderableWidget(sableButton);
    }

    /**
     * After a successful Sable assembly the server drops the station out of assembly mode (just like Create does
     * after a normal assembly). Create's own flow then relies on the base screen detecting the new {@code Train}
     * and switching to the idle {@link StationScreen} — but a Sable assembly produces no Create {@code Train}, so
     * that never fires and the player is stuck on the assembly screen until they reopen it. We detect the station
     * leaving assembly mode here and switch to the idle screen ourselves.
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void loconautics$switchToIdleWhenAssembled(CallbackInfo ci) {
        if (this.minecraft == null) {
            return;
        }
        StationBlockEntity be = ((AbstractStationScreenAccessor) (Object) this).loconautics$getBlockEntity();
        GlobalStation station = ((AbstractStationScreenAccessor) (Object) this).loconautics$getStation();
        if (station != null && !be.isAssembling()) {
            this.minecraft.setScreen(new StationScreen(be, station));
        }
    }
}
