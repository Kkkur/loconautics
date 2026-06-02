/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipLargerStreamCodecs
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.equipment.zapper.terrainzapper;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.equipment.zapper.ConfigureZapperPacket;
import com.simibubi.create.content.equipment.zapper.PlacementPatterns;
import com.simibubi.create.content.equipment.zapper.terrainzapper.PlacementOptions;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainBrushes;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainTools;
import com.simibubi.create.content.equipment.zapper.terrainzapper.WorldshaperItem;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipLargerStreamCodecs;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class ConfigureWorldshaperPacket
extends ConfigureZapperPacket {
    public static final StreamCodec<ByteBuf, ConfigureWorldshaperPacket> STREAM_CODEC = CatnipLargerStreamCodecs.composite((StreamCodec)CatnipStreamCodecs.HAND, packet -> packet.hand, PlacementPatterns.STREAM_CODEC, packet -> packet.pattern, TerrainBrushes.STREAM_CODEC, packet -> packet.brush, (StreamCodec)ByteBufCodecs.VAR_INT, packet -> packet.brushParamX, (StreamCodec)ByteBufCodecs.VAR_INT, packet -> packet.brushParamY, (StreamCodec)ByteBufCodecs.VAR_INT, packet -> packet.brushParamZ, TerrainTools.STREAM_CODEC, packet -> packet.tool, PlacementOptions.STREAM_CODEC, packet -> packet.placement, ConfigureWorldshaperPacket::new);
    private final TerrainBrushes brush;
    private final int brushParamX;
    private final int brushParamY;
    private final int brushParamZ;
    private final TerrainTools tool;
    private final PlacementOptions placement;

    public ConfigureWorldshaperPacket(InteractionHand hand, PlacementPatterns pattern, TerrainBrushes brush, int brushParamX, int brushParamY, int brushParamZ, TerrainTools tool, PlacementOptions placement) {
        super(hand, pattern);
        this.brush = brush;
        this.brushParamX = brushParamX;
        this.brushParamY = brushParamY;
        this.brushParamZ = brushParamZ;
        this.tool = tool;
        this.placement = placement;
    }

    @Override
    public void configureZapper(ItemStack stack) {
        WorldshaperItem.configureSettings(stack, this.pattern, this.brush, this.brushParamX, this.brushParamY, this.brushParamZ, this.tool, this.placement);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONFIGURE_WORLDSHAPER;
    }
}
