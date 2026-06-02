/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.placement.PlacementClient
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.LayeredDraw$Layer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 */
package com.simibubi.create.content.trains;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import com.simibubi.create.content.trains.HonkPacket;
import com.simibubi.create.content.trains.TrainHUDUpdatePacket;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.ControlsUtil;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.placement.PlacementClient;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class TrainHUD {
    public static final LayeredDraw.Layer OVERLAY = TrainHUD::renderOverlay;
    static LerpedFloat displayedSpeed = LerpedFloat.linear();
    static LerpedFloat displayedThrottle = LerpedFloat.linear();
    static LerpedFloat displayedPromptSize = LerpedFloat.linear();
    static Double editedThrottle = null;
    static int hudPacketCooldown = 5;
    static int honkPacketCooldown = 5;
    public static Component currentPrompt;
    public static boolean currentPromptShadow;
    public static int promptKeepAlive;
    static boolean usedToHonk;

    public static void tick() {
        if (promptKeepAlive > 0) {
            --promptKeepAlive;
        } else {
            currentPrompt = null;
        }
        Minecraft mc = Minecraft.getInstance();
        displayedPromptSize.chase(currentPrompt != null ? (double)(mc.font.width((FormattedText)currentPrompt) + 17) : 0.0, 0.5, LerpedFloat.Chaser.EXP);
        displayedPromptSize.tickChaser();
        Carriage carriage = TrainHUD.getCarriage();
        if (carriage == null) {
            return;
        }
        Train train = carriage.train;
        double value = Math.abs(train.speed) / (double)(train.maxSpeed() * AllConfigs.server().trains.manualTrainSpeedModifier.getF());
        value = Mth.clamp((double)(value + (double)0.05f), (double)0.0, (double)1.0);
        displayedSpeed.chase((double)((float)((int)(value * 18.0)) / 18.0f), 0.5, LerpedFloat.Chaser.EXP);
        displayedSpeed.tickChaser();
        displayedThrottle.chase(editedThrottle != null ? editedThrottle : train.throttle, 0.75, LerpedFloat.Chaser.EXP);
        displayedThrottle.tickChaser();
        boolean isSprintKeyPressed = ControlsUtil.isActuallyPressed(mc.options.keySprint);
        if (isSprintKeyPressed && honkPacketCooldown-- <= 0) {
            train.determineHonk((Level)mc.level);
            if (train.lowHonk != null) {
                CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new HonkPacket.Serverbound(train, true));
                honkPacketCooldown = 5;
                usedToHonk = true;
            }
        }
        if (!isSprintKeyPressed && usedToHonk) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new HonkPacket.Serverbound(train, false));
            honkPacketCooldown = 0;
            usedToHonk = false;
        }
        if (editedThrottle == null) {
            return;
        }
        if (Mth.equal((double)editedThrottle, (double)train.throttle)) {
            editedThrottle = null;
            hudPacketCooldown = 5;
            return;
        }
        if (hudPacketCooldown-- <= 0) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new TrainHUDUpdatePacket.Serverbound(train, editedThrottle));
            hudPacketCooldown = 5;
        }
    }

    private static Carriage getCarriage() {
        AbstractContraptionEntity abstractContraptionEntity = ControlsHandler.getContraption();
        if (!(abstractContraptionEntity instanceof CarriageContraptionEntity)) {
            return null;
        }
        CarriageContraptionEntity cce = (CarriageContraptionEntity)abstractContraptionEntity;
        return cce.getCarriage();
    }

    private static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(false);
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        AbstractContraptionEntity abstractContraptionEntity = ControlsHandler.getContraption();
        if (!(abstractContraptionEntity instanceof CarriageContraptionEntity)) {
            return;
        }
        CarriageContraptionEntity cce = (CarriageContraptionEntity)abstractContraptionEntity;
        Carriage carriage = cce.getCarriage();
        if (carriage == null) {
            return;
        }
        Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
        if (cameraEntity == null) {
            return;
        }
        BlockPos localPos = ControlsHandler.getControlsPos();
        if (localPos == null) {
            return;
        }
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate((float)(guiGraphics.guiWidth() / 2 - 91), (float)(guiGraphics.guiHeight() - 29), 0.0f);
        AllGuiTextures.TRAIN_HUD_FRAME.render(guiGraphics, -2, 1);
        AllGuiTextures.TRAIN_HUD_SPEED_BG.render(guiGraphics, 0, 0);
        int w = (int)((float)AllGuiTextures.TRAIN_HUD_SPEED.getWidth() * displayedSpeed.getValue(partialTicks));
        int h = AllGuiTextures.TRAIN_HUD_SPEED.getHeight();
        guiGraphics.blit(AllGuiTextures.TRAIN_HUD_SPEED.location, 0, 0, 0, (float)AllGuiTextures.TRAIN_HUD_SPEED.getStartX(), (float)AllGuiTextures.TRAIN_HUD_SPEED.getStartY(), w, h, 256, 256);
        int promptSize = (int)displayedPromptSize.getValue(partialTicks);
        if (promptSize > 1) {
            poseStack.pushPose();
            poseStack.translate((float)promptSize / -2.0f + 91.0f, -27.0f, 0.0f);
            AllGuiTextures.TRAIN_PROMPT_L.render(guiGraphics, -3, 0);
            AllGuiTextures.TRAIN_PROMPT_R.render(guiGraphics, promptSize, 0);
            guiGraphics.blit(AllGuiTextures.TRAIN_PROMPT.location, 0, 0, 0, (float)AllGuiTextures.TRAIN_PROMPT.getStartX() + (128.0f - (float)promptSize / 2.0f), (float)AllGuiTextures.TRAIN_PROMPT.getStartY(), promptSize, AllGuiTextures.TRAIN_PROMPT.getHeight(), 256, 256);
            poseStack.popPose();
            Font font = mc.font;
            if (currentPrompt != null && font.width((FormattedText)currentPrompt) < promptSize - 10) {
                poseStack.pushPose();
                poseStack.translate((float)font.width((FormattedText)currentPrompt) / -2.0f + 82.0f, -27.0f, 100.0f);
                if (currentPromptShadow) {
                    guiGraphics.drawString(font, currentPrompt, 9, 4, 0x544D45);
                } else {
                    guiGraphics.drawString(font, currentPrompt, 9, 4, 0x544D45, false);
                }
                poseStack.popPose();
            }
        }
        AllGuiTextures.TRAIN_HUD_DIRECTION.render(guiGraphics, 77, -20);
        w = (int)((float)AllGuiTextures.TRAIN_HUD_THROTTLE.getWidth() * (1.0f - displayedThrottle.getValue(partialTicks)));
        int invW = AllGuiTextures.TRAIN_HUD_THROTTLE.getWidth() - w;
        guiGraphics.blit(AllGuiTextures.TRAIN_HUD_THROTTLE.location, invW, 0, 0, (float)(AllGuiTextures.TRAIN_HUD_THROTTLE.getStartX() + invW), (float)AllGuiTextures.TRAIN_HUD_THROTTLE.getStartY(), w, h, 256, 256);
        AllGuiTextures.TRAIN_HUD_THROTTLE_POINTER.render(guiGraphics, Math.max(1, AllGuiTextures.TRAIN_HUD_THROTTLE.getWidth() - w) - 3, -2);
        StructureTemplate.StructureBlockInfo info = cce.getContraption().getBlocks().get(localPos);
        Direction initialOrientation = cce.getInitialOrientation().getCounterClockWise();
        boolean inverted = false;
        if (info != null && info.state().hasProperty((Property)ControlsBlock.FACING)) {
            inverted = !((Direction)info.state().getValue((Property)ControlsBlock.FACING)).equals((Object)initialOrientation);
        }
        boolean reversing = ControlsHandler.currentlyPressed.contains(1);
        inverted ^= reversing;
        int angleOffset = (ControlsHandler.currentlyPressed.contains(2) ? -45 : 0) + (ControlsHandler.currentlyPressed.contains(3) ? 45 : 0);
        if (reversing) {
            angleOffset *= -1;
        }
        float snapSize = 22.5f;
        float diff = AngleHelper.getShortestAngleDiff((double)cameraEntity.getYRot(), (double)cce.yaw) + (float)(inverted ? -90 : 90);
        if (Math.abs(diff) < 60.0f) {
            diff = 0.0f;
        }
        float angle = diff + (float)angleOffset;
        float snappedAngle = snapSize * (float)Math.round(angle / snapSize) % 360.0f;
        poseStack.translate(91.0f, -9.0f, 0.0f);
        poseStack.scale(0.925f, 0.925f, 1.0f);
        PlacementClient.textured((PoseStack)poseStack, (float)0.0f, (float)0.0f, (float)1.0f, (float)snappedAngle);
        poseStack.popPose();
    }

    public static boolean onScroll(double delta) {
        Carriage carriage = TrainHUD.getCarriage();
        if (carriage == null) {
            return false;
        }
        double prevThrottle = editedThrottle == null ? carriage.train.throttle : editedThrottle;
        editedThrottle = Mth.clamp((double)(prevThrottle + (double)((float)(delta > 0.0 ? 1 : -1) / 18.0f)), (double)0.0555555559694767, (double)1.0);
        return true;
    }

    static {
        promptKeepAlive = 0;
    }
}
