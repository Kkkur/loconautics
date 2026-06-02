/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipLargerStreamCodecs
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.BlockHitResult
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.simibubi.create.AllPackets;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipLargerStreamCodecs;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ValueSettingsPacket
extends BlockEntityConfigurationPacket<SmartBlockEntity> {
    public static final StreamCodec<ByteBuf, ValueSettingsPacket> STREAM_CODEC = CatnipLargerStreamCodecs.composite((StreamCodec)BlockPos.STREAM_CODEC, p -> p.pos, (StreamCodec)ByteBufCodecs.VAR_INT, p -> p.row, (StreamCodec)ByteBufCodecs.VAR_INT, p -> p.value, (StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)CatnipStreamCodecs.HAND), p -> p.interactHand, (StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)CatnipStreamCodecs.BLOCK_HIT_RESULT), p -> p.hitResult, (StreamCodec)Direction.STREAM_CODEC, p -> p.side, (StreamCodec)ByteBufCodecs.BOOL, p -> p.ctrlDown, (StreamCodec)ByteBufCodecs.VAR_INT, p -> p.behaviourIndex, ValueSettingsPacket::new);
    private final int row;
    private final int value;
    private final InteractionHand interactHand;
    private final Direction side;
    private final boolean ctrlDown;
    private final int behaviourIndex;
    private final BlockHitResult hitResult;

    public ValueSettingsPacket(BlockPos pos, int row, int value, @Nullable InteractionHand interactHand, @Nullable BlockHitResult hitResult, Direction side, boolean ctrlDown, int behaviourIndex) {
        super(pos);
        this.row = row;
        this.value = value;
        this.interactHand = interactHand;
        this.hitResult = hitResult;
        this.side = side;
        this.ctrlDown = ctrlDown;
        this.behaviourIndex = behaviourIndex;
    }

    @Override
    protected void applySettings(ServerPlayer player, SmartBlockEntity be) {
        for (BlockEntityBehaviour behaviour : be.getAllBehaviours()) {
            ValueSettingsBehaviour valueSettingsBehaviour;
            if (!(behaviour instanceof ValueSettingsBehaviour) || !(valueSettingsBehaviour = (ValueSettingsBehaviour)((Object)behaviour)).acceptsValueSettings() || this.behaviourIndex != valueSettingsBehaviour.netId()) continue;
            if (this.interactHand != null) {
                valueSettingsBehaviour.onShortInteract((Player)player, this.interactHand, this.side, this.hitResult);
                return;
            }
            valueSettingsBehaviour.setValueSettings((Player)player, new ValueSettingsBehaviour.ValueSettings(this.row, this.value), this.ctrlDown);
            return;
        }
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.VALUE_SETTINGS;
    }
}
