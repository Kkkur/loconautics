/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.ServerPacketContext
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.network.packets.linked_typewriter;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import foundry.veil.api.network.handler.ServerPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;

public record TypewriterDisconnectUser(BlockPos pos) implements CustomPacketPayload
{
    public static CustomPacketPayload.Type<TypewriterDisconnectUser> TYPE = new CustomPacketPayload.Type(Simulated.path("typewriter_disconnect_user"));
    public static StreamCodec<ByteBuf, TypewriterDisconnectUser> CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, TypewriterDisconnectUser::pos, TypewriterDisconnectUser::new);

    public void handle(ServerPacketContext context) {
        LinkedTypewriterBlockEntity lbe;
        BlockEntity blockEntity = context.level().getBlockEntity(this.pos);
        if (blockEntity instanceof LinkedTypewriterBlockEntity && (lbe = (LinkedTypewriterBlockEntity)blockEntity).checkUser(context.player().getUUID())) {
            lbe.disconnectUser();
        }
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
