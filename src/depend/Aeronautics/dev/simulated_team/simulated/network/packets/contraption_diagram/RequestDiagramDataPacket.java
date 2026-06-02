/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  foundry.veil.api.network.handler.ServerPacketContext
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.Level
 */
package dev.simulated_team.simulated.network.packets.contraption_diagram;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.entities.diagram.DiagramEntity;
import foundry.veil.api.network.handler.ServerPacketContext;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public record RequestDiagramDataPacket(UUID subLevel) implements CustomPacketPayload
{
    public static CustomPacketPayload.Type<RequestDiagramDataPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("request_diagram_data"));
    public static StreamCodec<RegistryFriendlyByteBuf, RequestDiagramDataPacket> CODEC = StreamCodec.composite((StreamCodec)UUIDUtil.STREAM_CODEC, RequestDiagramDataPacket::subLevel, RequestDiagramDataPacket::new);

    public void handle(ServerPacketContext context) {
        ServerPlayer player = context.player();
        Level level = player.level();
        SubLevelContainer container = SubLevelContainer.getContainer((Level)level);
        assert (container != null);
        SubLevel subLevel = container.getSubLevel(this.subLevel);
        if (subLevel instanceof ServerSubLevel) {
            ServerSubLevel serverSubLevel = (ServerSubLevel)subLevel;
            DiagramEntity.queueDiagramDataFor((SubLevel)serverSubLevel, player);
        }
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
