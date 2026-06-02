/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 */
package com.simibubi.create.content.contraptions.actors.seat;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ContraptionPlayerPassengerRotation {
    static boolean active;
    static int prevId;
    static float prevYaw;
    static float prevPitch;

    public static void tick() {
        active = (Boolean)AllConfigs.client().rotateWhenSeated.get();
    }

    public static void frame() {
        float pitch;
        float f;
        LocalPlayer player = Minecraft.getInstance().player;
        if (!active) {
            return;
        }
        if (player == null || !player.isPassenger()) {
            prevId = 0;
            return;
        }
        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof AbstractContraptionEntity)) {
            return;
        }
        AbstractContraptionEntity contraptionEntity = (AbstractContraptionEntity)vehicle;
        AbstractContraptionEntity.ContraptionRotationState rotationState = contraptionEntity.getRotationState();
        if (contraptionEntity instanceof CarriageContraptionEntity) {
            CarriageContraptionEntity cce = (CarriageContraptionEntity)contraptionEntity;
            f = cce.getViewYRot(AnimationTickHolder.getPartialTicks());
        } else {
            f = rotationState.yRotation;
        }
        float yaw = AngleHelper.wrapAngle180((float)f);
        if (contraptionEntity instanceof CarriageContraptionEntity) {
            CarriageContraptionEntity cce = (CarriageContraptionEntity)contraptionEntity;
            v1 = cce.getViewXRot(AnimationTickHolder.getPartialTicks());
        } else {
            v1 = pitch = 0.0f;
        }
        if (prevId != contraptionEntity.getId()) {
            prevId = contraptionEntity.getId();
            prevYaw = yaw;
            prevPitch = pitch;
        }
        float yawDiff = AngleHelper.getShortestAngleDiff((double)yaw, (double)prevYaw);
        float pitchDiff = AngleHelper.getShortestAngleDiff((double)pitch, (double)prevPitch);
        prevYaw = yaw;
        prevPitch = pitch;
        float yawRelativeToTrain = Mth.abs((float)AngleHelper.getShortestAngleDiff((double)player.getYRot(), (double)(-yaw - 90.0f)));
        if (yawRelativeToTrain > 120.0f) {
            pitchDiff *= -1.0f;
        } else if (yawRelativeToTrain > 60.0f) {
            pitchDiff *= 0.0f;
        }
        player.setYRot(player.getYRot() + yawDiff);
        player.setXRot(player.getXRot() + pitchDiff);
    }
}
