/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.api.sublevel.SubLevelTrackingPlugin
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.content.blocks.rope.strand.server;

import dev.ryanhcode.sable.api.sublevel.SubLevelTrackingPlugin;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.RopeAttachment;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.RopeAttachmentPoint;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.ServerLevelRopeManager;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.ServerRopeStrand;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ServerRopeTrackingSystem
implements SubLevelTrackingPlugin {
    private final ServerLevel level;

    public ServerRopeTrackingSystem(ServerLevel level) {
        this.level = level;
    }

    private ServerLevelRopeManager getRopeManager() {
        return ServerLevelRopeManager.getOrCreate((Level)this.level);
    }

    public Iterable<UUID> neededPlayers() {
        ServerLevelRopeManager ropeManager = this.getRopeManager();
        Collection<ServerRopeStrand> strands = ropeManager.getAllStrands();
        if (strands.isEmpty()) {
            return List.of();
        }
        ObjectOpenHashSet players = new ObjectOpenHashSet();
        for (ServerRopeStrand strand : strands) {
            RopeAttachment attachment;
            BlockPos block;
            RopeStrandHolderBehavior holder;
            if (!strand.isActive()) continue;
            strand.updatePose();
            if (!strand.needsSync() && strand.networkingStopped || (holder = (RopeStrandHolderBehavior)RopeStrandHolderBehavior.get((BlockEntity)this.level.getBlockEntity(block = (attachment = strand.getAttachment(RopeAttachmentPoint.START)).blockAttachment()), RopeStrandHolderBehavior.TYPE)) == null) continue;
            for (ServerPlayer player : holder.getStrandTrackingPlayers()) {
                players.add(player.getUUID());
            }
        }
        return players;
    }

    public void sendTrackingData(int interpolationTick) {
        ServerLevelRopeManager ropeManager = this.getRopeManager();
        Collection<ServerRopeStrand> strands = ropeManager.getAllStrands();
        for (ServerRopeStrand strand : strands) {
            RopeStrandHolderBehavior holder;
            BlockPos block;
            RopeAttachment attachment;
            if (!strand.isActive()) continue;
            if (strand.needsSync()) {
                strand.networkingStopped = false;
                attachment = strand.getAttachment(RopeAttachmentPoint.START);
                block = attachment.blockAttachment();
                holder = (RopeStrandHolderBehavior)RopeStrandHolderBehavior.get((BlockEntity)this.level.getBlockEntity(block), RopeStrandHolderBehavior.TYPE);
                if (holder == null) continue;
                holder.getStrandPacketSink().sendPacket(new CustomPacketPayload[]{holder.makeUpdatePacket()});
                strand.justSynced();
                continue;
            }
            if (strand.networkingStopped) continue;
            strand.networkingStopped = true;
            attachment = strand.getAttachment(RopeAttachmentPoint.START);
            block = attachment.blockAttachment();
            holder = (RopeStrandHolderBehavior)RopeStrandHolderBehavior.get((BlockEntity)this.level.getBlockEntity(block), RopeStrandHolderBehavior.TYPE);
            if (holder == null) continue;
            holder.getStrandPacketSink().sendPacket(new CustomPacketPayload[]{holder.makeStopPacket()});
        }
    }
}
