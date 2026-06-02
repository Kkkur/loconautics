/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.content.kinetics.chainConveyor;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ChainConveyorConnectionPacket
extends BlockEntityConfigurationPacket<ChainConveyorBlockEntity> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ChainConveyorConnectionPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, packet -> packet.pos, (StreamCodec)BlockPos.STREAM_CODEC, packet -> packet.targetPos, (StreamCodec)ItemStack.STREAM_CODEC, packet -> packet.chain, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.connect, ChainConveyorConnectionPacket::new);
    private final BlockPos targetPos;
    private final ItemStack chain;
    private final boolean connect;

    public ChainConveyorConnectionPacket(BlockPos pos, BlockPos targetPos, ItemStack chain, boolean connect) {
        super(pos);
        this.targetPos = targetPos;
        this.chain = chain;
        this.connect = connect;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CHAIN_CONVEYOR_CONNECT;
    }

    @Override
    protected int maxRange() {
        return (Integer)AllConfigs.server().kinetics.maxChainConveyorLength.get() + 16;
    }

    @Override
    protected void applySettings(ServerPlayer player, ChainConveyorBlockEntity be) {
        if (!be.getBlockPos().closerThan((Vec3i)this.targetPos, (double)(this.maxRange() - 16 + 1))) {
            return;
        }
        BlockEntity blockEntity = be.getLevel().getBlockEntity(this.targetPos);
        if (!(blockEntity instanceof ChainConveyorBlockEntity)) {
            return;
        }
        ChainConveyorBlockEntity clbe = (ChainConveyorBlockEntity)blockEntity;
        if (this.connect && !player.isCreative()) {
            int chainCost = ChainConveyorBlockEntity.getChainCost(this.targetPos.subtract((Vec3i)be.getBlockPos()));
            boolean hasEnough = ChainConveyorBlockEntity.getChainsFromInventory((Player)player, this.chain, chainCost, true);
            if (!hasEnough) {
                return;
            }
            ChainConveyorBlockEntity.getChainsFromInventory((Player)player, this.chain, chainCost, false);
        }
        if (!this.connect) {
            if (!player.isCreative()) {
                for (int chainCost = ChainConveyorBlockEntity.getChainCost(this.targetPos.subtract((Vec3i)this.pos)); chainCost > 0; chainCost -= 64) {
                    player.getInventory().placeItemBackInInventory(new ItemStack((ItemLike)Items.CHAIN, Math.min(chainCost, 64)));
                }
            }
            be.chainDestroyed(this.targetPos.subtract((Vec3i)be.getBlockPos()), false, true);
            be.getLevel().playSound(null, player.blockPosition(), SoundEvents.CHAIN_BREAK, SoundSource.BLOCKS);
        }
        if (this.connect) {
            if (!clbe.addConnectionTo(be.getBlockPos())) {
                return;
            }
        } else {
            clbe.removeConnectionTo(be.getBlockPos());
        }
        if (this.connect) {
            if (!be.addConnectionTo(this.targetPos)) {
                clbe.removeConnectionTo(be.getBlockPos());
            }
        } else {
            be.removeConnectionTo(this.targetPos);
        }
    }
}
