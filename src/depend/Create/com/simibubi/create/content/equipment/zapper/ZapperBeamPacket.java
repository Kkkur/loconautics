/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.equipment.zapper;

import com.simibubi.create.AllPackets;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.equipment.zapper.ShootGadgetPacket;
import com.simibubi.create.content.equipment.zapper.ShootableGadgetRenderHandler;
import com.simibubi.create.content.equipment.zapper.ZapperRenderHandler;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ZapperBeamPacket
extends ShootGadgetPacket {
    public static final StreamCodec<ByteBuf, ZapperBeamPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)CatnipStreamCodecs.VEC3, packet -> packet.location, (StreamCodec)CatnipStreamCodecs.HAND, packet -> packet.hand, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.self, (StreamCodec)CatnipStreamCodecs.VEC3, packet -> packet.target, ZapperBeamPacket::new);
    private final Vec3 target;

    public ZapperBeamPacket(Vec3 start, InteractionHand hand, boolean self, Vec3 target) {
        super(start, hand, self);
        this.target = target;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    protected ShootableGadgetRenderHandler getHandler() {
        return CreateClient.ZAPPER_RENDER_HANDLER;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    protected void handleAdditional() {
        CreateClient.ZAPPER_RENDER_HANDLER.addBeam(new ZapperRenderHandler.LaserBeam(this.location, this.target));
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.BEAM_EFFECT;
    }
}
