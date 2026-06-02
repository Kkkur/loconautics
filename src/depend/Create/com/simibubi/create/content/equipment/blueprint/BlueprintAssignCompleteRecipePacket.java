/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package com.simibubi.create.content.equipment.blueprint;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.equipment.blueprint.BlueprintItem;
import com.simibubi.create.content.equipment.blueprint.BlueprintMenu;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record BlueprintAssignCompleteRecipePacket(ResourceLocation recipeId) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, BlueprintAssignCompleteRecipePacket> STREAM_CODEC = ResourceLocation.STREAM_CODEC.map(BlueprintAssignCompleteRecipePacket::new, BlueprintAssignCompleteRecipePacket::recipeId);

    public void handle(ServerPlayer player) {
        AbstractContainerMenu abstractContainerMenu = player.containerMenu;
        if (abstractContainerMenu instanceof BlueprintMenu) {
            BlueprintMenu c = (BlueprintMenu)abstractContainerMenu;
            player.level().getRecipeManager().byKey(this.recipeId).ifPresent(r -> BlueprintItem.assignCompleteRecipe(c.player.level(), c.ghostInventory, r.value()));
        }
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.BLUEPRINT_COMPLETE_RECIPE;
    }
}
