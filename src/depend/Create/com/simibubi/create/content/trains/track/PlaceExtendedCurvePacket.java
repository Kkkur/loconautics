/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.trains.track;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllPackets;
import com.simibubi.create.AllTags;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public record PlaceExtendedCurvePacket(boolean mainHand, boolean ctrlDown) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, PlaceExtendedCurvePacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.BOOL, PlaceExtendedCurvePacket::mainHand, (StreamCodec)ByteBufCodecs.BOOL, PlaceExtendedCurvePacket::ctrlDown, PlaceExtendedCurvePacket::new);

    public void handle(ServerPlayer sender) {
        ItemStack stack = sender.getItemInHand(this.mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        if (!AllTags.AllBlockTags.TRACKS.matches(stack)) {
            return;
        }
        stack.set(AllDataComponents.TRACK_EXTENDED_CURVE, (Object)true);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.PLACE_CURVED_TRACK;
    }
}
