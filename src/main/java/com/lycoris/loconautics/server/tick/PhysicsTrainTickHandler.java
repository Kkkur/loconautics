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
import com.lycoris.loconautics.server.assembly.PhysicsTrainDisassembler;

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
                driveTrain(server, tag, log);
            } catch (Throwable t) {
                LoconauticsConstants.LOGGER.error("Error driving physics train {}", tag.trainId(), t);
            }
        }
    }

    private static void driveTrain(MinecraftServer server, PhysicsTrainTag tag, boolean log) {
        Train train = Create.RAILWAYS.trains.get(tag.trainId());
        if (train == null) {
            // Orphan: the Create train is gone (disassembled / removed). Tear down the sub-levels.
            PhysicsTrainDisassembler.disassemble(server, tag.trainId());
            return;
        }

        List<Carriage> carriages = train.carriages;
        int count = Math.min(carriages.size(), tag.subLevelIds().size());
        int driven = 0;
        boolean sawContainer = false;

        for (int i = 0; i < count; i++) {
            Carriage carriage = carriages.get(i);
            UUID subLevelId = tag.subLevelIds().get(i);
            if (subLevelId == null) {
                continue;
            }

            CarriageContraptionEntity entity = carriage.anyAvailableEntity();
            if (entity == null || !(entity.level() instanceof ServerLevel level)) {
                continue;
            }

            ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
            if (container == null) {
                continue;
            }
            sawContainer = true;
            SubLevel sub = container.getSubLevel(subLevelId);
            if (!(sub instanceof ServerSubLevel serverSub)) {
                continue;
            }

            PhysicsPipeline pipeline = container.physicsSystem().getPipeline();

            Quaterniond orientation = orientationOf(entity);
            Vector3d target = targetPosition(entity, serverSub, orientation);

            pipeline.teleport(serverSub, target, orientation);
            pipeline.resetVelocity(serverSub);
            driven++;

            if (log && i == 0) {
                Vector3dc current = serverSub.logicalPose().position();
                LoconauticsConstants.LOGGER.info(
                        "[drive] carriage 0: entityPos={} -> teleport target=({},{},{}) | subPoseBefore=({},{},{})",
                        entity.position(), fmt(target.x), fmt(target.y), fmt(target.z),
                        fmt(current.x()), fmt(current.y()), fmt(current.z()));
            }
        }

        // Train exists but its sub-levels are gone (e.g. removed after a reload): drop the binding.
        if (driven == 0 && sawContainer && count > 0) {
            PhysicsTrainDisassembler.disassemble(server, tag.trainId());
        }
    }

    /**
     * The world position to teleport the sub-level to so its blocks line up with where Create
     * renders the carriage.
     *
     * <p>Teleport moves the sub-level's rotation point. A block at local position {@code bp} renders
     * at {@code position + rotate(bp - rotationPoint, Q)}. We want the bogey block (at the plot's
     * anchor) to land at {@code entity.position()}, which solves to:
     * {@code position = entityPos + rotate(rotationPoint - plotAnchor, Q)}.
     *
     * <p>We read the sub-level's actual {@code rotationPoint} (not the center of mass, which can be
     * null right after assembly), so the lift is always consistent with how the sub-level was built.
     */
    private static Vector3d targetPosition(CarriageContraptionEntity entity, ServerSubLevel sub, Quaterniond orientation) {
        Vec3 pos = entity.position();
        Vector3d target = new Vector3d(pos.x, pos.y, pos.z);

        Vector3dc rotationPoint = sub.logicalPose().rotationPoint();
        BlockPos plotAnchor = sub.getPlot().getCenterBlock();
        Vector3d offset = new Vector3d(
                rotationPoint.x() - (plotAnchor.getX() + 0.5),
                rotationPoint.y() - (plotAnchor.getY() + 0.5),
                rotationPoint.z() - (plotAnchor.getZ() + 0.5));
        orientation.transform(offset); // rotate the local offset into world space
        target.add(offset);
        return target;
    }

    /**
     * Converts the carriage entity's yaw/pitch into a JOML quaternion for Sable.
     * The +180 yaw corrects the sub-level appearing turned around vs the Create carriage.
     */
    private static Quaterniond orientationOf(CarriageContraptionEntity entity) {
        double yawRad = Math.toRadians(-entity.yaw + 180.0);
        double pitchRad = Math.toRadians(entity.pitch);
        return new Quaterniond().rotationYXZ(yawRad, pitchRad, 0.0);
    }

    private static String fmt(double d) {
        return String.format("%.2f", d);
    }
}
