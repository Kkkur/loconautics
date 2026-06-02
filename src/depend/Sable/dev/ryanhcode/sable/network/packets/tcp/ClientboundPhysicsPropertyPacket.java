/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.PacketContext
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 */
package dev.ryanhcode.sable.network.packets.tcp;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.network.tcp.SableTCPPacket;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertiesDefinition;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertiesDefinitionLoader;
import foundry.veil.api.network.handler.PacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundPhysicsPropertyPacket(PhysicsBlockPropertiesDefinition definition) implements SableTCPPacket
{
    public static final CustomPacketPayload.Type<ClientboundPhysicsPropertyPacket> TYPE = new CustomPacketPayload.Type(Sable.sablePath("physics_property"));
    public static final StreamCodec<ByteBuf, ClientboundPhysicsPropertyPacket> CODEC = StreamCodec.composite(PhysicsBlockPropertiesDefinition.STREAM_CODEC, ClientboundPhysicsPropertyPacket::definition, ClientboundPhysicsPropertyPacket::new);

    @Override
    public void handle(PacketContext context) {
        Minecraft.getInstance().execute(() -> PhysicsBlockPropertiesDefinitionLoader.applyToBlocks(this.definition));
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
