/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.vertex.PoseStack
 *  journeymap.api.v2.client.IClientAPI
 *  journeymap.api.v2.client.IClientPlugin
 *  journeymap.api.v2.client.JourneyMapPlugin
 *  journeymap.api.v2.client.display.Context$UI
 *  journeymap.api.v2.client.event.FullscreenRenderEvent
 *  journeymap.api.v2.client.fullscreen.IFullscreen
 *  journeymap.api.v2.client.util.UIState
 *  journeymap.api.v2.common.event.FullscreenEventRegistry
 *  journeymap.client.ui.fullscreen.Fullscreen
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.Mth
 *  net.neoforged.neoforge.client.event.InputEvent$MouseButton$Pre
 */
package com.simibubi.create.compat.trainmap;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.trainmap.TrainMapManager;
import com.simibubi.create.compat.trainmap.TrainMapSyncClient;
import com.simibubi.create.foundation.gui.RemovedGuiUtils;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.client.JourneyMapPlugin;
import journeymap.api.v2.client.display.Context;
import journeymap.api.v2.client.event.FullscreenRenderEvent;
import journeymap.api.v2.client.fullscreen.IFullscreen;
import journeymap.api.v2.client.util.UIState;
import journeymap.api.v2.common.event.FullscreenEventRegistry;
import journeymap.client.ui.fullscreen.Fullscreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.InputEvent;

@JourneyMapPlugin(apiVersion="2.0.0")
public class JourneyTrainMap
implements IClientPlugin {
    private static boolean requesting;

    public void initialize(IClientAPI jmClientApi) {
        FullscreenEventRegistry.FULLSCREEN_RENDER_EVENT.subscribe("create", JourneyTrainMap::onRender);
    }

    public String getModId() {
        return "create";
    }

    public static void tick() {
        if (!((Boolean)AllConfigs.client().showTrainMapOverlay.get()).booleanValue() || !(Minecraft.getInstance().screen instanceof Fullscreen)) {
            if (requesting) {
                TrainMapSyncClient.stopRequesting();
            }
            requesting = false;
            return;
        }
        TrainMapManager.tick();
        requesting = true;
        TrainMapSyncClient.requestData();
    }

    public static void mouseClick(InputEvent.MouseButton.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Screen screen = mc.screen;
        if (!(screen instanceof Fullscreen)) {
            return;
        }
        Fullscreen screen2 = (Fullscreen)screen;
        Window window = mc.getWindow();
        double mX = mc.mouseHandler.xpos() * (double)window.getGuiScaledWidth() / (double)window.getScreenWidth();
        double mY = mc.mouseHandler.ypos() * (double)window.getGuiScaledHeight() / (double)window.getScreenHeight();
        if (TrainMapManager.handleToggleWidgetClick(Mth.floor((double)mX), Mth.floor((double)mY), 3, 30)) {
            event.setCanceled(true);
        }
    }

    public static void onRender(FullscreenRenderEvent event) {
        GuiGraphics graphics = event.getGraphics();
        IFullscreen fullscreen = event.getFullscreen();
        Screen screen = fullscreen.getScreen();
        double x = fullscreen.getCenterBlockX(true);
        double z = fullscreen.getCenterBlockZ(true);
        int mX = event.getMouseX();
        int mY = event.getMouseY();
        float pt = event.getPartialTicks();
        UIState state = fullscreen.getUiState();
        if (state == null) {
            return;
        }
        if (state.ui != Context.UI.Fullscreen) {
            return;
        }
        if (!state.active) {
            return;
        }
        if (!((Boolean)AllConfigs.client().showTrainMapOverlay.get()).booleanValue()) {
            JourneyTrainMap.renderToggleWidgetAndTooltip(graphics, screen, mX, mY);
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        double guiScale = (double)window.getScreenWidth() / (double)window.getGuiScaledWidth();
        double scale = state.blockSize / guiScale;
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate((float)screen.width / 2.0f, (float)screen.height / 2.0f, 0.0f);
        pose.scale((float)scale, (float)scale, 1.0f);
        pose.translate(-x, -z, 0.0);
        float mouseX = (float)mX - (float)screen.width / 2.0f;
        float mouseY = (float)mY - (float)screen.height / 2.0f;
        Rect2i bounds = new Rect2i(Mth.floor((double)((double)((float)(-screen.width) / 2.0f) / scale + x)), Mth.floor((double)((double)((float)(-screen.height) / 2.0f) / scale + z)), Mth.floor((double)((double)screen.width / scale)), Mth.floor((double)((double)screen.height / scale)));
        List<FormattedText> tooltip = TrainMapManager.renderAndPick(graphics, Mth.floor((float)(mouseX /= (float)scale)), Mth.floor((float)(mouseY /= (float)scale)), false, bounds);
        pose.popPose();
        if (!JourneyTrainMap.renderToggleWidgetAndTooltip(graphics, screen, mX, mY) && tooltip != null) {
            RemovedGuiUtils.drawHoveringText(graphics, tooltip, mX, mY, screen.width, screen.height, 256, mc.font);
        }
    }

    private static boolean renderToggleWidgetAndTooltip(GuiGraphics graphics, Screen screen, int mouseX, int mouseY) {
        TrainMapManager.renderToggleWidget(graphics, 3, 30);
        if (!TrainMapManager.isToggleWidgetHovered(mouseX, mouseY, 3, 30)) {
            return false;
        }
        RemovedGuiUtils.drawHoveringText(graphics, List.of(CreateLang.translate("train_map.toggle", new Object[0]).component()), mouseX, mouseY + 20, screen.width, screen.height, 256, Minecraft.getInstance().font);
        return true;
    }
}
