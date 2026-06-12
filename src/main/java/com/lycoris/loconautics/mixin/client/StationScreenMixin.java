package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.allsable.SableTrainClientRegistry;
import com.lycoris.loconautics.network.packets.DisassembleSableTrainPacket;

import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.station.StationScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.IconButton;

import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes Create's own station "train present" screen render for a parked Sable train, which has no Create
 * {@code Train} and so never drives Create's {@code displayedTrain}-keyed UI. After Create's {@code tick()} /
 * {@code renderWindow()} have run (assuming no train), we override the relevant widgets so the SAME screen shows:
 * <ul>
 *   <li>Create's own {@code disassembleTrainButton} (active, routed to {@link DisassembleSableTrainPacket}),</li>
 *   <li>Create's own {@code dropScheduleButton} as an inactive dummy (schedules unimplemented for Sable),</li>
 *   <li>the {@code trainNameBox} (Create removes it each tick when no train, so we re-add it), and</li>
 *   <li>the train-display textbox frame, drawn over Create's "idle" header.</li>
 * </ul>
 * The overlapping {@code newTrainButton} is hidden so only the disassemble button shows. No new buttons are made.
 * When no Sable train is present we touch nothing, so a Create train uses Create's untouched flow.
 *
 * <p>remap=false: targets a Create class (Mojang-mapped at runtime, like the assembly screen mixin).
 */
@Mixin(value = StationScreen.class, remap = false)
public abstract class StationScreenMixin extends Screen {

    @Shadow
    private IconButton disassembleTrainButton;

    @Shadow
    private IconButton newTrainButton;

    @Shadow
    private IconButton dropScheduleButton;

    @Shadow
    private EditBox trainNameBox;

    /** True while we are forcing the present-screen state, so we can tear it down once the train leaves. */
    @Unique
    private boolean loconautics$forced;

    /** Dummy constructor — never invoked at runtime; Mixin bypasses constructors entirely. */
    private StationScreenMixin() {
        super(Component.empty());
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void loconautics$showSableDisassemble(CallbackInfo ci) {
        StationBlockEntity be = ((AbstractStationScreenAccessor) (Object) this).loconautics$getBlockEntity();
        if (!SableTrainClientRegistry.isStationParked(be.getBlockPos())) {
            if (loconautics$forced) {
                // Train left: drop the forced widgets; Create's own tick restores the new-train button + name box.
                this.disassembleTrainButton.visible = false;
                this.disassembleTrainButton.active = false;
                this.dropScheduleButton.visible = false;
                this.dropScheduleButton.active = false;
                loconautics$forced = false;
            }
            return; // no present Sable train — leave Create's button state alone
        }
        loconautics$forced = true;

        // Reuse Create's own disassemble button; hide the overlapping "new train" button so only it shows.
        this.newTrainButton.visible = false;
        this.newTrainButton.active = false;
        this.disassembleTrainButton.visible = true;
        this.disassembleTrainButton.active = true;
        this.disassembleTrainButton.withCallback(() ->
                CatnipServices.NETWORK.sendToServer(
                        (CustomPacketPayload) new DisassembleSableTrainPacket(be.getBlockPos())));

        // Show Create's drop-schedule button as an inactive dummy (Sable schedules unimplemented) so the button
        // row matches Create's present layout (drop-schedule left of disassemble) instead of one lone off-centre button.
        this.dropScheduleButton.visible = true;
        this.dropScheduleButton.active = false;

        // Create's tickTrainDisplay() removes the train-name box every tick while no Create train is displayed, so
        // re-add it here. Give it a placeholder name (Sable trains are unnamed for now).
        if (!this.trainNameBox.active) {
            this.trainNameBox.active = true;
            this.trainNameBox.setValue("Sable Train");
            this.addRenderableWidget(this.trainNameBox);
        }
    }

    @Inject(method = "renderWindow", at = @At("TAIL"))
    private void loconautics$renderSableTrainDisplay(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks,
                                                     CallbackInfo ci) {
        StationBlockEntity be = ((AbstractStationScreenAccessor) (Object) this).loconautics$getBlockEntity();
        if (!SableTrainClientRegistry.isStationParked(be.getBlockPos())) {
            return;
        }
        int x = (this.width - AllGuiTextures.STATION.getWidth()) / 2;
        int y = (this.height - AllGuiTextures.STATION.getHeight()) / 2;

        // Train-display placeholder (no Create carriage icons exist for a Sable train yet).
        Component label = Component.literal("Sable Train");
        graphics.drawString(this.font, label, x + 100 - this.font.width(label) / 2, y + 26, 0xC6C6C6, false);

        // Same speech-bubble textbox Create draws under a present train, so the name box has its frame.
        AllGuiTextures.STATION_TEXTBOX_TOP.render(graphics, x + 21, y + 42);
        UIRenderHelper.drawStretched(graphics, x + 21, y + 60, 150, 26, 0,
                (TextureSheetSegment) AllGuiTextures.STATION_TEXTBOX_MIDDLE);
        AllGuiTextures.STATION_TEXTBOX_BOTTOM.render(graphics, x + 21, y + 86);
        AllGuiTextures.STATION_TEXTBOX_SPEECH.render(graphics, x + 25, y + 38);
    }
}
