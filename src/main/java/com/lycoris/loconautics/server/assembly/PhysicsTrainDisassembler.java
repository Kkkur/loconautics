package com.lycoris.loconautics.server.assembly;

import java.util.UUID;

import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.core.PhysicsTrainTag;
import com.lycoris.loconautics.network.LoconauticsNetwork;
import com.lycoris.loconautics.network.PhysicsTrainSyncPacket;
import com.lycoris.loconautics.server.PhysicsTrainRegistry;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

/**
 * Tears down a physics train: removes its Sable sub-levels and clears the registry/clients.
 *
 * <p>Used both when the player disassembles the train (via a mixin on {@code Train.disassemble},
 * for prompt removal before the sub-level can drift) and as a tick-time safety net for orphans
 * (train gone, or its sub-levels vanished after a reload).
 *
 * <p>We only DELETE the sub-levels; we never place their blocks back, because Create's own
 * disassembly already returns the carriage blocks to the world. Re-placing them would duplicate.
 */
public final class PhysicsTrainDisassembler {

    private PhysicsTrainDisassembler() {
    }

    /** Removes the physics train's sub-levels and unregisters it. No-op if it isn't a physics train. */
    public static void disassemble(MinecraftServer server, UUID trainId) {
        PhysicsTrainRegistry registry = PhysicsTrainRegistry.get(server);
        PhysicsTrainTag tag = registry.get(trainId);
        if (tag == null) {
            return;
        }

        int removed = 0;
        for (ServerLevel level : server.getAllLevels()) {
            ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
            if (container == null) {
                continue;
            }
            for (UUID subLevelId : tag.subLevelIds()) {
                if (subLevelId == null) {
                    continue;
                }
                SubLevel sub = container.getSubLevel(subLevelId);
                if (sub != null) {
                    container.removeSubLevel(sub, SubLevelRemovalReason.REMOVED);
                    removed++;
                }
            }
        }

        registry.unregister(trainId);
        LoconauticsNetwork.sendToAll(new PhysicsTrainSyncPacket(tag, true));
        LoconauticsConstants.LOGGER.info("Disassembled physics train {} ({} sub-level(s) removed)", trainId, removed);
    }
}
