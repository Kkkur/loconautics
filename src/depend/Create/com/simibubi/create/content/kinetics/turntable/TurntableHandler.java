/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.turntable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.turntable.TurntableBlockEntity;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class TurntableHandler {
    public static void gameRenderFrame(DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        BlockPos pos = mc.player.getOnPos();
        if (mc.gameMode == null) {
            return;
        }
        if (!AllBlocks.TURNTABLE.has(mc.level.getBlockState(pos))) {
            return;
        }
        if (!mc.player.onGround()) {
            return;
        }
        if (mc.isPaused()) {
            return;
        }
        BlockEntity blockEntity = mc.level.getBlockEntity(pos);
        if (!(blockEntity instanceof TurntableBlockEntity)) {
            return;
        }
        TurntableBlockEntity turnTable = (TurntableBlockEntity)blockEntity;
        float tickSpeed = mc.level.tickRateManager().tickrate() / 20.0f;
        float speed = turnTable.getSpeed() * 0.6666667f * tickSpeed * deltaTracker.getRealtimeDeltaTicks();
        if (speed == 0.0f) {
            return;
        }
        Vec3 origin = VecHelper.getCenterOf((Vec3i)pos);
        Vec3 offset = mc.player.position().subtract(origin);
        if (offset.length() > 0.25) {
            speed *= (float)Mth.clamp((double)((0.5 - offset.length()) * 2.0), (double)0.0, (double)1.0);
        }
        float yRotOffset = speed * deltaTracker.getGameTimeDeltaPartialTick(false);
        mc.player.setYRot(mc.player.getYRot() - yRotOffset);
        mc.player.yBodyRot -= yRotOffset;
    }
}
