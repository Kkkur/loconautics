/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 */
package com.simibubi.create.content.equipment.toolbox;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.equipment.toolbox.ItemReturnInvWrapper;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public record ToolboxEquipPacket(BlockPos toolboxPos, int slot, int hotbarSlot) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, ToolboxEquipPacket> STREAM_CODEC = StreamCodec.composite((StreamCodec)CatnipStreamCodecBuilders.nullable((StreamCodec)BlockPos.STREAM_CODEC), ToolboxEquipPacket::toolboxPos, (StreamCodec)ByteBufCodecs.VAR_INT, ToolboxEquipPacket::slot, (StreamCodec)ByteBufCodecs.VAR_INT, ToolboxEquipPacket::hotbarSlot, ToolboxEquipPacket::new);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.TOOLBOX_EQUIP;
    }

    public void handle(ServerPlayer player) {
        Level world = player.level();
        if (this.toolboxPos == null) {
            ToolboxHandler.unequip((Player)player, this.hotbarSlot, false);
            ToolboxHandler.syncData((Player)player);
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(this.toolboxPos);
        double maxRange = ToolboxHandler.getMaxRange((Player)player);
        if (player.distanceToSqr((double)this.toolboxPos.getX() + 0.5, (double)this.toolboxPos.getY(), (double)this.toolboxPos.getZ() + 0.5) > maxRange * maxRange) {
            return;
        }
        if (!(blockEntity instanceof ToolboxBlockEntity)) {
            return;
        }
        ToolboxBlockEntity toolboxBlockEntity = (ToolboxBlockEntity)blockEntity;
        ToolboxHandler.unequip((Player)player, this.hotbarSlot, false);
        if (this.slot < 0 || this.slot >= 8) {
            ToolboxHandler.syncData((Player)player);
            return;
        }
        ItemStack playerStack = player.getInventory().getItem(this.hotbarSlot);
        if (!playerStack.isEmpty() && !ToolboxInventory.canItemsShareCompartment(playerStack, toolboxBlockEntity.inventory.filters.get(this.slot))) {
            toolboxBlockEntity.inventory.inLimitedMode(inventory -> {
                ItemStack remainder = ItemHandlerHelper.insertItemStacked((IItemHandler)inventory, (ItemStack)playerStack, (boolean)false);
                if (!remainder.isEmpty()) {
                    remainder = ItemHandlerHelper.insertItemStacked((IItemHandler)new ItemReturnInvWrapper(player.getInventory()), (ItemStack)remainder, (boolean)false);
                }
                if (remainder.getCount() != playerStack.getCount()) {
                    player.getInventory().setItem(this.hotbarSlot, remainder);
                }
            });
        }
        CompoundTag compound = player.getPersistentData().getCompound("CreateToolboxData");
        String key = String.valueOf(this.hotbarSlot);
        CompoundTag data = new CompoundTag();
        data.putInt("Slot", this.slot);
        data.put("Pos", NbtUtils.writeBlockPos((BlockPos)this.toolboxPos));
        compound.put(key, (Tag)data);
        player.getPersistentData().put("CreateToolboxData", (Tag)compound);
        toolboxBlockEntity.connectPlayer(this.slot, (Player)player, this.hotbarSlot);
        ToolboxHandler.syncData((Player)player);
    }
}
