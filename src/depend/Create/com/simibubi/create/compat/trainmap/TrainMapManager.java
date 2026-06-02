/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.util.FastColor$ABGR32
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.compat.trainmap;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.CreateClient;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.trainmap.TrainMapRenderer;
import com.simibubi.create.compat.trainmap.TrainMapSync;
import com.simibubi.create.compat.trainmap.TrainMapSyncClient;
import com.simibubi.create.compat.trainmap.XaeroTrainMap;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CClient;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TrainMapManager {
    static final int PHASE_BACKGROUND = 0;
    static final int PHASE_STRAIGHTS = 1;
    static final int PHASE_CURVES = 2;

    public static void tick() {
        ResourceKey playerDimension = Minecraft.getInstance().level.dimension();
        if (Mods.XAEROWORLDMAP.isLoaded() && XaeroTrainMap.isMapOpen(Minecraft.getInstance().screen)) {
            ResourceKey renderedDimension = XaeroTrainMap.getRenderedDimension();
            TrainMapManager.tick((ResourceKey<Level>)(renderedDimension != null ? renderedDimension : playerDimension));
        } else {
            TrainMapManager.tick((ResourceKey<Level>)playerDimension);
        }
    }

    public static void tick(ResourceKey<Level> dimension) {
        TrainMapRenderer map = TrainMapRenderer.INSTANCE;
        if (map.trackingVersion != CreateClient.RAILWAYS.version || map.trackingDim != dimension || map.trackingTheme != AllConfigs.client().trainMapColorTheme.get()) {
            TrainMapManager.redrawAll(dimension);
        }
    }

    public static List<FormattedText> renderAndPick(GuiGraphics graphics, int mouseX, int mouseY, boolean linearFiltering, Rect2i bounds) {
        Object hoveredElement = null;
        int offScreenMargin = 32;
        bounds.setX(bounds.getX() - offScreenMargin);
        bounds.setY(bounds.getY() - offScreenMargin);
        bounds.setWidth(bounds.getWidth() + 2 * offScreenMargin);
        bounds.setHeight(bounds.getHeight() + 2 * offScreenMargin);
        TrainMapRenderer.INSTANCE.render(graphics, linearFiltering, bounds);
        hoveredElement = TrainMapManager.drawTrains(graphics, mouseX, mouseY, hoveredElement, bounds);
        hoveredElement = TrainMapManager.drawPoints(graphics, mouseX, mouseY, hoveredElement, bounds);
        graphics.bufferSource().endBatch();
        if (hoveredElement instanceof GlobalStation) {
            GlobalStation station = (GlobalStation)hoveredElement;
            return List.of(Component.literal((String)station.name));
        }
        if (hoveredElement instanceof Train) {
            Train train = (Train)hoveredElement;
            return TrainMapManager.listTrainDetails(train);
        }
        return null;
    }

    public static void renderToggleWidget(GuiGraphics graphics, int x, int y) {
        boolean enabled = (Boolean)AllConfigs.client().showTrainMapOverlay.get();
        if (CreateClient.RAILWAYS.trackNetworks.isEmpty()) {
            return;
        }
        RenderSystem.enableBlend();
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(0.0f, 0.0f, 300.0f);
        AllGuiTextures.TRAINMAP_TOGGLE_PANEL.render(graphics, x, y);
        (enabled ? AllGuiTextures.TRAINMAP_TOGGLE_ON : AllGuiTextures.TRAINMAP_TOGGLE_OFF).render(graphics, x + 18, y + 3);
        pose.popPose();
    }

    public static boolean handleToggleWidgetClick(int mouseX, int mouseY, int x, int y) {
        if (!TrainMapManager.isToggleWidgetHovered(mouseX, mouseY, x, y)) {
            return false;
        }
        CClient config = AllConfigs.client();
        config.showTrainMapOverlay.set((Object)((Boolean)config.showTrainMapOverlay.get() == false ? 1 : 0));
        return true;
    }

    public static boolean isToggleWidgetHovered(int mouseX, int mouseY, int x, int y) {
        if (CreateClient.RAILWAYS.trackNetworks.isEmpty()) {
            return false;
        }
        if (mouseX < x || mouseX >= x + AllGuiTextures.TRAINMAP_TOGGLE_PANEL.getWidth()) {
            return false;
        }
        return mouseY >= y && mouseY < y + AllGuiTextures.TRAINMAP_TOGGLE_PANEL.getHeight();
    }

    private static List<FormattedText> listTrainDetails(Train train) {
        ArrayList<FormattedText> output = new ArrayList<FormattedText>();
        int blue = 13885148;
        int darkBlue = 9611709;
        int bright = 0xFFEFEF;
        int orange = 16756064;
        TrainMapSync.TrainMapSyncEntry trainEntry = TrainMapSyncClient.currentData.get(train.id);
        if (trainEntry == null) {
            return Collections.emptyList();
        }
        TrainMapSync.TrainState state = trainEntry.state;
        TrainMapSync.SignalState signalState = trainEntry.signalState;
        CreateLang.text(train.name.getString()).color(bright).addTo(output);
        if (!trainEntry.ownerName.isBlank()) {
            CreateLang.translate("train_map.train_owned_by", trainEntry.ownerName).color(blue).addTo(output);
        }
        switch (state) {
            case CONDUCTOR_MISSING: {
                CreateLang.translate("train_map.conductor_missing", new Object[0]).color(orange).addTo(output);
                return output;
            }
            case DERAILED: {
                CreateLang.translate("train_map.derailed", new Object[0]).color(orange).addTo(output);
                return output;
            }
            case NAVIGATION_FAILED: {
                CreateLang.translate("train_map.navigation_failed", new Object[0]).color(orange).addTo(output);
                return output;
            }
            case SCHEDULE_INTERRUPTED: {
                CreateLang.translate("train_map.schedule_interrupted", new Object[0]).color(orange).addTo(output);
                return output;
            }
            case RUNNING_MANUALLY: {
                CreateLang.translate("train_map.player_controlled", new Object[0]).color(blue).addTo(output);
                break;
            }
        }
        String currentStation = trainEntry.targetStationName;
        int targetStationDistance = trainEntry.targetStationDistance;
        if (!currentStation.isBlank()) {
            if (targetStationDistance == 0) {
                CreateLang.translate("train_map.train_at_station", currentStation).color(darkBlue).addTo(output);
            } else {
                CreateLang.translate("train_map.train_moving_to_station", currentStation, targetStationDistance).color(darkBlue).addTo(output);
            }
        }
        if (signalState != TrainMapSync.SignalState.NOT_WAITING) {
            boolean chainSignal = signalState == TrainMapSync.SignalState.CHAIN_SIGNAL;
            CreateLang.translate("train_map.waiting_at_signal", new Object[0]).color(orange).addTo(output);
            if (signalState == TrainMapSync.SignalState.WAITING_FOR_REDSTONE) {
                CreateLang.translate("train_map.redstone_powered", new Object[0]).color(blue).addTo(output);
            } else {
                Train trainWaitingFor;
                UUID waitingFor = trainEntry.waitingForTrain;
                boolean trainFound = false;
                if (waitingFor != null && (trainWaitingFor = CreateClient.RAILWAYS.trains.get(waitingFor)) != null) {
                    CreateLang.translate("train_map.for_other_train", trainWaitingFor.name.getString()).color(blue).addTo(output);
                    trainFound = true;
                }
                if (!trainFound) {
                    if (chainSignal) {
                        CreateLang.translate("train_map.cannot_traverse_section", new Object[0]).color(blue).addTo(output);
                    } else {
                        CreateLang.translate("train_map.section_reserved", new Object[0]).color(blue).addTo(output);
                    }
                }
            }
        }
        if (trainEntry.fueled) {
            CreateLang.translate("train_map.fuel_boosted", new Object[0]).color(darkBlue).addTo(output);
        }
        return output;
    }

    private static Object drawPoints(GuiGraphics graphics, int mouseX, int mouseY, Object hoveredElement, Rect2i bounds) {
        PoseStack pose = graphics.pose();
        RenderSystem.enableDepthTest();
        for (TrackGraph graph : CreateClient.RAILWAYS.trackNetworks.values()) {
            for (GlobalStation station : graph.getPoints(EdgePointType.STATION)) {
                int y;
                double tLength;
                double t;
                Vec3 position;
                int x;
                TrackEdge edge;
                Couple edgeLocation = station.edgeLocation;
                TrackNode node = graph.locateNode((TrackNodeLocation)((Object)edgeLocation.getFirst()));
                TrackNode other = graph.locateNode((TrackNodeLocation)((Object)edgeLocation.getSecond()));
                if (node == null || other == null || node.getLocation().dimension != TrainMapRenderer.INSTANCE.trackingDim || (edge = graph.getConnection((Couple<TrackNode>)Couple.create((Object)node, (Object)other))) == null || !bounds.contains(x = Mth.floor((double)(position = edge.getPosition(graph, t = (tLength = station.getLocationOn(edge)) / edge.getLength())).x()), y = Mth.floor((double)position.z()))) continue;
                Vec3 diff = edge.getDirectionAt(tLength).normalize();
                int rotation = Mth.positiveModulo((int)Mth.floor((double)(0.5 + (Math.atan2(diff.z, diff.x) * 57.2957763671875 + 90.0 + (double)(station.isPrimary(node) ? 180 : 0)) / 45.0)), (int)8);
                AllGuiTextures sprite = AllGuiTextures.TRAINMAP_STATION_ORTHO;
                AllGuiTextures highlightSprite = AllGuiTextures.TRAINMAP_STATION_ORTHO_HIGHLIGHT;
                if (rotation % 2 != 0) {
                    sprite = AllGuiTextures.TRAINMAP_STATION_DIAGO;
                    highlightSprite = AllGuiTextures.TRAINMAP_STATION_DIAGO_HIGHLIGHT;
                }
                boolean highlight = hoveredElement == null && Math.max(Math.abs(mouseX - x), Math.abs(mouseY - y)) < 3;
                pose.pushPose();
                pose.translate((float)(x - 2), (float)(y - 2), 5.0f);
                pose.translate((double)sprite.getWidth() / 2.0, (double)sprite.getHeight() / 2.0, 0.0);
                pose.mulPose(Axis.ZP.rotationDegrees((float)(90 * (rotation / 2))));
                pose.translate((double)(-sprite.getWidth()) / 2.0, (double)(-sprite.getHeight()) / 2.0, 0.0);
                sprite.render(graphics, 0, 0);
                sprite.render(graphics, 0, 0);
                if (highlight) {
                    pose.translate(0.0f, 0.0f, 5.0f);
                    highlightSprite.render(graphics, -1, -1);
                    hoveredElement = station;
                }
                pose.popPose();
            }
        }
        return hoveredElement;
    }

    private static Object drawTrains(GuiGraphics graphics, int mouseX, int mouseY, Object hoveredElement, Rect2i bounds) {
        PoseStack pose = graphics.pose();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        int spriteYOffset = -3;
        double time = AnimationTickHolder.getTicks();
        time += (double)AnimationTickHolder.getPartialTicks();
        time -= TrainMapSyncClient.lastPacket;
        time /= 5.0;
        time = Mth.clamp((double)time, (double)0.0, (double)1.0);
        int[] sliceXShiftByRotationIndex = new int[]{0, 1, 2, 2, 3, -2, -2, -1};
        int[] sliceYShiftByRotationIndex = new int[]{3, 2, 2, 1, 0, 1, 2, 2};
        for (Train train : CreateClient.RAILWAYS.trains.values()) {
            int i;
            TrainMapSync.TrainMapSyncEntry trainEntry = TrainMapSyncClient.currentData.get(train.id);
            if (trainEntry == null) continue;
            Vec3 frontPos = Vec3.ZERO;
            List<Carriage> carriages = train.carriages;
            boolean otherDim = true;
            double avgY = 0.0;
            for (i = 0; i < carriages.size(); ++i) {
                for (boolean firstBogey : Iterate.trueAndFalse) {
                    avgY += trainEntry.getPosition(i, firstBogey, time).y();
                }
            }
            avgY /= (double)(carriages.size() * 2);
            for (i = 0; i < carriages.size(); ++i) {
                Carriage carriage = carriages.get(i);
                Vec3 pos1 = trainEntry.getPosition(i, true, time);
                Vec3 pos2 = trainEntry.getPosition(i, false, time);
                ResourceKey<Level> dim = trainEntry.dimensions.get(i);
                if (dim == null || dim != TrainMapRenderer.INSTANCE.trackingDim || !bounds.contains(Mth.floor((double)pos1.x()), Mth.floor((double)pos1.z())) && !bounds.contains(Mth.floor((double)pos2.x()), Mth.floor((double)pos2.z()))) continue;
                otherDim = false;
                if (!trainEntry.backwards && i == 0) {
                    frontPos = pos1;
                }
                if (trainEntry.backwards && i == train.carriages.size() - 1) {
                    frontPos = pos2;
                }
                Vec3 diff = pos2.subtract(pos1);
                int size = carriage.bogeySpacing + 1;
                Vec3 center = pos1.add(pos2).scale(0.5);
                double pX = center.x;
                double pY = center.z;
                int rotation = Mth.positiveModulo((int)Mth.floor((double)(0.5 + Math.atan2(diff.x, diff.z) * 57.2957763671875 / 22.5)), (int)8);
                if (trainEntry.state == TrainMapSync.TrainState.DERAILED) {
                    rotation = Mth.positiveModulo((int)((AnimationTickHolder.getTicks() / 8 + i * 3) * (i % 2 == 0 ? 1 : -1)), (int)8);
                }
                AllGuiTextures sprite = AllGuiTextures.TRAINMAP_SPRITES;
                int slices = 2;
                slices = rotation == 0 || rotation == 4 ? (slices += Mth.floor((double)((double)(size - 2) / 3.0 + 0.5))) : (rotation == 2 || rotation == 6 ? (slices += Mth.floor((double)((double)(((float)size - (5.0f - 2.0f * Mth.SQRT_OF_TWO)) / (2.0f * Mth.SQRT_OF_TWO)) + 0.5))) : (slices += Mth.floor((double)((double)(((float)size - (5.0f - Mth.sqrt((float)5.0f))) / Mth.sqrt((float)5.0f)) + 0.5))));
                slices = Math.max(2, slices);
                sprite.bind();
                pose.pushPose();
                float pivotX = 7.5f + (float)((slices - 3) * sliceXShiftByRotationIndex[rotation]) / 2.0f;
                float pivotY = 6.5f + (float)((slices - 3) * sliceYShiftByRotationIndex[rotation]) / 2.0f;
                pose.translate(pX - (double)pivotX, pY - (double)pivotY, 10.0 + avgY / 512.0 + (1024.0 + center.z() % 8192.0) / 1024.0);
                int trainColorIndex = train.mapColorIndex;
                int colorRow = trainColorIndex / 4;
                int colorCol = trainColorIndex % 4;
                for (int slice = 0; slice < slices; ++slice) {
                    int row;
                    int n = slice == 0 ? 1 : (row = slice == slices - 1 ? 2 : 3);
                    int sliceShifts = slice == 0 ? 0 : (slice == slices - 1 ? slice - 2 : slice - 1);
                    int col = rotation;
                    int positionX = sliceShifts * sliceXShiftByRotationIndex[rotation];
                    int positionY = sliceShifts * sliceYShiftByRotationIndex[rotation] + spriteYOffset;
                    int sheetX = col * 16 + colorCol * 128;
                    int sheetY = row * 16 + colorRow * 64;
                    graphics.blit(sprite.location, positionX, positionY, (float)sheetX, (float)sheetY, 16, 16, sprite.getWidth(), sprite.getHeight());
                }
                pose.popPose();
                boolean margin = true;
                int sizeX = 8 + (slices - 3) * sliceXShiftByRotationIndex[rotation];
                int sizeY = 12 + (slices - 3) * sliceYShiftByRotationIndex[rotation];
                double pXm = pX - (double)(sizeX / 2);
                double pYm = pY - (double)(sizeY / 2) + (double)spriteYOffset;
                if (hoveredElement != null || !((double)mouseX < pXm + (double)margin + (double)sizeX) || !((double)mouseX > pXm - (double)margin) || !((double)mouseY < pYm + (double)margin + (double)sizeY) || !((double)mouseY > pYm - (double)margin)) continue;
                hoveredElement = train;
            }
            if (otherDim || trainEntry.signalState == TrainMapSync.SignalState.NOT_WAITING) continue;
            pose.pushPose();
            pose.translate(frontPos.x - 0.5, frontPos.z - 0.5, 20.0 + (1024.0 + frontPos.z() % 8192.0) / 1024.0);
            AllGuiTextures.TRAINMAP_SIGNAL.render(graphics, 0, -3);
            pose.popPose();
        }
        return hoveredElement;
    }

    public static void redrawAll(ResourceKey<Level> dimension) {
        TrainMapRenderer map = TrainMapRenderer.INSTANCE;
        map.trackingVersion = CreateClient.RAILWAYS.version;
        map.trackingDim = dimension;
        map.trackingTheme = (CClient.TrainMapTheme)((Object)AllConfigs.client().trainMapColorTheme.get());
        map.startDrawing();
        int mainColor = -8628268;
        int darkerColor = -9419907;
        int darkerColorShadow = -11917484;
        switch (map.trackingTheme) {
            case GREY: {
                mainColor = -5720651;
                darkerColor = -8950164;
                darkerColorShadow = -11120562;
                break;
            }
            case WHITE: {
                mainColor = -1508871;
                darkerColor = -7826027;
                darkerColorShadow = -11120562;
                break;
            }
        }
        ObjectArrayList collisions = new ObjectArrayList();
        for (int phase = 0; phase <= 2; ++phase) {
            TrainMapManager.renderPhase(map, (List<Couple<Integer>>)collisions, mainColor, darkerColor, phase);
        }
        TrainMapManager.highlightYDifferences(map, (List<Couple<Integer>>)collisions, mainColor, darkerColor, darkerColor, darkerColorShadow);
        map.finishDrawing();
    }

    private static void renderPhase(TrainMapRenderer map, List<Couple<Integer>> collisions, int mainColor, int darkerColor, int phase) {
        int outlineColor = -16777216;
        int portalFrameColor = -11784869;
        int portalColor = -32810;
        for (TrackGraph graph : CreateClient.RAILWAYS.trackNetworks.values()) {
            for (TrackNodeLocation nodeLocation : graph.getNodes()) {
                if (nodeLocation.dimension != map.trackingDim) continue;
                TrackNode node = graph.locateNode(nodeLocation);
                Map<TrackNode, TrackEdge> connectionsFrom = graph.getConnectionsFrom(node);
                int hashCode = node.hashCode();
                block2: for (Map.Entry<TrackNode, TrackEdge> entry : connectionsFrom.entrySet()) {
                    int a;
                    TrackNode other = entry.getKey();
                    TrackNodeLocation otherLocation = other.getLocation();
                    TrackEdge edge = entry.getValue();
                    BezierConnection turn = edge.getTurn();
                    if (edge.isInterDimensional()) {
                        Vec3 vec = node.getLocation().getLocation();
                        int x = Mth.floor((double)vec.x);
                        int z = Mth.floor((double)vec.z);
                        if (phase == 2) continue;
                        if (phase == 0) {
                            map.setPixels(x - 3, z - 2, x + 3, z + 2, outlineColor);
                            map.setPixels(x - 2, z - 3, x + 2, z + 3, outlineColor);
                            continue;
                        }
                        int a2 = TrainMapManager.mapYtoAlpha(Mth.floor((double)vec.y()));
                        for (int xi = x - 2; xi <= x + 2; ++xi) {
                            for (int zi = z - 2; zi <= z + 2; ++zi) {
                                int c;
                                int alphaAt = map.alphaAt(xi, zi);
                                if (alphaAt > 0 && alphaAt != a2) {
                                    collisions.add((Couple<Integer>)Couple.create((Object)xi, (Object)zi));
                                }
                                int n = c = (xi - x) * (xi - x) + (zi - z) * (zi - z) > 2 ? portalFrameColor : portalColor;
                                if (alphaAt > a2) continue;
                                map.setPixel(xi, zi, TrainMapManager.markY(c, vec.y()));
                            }
                        }
                        continue;
                    }
                    if (other.hashCode() > hashCode) continue;
                    if (turn == null) {
                        int x;
                        int z;
                        boolean diagonal;
                        if (phase == 2) continue;
                        float x1 = nodeLocation.getX();
                        float z1 = nodeLocation.getZ();
                        float x2 = otherLocation.getX();
                        float z2 = otherLocation.getZ();
                        double y1 = nodeLocation.getLocation().y();
                        double y2 = otherLocation.getLocation().y();
                        float xDiffSign = Math.signum(x2 - x1);
                        float zDiffSign = Math.signum(z2 - z1);
                        boolean bl = diagonal = xDiffSign != 0.0f && zDiffSign != 0.0f;
                        if (xDiffSign != 0.0f) {
                            x2 = (float)((double)x2 - (double)xDiffSign * 0.25);
                            x1 = (float)((double)x1 + (double)xDiffSign * 0.25);
                        }
                        if (zDiffSign != 0.0f) {
                            z2 = (float)((double)z2 - (double)zDiffSign * 0.25);
                            z1 = (float)((double)z1 + (double)zDiffSign * 0.25);
                        }
                        x1 /= 2.0f;
                        x2 /= 2.0f;
                        z1 /= 2.0f;
                        z2 /= 2.0f;
                        int y = Mth.floor((double)y1);
                        a = TrainMapManager.mapYtoAlpha(y);
                        if (diagonal) {
                            z = Mth.floor((float)z1);
                            x = Mth.floor((float)x1);
                            int s = 0;
                            while ((float)s <= Math.abs(x1 - x2)) {
                                if (phase == 0) {
                                    map.setPixels(x - 1, z, x + 1, z + 1, outlineColor);
                                    map.setPixels(x, z - 1, x, z + 2, outlineColor);
                                    x = (int)((float)x + xDiffSign);
                                    z = (int)((float)z + zDiffSign);
                                } else {
                                    int alphaAt = map.alphaAt(x, z);
                                    if (alphaAt > 0 && alphaAt != a) {
                                        collisions.add((Couple<Integer>)Couple.create((Object)x, (Object)z));
                                    }
                                    if (alphaAt <= a) {
                                        map.setPixel(x, z, TrainMapManager.markY(mainColor, y));
                                    }
                                    if (map.alphaAt(x, z + 1) < a) {
                                        map.setPixel(x, z + 1, TrainMapManager.markY(darkerColor, y));
                                    }
                                    x = (int)((float)x + xDiffSign);
                                    z = (int)((float)z + zDiffSign);
                                }
                                ++s;
                            }
                            continue;
                        }
                        if (phase == 0) {
                            int x1i = Mth.floor((float)Math.min(x1, x2));
                            int z1i = Mth.floor((float)Math.min(z1, z2));
                            int x2i = Mth.floor((float)Math.max(x1, x2));
                            int z2i = Mth.floor((float)Math.max(z1, z2));
                            map.setPixels(x1i - 1, z1i, x2i + 1, z2i, outlineColor);
                            map.setPixels(x1i, z1i - 1, x2i, z2i + 1, outlineColor);
                            continue;
                        }
                        z = Mth.floor((float)z1);
                        x = Mth.floor((float)x1);
                        float diff = Math.max(Math.abs(x1 - x2), Math.abs(z1 - z2));
                        double yStep = (y2 - y1) / (double)diff;
                        int s = 0;
                        while ((float)s <= diff) {
                            int alphaAt = map.alphaAt(x, z);
                            if (alphaAt > 0 && alphaAt != a) {
                                collisions.add((Couple<Integer>)Couple.create((Object)x, (Object)z));
                            }
                            if (alphaAt <= a) {
                                map.setPixel(x, z, TrainMapManager.markY(mainColor, y));
                            }
                            x = (int)((float)x + xDiffSign);
                            y = (int)((double)y + yStep);
                            z = (int)((float)z + zDiffSign);
                            ++s;
                        }
                        continue;
                    }
                    if (phase == 1) continue;
                    BlockPos origin = (BlockPos)turn.bePositions.getFirst();
                    Map<Pair<Integer, Integer>, Double> rasterise = turn.rasterise();
                    for (boolean antialias : Iterate.falseAndTrue) {
                        for (Map.Entry<Pair<Integer, Integer>, Double> offset : rasterise.entrySet()) {
                            boolean mainColorBelowRight;
                            Pair<Integer, Integer> xz = offset.getKey();
                            int x = origin.getX() + (Integer)xz.getFirst();
                            int y = Mth.floor((double)((double)origin.getY() + offset.getValue() + 0.5));
                            int z = origin.getZ() + (Integer)xz.getSecond();
                            if (phase == 0) {
                                map.setPixels(x - 1, z, x + 1, z, outlineColor);
                                map.setPixels(x, z - 1, x, z + 1, outlineColor);
                                continue;
                            }
                            a = TrainMapManager.mapYtoAlpha(y);
                            if (!antialias) {
                                int alphaAt = map.alphaAt(x, z);
                                if (alphaAt > 0 && alphaAt != a) {
                                    collisions.add((Couple<Integer>)Couple.create((Object)x, (Object)z));
                                }
                                if (alphaAt > a) continue;
                                map.setPixel(x, z, TrainMapManager.markY(mainColor, y));
                                continue;
                            }
                            boolean mainColorBelowLeft = map.is(x + 1, z + 1, mainColor) && Math.abs(map.alphaAt(x + 1, z + 1) - a) <= 1;
                            boolean bl = mainColorBelowRight = map.is(x - 1, z + 1, mainColor) && Math.abs(map.alphaAt(x - 1, z + 1) - a) <= 1;
                            if (!mainColorBelowLeft && !mainColorBelowRight) continue;
                            int alphaAt = map.alphaAt(x, z + 1);
                            if (alphaAt > 0 && alphaAt != a) {
                                collisions.add((Couple<Integer>)Couple.create((Object)x, (Object)z));
                            }
                            if (alphaAt >= a) continue;
                            map.setPixel(x, z + 1, TrainMapManager.markY(darkerColor, y));
                            if (map.isEmpty(x + 1, z + 1)) {
                                map.setPixel(x + 1, z + 1, outlineColor);
                            }
                            if (map.isEmpty(x - 1, z + 1)) {
                                map.setPixel(x - 1, z + 1, outlineColor);
                            }
                            if (!map.isEmpty(x, z + 2)) continue;
                            map.setPixel(x, z + 2, outlineColor);
                        }
                        if (phase == 0) continue block2;
                    }
                }
            }
        }
    }

    private static void highlightYDifferences(TrainMapRenderer map, List<Couple<Integer>> collisions, int mainColor, int darkerColor, int mainColorShadow, int darkerColorShadow) {
        for (Couple<Integer> couple : collisions) {
            int z;
            int x = (Integer)couple.getFirst();
            int a = map.alphaAt(x, z = ((Integer)couple.getSecond()).intValue());
            if (a == 0) continue;
            for (int xi = x - 2; xi <= x + 2; ++xi) {
                for (int zi = z - 2; zi <= z + 2; ++zi) {
                    if (map.alphaAt(xi, zi) >= a) continue;
                    if (map.is(xi, zi, mainColor)) {
                        map.setPixel(xi, zi, FastColor.ABGR32.color((int)a, (int)mainColorShadow));
                        continue;
                    }
                    if (!map.is(xi, zi, darkerColor)) continue;
                    map.setPixel(xi, zi, FastColor.ABGR32.color((int)a, (int)darkerColorShadow));
                }
            }
        }
    }

    private static int mapYtoAlpha(double y) {
        int minY = Minecraft.getInstance().level.getMinBuildHeight();
        return Mth.clamp((int)(32 + Mth.floor((double)((y - (double)minY) / 4.0))), (int)0, (int)255);
    }

    private static int markY(int color, double y) {
        return FastColor.ABGR32.color((int)TrainMapManager.mapYtoAlpha(y), (int)color);
    }
}
