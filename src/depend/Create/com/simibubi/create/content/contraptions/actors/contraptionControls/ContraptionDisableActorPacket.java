/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.content.contraptions.actors.contraptionControls;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsMovement;
import java.util.List;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record ContraptionDisableActorPacket(int entityId, ItemStack filter, boolean enable) implements ClientboundPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ContraptionDisableActorPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, ContraptionDisableActorPacket::entityId, (StreamCodec)ItemStack.OPTIONAL_STREAM_CODEC, ContraptionDisableActorPacket::filter, (StreamCodec)ByteBufCodecs.BOOL, ContraptionDisableActorPacket::enable, ContraptionDisableActorPacket::new);

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        Entity entityByID = player.clientLevel.getEntity(this.entityId);
        if (!(entityByID instanceof AbstractContraptionEntity)) {
            return;
        }
        AbstractContraptionEntity ace = (AbstractContraptionEntity)entityByID;
        Contraption contraption = ace.getContraption();
        List<ItemStack> disabledActors = contraption.getDisabledActors();
        if (this.filter.isEmpty()) {
            disabledActors.clear();
        }
        if (!this.enable) {
            disabledActors.add(this.filter);
            contraption.setActorsActive(this.filter, false);
            return;
        }
        disabledActors.removeIf(next -> ContraptionControlsMovement.isSameFilter(next, this.filter) || next.isEmpty());
        contraption.setActorsActive(this.filter, true);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONTRAPTION_ACTOR_TOGGLE;
    }
}
