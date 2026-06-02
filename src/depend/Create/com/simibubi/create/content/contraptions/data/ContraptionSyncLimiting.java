/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 */
package com.simibubi.create.content.contraptions.data;

import com.simibubi.create.compat.Mods;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

public class ContraptionSyncLimiting {
    public static final int SIZE_LIMIT = 0x100000;
    public static final int PACKET_FIXER_LIMIT = 0x6400000;
    public static final int XL_PACKETS_LIMIT = Integer.MAX_VALUE;
    public static final int BUFFER = 20000;
    public static final int LIMIT = (Integer)Util.make(() -> {
        if (Mods.PACKETFIXER.isLoaded()) {
            return 0x6400000;
        }
        if (Mods.XLPACKETS.isLoaded()) {
            return Integer.MAX_VALUE;
        }
        return 0x100000;
    }) - 20000;

    public static void writeSafe(CompoundTag compound, FriendlyByteBuf dst) {
        int writerIndexBefore = dst.writerIndex();
        dst.writeNbt((Tag)compound);
        if (dst.writerIndex() > LIMIT) {
            dst.writerIndex(writerIndexBefore);
            dst.writeNbt(null);
        }
    }
}
