/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  foundry.veil.api.network.handler.ServerPacketContext
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.network.packets;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.RopeAttachment;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.RopeAttachmentPoint;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.ServerLevelRopeManager;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.ServerRopeStrand;
import foundry.veil.api.network.handler.ServerPacketContext;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record RopeBreakPacket(UUID uuid) implements CustomPacketPayload
{
    public static CustomPacketPayload.Type<RopeBreakPacket> TYPE = new CustomPacketPayload.Type(Simulated.path("break_rope"));
    public static StreamCodec<RegistryFriendlyByteBuf, RopeBreakPacket> CODEC = StreamCodec.composite((StreamCodec)UUIDUtil.STREAM_CODEC, RopeBreakPacket::uuid, RopeBreakPacket::new);

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPacketContext ctx) {
        ServerPlayer player = ctx.player();
        Level level = player.level();
        ServerLevelRopeManager manager = ServerLevelRopeManager.getOrCreate(level);
        ServerRopeStrand strand = manager.getStrand(this.uuid);
        if (strand != null) {
            RopeAttachment startAttachment = strand.getAttachment(RopeAttachmentPoint.START);
            if (startAttachment == null) {
                return;
            }
            BlockPos blockAttachment = startAttachment.blockAttachment();
            BlockEntity blockEntity = level.getBlockEntity(blockAttachment);
            if (!(blockEntity instanceof SmartBlockEntity)) {
                return;
            }
            SmartBlockEntity smartBlockEntity = (SmartBlockEntity)blockEntity;
            RopeStrandHolderBehavior holder = (RopeStrandHolderBehavior)smartBlockEntity.getBehaviour(RopeStrandHolderBehavior.TYPE);
            if (holder == null) {
                return;
            }
            holder.destroyRope(player, null);
        }
    }
}
