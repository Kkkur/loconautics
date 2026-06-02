/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.equipment.symmetryWand;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryWandItem;
import com.simibubi.create.content.equipment.symmetryWand.mirror.SymmetryMirror;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public record ConfigureSymmetryWandPacket(InteractionHand hand, SymmetryMirror mirror) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, ConfigureSymmetryWandPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)CatnipStreamCodecs.HAND, ConfigureSymmetryWandPacket::hand, SymmetryMirror.STREAM_CODEC, ConfigureSymmetryWandPacket::mirror, ConfigureSymmetryWandPacket::new);

    public void handle(ServerPlayer player) {
        ItemStack stack = player.getItemInHand(this.hand);
        if (stack.getItem() instanceof SymmetryWandItem) {
            SymmetryWandItem.configureSettings(stack, this.mirror);
        }
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONFIGURE_SYMMETRY_WAND;
    }
}
