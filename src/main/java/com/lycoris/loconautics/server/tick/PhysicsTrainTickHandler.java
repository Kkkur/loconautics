package com.lycoris.loconautics.server.tick;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.core.PhysicsTrainTag;
import com.lycoris.loconautics.server.PhysicsTrainRegistry;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;

import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Phase 4a driver. Each server tick, drives every physics train's carriage sub-levels to follow the
 * pose Create already computed for that carriage (rail-following stays 100% Create's job).
 *
 * <p>Option B (kinematic / deterministic): we don't simulate rigid-body forces; we teleport each
 * {@link ServerSubLevel} to the carriage pose via {@link PhysicsPipeline#teleport} and cancel its
 * velocity so gravity doesn't drag it down between ticks.
 */
@EventBusSubscriber(modid = LoconauticsConstants.MOD_ID)
public final class PhysicsTrainTickHandler {

    /** Throttle for diagnostic logging (ticks). Set high enough not to spam. */
    private static final int LOG_INTERVAL = 40;
    private static int tickCounter = 0;

    private PhysicsTrainTickHandler() {
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        PhysicsTrainRegistry registry = PhysicsTrainRegistry.get(server);
        if (registry.all().isEmpty()) {
            return;
        }
        boolean log = (tickCounter++ % LOG_INTERVAL) == 0;
        for (PhysicsTrainTag tag : new ArrayList<>(registry.all())) {
            try {
                driveTrain(tag, log);
            } catch (Throwable t) {
                LoconauticsConstants.LOGGER.error("Error driving physics train {}", tag.trainId(), t);
            }
        }
    }

    private static void driveTrain(PhysicsTrainTag tag, boolean log) {
        Train train = Create.RAILWAYS.trains.get(tag.trainId());
        if (train == null) {
            if (log) {
                LoconauticsConstants.LOGGER.info("[drive] train {} not found in RAILWAYS", tag.trainId());
            }
            return;
        }

        List<Carriage> carriages = train.carriages;
        int count = Math.min(carriages.size(), tag.subLevelIds().size());

        for (int i = 0; i < count; i++) {
            Carriage carriage = carriages.get(i);
            UUID subLevelId = tag.subLevelIds().get(i);
            if (subLevelId == null) {
                continue;
            }

            CarriageContraptionEntity entity = carriage.anyAvailableEntity();
            if (entity == null || !(entity.level() instanceof ServerLevel level)) {
                if (log && i == 0) {
                    LoconauticsConstants.LOGGER.info("[drive] carriage {} has no server-side entity", i);
                }
                continue;
            }

            ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
            if (container == null) {
                if (log && i == 0) {
                    LoconauticsConstants.LOGGER.info("[drive] no sub-level container for {}", level.dimension().location());
                }
                continue;
            }
            SubLevel sub = container.getSubLevel(subLevelId);
            if (!(sub instanceof ServerSubLevel serverSub)) {
                if (log && i == 0) {
                    LoconauticsConstants.LOGGER.info("[drive] sub-level {} not found in container", subLevelId);
                }
                continue;
            }

            PhysicsPipeline pipeline = container.physicsSystem().getPipeline();

            Quaterniond orientation = orientationOf(entity);
            Vector3d target = targetPosition(entity, serverSub, orientation);

            pipeline.teleport(serverSub, target, orientation);
            pipeline.resetVelocity(serverSub);

            if (log && i == 0) {
                Vector3dc current = serverSub.logicalPose().position();
                LoconauticsConstants.LOGGER.info(
                        "[drive] carriage 0: entityPos={} -> teleport target=({},{},{}) | subPoseBefore=({},{},{})",
                        entity.position(), fmt(target.x), fmt(target.y), fmt(target.z),
                        fmt(current.x()), fmt(current.y()), fmt(current.z()));
            }
        }
    }

    /**
     * The world position the sub-level's center of mass should occupy so the carriage blocks line
     * up with where Create renders the carriage.
     *
     * <p>{@code entity.position()} is the bogey anchor (track level). The sub-level's rotation point
     * is its center of mass, which sits above the bogey, so we add the (rotated) center-of-mass
     * offset measured against the plot's anchor block.
     */
    private static Vector3d targetPosition(CarriageContraptionEntity entity, ServerSubLevel sub, Quaterniond orientation) {
        Vec3 pos = entity.position();
        Vector3d target = new Vector3d(pos.x, pos.y, pos.z);

        Vector3dc com = sub.getMassTracker().getCenterOfMass();
        if (com != null) {
            BlockPos plotCenter = sub.getPlot().getCenterBlock();
            Vector3d offset = new Vector3d(
                    com.x() - (plotCenter.getX() + 0.5),
                    com.y() - (plotCenter.getY() + 0.5),
                    com.z() - (plotCenter.getZ() + 0.5));
            orientation.transform(offset); // rotate the local offset into world space
            target.add(offset);
        }
        return target;
    }

    /**
     * Converts the carriage entity's yaw/pitch into a JOML quaternion for Sable.
     * Convention may still need tuning against in-game results.
     */
    private static Quaterniond orientationOf(CarriageContraptionEntity entity) {
        double yawRad = Math.toRadians(-entity.yaw);
        double pitchRad = Math.toRadians(entity.pitch);
        return new Quaterniond().rotationYXZ(yawRad, pitchRad, 0.0);
    }

    private static String fmt(double d) {
        return String.format("%.2f", d);
    }
}
