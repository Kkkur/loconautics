/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.simibubi.create.AllPackets;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.equipment.zapper.ShootGadgetPacket;
import com.simibubi.create.content.equipment.zapper.ShootableGadgetRenderHandler;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class PotatoCannonPacket
extends ShootGadgetPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, PotatoCannonPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)CatnipStreamCodecs.VEC3, packet -> packet.location, (StreamCodec)CatnipStreamCodecs.VEC3, packet -> packet.motion, (StreamCodec)ItemStack.OPTIONAL_STREAM_CODEC, packet -> packet.item, (StreamCodec)CatnipStreamCodecs.HAND, packet -> packet.hand, (StreamCodec)ByteBufCodecs.FLOAT, packet -> Float.valueOf(packet.pitch), (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.self, PotatoCannonPacket::new);
    private final float pitch;
    private final Vec3 motion;
    private final ItemStack item;

    public PotatoCannonPacket(Vec3 location, Vec3 motion, ItemStack item, InteractionHand hand, float pitch, boolean self) {
        super(location, hand, self);
        this.motion = motion;
        this.item = item;
        this.pitch = pitch;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    protected void handleAdditional() {
        CreateClient.POTATO_CANNON_RENDER_HANDLER.beforeShoot(this.pitch, this.location, this.motion, this.item);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    protected ShootableGadgetRenderHandler getHandler() {
        return CreateClient.POTATO_CANNON_RENDER_HANDLER;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.POTATO_CANNON;
    }
}
