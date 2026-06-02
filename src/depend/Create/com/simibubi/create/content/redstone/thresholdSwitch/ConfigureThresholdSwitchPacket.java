/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 */
package com.simibubi.create.content.redstone.thresholdSwitch;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class ConfigureThresholdSwitchPacket
extends BlockEntityConfigurationPacket<ThresholdSwitchBlockEntity> {
    public static final StreamCodec<ByteBuf, ConfigureThresholdSwitchPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, packet -> packet.pos, (StreamCodec)ByteBufCodecs.INT, packet -> packet.offBelow, (StreamCodec)ByteBufCodecs.INT, packet -> packet.onAbove, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.invert, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.inStacks, ConfigureThresholdSwitchPacket::new);
    private final int offBelow;
    private final int onAbove;
    private final boolean invert;
    private final boolean inStacks;

    public ConfigureThresholdSwitchPacket(BlockPos pos, int offBelow, int onAbove, boolean invert, boolean inStacks) {
        super(pos);
        this.offBelow = offBelow;
        this.onAbove = onAbove;
        this.invert = invert;
        this.inStacks = inStacks;
    }

    @Override
    protected void applySettings(ServerPlayer player, ThresholdSwitchBlockEntity be) {
        be.offWhenBelow = this.offBelow;
        be.onWhenAbove = this.onAbove;
        be.setInverted(this.invert);
        be.inStacks = this.inStacks;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONFIGURE_STOCKSWITCH;
    }
}
