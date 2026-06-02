/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  foundry.veil.api.network.handler.ClientPacketContext
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 */
package dev.simulated_team.simulated.network.packets.contraption_diagram;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.entities.diagram.DiagramConfig;
import dev.simulated_team.simulated.content.entities.diagram.DiagramEntity;
import dev.simulated_team.simulated.content.entities.diagram.screen.DiagramScreen;
import foundry.veil.api.network.handler.ClientPacketContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record DiagramOpenPacket(int entityID, DiagramConfig config) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<DiagramOpenPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("open_diagram"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DiagramOpenPacket> CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, DiagramOpenPacket::entityID, DiagramConfig.STREAM_CODEC, DiagramOpenPacket::config, DiagramOpenPacket::new);

    public void handle(ClientPacketContext context) {
        Level level = context.level();
        assert (level != null);
        Entity entity = level.getEntity(this.entityID());
        if (entity instanceof DiagramEntity) {
            DiagramEntity diagram = (DiagramEntity)entity;
            SubLevel subLevel = Sable.HELPER.getContaining((Entity)diagram);
            if (subLevel == null) {
                return;
            }
            DiagramScreen.open(diagram, this.config, subLevel);
        }
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
