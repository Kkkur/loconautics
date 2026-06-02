/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 */
package com.simibubi.create.foundation.gui.menu;

import com.simibubi.create.AllPackets;
import com.simibubi.create.foundation.gui.menu.IClearableMenu;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public enum ClearMenuPacket implements ServerboundPacketPayload
{
    INSTANCE;

    public static final StreamCodec<ByteBuf, ClearMenuPacket> STREAM_CODEC;

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CLEAR_CONTAINER;
    }

    public void handle(ServerPlayer player) {
        if (player == null) {
            return;
        }
        if (!(player.containerMenu instanceof IClearableMenu)) {
            return;
        }
        ((IClearableMenu)player.containerMenu).clearContents();
    }

    static {
        STREAM_CODEC = StreamCodec.unit((Object)((Object)INSTANCE));
    }
}
