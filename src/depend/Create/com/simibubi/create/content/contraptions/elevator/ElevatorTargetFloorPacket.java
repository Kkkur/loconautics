/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.contraptions.elevator;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.elevator.ElevatorColumn;
import com.simibubi.create.content.contraptions.elevator.ElevatorContactBlock;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record ElevatorTargetFloorPacket(int entityId, int targetY) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, ElevatorTargetFloorPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, ElevatorTargetFloorPacket::entityId, (StreamCodec)ByteBufCodecs.INT, ElevatorTargetFloorPacket::targetY, ElevatorTargetFloorPacket::new);

    public ElevatorTargetFloorPacket(AbstractContraptionEntity entity, int targetY) {
        this(entity.getId(), targetY);
    }

    public void handle(ServerPlayer sender) {
        Entity entityByID = sender.serverLevel().getEntity(this.entityId);
        if (!(entityByID instanceof AbstractContraptionEntity)) {
            return;
        }
        AbstractContraptionEntity ace = (AbstractContraptionEntity)entityByID;
        Contraption contraption = ace.getContraption();
        if (!(contraption instanceof ElevatorContraption)) {
            return;
        }
        ElevatorContraption ec = (ElevatorContraption)contraption;
        if (ace.distanceToSqr((Entity)sender) > 2500.0) {
            return;
        }
        Level level = sender.level();
        ElevatorColumn elevatorColumn = ElevatorColumn.get((LevelAccessor)level, ec.getGlobalColumn());
        if (!elevatorColumn.contacts.contains(this.targetY)) {
            return;
        }
        if (ec.isTargetUnreachable(this.targetY)) {
            return;
        }
        BlockPos pos = elevatorColumn.contactAt(this.targetY);
        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();
        if (!(block instanceof ElevatorContactBlock)) {
            return;
        }
        ElevatorContactBlock ecb = (ElevatorContactBlock)block;
        ecb.callToContactAndUpdate(elevatorColumn, blockState, level, pos, false);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.ELEVATOR_SET_FLOOR;
    }
}
