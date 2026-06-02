/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.ClientPacketContext
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 */
package dev.simulated_team.simulated.network.packets.handle;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.handle.PlayerHoldingHandleRenderer;
import foundry.veil.api.network.handler.ClientPacketContext;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundPlayersHoldingHandlePacket(Collection<UUID> uuids) implements CustomPacketPayload
{
    public static CustomPacketPayload.Type<ClientboundPlayersHoldingHandlePacket> TYPE = new CustomPacketPayload.Type(Simulated.path("players_holding_handles"));
    public static final StreamCodec<ByteBuf, ClientboundPlayersHoldingHandlePacket> CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.collection(HashSet::new, (StreamCodec)UUIDUtil.STREAM_CODEC), ClientboundPlayersHoldingHandlePacket::uuids, ClientboundPlayersHoldingHandlePacket::new);

    public void handle(ClientPacketContext context) {
        PlayerHoldingHandleRenderer.updatePlayerList(this.uuids);
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
