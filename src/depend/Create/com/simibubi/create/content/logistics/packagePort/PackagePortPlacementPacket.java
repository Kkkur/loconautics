/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.logistics.packagePort;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.packagePort.PackagePortBlockEntity;
import com.simibubi.create.content.logistics.packagePort.PackagePortTarget;
import com.simibubi.create.content.logistics.packagePort.PackagePortTargetSelectionHandler;
import com.simibubi.create.infrastructure.config.AllConfigs;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public record PackagePortPlacementPacket(PackagePortTarget target, BlockPos pos) implements ServerboundPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, PackagePortPlacementPacket> STREAM_CODEC = StreamCodec.composite(PackagePortTarget.STREAM_CODEC, PackagePortPlacementPacket::target, (StreamCodec)BlockPos.STREAM_CODEC, PackagePortPlacementPacket::pos, PackagePortPlacementPacket::new);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.PLACE_PACKAGE_PORT;
    }

    public void handle(ServerPlayer player) {
        if (player == null) {
            return;
        }
        Level world = player.level();
        if (world == null || !world.isLoaded(this.pos)) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(this.pos);
        if (!(blockEntity instanceof PackagePortBlockEntity)) {
            return;
        }
        PackagePortBlockEntity ppbe = (PackagePortBlockEntity)blockEntity;
        if (!this.target.canSupport(ppbe)) {
            return;
        }
        Vec3 targetLocation = this.target.getExactTargetLocation(ppbe, (LevelAccessor)world, this.pos);
        if (targetLocation == Vec3.ZERO || !targetLocation.closerThan((Position)Vec3.atBottomCenterOf((Vec3i)this.pos), (double)((Integer)AllConfigs.server().logistics.packagePortRange.get() + 2))) {
            return;
        }
        this.target.setup(ppbe, (LevelAccessor)world, this.pos);
        ppbe.target = this.target;
        ppbe.notifyUpdate();
        ppbe.use((Player)player);
    }

    public record ClientBoundRequest(BlockPos pos) implements ClientboundPacketPayload
    {
        public static final StreamCodec<ByteBuf, ClientBoundRequest> STREAM_CODEC = BlockPos.STREAM_CODEC.map(ClientBoundRequest::new, ClientBoundRequest::pos);

        public BasePacketPayload.PacketTypeProvider getTypeProvider() {
            return AllPackets.S_PLACE_PACKAGE_PORT;
        }

        public void handle(LocalPlayer player) {
            PackagePortTargetSelectionHandler.flushSettings(this.pos);
        }
    }
}
