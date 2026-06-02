/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.ftb.mods.ftbchunks.client.gui.LargeMapScreen
 *  dev.ftb.mods.ftbchunks.client.gui.RegionMapPanel
 *  dev.ftb.mods.ftblibrary.ui.BaseScreen
 *  dev.ftb.mods.ftblibrary.ui.ScreenWrapper
 *  dev.ftb.mods.ftblibrary.ui.Widget
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.neoforged.fml.util.ObfuscationReflectionHelper
 *  net.neoforged.neoforge.client.event.InputEvent$MouseButton$Pre
 *  net.neoforged.neoforge.client.event.RenderTooltipEvent$Pre
 *  net.neoforged.neoforge.client.event.ScreenEvent$Render$Post
 */
package com.simibubi.create.compat.trainmap;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.trainmap.TrainMapManager;
import com.simibubi.create.compat.trainmap.TrainMapSyncClient;
import com.simibubi.create.foundation.gui.RemovedGuiUtils;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.ftb.mods.ftbchunks.client.gui.LargeMapScreen;
import dev.ftb.mods.ftbchunks.client.gui.RegionMapPanel;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.ScreenWrapper;
import dev.ftb.mods.ftblibrary.ui.Widget;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

public class FTBChunksTrainMap {
    private static int cancelTooltips = 0;
    private static boolean renderingTooltip = false;
    private static boolean requesting;

    public static void tick() {
        if (cancelTooltips > 0) {
            --cancelTooltips;
        }
        LargeMapScreen mapScreen = FTBChunksTrainMap.getAsLargeMapScreen(Minecraft.getInstance().screen);
        if (!((Boolean)AllConfigs.client().showTrainMapOverlay.get()).booleanValue() || mapScreen == null) {
            if (requesting) {
                TrainMapSyncClient.stopRequesting();
            }
            requesting = false;
            return;
        }
        TrainMapManager.tick((ResourceKey<Level>)mapScreen.currentDimension());
        requesting = true;
        TrainMapSyncClient.requestData();
    }

    public static void cancelTooltips(RenderTooltipEvent.Pre event) {
        if (FTBChunksTrainMap.getAsLargeMapScreen(Minecraft.getInstance().screen) == null) {
            return;
        }
        if (renderingTooltip || cancelTooltips == 0) {
            return;
        }
        event.setCanceled(true);
    }

    public static void mouseClick(InputEvent.MouseButton.Pre event) {
        LargeMapScreen screen = FTBChunksTrainMap.getAsLargeMapScreen(Minecraft.getInstance().screen);
        if (screen == null) {
            return;
        }
        if (TrainMapManager.handleToggleWidgetClick(screen.getMouseX(), screen.getMouseY(), 20, 2)) {
            event.setCanceled(true);
        }
    }

    public static void renderGui(ScreenEvent.Render.Post event) {
        LargeMapScreen largeMapScreen = FTBChunksTrainMap.getAsLargeMapScreen(event.getScreen());
        if (largeMapScreen == null) {
            return;
        }
        Object panel = ObfuscationReflectionHelper.getPrivateValue(LargeMapScreen.class, (Object)largeMapScreen, (String)"regionPanel");
        if (!(panel instanceof RegionMapPanel)) {
            return;
        }
        RegionMapPanel regionMapPanel = (RegionMapPanel)panel;
        GuiGraphics graphics = event.getGuiGraphics();
        if (!((Boolean)AllConfigs.client().showTrainMapOverlay.get()).booleanValue()) {
            FTBChunksTrainMap.renderToggleWidgetAndTooltip(event, largeMapScreen, graphics);
            return;
        }
        int blocksPerRegion = 512;
        int minX = Mth.floor((double)regionMapPanel.getScrollX());
        int minY = Mth.floor((double)regionMapPanel.getScrollY());
        float regionTileSize = (float)largeMapScreen.getRegionTileSize() / (float)blocksPerRegion;
        int regionMinX = (Integer)ObfuscationReflectionHelper.getPrivateValue(RegionMapPanel.class, (Object)regionMapPanel, (String)"regionMinX");
        int regionMinZ = (Integer)ObfuscationReflectionHelper.getPrivateValue(RegionMapPanel.class, (Object)regionMapPanel, (String)"regionMinZ");
        float mouseX = event.getMouseX();
        float mouseY = event.getMouseY();
        boolean linearFiltering = (double)largeMapScreen.getRegionTileSize() * Minecraft.getInstance().getWindow().getGuiScale() < 512.0;
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate((float)(-minX), (float)(-minY), 0.0f);
        pose.scale(regionTileSize, regionTileSize, 1.0f);
        pose.translate((float)(-regionMinX * blocksPerRegion), (float)(-regionMinZ * blocksPerRegion), 0.0f);
        mouseX += (float)minX;
        mouseY += (float)minY;
        mouseX /= regionTileSize;
        mouseY /= regionTileSize;
        Rect2i bounds = new Rect2i(Mth.floor((float)((float)minX / regionTileSize + (float)(regionMinX * blocksPerRegion))), Mth.floor((float)((float)minY / regionTileSize + (float)(regionMinZ * blocksPerRegion))), Mth.floor((float)((float)largeMapScreen.width / regionTileSize)), Mth.floor((float)((float)largeMapScreen.height / regionTileSize)));
        List<FormattedText> tooltip = TrainMapManager.renderAndPick(graphics, Mth.floor((float)(mouseX += (float)(regionMinX * blocksPerRegion))), Mth.floor((float)(mouseY += (float)(regionMinZ * blocksPerRegion))), linearFiltering, bounds);
        pose.popPose();
        if (!FTBChunksTrainMap.renderToggleWidgetAndTooltip(event, largeMapScreen, graphics) && tooltip != null) {
            renderingTooltip = true;
            RemovedGuiUtils.drawHoveringText(graphics, tooltip, event.getMouseX(), event.getMouseY(), largeMapScreen.width, largeMapScreen.height, 256, Minecraft.getInstance().font);
            renderingTooltip = false;
            cancelTooltips = 5;
        }
        pose.pushPose();
        pose.translate(0.0f, 0.0f, 300.0f);
        for (Widget widget : largeMapScreen.getWidgets()) {
            if (!widget.isEnabled() || widget == panel) continue;
            widget.draw(graphics, largeMapScreen.getTheme(), widget.getPosX(), widget.getPosY(), widget.getWidth(), widget.getHeight());
        }
        pose.popPose();
    }

    private static boolean renderToggleWidgetAndTooltip(ScreenEvent.Render.Post event, LargeMapScreen largeMapScreen, GuiGraphics graphics) {
        TrainMapManager.renderToggleWidget(graphics, 20, 2);
        if (!TrainMapManager.isToggleWidgetHovered(event.getMouseX(), event.getMouseY(), 20, 2)) {
            return false;
        }
        renderingTooltip = true;
        RemovedGuiUtils.drawHoveringText(graphics, List.of(CreateLang.translate("train_map.toggle", new Object[0]).component()), event.getMouseX(), event.getMouseY() + 20, largeMapScreen.width, largeMapScreen.height, 256, Minecraft.getInstance().font);
        renderingTooltip = false;
        cancelTooltips = 5;
        return true;
    }

    private static LargeMapScreen getAsLargeMapScreen(Screen screen) {
        if (!(screen instanceof ScreenWrapper)) {
            return null;
        }
        ScreenWrapper screenWrapper = (ScreenWrapper)screen;
        BaseScreen wrapped = screenWrapper.getGui();
        if (!(wrapped instanceof LargeMapScreen)) {
            return null;
        }
        LargeMapScreen largeMapScreen = (LargeMapScreen)wrapped;
        return largeMapScreen;
    }
}
