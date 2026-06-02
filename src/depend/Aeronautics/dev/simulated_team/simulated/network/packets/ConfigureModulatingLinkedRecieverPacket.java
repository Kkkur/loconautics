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
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.network.packets;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.redstone.modulating_receiver.ModulatingLinkedReceiverBlockEntity;
import dev.simulated_team.simulated.network.packets.helpers.SimBlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class ConfigureModulatingLinkedRecieverPacket
extends SimBlockEntityConfigurationPacket<ModulatingLinkedReceiverBlockEntity> {
    public static final CustomPacketPayload.Type<ConfigureModulatingLinkedRecieverPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("configure_modulating_linked_reciever"));
    public static final StreamCodec<ByteBuf, ConfigureModulatingLinkedRecieverPacket> CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, SimBlockEntityConfigurationPacket::getPos, (StreamCodec)ByteBufCodecs.INT, ConfigureModulatingLinkedRecieverPacket::getMinRange, (StreamCodec)ByteBufCodecs.INT, ConfigureModulatingLinkedRecieverPacket::getMaxRange, ConfigureModulatingLinkedRecieverPacket::new);
    private final int minRange;
    private final int maxRange;

    public ConfigureModulatingLinkedRecieverPacket(BlockPos pos, int minRange, int maxRange) {
        super(pos);
        this.minRange = minRange;
        this.maxRange = maxRange;
    }

    public int getMinRange() {
        return this.minRange;
    }

    public int getMaxRange() {
        return this.maxRange;
    }

    @NotNull
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    protected void applySettings(ServerPlayer serverPlayer, ModulatingLinkedReceiverBlockEntity be) {
        if (be instanceof ModulatingLinkedReceiverBlockEntity) {
            ModulatingLinkedReceiverBlockEntity abe = be;
            abe.minRange = this.minRange;
            abe.maxRange = this.maxRange;
            abe.notifyUpdate();
        }
    }
}
