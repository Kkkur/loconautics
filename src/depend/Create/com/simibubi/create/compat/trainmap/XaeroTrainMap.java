/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.client.event.InputEvent$MouseButton$Pre
 *  xaero.lib.client.gui.ScreenBase
 *  xaero.map.gui.GuiMap
 */
package com.simibubi.create.compat.trainmap;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.Create;
import com.simibubi.create.compat.trainmap.TrainMapManager;
import com.simibubi.create.compat.trainmap.TrainMapSyncClient;
import com.simibubi.create.foundation.gui.RemovedGuiUtils;
import com.simibubi.create.foundation.mixin.compat.xaeros.XaeroFullscreenMapAccessor;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.InputEvent;
import xaero.lib.client.gui.ScreenBase;
import xaero.map.gui.GuiMap;

public class XaeroTrainMap {
    private static boolean requesting;
    private static ResourceKey<Level> renderedDimension;
    private static boolean encounteredException;

    public static void tick() {
        if (!((Boolean)AllConfigs.client().showTrainMapOverlay.get()).booleanValue() || !XaeroTrainMap.isMapOpen(Minecraft.getInstance().screen)) {
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
        if (encounteredException) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        try {
            if (!(mc.screen instanceof GuiMap)) {
                return;
            }
        }
        catch (Throwable e) {
            Create.LOGGER.error("Failed to handle mouseClick for Xaero's World Map train map integration:", e);
            encounteredException = true;
            return;
        }
        Window window = mc.getWindow();
        double mX = mc.mouseHandler.xpos() * (double)window.getGuiScaledWidth() / (double)window.getScreenWidth();
        double mY = mc.mouseHandler.ypos() * (double)window.getGuiScaledHeight() / (double)window.getScreenHeight();
        if (TrainMapManager.handleToggleWidgetClick(Mth.floor((double)mX), Mth.floor((double)mY), 3, 30)) {
            event.setCanceled(true);
        }
    }

    public static void onRender(GuiGraphics graphics, GuiMap screen, int mX, int mY, float pt) {
        double x = ((XaeroFullscreenMapAccessor)screen).create$getCameraX();
        double z = ((XaeroFullscreenMapAccessor)screen).create$getCameraZ();
        double mapScale = ((XaeroFullscreenMapAccessor)screen).create$getScale();
        renderedDimension = ((XaeroFullscreenMapAccessor)screen).create$getMapProcessor().getMapWorld().getCurrentDimension().getDimId();
        if (!((Boolean)AllConfigs.client().showTrainMapOverlay.get()).booleanValue()) {
            XaeroTrainMap.renderToggleWidgetAndTooltip(graphics, screen, mX, mY);
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        double guiScale = (double)window.getScreenWidth() / (double)window.getGuiScaledWidth();
        double interfaceScale = (double)window.getWidth() / (double)window.getScreenWidth();
        double scale = mapScale / guiScale / interfaceScale;
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate((float)screen.width / 2.0f, (float)screen.height / 2.0f, 0.0f);
        pose.scale((float)scale, (float)scale, 1.0f);
        pose.translate(-x, -z, 0.0);
        float mouseX = (float)mX - (float)screen.width / 2.0f;
        float mouseY = (float)mY - (float)screen.height / 2.0f;
        mouseX = (float)((double)mouseX / scale);
        mouseY = (float)((double)mouseY / scale);
        mouseX = (float)((double)mouseX + x);
        mouseY = (float)((double)mouseY + z);
        Rect2i bounds = new Rect2i(Mth.floor((double)((double)((float)(-screen.width) / 2.0f) / scale + x)), Mth.floor((double)((double)((float)(-screen.height) / 2.0f) / scale + z)), Mth.floor((double)((double)screen.width / scale)), Mth.floor((double)((double)screen.height / scale)));
        List<FormattedText> tooltip = TrainMapManager.renderAndPick(graphics, Mth.floor((float)mouseX), Mth.floor((float)mouseY), false, bounds);
        pose.popPose();
        if (!XaeroTrainMap.renderToggleWidgetAndTooltip(graphics, screen, mX, mY) && tooltip != null) {
            RemovedGuiUtils.drawHoveringText(graphics, tooltip, mX, mY, screen.width, screen.height, 256, mc.font);
        }
    }

    private static boolean renderToggleWidgetAndTooltip(GuiGraphics graphics, GuiMap screen, int mouseX, int mouseY) {
        TrainMapManager.renderToggleWidget(graphics, 3, 30);
        if (!TrainMapManager.isToggleWidgetHovered(mouseX, mouseY, 3, 30)) {
            return false;
        }
        RemovedGuiUtils.drawHoveringText(graphics, List.of(CreateLang.translate("train_map.toggle", new Object[0]).component()), mouseX, mouseY + 20, screen.width, screen.height, 256, Minecraft.getInstance().font);
        return true;
    }

    public static ResourceKey<Level> getRenderedDimension() {
        return renderedDimension;
    }

    public static boolean isMapOpen(Screen screen) {
        if (encounteredException) {
            return false;
        }
        try {
            ScreenBase screenBase;
            return screen instanceof ScreenBase && ((screenBase = (ScreenBase)screen) instanceof GuiMap || screenBase.parent instanceof GuiMap);
        }
        catch (Throwable e) {
            Create.LOGGER.error("Failed to check if Xaero's World Map was open for train map integration:", e);
            encounteredException = true;
            return false;
        }
    }

    static {
        encounteredException = false;
    }
}
