/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.logistics.redstoneRequester;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class RedstoneRequesterConfigurationPacket
extends BlockEntityConfigurationPacket<RedstoneRequesterBlockEntity> {
    public static final StreamCodec<ByteBuf, RedstoneRequesterConfigurationPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, packet -> packet.pos, (StreamCodec)ByteBufCodecs.STRING_UTF8, packet -> packet.address, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.allowPartial, (StreamCodec)CatnipStreamCodecBuilders.list((StreamCodec)ByteBufCodecs.INT), packet -> packet.amounts, RedstoneRequesterConfigurationPacket::new);
    private final String address;
    private final boolean allowPartial;
    private final List<Integer> amounts;

    public RedstoneRequesterConfigurationPacket(BlockPos pos, String address, boolean allowPartial, List<Integer> amounts) {
        super(pos);
        this.address = address;
        this.allowPartial = allowPartial;
        this.amounts = amounts;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONFIGURE_REDSTONE_REQUESTER;
    }

    @Override
    protected void applySettings(ServerPlayer player, RedstoneRequesterBlockEntity be) {
        be.encodedTargetAdress = this.address;
        List<BigItemStack> stacks = be.encodedRequest.stacks();
        for (int i = 0; i < stacks.size() && i < this.amounts.size(); ++i) {
            ItemStack stack = stacks.get((int)i).stack;
            if (stack.isEmpty()) continue;
            stacks.set(i, new BigItemStack(stack, this.amounts.get(i)));
        }
        if (!be.encodedRequest.orderedStacksMatchOrderedRecipes()) {
            be.encodedRequest = PackageOrderWithCrafts.simple(be.encodedRequest.stacks());
        }
        be.allowPartialRequests = this.allowPartial;
    }
}
