/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.StreamCodec
 */
package com.simibubi.create.content.logistics.tableCloth;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityDataPacket;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;

public class ShopUpdatePacket
extends BlockEntityDataPacket<TableClothBlockEntity> {
    public static final StreamCodec<ByteBuf, ShopUpdatePacket> STREAM_CODEC = BlockPos.STREAM_CODEC.map(ShopUpdatePacket::new, i -> i.pos);

    public ShopUpdatePacket(BlockPos pos) {
        super(pos);
    }

    @Override
    protected void handlePacket(TableClothBlockEntity be) {
        if (!be.hasLevel()) {
            return;
        }
        be.invalidateItemsForRender();
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.SHOP_UPDATE;
    }
}
