/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids.transfer;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.fluids.FluidFX;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;

public record FluidSplashPacket(BlockPos pos, FluidStack fluid) implements ClientboundPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidSplashPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, FluidSplashPacket::pos, (StreamCodec)FluidStack.OPTIONAL_STREAM_CODEC, FluidSplashPacket::fluid, FluidSplashPacket::new);

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        Vec3 vec3 = new Vec3((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ());
        if (player.position().distanceTo(vec3) > 100.0) {
            return;
        }
        FluidFX.splash(this.pos, this.fluid);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.FLUID_SPLASH;
    }
}
