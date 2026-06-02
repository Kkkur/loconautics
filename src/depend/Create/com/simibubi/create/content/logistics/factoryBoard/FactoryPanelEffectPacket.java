/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.logistics.factoryBoard;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnection;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record FactoryPanelEffectPacket(FactoryPanelPosition fromPos, FactoryPanelPosition toPos, boolean success) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, FactoryPanelEffectPacket> STREAM_CODEC = StreamCodec.composite(FactoryPanelPosition.STREAM_CODEC, FactoryPanelEffectPacket::fromPos, FactoryPanelPosition.STREAM_CODEC, FactoryPanelEffectPacket::toPos, (StreamCodec)ByteBufCodecs.BOOL, FactoryPanelEffectPacket::success, FactoryPanelEffectPacket::new);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.FACTORY_PANEL_EFFECT;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        ClientLevel level = Minecraft.getInstance().level;
        BlockState blockState = level.getBlockState(this.fromPos.pos());
        if (!AllBlocks.FACTORY_GAUGE.has(blockState)) {
            return;
        }
        FactoryPanelBehaviour panelBehaviour = FactoryPanelBehaviour.at((BlockAndTintGetter)level, this.toPos);
        if (panelBehaviour != null) {
            panelBehaviour.bulb.setValue(1.0);
            FactoryPanelConnection connection = panelBehaviour.targetedBy.get(this.fromPos);
            if (connection != null) {
                connection.success = this.success;
            }
        }
    }
}
