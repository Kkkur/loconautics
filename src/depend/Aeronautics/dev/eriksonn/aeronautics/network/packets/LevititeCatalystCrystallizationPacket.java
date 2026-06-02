/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.ServerPacketContext
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecs
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 */
package dev.eriksonn.aeronautics.network.packets;

import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.CrystalPropagationContext;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeBlendHelper;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeCatalyzerHandler;
import dev.eriksonn.aeronautics.index.AeroLevititeBlendPropagationContexts;
import dev.eriksonn.aeronautics.index.AeroTags;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record LevititeCatalystCrystallizationPacket(BlockPos pos, InteractionHand hand) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<LevititeCatalystCrystallizationPacket> TYPE = new CustomPacketPayload.Type(Aeronautics.path("levitite_blend_crystallize"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LevititeCatalystCrystallizationPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, LevititeCatalystCrystallizationPacket::pos, (StreamCodec)CatnipStreamCodecs.HAND, LevititeCatalystCrystallizationPacket::hand, LevititeCatalystCrystallizationPacket::new);

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPacketContext context) {
        ServerPlayer player = context.player();
        ItemStack item = player.getItemInHand(this.hand);
        if (!LevititeCatalyzerHandler.isCatalyzer(item)) {
            return;
        }
        if (!item.is(AeroTags.ItemTags.LEVITITE_CATALYZER_NO_CONSUME)) {
            if (item.isDamageableItem()) {
                item.hurtAndBreak(1, (LivingEntity)player, this.hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            } else if (item.isStackable() && !context.player().hasInfiniteMaterials()) {
                item.shrink(1);
            }
        }
        player.swing(this.hand);
        CrystalPropagationContext itemContext = item.is(AeroTags.ItemTags.LEVITITE_SOUL_CATALYZER) ? (CrystalPropagationContext)AeroLevititeBlendPropagationContexts.SOUL_CONTEXT.get() : (CrystalPropagationContext)AeroLevititeBlendPropagationContexts.STANDARD_CONTEXT.get();
        LevititeBlendHelper.addLevititeBlendTicker(context.level(), this.pos, false, false, itemContext.getContextForSpread(context.level(), this.pos));
    }
}
