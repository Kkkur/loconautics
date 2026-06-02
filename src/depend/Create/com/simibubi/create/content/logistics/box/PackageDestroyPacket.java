/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.logistics.box;

import com.simibubi.create.AllPackets;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record PackageDestroyPacket(Vec3 location, ItemStack box) implements ClientboundPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, PackageDestroyPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)CatnipStreamCodecs.VEC3, PackageDestroyPacket::location, (StreamCodec)ItemStack.STREAM_CODEC, PackageDestroyPacket::box, PackageDestroyPacket::new);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.PACKAGE_DESTROYED;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        ClientLevel level = Minecraft.getInstance().level;
        Vec3 motion = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)level.getRandom(), (float)0.125f);
        Vec3 pos = this.location.add(motion.scale(4.0));
        level.addParticle((ParticleOptions)new ItemParticleOption(ParticleTypes.ITEM, this.box), pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
    }
}
