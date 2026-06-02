/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.equipment.extendoGrip;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public record ExtendoGripInteractionPacket(InteractionHand hand, int target, Vec3 point) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, ExtendoGripInteractionPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)CatnipStreamCodecs.HAND), ExtendoGripInteractionPacket::hand, (StreamCodec)ByteBufCodecs.INT, ExtendoGripInteractionPacket::target, (StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)CatnipStreamCodecs.VEC3), ExtendoGripInteractionPacket::point, ExtendoGripInteractionPacket::new);

    public ExtendoGripInteractionPacket(Entity target) {
        this(target, null);
    }

    public ExtendoGripInteractionPacket(Entity target, InteractionHand hand) {
        this(target, hand, null);
    }

    public ExtendoGripInteractionPacket(Entity target, InteractionHand hand, Vec3 specificPoint) {
        this(hand, target.getId(), specificPoint);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.EXTENDO_INTERACT;
    }

    public void handle(ServerPlayer sender) {
        if (sender == null) {
            return;
        }
        Entity entityByID = sender.level().getEntity(this.target);
        if (entityByID != null && ExtendoGripItem.isHoldingExtendoGrip((Player)sender)) {
            double d = sender.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
            if (!sender.hasLineOfSight(entityByID)) {
                d -= 3.0;
            }
            d *= d;
            if (sender.distanceToSqr(entityByID) > d) {
                return;
            }
            if (this.hand == null) {
                sender.attack(entityByID);
            } else if (this.point == null) {
                sender.interactOn(entityByID, this.hand);
            } else {
                entityByID.interactAt((Player)sender, this.point, this.hand);
            }
        }
    }
}
