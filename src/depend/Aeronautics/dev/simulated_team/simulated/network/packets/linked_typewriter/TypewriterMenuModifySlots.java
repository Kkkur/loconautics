/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.handler.ServerPacketContext
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.ItemStack
 */
package dev.simulated_team.simulated.network.packets.linked_typewriter;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen.LinkedTypewriterMenuCommon;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public record TypewriterMenuModifySlots(ItemStack first, ItemStack second) implements CustomPacketPayload
{
    public static CustomPacketPayload.Type<TypewriterMenuModifySlots> TYPE = new CustomPacketPayload.Type(Simulated.path("entry_modify"));
    public static StreamCodec<RegistryFriendlyByteBuf, TypewriterMenuModifySlots> CODEC = StreamCodec.composite((StreamCodec)ItemStack.OPTIONAL_STREAM_CODEC, TypewriterMenuModifySlots::first, (StreamCodec)ItemStack.OPTIONAL_STREAM_CODEC, TypewriterMenuModifySlots::second, TypewriterMenuModifySlots::new);

    public void handle(ServerPacketContext context) {
        ServerPlayer player = context.player();
        AbstractContainerMenu abstractContainerMenu = player.containerMenu;
        if (abstractContainerMenu instanceof LinkedTypewriterMenuCommon) {
            LinkedTypewriterMenuCommon menu = (LinkedTypewriterMenuCommon)abstractContainerMenu;
            menu.ghostInventory.setStackInSlot(0, this.first);
            menu.ghostInventory.setStackInSlot(1, this.second);
        }
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
