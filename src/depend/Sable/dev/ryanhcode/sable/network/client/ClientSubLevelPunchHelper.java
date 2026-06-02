/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.Position
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.network.client;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.index.SableAttributes;
import dev.ryanhcode.sable.mixinterface.entity.entity_sublevel_collision.EntityMovementExtension;
import dev.ryanhcode.sable.network.packets.tcp.ServerboundPunchSubLevelPacket;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class ClientSubLevelPunchHelper {
    public static void clientTryPunch(BlockHitResult hitResult, Level level, boolean testCreativeBreaking) {
        int customCooldown;
        SubLevel trackingSubLevel;
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player.blockActionRestricted(level, hitResult.getBlockPos(), minecraft.gameMode.getPlayerMode()) || player.getCooldowns().isOnCooldown(player.getMainHandItem().getItem())) {
            return;
        }
        if (player.isCreative() && testCreativeBreaking) {
            BlockState blockState = minecraft.level.getBlockState(hitResult.getBlockPos());
            if (player.getMainHandItem().getItem().canAttackBlock(blockState, (Level)minecraft.level, hitResult.getBlockPos(), (Player)player)) {
                return;
            }
        }
        Vector3d hitPosition = JOMLConversion.toJOML((Position)hitResult.getLocation());
        Vector3d hitDirection = JOMLConversion.toJOML((Position)player.getLookAngle());
        SubLevel targetSubLevel = Sable.HELPER.getContaining(level, (Vector3dc)hitPosition);
        if (targetSubLevel == (trackingSubLevel = ((EntityMovementExtension)player).sable$getTrackingSubLevel())) {
            return;
        }
        if (targetSubLevel != null) {
            targetSubLevel.lastPose().transformPosition(hitPosition);
            hitPosition.sub(targetSubLevel.lastPose().position());
        }
        if (trackingSubLevel != null) {
            trackingSubLevel.lastPose().transformNormalInverse(hitDirection);
        }
        if ((customCooldown = SableAttributes.getPushCooldownTicks((LivingEntity)player)) > 0) {
            player.getCooldowns().addCooldown(player.getMainHandItem().getItem(), customCooldown);
        }
        minecraft.getConnection().send((Packet)new ServerboundCustomPayloadPacket((CustomPacketPayload)new ServerboundPunchSubLevelPacket(hitResult.getBlockPos(), (Vector3dc)hitPosition, (Vector3dc)hitDirection)));
    }
}
