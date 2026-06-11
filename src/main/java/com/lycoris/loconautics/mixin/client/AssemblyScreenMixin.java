package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.network.packets.AssembleSableTrainPacket;
import com.simibubi.create.content.trains.station.AssemblyScreen;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
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

        IconButton sableButton = new IconButton(x + 52, by, AllIcons.I_CONFIRM);
        sableButton.setToolTip(Component.literal("Assemble as Sable Train"));
        sableButton.withCallback(() ->
                CatnipServices.NETWORK.sendToServer(
                        (CustomPacketPayload) new AssembleSableTrainPacket(be.getBlockPos())
                )
        );
        this.addRenderableWidget(sableButton);
    }
}
