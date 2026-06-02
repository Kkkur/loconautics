/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 */
package com.simibubi.create.content.redstone.displayLink;

import com.simibubi.create.AllPackets;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class DisplayLinkConfigurationPacket
extends BlockEntityConfigurationPacket<DisplayLinkBlockEntity> {
    public static final StreamCodec<ByteBuf, DisplayLinkConfigurationPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, packet -> packet.pos, (StreamCodec)ByteBufCodecs.COMPOUND_TAG, packet -> packet.configData, (StreamCodec)ByteBufCodecs.VAR_INT, packet -> packet.targetLine, DisplayLinkConfigurationPacket::new);
    private final CompoundTag configData;
    private final int targetLine;

    public DisplayLinkConfigurationPacket(BlockPos pos, CompoundTag configData, int targetLine) {
        super(pos);
        this.configData = configData;
        this.targetLine = targetLine;
    }

    @Override
    protected void applySettings(ServerPlayer player, DisplayLinkBlockEntity be) {
        be.targetLine = this.targetLine;
        if (!this.configData.contains("Id")) {
            be.notifyUpdate();
            return;
        }
        ResourceLocation id = ResourceLocation.tryParse((String)this.configData.getString("Id"));
        DisplaySource source = DisplaySource.get(id);
        if (source == null) {
            be.notifyUpdate();
            return;
        }
        if (be.activeSource == null || be.activeSource != source) {
            be.activeSource = source;
            be.setSourceConfig(this.configData.copy());
        } else {
            be.getSourceConfig().merge(this.configData);
        }
        be.updateGatheredData();
        be.notifyUpdate();
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONFIGURE_DATA_GATHERER;
    }
}
