/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.network.packets;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.altitude_sensor.AltitudeSensorBlockEntity;
import dev.simulated_team.simulated.network.packets.helpers.SimBlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ConfigureAltitudeSensorPacket
extends SimBlockEntityConfigurationPacket<AltitudeSensorBlockEntity> {
    public static final CustomPacketPayload.Type<ConfigureAltitudeSensorPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("configure_altitude_sensor"));
    public static final StreamCodec<ByteBuf, ConfigureAltitudeSensorPacket> CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, SimBlockEntityConfigurationPacket::getPos, (StreamCodec)ByteBufCodecs.FLOAT, ConfigureAltitudeSensorPacket::highSignal, (StreamCodec)ByteBufCodecs.FLOAT, ConfigureAltitudeSensorPacket::lowSignal, ConfigureAltitudeSensorPacket::new);
    private final float highSignal;
    private final float lowSignal;

    public ConfigureAltitudeSensorPacket(BlockPos pos, float highSignal, float lowSignal) {
        super(pos);
        this.highSignal = Mth.clamp((float)highSignal, (float)0.0f, (float)1.0f);
        this.lowSignal = Mth.clamp((float)lowSignal, (float)0.0f, (float)1.0f);
    }

    private float lowSignal() {
        return this.lowSignal;
    }

    private float highSignal() {
        return this.highSignal;
    }

    @NotNull
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    protected void applySettings(ServerPlayer serverPlayer, AltitudeSensorBlockEntity be) {
        if (be instanceof AltitudeSensorBlockEntity) {
            AltitudeSensorBlockEntity abe = be;
            abe.highSignal = this.highSignal;
            abe.lowSignal = this.lowSignal;
            abe.notifyUpdate();
        }
    }
}
