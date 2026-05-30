/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.trains.station;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.decoration.slidingDoor.DoorControl;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlock;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class StationEditPacket
extends BlockEntityConfigurationPacket<StationBlockEntity> {
    public static final StreamCodec<ByteBuf, StationEditPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, packet -> packet.pos, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.dropSchedule, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.assemblyMode, (StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)ByteBufCodecs.BOOL), packet -> packet.tryAssemble, (StreamCodec)CatnipStreamCodecBuilders.nullable(DoorControl.STREAM_CODEC), packet -> packet.doorControl, (StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)ByteBufCodecs.stringUtf8((int)256)), packet -> packet.name, StationEditPacket::new);
    private final boolean dropSchedule;
    private final boolean assemblyMode;
    private final Boolean tryAssemble;
    private final DoorControl doorControl;
    private final String name;

    public static StationEditPacket dropSchedule(BlockPos pos) {
        return new StationEditPacket(pos, true, false, false, null, null);
    }

    public static StationEditPacket tryAssemble(BlockPos pos) {
        return new StationEditPacket(pos, false, false, true, null, null);
    }

    public static StationEditPacket tryDisassemble(BlockPos pos) {
        return new StationEditPacket(pos, false, false, false, null, null);
    }

    public static StationEditPacket configure(BlockPos pos, boolean assemble, String name, DoorControl doorControl) {
        return new StationEditPacket(pos, false, assemble, null, doorControl, name);
    }

    private StationEditPacket(BlockPos pos, boolean dropSchedule, boolean assemblyMode, Boolean tryAssemble, DoorControl doorControl, String name) {
        super(pos);
        this.dropSchedule = dropSchedule;
        this.assemblyMode = assemblyMode;
        this.tryAssemble = tryAssemble;
        this.doorControl = doorControl;
        this.name = name;
    }

    @Override
    protected void applySettings(ServerPlayer player, StationBlockEntity be) {
        Level level = be.getLevel();
        BlockPos blockPos = be.getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);
        GlobalStation station = be.getStation();
        if (this.dropSchedule) {
            if (station == null) {
                return;
            }
            be.dropSchedule(player, station.getPresentTrain());
            return;
        }
        if (this.doorControl != null) {
            be.doorControls.set(this.doorControl);
        }
        if (this.name != null && !this.name.isBlank()) {
            be.updateName(this.name);
        }
        if (!(blockState.getBlock() instanceof StationBlock)) {
            return;
        }
        Boolean isAssemblyMode = (Boolean)blockState.getValue((Property)StationBlock.ASSEMBLING);
        boolean assemblyComplete = false;
        if (this.tryAssemble != null) {
            if (!isAssemblyMode.booleanValue()) {
                return;
            }
            if (this.tryAssemble.booleanValue()) {
                be.assemble(player.getUUID());
                assemblyComplete = station != null && station.getPresentTrain() != null;
            } else if (be.tryDisassembleTrain(player) && be.tryEnterAssemblyMode()) {
                be.refreshAssemblyInfo();
            }
            if (!assemblyComplete) {
                return;
            }
        }
        if (this.assemblyMode) {
            be.enterAssemblyMode(player);
        } else {
            be.exitAssemblyMode();
        }
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONFIGURE_STATION;
    }
}
