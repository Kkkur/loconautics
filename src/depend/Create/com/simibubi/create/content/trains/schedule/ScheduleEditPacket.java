/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.trains.schedule;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.trains.schedule.Schedule;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record ScheduleEditPacket(Schedule schedule) implements ServerboundPacketPayload
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ScheduleEditPacket> STREAM_CODEC = Schedule.STREAM_CODEC.map(ScheduleEditPacket::new, ScheduleEditPacket::schedule);

    public void handle(ServerPlayer sender) {
        ItemStack mainHandItem = sender.getMainHandItem();
        if (!AllItems.SCHEDULE.isIn(mainHandItem)) {
            return;
        }
        if (this.schedule.entries.isEmpty()) {
            mainHandItem.remove(AllDataComponents.TRAIN_SCHEDULE);
        } else {
            mainHandItem.set(AllDataComponents.TRAIN_SCHEDULE, (Object)this.schedule.write((HolderLookup.Provider)sender.registryAccess()));
        }
        sender.getCooldowns().addCooldown(mainHandItem.getItem(), 5);
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONFIGURE_SCHEDULE;
    }
}
