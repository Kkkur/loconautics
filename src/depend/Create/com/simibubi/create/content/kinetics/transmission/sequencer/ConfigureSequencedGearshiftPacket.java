/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 */
package com.simibubi.create.content.kinetics.transmission.sequencer;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.kinetics.transmission.sequencer.Instruction;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity;
import com.simibubi.create.foundation.codec.CreateStreamCodecs;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import java.util.Vector;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class ConfigureSequencedGearshiftPacket
extends BlockEntityConfigurationPacket<SequencedGearshiftBlockEntity> {
    public static final StreamCodec<ByteBuf, ConfigureSequencedGearshiftPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, packet -> packet.pos, (StreamCodec)Instruction.STREAM_CODEC.apply(CreateStreamCodecs.vector()), packet -> packet.instructions, ConfigureSequencedGearshiftPacket::new);
    private final Vector<Instruction> instructions;

    public ConfigureSequencedGearshiftPacket(BlockPos pos, Vector<Instruction> instructions) {
        super(pos);
        this.instructions = instructions;
    }

    @Override
    protected void applySettings(ServerPlayer player, SequencedGearshiftBlockEntity be) {
        if (be.computerBehaviour.hasAttachedComputer()) {
            return;
        }
        be.run(-1);
        be.instructions = this.instructions;
        be.sendData();
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONFIGURE_SEQUENCER;
    }
}
