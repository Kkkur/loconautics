/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 */
package com.simibubi.create.content.contraptions.elevator;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.elevator.ElevatorContactBlockEntity;
import com.simibubi.create.content.decoration.slidingDoor.DoorControl;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class ElevatorContactEditPacket
extends BlockEntityConfigurationPacket<ElevatorContactBlockEntity> {
    public static final StreamCodec<FriendlyByteBuf, ElevatorContactEditPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, packet -> packet.pos, (StreamCodec)ByteBufCodecs.stringUtf8((int)4), packet -> packet.shortName, (StreamCodec)ByteBufCodecs.stringUtf8((int)90), packet -> packet.longName, DoorControl.STREAM_CODEC, packet -> packet.doorControl, ElevatorContactEditPacket::new);
    private final String shortName;
    private final String longName;
    private final DoorControl doorControl;

    public ElevatorContactEditPacket(BlockPos pos, String shortName, String longName, DoorControl doorControl) {
        super(pos);
        this.shortName = shortName;
        this.longName = longName;
        this.doorControl = doorControl;
    }

    @Override
    protected void applySettings(ServerPlayer player, ElevatorContactBlockEntity be) {
        be.updateName(this.shortName, this.longName);
        be.doorControls.set(this.doorControl);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONFIGURE_ELEVATOR_CONTACT;
    }
}
