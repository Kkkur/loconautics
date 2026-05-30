package com.lycoris.loconautics.server.assembly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lycoris.loconautics.core.LoconauticsConstants;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Holds NBT snapshots from sub-level block entities and writes them back to the
 * world on the next server tick, after Create has restored the blocks.
 */
@EventBusSubscriber(modid = LoconauticsConstants.MOD_ID)
public final class PendingNbtRestoreQueue {

    private record PendingRestore(ServerLevel level, Map<BlockPos, CompoundTag> snapshot) {}

    private static final List<PendingRestore> queue = new ArrayList<>();

    private PendingNbtRestoreQueue() {}

    public static void enqueue(ServerLevel level, Map<BlockPos, CompoundTag> snapshot) {
        queue.add(new PendingRestore(level, snapshot));
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (queue.isEmpty()) return;

        List<PendingRestore> toProcess = new ArrayList<>(queue);
        queue.clear();

        for (PendingRestore pending : toProcess) {
            int restored = 0;
            for (Map.Entry<BlockPos, CompoundTag> entry : pending.snapshot().entrySet()) {
                BlockPos pos = entry.getKey();
                BlockEntity be = pending.level().getBlockEntity(pos);
                if (be == null) continue;
                try {
                    be.loadWithComponents(entry.getValue(), pending.level().registryAccess());
                    be.setChanged();
                    restored++;
                } catch (Throwable t) {
                    LoconauticsConstants.LOGGER.warn("Failed to restore NBT at {}: {}", pos, t.getMessage());
                }
            }
            LoconauticsConstants.LOGGER.info("Restored NBT for {} block entities after disassembly", restored);
        }
    }
}