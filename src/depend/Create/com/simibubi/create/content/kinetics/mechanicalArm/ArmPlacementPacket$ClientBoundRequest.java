/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.StreamCodec
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointHandler;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record ArmPlacementPacket.ClientBoundRequest(BlockPos pos) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, ArmPlacementPacket.ClientBoundRequest> STREAM_CODEC = BlockPos.STREAM_CODEC.map(ArmPlacementPacket.ClientBoundRequest::new, ArmPlacementPacket.ClientBoundRequest::pos);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.S_PLACE_ARM;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        ArmInteractionPointHandler.flushSettings(this.pos);
    }
}
