/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ServerboundChainConveyorRidingPacket;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class ChainConveyorRidingHandler {
    public static BlockPos ridingChainConveyor;
    public static float chainPosition;
    public static BlockPos ridingConnection;
    public static boolean flipped;
    public static int catchingUp;

    public static void embark(BlockPos lift, float position, BlockPos connection) {
        ridingChainConveyor = lift;
        chainPosition = position;
        ridingConnection = connection;
        catchingUp = 20;
        Minecraft mc = Minecraft.getInstance();
        BlockEntity blockEntity = mc.level.getBlockEntity(ridingChainConveyor);
        if (blockEntity instanceof ChainConveyorBlockEntity) {
            ChainConveyorBlockEntity clbe = (ChainConveyorBlockEntity)blockEntity;
            flipped = clbe.getSpeed() < 0.0f;
        }
        MutableComponent component = Component.translatable((String)"mount.onboard", (Object[])new Object[]{mc.options.keyShift.getTranslatedKeyMessage()});
        mc.gui.setOverlayMessage((Component)component, false);
        mc.getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)SoundEvents.CHAIN_HIT, (float)1.0f, (float)0.5f));
    }

    public static void clientTick() {
        Vec3 targetPosition;
        if (ridingChainConveyor == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.isPaused()) {
            return;
        }
        if (!mc.player.isHolding(AllTags.AllItemTags.CHAIN_RIDEABLE::matches)) {
            ChainConveyorRidingHandler.stopRiding();
            return;
        }
        BlockEntity blockEntity = mc.level.getBlockEntity(ridingChainConveyor);
        if (mc.player.isShiftKeyDown() || !(blockEntity instanceof ChainConveyorBlockEntity)) {
            ChainConveyorRidingHandler.stopRiding();
            return;
        }
        ChainConveyorBlockEntity clbe = (ChainConveyorBlockEntity)blockEntity;
        if (ridingConnection != null && !clbe.connections.contains(ridingConnection)) {
            ChainConveyorRidingHandler.stopRiding();
            return;
        }
        clbe.prepareStats();
        float chainYOffset = 0.5f * mc.player.getScale();
        Vec3 playerPosition = mc.player.position().add(0.0, mc.player.getBoundingBox().getYsize() + (double)chainYOffset, 0.0);
        ChainConveyorRidingHandler.updateTargetPosition(mc, clbe);
        blockEntity = mc.level.getBlockEntity(ridingChainConveyor);
        if (!(blockEntity instanceof ChainConveyorBlockEntity)) {
            return;
        }
        clbe = (ChainConveyorBlockEntity)blockEntity;
        clbe.prepareStats();
        if (ridingConnection != null) {
            ChainConveyorBlockEntity.ConnectionStats stats = clbe.connectionStats.get(ridingConnection);
            targetPosition = stats.start().add(stats.end().subtract(stats.start()).normalize().scale((double)Math.min(stats.chainLength(), chainPosition)));
        } else {
            targetPosition = Vec3.atBottomCenterOf((Vec3i)ridingChainConveyor).add(VecHelper.rotate((Vec3)new Vec3(0.0, 0.25, 1.0), (double)chainPosition, (Direction.Axis)Direction.Axis.Y));
        }
        if (catchingUp > 0) {
            --catchingUp;
        }
        Vec3 diff = targetPosition.subtract(playerPosition);
        if (catchingUp == 0 && (diff.length() > 3.0 || diff.y < -1.0)) {
            ChainConveyorRidingHandler.stopRiding();
            return;
        }
        mc.player.setDeltaMovement(mc.player.getDeltaMovement().scale(0.75).add(diff.scale(0.25)));
        if (AnimationTickHolder.getTicks() % 10 == 0) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ServerboundChainConveyorRidingPacket(ridingChainConveyor, false));
        }
    }

    private static void stopRiding() {
        if (ridingChainConveyor != null) {
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new ServerboundChainConveyorRidingPacket(ridingChainConveyor, true));
        }
        ridingChainConveyor = null;
        ridingConnection = null;
        Minecraft.getInstance().getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)SoundEvents.CHAIN_HIT, (float)0.75f, (float)0.35f));
    }

    private static void updateTargetPosition(Minecraft mc, ChainConveyorBlockEntity clbe) {
        float serverSpeed = ServerSpeedProvider.get();
        float speed = clbe.getSpeed() / 360.0f;
        float radius = 1.5f;
        float distancePerTick = Math.abs(speed);
        float degreesPerTick = speed / ((float)Math.PI * radius) * 360.0f;
        if (ridingConnection != null) {
            ChainConveyorBlockEntity.ConnectionStats stats = clbe.connectionStats.get(ridingConnection);
            if (flipped != clbe.getSpeed() < 0.0f) {
                flipped = clbe.getSpeed() < 0.0f;
                ridingChainConveyor = clbe.getBlockPos().offset((Vec3i)ridingConnection);
                chainPosition = stats.chainLength() - chainPosition;
                ridingConnection = ridingConnection.multiply(-1);
                return;
            }
            chainPosition += serverSpeed * distancePerTick;
            chainPosition = Math.min(stats.chainLength(), chainPosition);
            if (chainPosition < stats.chainLength()) {
                return;
            }
            BlockEntity blockEntity = mc.level.getBlockEntity(clbe.getBlockPos().offset((Vec3i)ridingConnection));
            if (blockEntity instanceof ChainConveyorBlockEntity) {
                ChainConveyorBlockEntity clbe2 = (ChainConveyorBlockEntity)blockEntity;
                chainPosition = clbe.wrapAngle(stats.tangentAngle() + 180.0f + (float)(70 * (clbe.reversed ? -1 : 1)));
                ridingChainConveyor = clbe2.getBlockPos();
                ridingConnection = null;
            }
            return;
        }
        float prevChainPosition = chainPosition;
        chainPosition += serverSpeed * degreesPerTick;
        chainPosition = clbe.wrapAngle(chainPosition);
        BlockPos nearestLooking = BlockPos.ZERO;
        double bestDiff = Double.MAX_VALUE;
        for (BlockPos connection : clbe.connections) {
            double diff = Vec3.atLowerCornerOf((Vec3i)connection).normalize().distanceToSqr(mc.player.getLookAngle().normalize());
            if (diff > bestDiff) continue;
            nearestLooking = connection;
            bestDiff = diff;
        }
        if (nearestLooking == BlockPos.ZERO) {
            return;
        }
        float offBranchAngle = clbe.connectionStats.get(nearestLooking).tangentAngle();
        if (!clbe.loopThresholdCrossed(chainPosition, prevChainPosition, offBranchAngle)) {
            return;
        }
        chainPosition = 0.0f;
        ridingConnection = nearestLooking;
    }
}
