package com.lycoris.loconautics.server.tick;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    /** Server ticks per second — used to turn a per-tick pose delta into a velocity. */
    private static final double TICKS_PER_SECOND = 20.0;

    /**
     * Last pose we drove each sub-level to, as {@code [px,py,pz, qx,qy,qz,qw]}, keyed by sub-level id.
     * Used to feed the physics body a velocity (delta * tickrate) instead of zeroing it, so the client
     * interpolates the body smoothly between server ticks rather than snapping. This mirrors
     * Create-Interactive's {@code ServerShipTransformProvider}, which supplies the body a linear and
     * angular velocity derived from the pose change each tick.
     */
    private static final Map<UUID, double[]> PREV_POSE = new ConcurrentHashMap<>();

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
            tag.subLevelIds().forEach(PREV_POSE::remove);
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

            // Capture the inputs BEFORE teleport so the log reflects what fed into targetPosition.
            Vector3dc rpBefore = serverSub.logicalPose().rotationPoint();
            BlockPos plotAnchor = serverSub.getPlot().getCenterBlock();
            Vector3dc com = serverSub.getMassTracker().getCenterOfMass();

            pipeline.teleport(serverSub, target, orientation);

            // Instead of zeroing the velocity (which makes the body snap from tick to tick on the
            // client), feed it the velocity implied by this tick's pose change, so Sable interpolates
            // it smoothly between ticks. resetVelocity first so the add SETS rather than accumulates.
            pipeline.resetVelocity(serverSub);
            double[] prev = PREV_POSE.get(subLevelId);
            if (prev != null) {
                Vector3d linVel = new Vector3d(
                        (target.x - prev[0]) * TICKS_PER_SECOND,
                        (target.y - prev[1]) * TICKS_PER_SECOND,
                        (target.z - prev[2]) * TICKS_PER_SECOND);
                Vector3d omega = angularVelocity(orientation, new Quaterniond(prev[3], prev[4], prev[5], prev[6]));
                pipeline.addLinearAndAngularVelocity(serverSub, linVel, omega);
            }
            PREV_POSE.put(subLevelId, new double[] {
                    target.x, target.y, target.z,
                    orientation.x, orientation.y, orientation.z, orientation.w });
            driven++;

            if (log && i == 0) {
                Vector3dc after = serverSub.logicalPose().position();
                LoconauticsConstants.LOGGER.info(
                        "[drive] c0: entityPos={} yaw={} pitch={} initYaw={} | plotAnchor={} rotationPoint=({},{},{}) com={} "
                        + "| orient=({},{},{},{}) -> target=({},{},{}) | poseAfter=({},{},{})",
                        entity.position(), fmt(entity.yaw), fmt(entity.pitch), fmt(entity.getInitialYaw()), plotAnchor,
                        fmt(rpBefore.x()), fmt(rpBefore.y()), fmt(rpBefore.z()),
                        com == null ? "null" : ("(" + fmt(com.x()) + "," + fmt(com.y()) + "," + fmt(com.z()) + ")"),
                        fmt(orientation.x), fmt(orientation.y), fmt(orientation.z), fmt(orientation.w),
                        fmt(target.x), fmt(target.y), fmt(target.z),
                        fmt(after.x()), fmt(after.y()), fmt(after.z()));
            }
        }

        // Train exists but its sub-levels are gone (e.g. removed after a reload): drop the binding.
        if (driven == 0 && sawContainer && count > 0) {
            tag.subLevelIds().forEach(PREV_POSE::remove);
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
        // Create's carriage entity sits at the anchor block's horizontal CENTER (x/z + 0.5) but at the
        // anchor's BOTTOM (y, no +0.5 — that's the rail height). So we only re-center on x/z; adding
        // +0.5 on y would sink the sub-level half a block into the ground. Verified empirically: the
        // sub-level's resting pose.y matches (rotationPoint.y - plotAnchor.y) above entity.y.
        Vector3d offset = new Vector3d(
                rotationPoint.x() - (plotAnchor.getX() + 0.5),
                rotationPoint.y() - plotAnchor.getY(),
                rotationPoint.z() - (plotAnchor.getZ() + 0.5));
        orientation.transform(offset); // rotate the local offset into world space
        target.add(offset);
        return target;
    }

    /**
     * Builds the carriage's world orientation as a JOML quaternion, replicating EXACTLY what Create
     * does in {@code OrientedContraptionEntity.applyLocalTransforms} (verified via javap):
     *
     * <pre>
     *   center -> rotateY(viewYaw) -> rotateZ(pitch) -> rotateY(initialYaw) -> uncenter
     * </pre>
     *
     * where (from {@code getViewYRot}/{@code getViewXRot}/{@code getInitialYaw}):
     * <ul>
     *   <li>{@code viewYaw  = -yaw}   — getViewYRot negates the (lerped) yaw;</li>
     *   <li>{@code pitch    =  pitch} — applied around <b>Z</b>, not X;</li>
     *   <li>{@code initialYaw = initialOrientation.toYRot()} (0/90/180/270, SOUTH=0).</li>
     * </ul>
     *
     * <p>At assembly the carriage faces its initial orientation, so {@code yaw == initialYaw} and this
     * reduces to identity — which matches the sub-level captured world-aligned (Rotation.NONE). As the
     * train turns, this rotates the sub-level the same way Create rotates the carriage. The previous
     * formula ({@code rotationYXZ(-yaw+180, pitch, 0)}) used a bogus +180, treated pitch as an X
     * rotation, and ignored {@code initialYaw} — hence the "twisted" look.
     *
     * <p>JOML {@code rotateY/rotateZ} post-multiply, matching Flywheel's {@code rotateYDegrees/
     * rotateZDegrees} call order, and share Minecraft's right-handed convention.
     */
    private static Quaterniond orientationOf(CarriageContraptionEntity entity) {
        double viewYaw = -entity.yaw;
        double pitch = entity.pitch;
        double initialYaw = entity.getInitialYaw();
        return new Quaterniond()
                .rotateY(Math.toRadians(viewYaw))
                .rotateZ(Math.toRadians(pitch))
                .rotateY(Math.toRadians(initialYaw));
    }

    /**
     * Angular velocity (rad/s) that carries {@code prevOrient} to {@code curOrient} over one tick,
     * replicating Create-Interactive's omega derivation: the vector part of the quaternion difference,
     * times two (small-angle approx of the rotation vector), sign-corrected, scaled by the tickrate.
     */
    private static Vector3d angularVelocity(Quaterniond curOrient, Quaterniond prevOrient) {
        Quaterniond diff = curOrient.difference(prevOrient, new Quaterniond()).normalize();
        Vector3d omega = new Vector3d(diff.x * 2.0, diff.y * 2.0, diff.z * 2.0);
        if (diff.w > 0.0) {
            omega.mul(-1.0);
        }
        return omega.mul(TICKS_PER_SECOND);
    }

    private static String fmt(double d) {
        return String.format("%.2f", d);
    }
}
