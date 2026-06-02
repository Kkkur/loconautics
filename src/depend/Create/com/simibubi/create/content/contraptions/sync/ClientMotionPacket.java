/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.sync;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.sync.LimbSwingUpdatePacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public record ClientMotionPacket(Vec3 motion, boolean onGround, float limbSwing) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, ClientMotionPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)CatnipStreamCodecs.VEC3, ClientMotionPacket::motion, (StreamCodec)ByteBufCodecs.BOOL, ClientMotionPacket::onGround, (StreamCodec)ByteBufCodecs.FLOAT, ClientMotionPacket::limbSwing, ClientMotionPacket::new);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CLIENT_MOTION;
    }

    public void handle(ServerPlayer sender) {
        if (sender == null) {
            return;
        }
        sender.setDeltaMovement(this.motion);
        sender.setOnGround(this.onGround);
        if (this.onGround) {
            sender.causeFallDamage(sender.fallDistance, 1.0f, sender.damageSources().fall());
            sender.fallDistance = 0.0f;
            sender.connection.aboveGroundTickCount = 0;
            sender.connection.aboveGroundVehicleTickCount = 0;
        }
        CatnipServices.NETWORK.sendToClientsTrackingEntity((Entity)sender, (CustomPacketPayload)new LimbSwingUpdatePacket(sender.getId(), sender.position(), this.limbSwing));
    }
}
