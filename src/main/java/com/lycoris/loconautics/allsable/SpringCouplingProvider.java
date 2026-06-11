package com.lycoris.loconautics.allsable;

import java.util.UUID;
import java.util.function.Consumer;

import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.simulated_team.simulated.content.blocks.spring.SpringBlock;
import dev.simulated_team.simulated.content.blocks.spring.SpringBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * {@link CouplingProvider} backed by Simulated springs — the sprung couplings that join carriages with a
 * {@link SpringBlock} (see Simulated's spring item/handler). A spring block lives inside a carriage sub-level and
 * names its partner via {@link SpringBlockEntity#getPartnerSubLevelID()}; two carriages are coupled when a spring in
 * one carriage points at the other's sub-level. A spring anchored to the open world (a {@code null} partner) couples
 * nothing.
 *
 * <p>Registered alongside {@link RopeCouplingProvider} so that {@link TrainConsist} unions both coupler kinds: a
 * hauling Bearing Axle then accounts for every carriage in the consist regardless of whether the links are steel
 * cables, springs, or a mix — and any consist-wide behaviour (weight-scaled drive/braking, etc.) follows through
 * springs the same way it does through ropes.
 *
 * <p>Like the rest of the driver ({@code bearingAxleIn}, {@code analogControllerIn}), springs are located by scanning
 * the sub-level's own block bounds — there is no global spring manager to enumerate.
 */
public final class SpringCouplingProvider implements CouplingProvider {

    @Override
    public void collectCoupledSubLevels(ServerLevel level, UUID subLevelId, Consumer<UUID> neighbours) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) {
            return;
        }
        if (!(container.getSubLevel(subLevelId) instanceof ServerSubLevel sub) || sub.isRemoved()) {
            return;
        }
        ServerLevel subLevel = sub.getLevel();
        var bounds = sub.getPlot().getBoundingBox();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
            for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
                for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                    pos.set(x, y, z);
                    if (subLevel.getBlockState(pos).getBlock() instanceof SpringBlock
                            && subLevel.getBlockEntity(pos) instanceof SpringBlockEntity spring) {
                        UUID partner = spring.getPartnerSubLevelID();
                        // A spring anchored to the open world (null) or back to this same sub-level couples nothing.
                        if (partner != null && !subLevelId.equals(partner)) {
                            neighbours.accept(partner);
                        }
                    }
                }
            }
        }
    }
}
