/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 */
package com.simibubi.create.content.redstone.link.controller;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.redstone.link.controller.LecternControllerBlockEntity;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerPacketBase;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerServerHandler;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class LinkedControllerInputPacket
extends LinkedControllerPacketBase {
    public static final StreamCodec<ByteBuf, LinkedControllerInputPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)CatnipStreamCodecBuilders.list((StreamCodec)ByteBufCodecs.INT), p -> p.activatedButtons, (StreamCodec)ByteBufCodecs.BOOL, p -> p.press, (StreamCodec)CatnipStreamCodecs.NULLABLE_BLOCK_POS, LinkedControllerPacketBase::getLecternPos, LinkedControllerInputPacket::new);
    private final List<Integer> activatedButtons;
    private final boolean press;

    public LinkedControllerInputPacket(Collection<Integer> activatedButtons, boolean press) {
        this(activatedButtons, press, null);
    }

    public LinkedControllerInputPacket(Collection<Integer> activatedButtons, boolean press, BlockPos lecternPos) {
        super(lecternPos);
        this.activatedButtons = List.copyOf(activatedButtons);
        this.press = press;
    }

    @Override
    protected void handleLectern(ServerPlayer player, LecternControllerBlockEntity lectern) {
        if (lectern.isUsedBy((Player)player)) {
            this.handleItem(player, lectern.getController());
        }
    }

    @Override
    protected void handleItem(ServerPlayer player, ItemStack heldItem) {
        Level world = player.getCommandSenderWorld();
        UUID uniqueID = player.getUUID();
        BlockPos pos = player.blockPosition();
        if (player.isSpectator() && this.press) {
            return;
        }
        LinkedControllerServerHandler.receivePressed((LevelAccessor)world, pos, uniqueID, this.activatedButtons.stream().map(i -> LinkedControllerItem.toFrequency(heldItem, i)).collect(Collectors.toList()), this.press);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.LINKED_CONTROLLER_INPUT;
    }
}
