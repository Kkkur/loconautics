/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.ServerPacketContext
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.network.packets.linked_typewriter;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import foundry.veil.api.network.handler.ServerPacketContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record TypewriterKeyInteractionPacket(BlockPos interactionPos, int key, int scanCode, int action) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<TypewriterKeyInteractionPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("key_interaction"));
    public static final StreamCodec<ByteBuf, TypewriterKeyInteractionPacket> CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, TypewriterKeyInteractionPacket::interactionPos, (StreamCodec)ByteBufCodecs.INT, TypewriterKeyInteractionPacket::key, (StreamCodec)ByteBufCodecs.INT, TypewriterKeyInteractionPacket::scanCode, (StreamCodec)ByteBufCodecs.INT, TypewriterKeyInteractionPacket::action, TypewriterKeyInteractionPacket::new);

    public void handle(ServerPacketContext context) {
        Level level = context.level();
        BlockEntity be = level.getBlockEntity(this.interactionPos);
        if (be instanceof LinkedTypewriterBlockEntity) {
            LinkedTypewriterBlockEntity typeWriter = (LinkedTypewriterBlockEntity)be;
            typeWriter.onKeyInteraction(context.player().getUUID(), null, this.key, this.action == 1);
        }
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
