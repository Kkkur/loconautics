/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.AllPackets;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.ContraptionRelocationPacket;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainRelocator;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public record TrainRelocationPacket(UUID trainId, BlockPos pos, Vec3 lookAngle, int entityId, boolean direction, BezierTrackPointLocation hoveredBezier) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, TrainRelocationPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)UUIDUtil.STREAM_CODEC, TrainRelocationPacket::trainId, (StreamCodec)BlockPos.STREAM_CODEC, TrainRelocationPacket::pos, (StreamCodec)CatnipStreamCodecs.VEC3, TrainRelocationPacket::lookAngle, (StreamCodec)ByteBufCodecs.INT, TrainRelocationPacket::entityId, (StreamCodec)ByteBufCodecs.BOOL, TrainRelocationPacket::direction, (StreamCodec)CatnipStreamCodecBuilders.nullable(BezierTrackPointLocation.STREAM_CODEC), TrainRelocationPacket::hoveredBezier, TrainRelocationPacket::new);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.RELOCATE_TRAIN;
    }

    public void handle(ServerPlayer sender) {
        Train train = Create.RAILWAYS.trains.get(this.trainId);
        Entity entity = sender.level().getEntity(this.entityId);
        String messagePrefix = sender.getName().getString() + " could not relocate Train ";
        if (train == null || !(entity instanceof CarriageContraptionEntity)) {
            Create.LOGGER.warn(messagePrefix + train.id.toString().substring(0, 5) + ": not present on server");
            return;
        }
        CarriageContraptionEntity cce = (CarriageContraptionEntity)entity;
        if (!train.id.equals(cce.trainId)) {
            return;
        }
        int verifyDistance = (Integer)AllConfigs.server().trains.maxTrackPlacementLength.get() * 2;
        if (!sender.canInteractWithBlock(this.pos, (double)verifyDistance)) {
            Create.LOGGER.warn(messagePrefix + train.name.getString() + ": player too far from clicked pos");
            return;
        }
        if (!sender.canInteractWithEntity((Entity)cce, (double)verifyDistance)) {
            Create.LOGGER.warn(messagePrefix + train.name.getString() + ": player too far from carriage entity");
            return;
        }
        if (TrainRelocator.relocate(train, sender.level(), this.pos, this.hoveredBezier, this.direction, this.lookAngle, false)) {
            sender.displayClientMessage((Component)CreateLang.translateDirect("train.relocate.success", new Object[0]).withStyle(ChatFormatting.GREEN), true);
            train.carriages.forEach(c -> c.forEachPresentEntity(e -> {
                e.nonDamageTicks = 10;
                CatnipServices.NETWORK.sendToClientsTrackingEntity((Entity)e, (CustomPacketPayload)new ContraptionRelocationPacket(e.getId()));
            }));
            return;
        }
        Create.LOGGER.warn(messagePrefix + train.name.getString() + ": relocation failed server-side");
    }
}
