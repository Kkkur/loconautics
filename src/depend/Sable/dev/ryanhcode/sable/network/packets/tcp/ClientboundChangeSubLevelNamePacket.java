/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.PacketContext
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.network.packets.tcp;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.network.tcp.SableTCPPacket;
import dev.ryanhcode.sable.sublevel.SubLevel;
import foundry.veil.api.network.handler.PacketContext;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;

public record ClientboundChangeSubLevelNamePacket(UUID subLevelID, @Nullable String name) implements SableTCPPacket
{
    public static CustomPacketPayload.Type<ClientboundChangeSubLevelNamePacket> TYPE = new CustomPacketPayload.Type(Sable.sablePath("change_sub_level_name"));
    public static StreamCodec<RegistryFriendlyByteBuf, ClientboundChangeSubLevelNamePacket> CODEC = StreamCodec.composite((StreamCodec)UUIDUtil.STREAM_CODEC, ClientboundChangeSubLevelNamePacket::subLevelID, (StreamCodec)ByteBufCodecs.optional((StreamCodec)ByteBufCodecs.STRING_UTF8), packet -> Optional.ofNullable(packet.name()), (uuid, optionalName) -> new ClientboundChangeSubLevelNamePacket((UUID)uuid, optionalName.orElse(null)));

    @Override
    public void handle(PacketContext context) {
        SubLevelContainer container = SubLevelContainer.getContainer(context.level());
        if (container != null) {
            SubLevel subLevel = container.getSubLevel(this.subLevelID);
            if (subLevel != null) {
                subLevel.setName(this.name);
            } else {
                Sable.LOGGER.error("Attempted to set name for a client sub-level that does not exist!");
            }
        }
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
