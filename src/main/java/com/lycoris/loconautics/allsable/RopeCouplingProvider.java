package com.lycoris.loconautics.allsable;

import java.util.UUID;
import java.util.function.Consumer;

import dev.simulated_team.simulated.content.blocks.rope.strand.server.RopeAttachment;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.ServerLevelRopeManager;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.ServerRopeStrand;

import net.minecraft.server.level.ServerLevel;

/**
 * {@link CouplingProvider} backed by Simulated rope strands — the steel-cable couplings that join carriages today
 * (see {@code SteelCableItem}). Two carriages are coupled when a strand has one attachment in each of their
 * sub-levels; a strand anchored to the open world (a {@code null} sub-level end) couples nothing.
 */
public final class RopeCouplingProvider implements CouplingProvider {

    @Override
    public void collectCoupledSubLevels(ServerLevel level, UUID subLevelId, Consumer<UUID> neighbours) {
        ServerLevelRopeManager manager = ServerLevelRopeManager.getOrCreate(level);
        if (manager == null) {
            return;
        }
        for (ServerRopeStrand strand : manager.getAllStrands()) {
            UUID a = null;
            UUID b = null;
            int seen = 0;
            // A strand has (up to) two attachments — gather the two sub-level ends.
            for (RopeAttachment attachment : strand.getAttachments()) {
                if (seen == 0) {
                    a = attachment.subLevelID();
                } else {
                    b = attachment.subLevelID();
                }
                seen++;
            }
            if (seen < 2) {
                continue; // a partially-attached strand couples nothing yet
            }
            // If this sub-level is one end, the other end (when it is a sub-level) is a coupled neighbour.
            if (subLevelId.equals(a) && b != null && !subLevelId.equals(b)) {
                neighbours.accept(b);
            } else if (subLevelId.equals(b) && a != null && !subLevelId.equals(a)) {
                neighbours.accept(a);
            }
        }
    }
}
