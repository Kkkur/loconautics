/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.contraptions.sync;

import com.simibubi.create.AllPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record LimbSwingUpdatePacket(int entityId, Vec3 position, float limbSwing) implements ClientboundPacketPayload
{
    public static final StreamCodec<ByteBuf, LimbSwingUpdatePacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, LimbSwingUpdatePacket::entityId, (StreamCodec)CatnipStreamCodecs.VEC3, LimbSwingUpdatePacket::position, (StreamCodec)ByteBufCodecs.FLOAT, LimbSwingUpdatePacket::limbSwing, LimbSwingUpdatePacket::new);

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        Entity entity = player.clientLevel.getEntity(this.entityId);
        if (entity == null) {
            return;
        }
        CompoundTag data = entity.getPersistentData();
        data.putInt("LastOverrideLimbSwingUpdate", 0);
        data.putFloat("OverrideLimbSwing", this.limbSwing);
        entity.lerpTo(this.position.x, this.position.y, this.position.z, entity.getYRot(), entity.getXRot(), 2);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.LIMBSWING_UPDATE;
    }
}
