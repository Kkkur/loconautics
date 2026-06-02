/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.AABB
 */
package com.simibubi.create.content.contraptions.sync;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public record ContraptionInteractionPacket(InteractionHand hand, int target, BlockPos localPos, Direction face) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, ContraptionInteractionPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)CatnipStreamCodecs.HAND), ContraptionInteractionPacket::hand, (StreamCodec)ByteBufCodecs.INT, ContraptionInteractionPacket::target, (StreamCodec)BlockPos.STREAM_CODEC, ContraptionInteractionPacket::localPos, (StreamCodec)Direction.STREAM_CODEC, ContraptionInteractionPacket::face, ContraptionInteractionPacket::new);

    public ContraptionInteractionPacket(AbstractContraptionEntity target, InteractionHand hand, BlockPos localPos, Direction side) {
        this(hand, target.getId(), localPos, side);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONTRAPTION_INTERACT;
    }

    public void handle(ServerPlayer sender) {
        if (sender == null) {
            return;
        }
        Entity entityByID = sender.level().getEntity(this.target);
        if (!(entityByID instanceof AbstractContraptionEntity)) {
            return;
        }
        AbstractContraptionEntity contraptionEntity = (AbstractContraptionEntity)entityByID;
        AABB bb = contraptionEntity.getBoundingBox();
        double boundsExtra = Math.max(bb.getXsize(), bb.getYsize());
        double d = sender.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 10.0 + boundsExtra;
        if (!sender.hasLineOfSight(entityByID)) {
            d -= 3.0;
        }
        d *= d;
        if (sender.distanceToSqr(entityByID) > d) {
            return;
        }
        if (contraptionEntity.handlePlayerInteraction((Player)sender, this.localPos, this.face, this.hand)) {
            sender.swing(this.hand, true);
        }
    }
}
