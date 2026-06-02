/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.ClientboundPacketPayload
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 */
package com.simibubi.create.foundation.networking;

import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class BlockEntityDataPacket<BE extends SyncedBlockEntity>
implements ClientboundPacketPayload {
    protected final BlockPos pos;

    public BlockEntityDataPacket(BlockPos pos) {
        this.pos = pos;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void handle(LocalPlayer player) {
        BlockEntity blockEntity = player.clientLevel.getBlockEntity(this.pos);
        if (blockEntity instanceof SyncedBlockEntity) {
            this.handlePacket((SyncedBlockEntity)blockEntity);
        }
    }

    protected abstract void handlePacket(BE var1);
}
