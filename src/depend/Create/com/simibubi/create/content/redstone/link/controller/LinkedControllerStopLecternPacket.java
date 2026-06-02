/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.redstone.link.controller;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.redstone.link.controller.LecternControllerBlockEntity;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerPacketBase;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class LinkedControllerStopLecternPacket
extends LinkedControllerPacketBase {
    public static final StreamCodec<ByteBuf, LinkedControllerStopLecternPacket> STREAM_CODEC = BlockPos.STREAM_CODEC.map(LinkedControllerStopLecternPacket::new, LinkedControllerPacketBase::getLecternPos);

    public LinkedControllerStopLecternPacket(BlockPos lecternPos) {
        super(Objects.requireNonNull(lecternPos));
    }

    @Override
    protected void handleLectern(ServerPlayer player, LecternControllerBlockEntity lectern) {
        lectern.tryStopUsing((Player)player);
    }

    @Override
    protected void handleItem(ServerPlayer player, ItemStack heldItem) {
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.LINKED_CONTROLLER_USE_LECTERN;
    }
}
