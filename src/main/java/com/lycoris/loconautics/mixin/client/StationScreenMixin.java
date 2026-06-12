package com.lycoris.loconautics.mixin.client;

import com.lycoris.loconautics.allsable.SableTrainClientRegistry;
import com.lycoris.loconautics.network.packets.DisassembleSableTrainPacket;
import com.lycoris.loconautics.network.packets.StationSetTrainNamePacket;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.content.trains.entity.TrainIconType;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.station.StationScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.IconButton;

import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes Create's own station "train present" screen render for a parked Sable train, which has no Create
 * {@code Train} and so never drives Create's {@code displayedTrain}-keyed UI. After Create's {@code tick()} /
 * {@code renderWindow()} have run (assuming no train), we override the relevant widgets so the SAME screen shows:
 * <ul>
 *   <li>Create's own {@code disassembleTrainButton} (active, routed to {@link DisassembleSableTrainPacket}),</li>
 *   <li>Create's own {@code dropScheduleButton} as an inactive dummy (schedules unimplemented for Sable),</li>
 *   <li>the {@code trainNameBox} (Create removes it each tick when no train, so we re-add it) showing the train's
 *       Sable sub-level name, edited and committed back via {@link StationSetTrainNamePacket}, and</li>
 *   <li>the train-display carriage icon + textbox frame, drawn over Create's "idle" header.</li>
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

    @Unique
    private StationBlockEntity loconautics$be() {
        return ((AbstractStationScreenAccessor) (Object) this).loconautics$getBlockEntity();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void loconautics$showSableDisassemble(CallbackInfo ci) {
        StationBlockEntity be = loconautics$be();
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
        // re-add it here (the same instance, so focus survives). Show the Sable sub-level's name, but never clobber
        // it while the player is typing — only refresh from the synced name when the box is unfocused.
        if (!this.trainNameBox.active) {
            this.trainNameBox.active = true;
            this.addRenderableWidget(this.trainNameBox);
        }
        if (!this.trainNameBox.isFocused()) {
            this.trainNameBox.setValue(SableTrainClientRegistry.stationTrainName(be.getBlockPos()));
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void loconautics$commitNameOnEnter(int key, int scan, int mods, CallbackInfoReturnable<Boolean> cir) {
        if ((key != GLFW.GLFW_KEY_ENTER && key != GLFW.GLFW_KEY_KP_ENTER) || !this.trainNameBox.isFocused()) {
            return;
        }
        StationBlockEntity be = loconautics$be();
        if (SableTrainClientRegistry.isStationParked(be.getBlockPos())) {
            loconautics$sendName(be);
        }
    }

    @Inject(method = "removed", at = @At("TAIL"))
    private void loconautics$commitNameOnClose(CallbackInfo ci) {
        if (this.trainNameBox == null) {
            return;
        }
        StationBlockEntity be = loconautics$be();
        if (SableTrainClientRegistry.isStationParked(be.getBlockPos())) {
            loconautics$sendName(be);
        }
    }

    @Unique
    private void loconautics$sendName(StationBlockEntity be) {
        CatnipServices.NETWORK.sendToServer(
                (CustomPacketPayload) new StationSetTrainNamePacket(be.getBlockPos(), this.trainNameBox.getValue()));
    }

    /**
     * Suppresses Create's "Station idle" header while a Sable train is parked here. Create draws it only when its
     * {@code displayedTrain} is null (always, for a Sable train), via the unique {@code drawString(Font, Component,
     * int, int, int, boolean)} overload in {@code renderWindow} (the only "..." overflow text uses the {@code String}
     * overload, so this redirect never touches it). Our own {@link #loconautics$renderSableTrainDisplay} draws the
     * train name / "Unnamed Train" placeholder in its place.
     */
    @Redirect(method = "renderWindow",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString("
                            + "Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I"),
            remap = false)
    private int loconautics$suppressIdleHeader(GuiGraphics graphics, Font font, Component text,
                                               int sx, int sy, int color, boolean shadow) {
        if (SableTrainClientRegistry.isStationParked(loconautics$be().getBlockPos())) {
            return 0; // a Sable train is present — no "idle" header
        }
        return graphics.drawString(font, text, sx, sy, color, shadow);
    }

    @Inject(method = "renderWindow", at = @At("TAIL"))
    private void loconautics$renderSableTrainDisplay(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks,
                                                     CallbackInfo ci) {
        StationBlockEntity be = loconautics$be();
        if (!SableTrainClientRegistry.isStationParked(be.getBlockPos())) {
            return;
        }
        int bgWidth = AllGuiTextures.STATION.getWidth();
        int x = (this.width - bgWidth) / 2;
        int y = (this.height - AllGuiTextures.STATION.getHeight()) / 2;

        // Create's own carriage icons, one per coupled car (front-first spans synced from the server). The front car
        // is drawn as the engine (ENGINE = -1) and the cars coupled behind as carriage icons sized by their span,
        // laid out left (rear) to right (front/engine) exactly like Create's present-train display.
        int[] spans = SableTrainClientRegistry.stationCarriages(be.getBlockPos());
        TrainIconType icon = TrainIconType.getDefault();

        int totalWidth = icon.getIconWidth(TrainIconType.ENGINE);
        for (int i = spans.length - 1; i >= 1; i--) {
            totalWidth += icon.getIconWidth(spans[i]) + 1;
        }
        int startX = x + bgWidth / 2 - totalWidth / 2;
        if (totalWidth > 130) {
            startX -= totalWidth - 130; // match Create: long consists shift left so the engine stays on screen
        }

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        int offset = 0;
        for (int i = spans.length - 1; i >= 1; i--) {
            offset += icon.render(spans[i], graphics, startX + offset, y + 20) + 1;
        }
        offset += icon.render(TrainIconType.ENGINE, graphics, startX + offset, y + 20);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Same speech-bubble textbox Create draws under a present train, so the name box has its frame.
        AllGuiTextures.STATION_TEXTBOX_TOP.render(graphics, x + 21, y + 42);
        UIRenderHelper.drawStretched(graphics, x + 21, y + 60, 150, 26, 0,
                (TextureSheetSegment) AllGuiTextures.STATION_TEXTBOX_MIDDLE);
        AllGuiTextures.STATION_TEXTBOX_BOTTOM.render(graphics, x + 21, y + 86);
        // Speech-bubble pointer, clamped to point at the right (engine) end of the icon row, like Create.
        int speechX = x + Mth.clamp(startX - x + offset - 13, 25, 159);
        AllGuiTextures.STATION_TEXTBOX_SPEECH.render(graphics, speechX, y + 38);

        // Name line: when the train is unnamed the (empty) Create name box shows nothing, so draw an "Unnamed Train"
        // placeholder in its place; always draw Create's edit icon next to the name, like the present-train screen.
        if (!this.trainNameBox.isFocused()) {
            String name = this.trainNameBox.getValue();
            String shown = name;
            if (name.isEmpty()) {
                Component placeholder = Component.translatable("loconautics.station.unnamed_train");
                shown = placeholder.getString();
                graphics.drawString(this.font, placeholder, loconautics$nameBoxX(shown), y + 47, 0x808080, false);
            }
            int buttonX = loconautics$nameBoxX(shown) + this.font.width(shown) + 5;
            AllGuiTextures.STATION_EDIT_TRAIN_NAME.render(graphics, Math.min(buttonX, x + 156), y + 44);
        }
    }

    /** Mirrors Create's {@code nameBoxX}: the left edge that centres {@code s} in the train-name box. */
    @Unique
    private int loconautics$nameBoxX(String s) {
        int bgWidth = AllGuiTextures.STATION.getWidth();
        int x = (this.width - bgWidth) / 2;
        return x + bgWidth / 2 - (Math.min(this.font.width(s), this.trainNameBox.getWidth()) + 10) / 2;
    }
}
