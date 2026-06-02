/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.net.base.ServerboundPacketPayload
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.foundation.networking;

import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import com.simibubi.create.foundation.utility.AdventureUtil;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BlockEntityConfigurationPacket<BE extends SyncedBlockEntity>
implements ServerboundPacketPayload {
    protected final BlockPos pos;

    public BlockEntityConfigurationPacket(BlockPos pos) {
        this.pos = pos;
    }

    public void handle(ServerPlayer player) {
        if (player == null || player.isSpectator() || AdventureUtil.isAdventure((Player)player)) {
            return;
        }
        Level world = player.level();
        if (!world.isLoaded(this.pos)) {
            return;
        }
        if (!player.canInteractWithBlock(this.pos, (double)this.maxRange())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(this.pos);
        if (blockEntity instanceof SyncedBlockEntity) {
            this.applySettings(player, (SyncedBlockEntity)blockEntity);
            if (!this.causeUpdate()) {
                return;
            }
            ((SyncedBlockEntity)blockEntity).sendData();
            blockEntity.setChanged();
        }
    }

    protected int maxRange() {
        return 20;
    }

    protected boolean causeUpdate() {
        return true;
    }

    protected abstract void applySettings(ServerPlayer var1, BE var2);
}
