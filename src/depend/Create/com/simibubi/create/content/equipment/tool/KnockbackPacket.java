/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.entity.LivingEntity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.equipment.tool;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.equipment.tool.CardboardSwordItem;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record KnockbackPacket(float yRot, float strength) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, KnockbackPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.FLOAT, KnockbackPacket::yRot, (StreamCodec)ByteBufCodecs.FLOAT, KnockbackPacket::strength, KnockbackPacket::new);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.KNOCKBACK;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        if (player != null) {
            CardboardSwordItem.knockback((LivingEntity)player, this.strength, this.yRot);
        }
    }
}
