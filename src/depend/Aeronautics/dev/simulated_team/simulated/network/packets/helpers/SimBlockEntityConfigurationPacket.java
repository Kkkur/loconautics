/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.SyncedBlockEntity
 *  foundry.veil.api.network.handler.ServerPacketContext
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.network.packets.helpers;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public abstract class SimBlockEntityConfigurationPacket<T extends SmartBlockEntity>
implements CustomPacketPayload {
    private final BlockPos pos;

    public SimBlockEntityConfigurationPacket(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void handle(ServerPacketContext context) {
        BlockEntity blockEntity;
        ServerPlayer player = context.player();
        Level world = player.level();
        if (world.isLoaded(this.pos) && player.distanceToSqr(Vec3.atBottomCenterOf((Vec3i)this.pos)) <= (double)this.maxRangeSqr() && (blockEntity = world.getBlockEntity(this.pos)) instanceof SyncedBlockEntity) {
            this.applySettings(player, (SmartBlockEntity)blockEntity);
            if (!this.causeUpdate()) {
                return;
            }
            ((SyncedBlockEntity)blockEntity).sendData();
            blockEntity.setChanged();
        }
    }

    protected int maxRangeSqr() {
        return 20;
    }

    protected boolean causeUpdate() {
        return true;
    }

    protected abstract void applySettings(ServerPlayer var1, T var2);
}
