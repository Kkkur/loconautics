/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.entity.Entity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.foundation.networking;

import com.simibubi.create.AllPackets;
import com.simibubi.create.foundation.networking.ISyncPersistentData;
import java.util.HashSet;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record ISyncPersistentData.PersistentDataPacket(int entityId, CompoundTag readData) implements ClientboundPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, ISyncPersistentData.PersistentDataPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.VAR_INT, ISyncPersistentData.PersistentDataPacket::entityId, (StreamCodec)ByteBufCodecs.COMPOUND_TAG, ISyncPersistentData.PersistentDataPacket::readData, ISyncPersistentData.PersistentDataPacket::new);

    public ISyncPersistentData.PersistentDataPacket(Entity entity) {
        this(entity.getId(), entity.getPersistentData());
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        Entity entityByID = player.clientLevel.getEntity(this.entityId);
        CompoundTag data = entityByID.getPersistentData();
        new HashSet(data.getAllKeys()).forEach(arg_0 -> ((CompoundTag)data).remove(arg_0));
        data.merge(this.readData);
        if (!(entityByID instanceof ISyncPersistentData)) {
            return;
        }
        ((ISyncPersistentData)entityByID).onPersistentDataUpdated();
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.PERSISTENT_DATA;
    }
}
