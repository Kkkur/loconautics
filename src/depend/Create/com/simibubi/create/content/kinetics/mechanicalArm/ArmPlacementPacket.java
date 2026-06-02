/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointHandler;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record ArmPlacementPacket(ListTag tag, BlockPos pos) implements ServerboundPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, ArmPlacementPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)CatnipStreamCodecs.COMPOUND_LIST_TAG, ArmPlacementPacket::tag, (StreamCodec)BlockPos.STREAM_CODEC, ArmPlacementPacket::pos, ArmPlacementPacket::new);

    public ArmPlacementPacket(Collection<ArmInteractionPoint> points, BlockPos pos) {
        this(new ListTag(), pos);
        for (ArmInteractionPoint point : points) {
            this.tag.add((Object)point.serialize(pos));
        }
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.PLACE_ARM;
    }

    public void handle(ServerPlayer player) {
        Level world = player.level();
        if (!world.isLoaded(this.pos)) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(this.pos);
        if (!(blockEntity instanceof ArmBlockEntity)) {
            return;
        }
        ArmBlockEntity arm = (ArmBlockEntity)blockEntity;
        arm.interactionPointTag = this.tag;
    }

    public record ClientBoundRequest(BlockPos pos) implements ClientboundPacketPayload
    {
        public static final StreamCodec<ByteBuf, ClientBoundRequest> STREAM_CODEC = BlockPos.STREAM_CODEC.map(ClientBoundRequest::new, ClientBoundRequest::pos);

        public BasePacketPayload.PacketTypeProvider getTypeProvider() {
            return AllPackets.S_PLACE_ARM;
        }

        @OnlyIn(value=Dist.CLIENT)
        public void handle(LocalPlayer player) {
            ArmInteractionPointHandler.flushSettings(this.pos);
        }
    }
}
