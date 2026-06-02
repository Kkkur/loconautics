/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.entity.Entity
 */
package com.simibubi.create.content.contraptions;

import com.simibubi.create.AllPackets;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import java.util.HashMap;
import java.util.Map;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record MountedStorageSyncPacket(int contraptionId, Map<BlockPos, MountedItemStorage> items, Map<BlockPos, MountedFluidStorage> fluids) implements ClientboundPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MountedStorageSyncPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, MountedStorageSyncPacket::contraptionId, (StreamCodec)ByteBufCodecs.map(HashMap::new, (StreamCodec)BlockPos.STREAM_CODEC, MountedItemStorage.STREAM_CODEC), MountedStorageSyncPacket::items, (StreamCodec)ByteBufCodecs.map(HashMap::new, (StreamCodec)BlockPos.STREAM_CODEC, MountedFluidStorage.STREAM_CODEC), MountedStorageSyncPacket::fluids, MountedStorageSyncPacket::new);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.MOUNTED_STORAGE_SYNC;
    }

    public void handle(LocalPlayer player) {
        Entity entity = Minecraft.getInstance().level.getEntity(this.contraptionId);
        if (!(entity instanceof AbstractContraptionEntity)) {
            return;
        }
        AbstractContraptionEntity contraption = (AbstractContraptionEntity)entity;
        contraption.getContraption().getStorage().handleSync(this, contraption);
    }
}
