/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  net.minecraft.Util
 *  net.minecraft.nbt.NbtAccounter
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 */
package com.simibubi.create.content.contraptions.data;

import com.simibubi.create.compat.Mods;
import com.simibubi.create.foundation.mixin.accessor.NbtAccounterAccessor;
import io.netty.buffer.Unpooled;
import net.minecraft.Util;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

public class ContraptionPickupLimiting {
    public static final int NBT_LIMIT = 0x200000;
    public static final int PACKET_FIXER_LIMIT = 0xC800000;
    public static final int XL_PACKETS_LIMIT = Integer.MAX_VALUE;
    public static final int BUFFER = 20000;
    public static final int LIMIT = (Integer)Util.make(() -> {
        if (Mods.PACKETFIXER.isLoaded()) {
            return 0xC800000;
        }
        if (Mods.XLPACKETS.isLoaded()) {
            return Integer.MAX_VALUE;
        }
        return 0x200000;
    }) - 20000;

    public static boolean isTooLargeForPickup(Tag data) {
        return ContraptionPickupLimiting.nbtSize(data) > (long)LIMIT;
    }

    private static long nbtSize(Tag data) {
        FriendlyByteBuf test = new FriendlyByteBuf(Unpooled.buffer());
        test.writeNbt(data);
        NbtAccounter sizeTracker = NbtAccounter.unlimitedHeap();
        test.readNbt(sizeTracker);
        long size = ((NbtAccounterAccessor)sizeTracker).create$getUsage();
        test.release();
        return size;
    }
}
