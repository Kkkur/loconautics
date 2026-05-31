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
 * Safety-net cleanup for physics trains: removes any leftover Sable sub-levels and clears the
 * registry/clients. Used by the tick-time orphan sweep ({@code PhysicsTrainTickHandler}) when a
 * physics train's Create train is gone (player disassembled it, world reload, crash, etc.).
 *
 * <p>The normal player disassembly is handled by {@code TrainDisassembleMixin} +
 * {@link SubLevelDisassembler}, which move the live blocks back to the world. This class only
 * deletes any sub-levels that are still around and forgets the train.
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
        LoconauticsConstants.LOGGER.info("Cleaned up physics train {} ({} leftover sub-level(s))", trainId, removed);
    }
}
