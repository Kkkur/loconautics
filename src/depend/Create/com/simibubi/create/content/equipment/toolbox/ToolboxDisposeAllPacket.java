/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package com.simibubi.create.content.equipment.toolbox;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.mutable.MutableBoolean;

public record ToolboxDisposeAllPacket(BlockPos toolboxPos) implements ServerboundPacketPayload
{
    public static final StreamCodec<ByteBuf, ToolboxDisposeAllPacket> STREAM_CODEC = BlockPos.STREAM_CODEC.map(ToolboxDisposeAllPacket::new, ToolboxDisposeAllPacket::toolboxPos);

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.TOOLBOX_DISPOSE_ALL;
    }

    public void handle(ServerPlayer player) {
        Level world = player.level();
        BlockEntity blockEntity = world.getBlockEntity(this.toolboxPos);
        double maxRange = ToolboxHandler.getMaxRange((Player)player);
        if (player.distanceToSqr((double)this.toolboxPos.getX() + 0.5, (double)this.toolboxPos.getY(), (double)this.toolboxPos.getZ() + 0.5) > maxRange * maxRange) {
            return;
        }
        if (!(blockEntity instanceof ToolboxBlockEntity)) {
            return;
        }
        ToolboxBlockEntity toolbox = (ToolboxBlockEntity)blockEntity;
        CompoundTag compound = player.getPersistentData().getCompound("CreateToolboxData");
        MutableBoolean sendData = new MutableBoolean(false);
        toolbox.inventory.inLimitedMode(inventory -> {
            for (int i = 0; i < 36; ++i) {
                ItemStack itemStack;
                ItemStack remainder;
                String key = String.valueOf(i);
                if (compound.contains(key) && NBTHelper.readBlockPos((CompoundTag)compound.getCompound(key), (String)"Pos").equals((Object)this.toolboxPos)) {
                    ToolboxHandler.unequip((Player)player, i, true);
                    sendData.setTrue();
                }
                if ((remainder = ItemHandlerHelper.insertItemStacked((IItemHandler)toolbox.inventory, (ItemStack)(itemStack = player.getInventory().getItem(i)), (boolean)false)).getCount() == itemStack.getCount()) continue;
                player.getInventory().setItem(i, remainder);
            }
        });
        if (sendData.booleanValue()) {
            ToolboxHandler.syncData((Player)player);
        }
    }
}
