/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.nbt.NBTProcessors
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponentMap
 *  net.minecraft.core.component.PatchedDataComponentMap
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.equipment.clipboard;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.equipment.clipboard.ClipboardBlockEntity;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import java.util.List;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.nbt.NBTProcessors;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public record ClipboardEditPacket(int hotbarSlot, @Nullable ClipboardContent clipboardContent, @Nullable BlockPos targetedBlock) implements ServerboundPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ClipboardEditPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.VAR_INT, ClipboardEditPacket::hotbarSlot, (StreamCodec)CatnipStreamCodecBuilders.nullable(ClipboardContent.STREAM_CODEC), ClipboardEditPacket::clipboardContent, (StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)BlockPos.STREAM_CODEC), ClipboardEditPacket::targetedBlock, ClipboardEditPacket::new);

    public void handle(ServerPlayer sender) {
        ClipboardContent processedContent = ClipboardEditPacket.clipboardProcessor(this.clipboardContent);
        if (this.targetedBlock != null) {
            Level world = sender.level();
            if (!world.isLoaded(this.targetedBlock)) {
                return;
            }
            if (!sender.canInteractWithBlock(this.targetedBlock, 20.0)) {
                return;
            }
            BlockEntity blockEntity = world.getBlockEntity(this.targetedBlock);
            if (blockEntity instanceof ClipboardBlockEntity) {
                ClipboardBlockEntity cbe = (ClipboardBlockEntity)blockEntity;
                PatchedDataComponentMap map = new PatchedDataComponentMap(cbe.components());
                if (processedContent == null) {
                    map.remove(AllDataComponents.CLIPBOARD_CONTENT);
                } else {
                    map.set(AllDataComponents.CLIPBOARD_CONTENT, (Object)processedContent);
                }
                cbe.setComponents((DataComponentMap)map);
                cbe.onEditedBy((Player)sender);
            }
            return;
        }
        ItemStack itemStack = sender.getInventory().getItem(this.hotbarSlot);
        if (!AllBlocks.CLIPBOARD.isIn(itemStack)) {
            return;
        }
        if (processedContent == null) {
            itemStack.remove(AllDataComponents.CLIPBOARD_CONTENT);
        } else {
            itemStack.set(AllDataComponents.CLIPBOARD_CONTENT, (Object)processedContent);
        }
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CLIPBOARD_EDIT;
    }

    public static ClipboardContent clipboardProcessor(@Nullable ClipboardContent content) {
        if (content == null) {
            return null;
        }
        for (List<ClipboardEntry> page : content.pages()) {
            for (ClipboardEntry entry : page) {
                if (!NBTProcessors.textComponentHasClickEvent((Component)entry.text)) continue;
                return null;
            }
        }
        return content;
    }
}
