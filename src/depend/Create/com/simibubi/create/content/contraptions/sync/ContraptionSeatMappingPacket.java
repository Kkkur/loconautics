/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.contraptions.sync;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record ContraptionSeatMappingPacket(int entityId, Map<UUID, Integer> mapping, int dismountedId) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, ContraptionSeatMappingPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, ContraptionSeatMappingPacket::entityId, (StreamCodec)ByteBufCodecs.map(HashMap::new, (StreamCodec)UUIDUtil.STREAM_CODEC, (StreamCodec)ByteBufCodecs.INT), ContraptionSeatMappingPacket::mapping, (StreamCodec)ByteBufCodecs.INT, ContraptionSeatMappingPacket::dismountedId, ContraptionSeatMappingPacket::new);

    public ContraptionSeatMappingPacket {
        mapping = Map.copyOf(mapping);
    }

    public ContraptionSeatMappingPacket(int entityID, Map<UUID, Integer> mapping) {
        this(entityID, mapping, -1);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        Vec3 transformedVector;
        Entity entityByID = player.clientLevel.getEntity(this.entityId);
        if (!(entityByID instanceof AbstractContraptionEntity)) {
            return;
        }
        AbstractContraptionEntity contraptionEntity = (AbstractContraptionEntity)entityByID;
        if (this.dismountedId == player.getId() && (transformedVector = contraptionEntity.getPassengerPosition((Entity)player, 1.0f)) != null) {
            player.getPersistentData().put("ContraptionDismountLocation", (Tag)VecHelper.writeNBT((Vec3)transformedVector));
        }
        contraptionEntity.getContraption().setSeatMapping(this.mapping);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONTRAPTION_SEAT_MAPPING;
    }
}
