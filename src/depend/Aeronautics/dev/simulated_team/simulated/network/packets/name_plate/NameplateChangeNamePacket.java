/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.ServerPacketContext
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.network.packets.name_plate;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.nameplate.NameplateBlockEntity;
import foundry.veil.api.network.handler.ServerPacketContext;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public record NameplateChangeNamePacket(BlockPos controllerPos, @Nullable String name) implements CustomPacketPayload
{
    public static CustomPacketPayload.Type<NameplateChangeNamePacket> TYPE = new CustomPacketPayload.Type(Simulated.path("nameplate_change_name"));
    public static StreamCodec<RegistryFriendlyByteBuf, NameplateChangeNamePacket> CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, NameplateChangeNamePacket::controllerPos, (StreamCodec)ByteBufCodecs.optional((StreamCodec)ByteBufCodecs.STRING_UTF8), packet -> Optional.ofNullable(packet.name()), NameplateChangeNamePacket::fromCodec);

    public static NameplateChangeNamePacket fromCodec(BlockPos controllerPos, Optional<String> name) {
        return new NameplateChangeNamePacket(controllerPos, name.orElse(null));
    }

    public void handle(ServerPacketContext context) {
        NameplateBlockEntity nbe;
        Level level = context.level();
        BlockEntity blockEntity = level.getBlockEntity(this.controllerPos());
        if (blockEntity instanceof NameplateBlockEntity && (nbe = (NameplateBlockEntity)blockEntity).allowsEditing()) {
            nbe.setName(this.name, true, (Player)context.player());
        }
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
