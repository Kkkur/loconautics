package com.lycoris.loconautics.allsable;

import java.util.UUID;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.lycoris.loconautics.Config;
import com.lycoris.loconautics.allsable.SableTrain.Car;
import com.lycoris.loconautics.content.bearingaxle.BearingAxleBlock;
import com.lycoris.loconautics.content.bearingaxle.BearingAxleBlockEntity;
import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.network.packets.SableTrainSyncPacket;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
// (AbstractBogeyBlock is used both for the yaw visual target and to detect when a car has lost all its wheels.)
import com.simibubi.create.infrastructure.config.AllConfigs;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import dev.ryanhcode.sable.api.physics.constraint.generic.GenericConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.force.ForceTotal;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.platform.SableEventPlatform;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Drives the Option-B custom trains ({@link SableTrain}): advances their motion once per game tick and pins
 * each carriage sub-level's pose to its {@link RailCarriage} every physics substep.
 *
 * <p>Bogey blocks stay inside the body sub-level at all times — they are never their own sub-level.
 * Per-bogey yaw is a visual-only effect driven via {@link BogeyYawVisual}: each tick the driver computes
 * the angle between the body's forward and the rail tangent sampled at each bogey's position (from its own
 * {@link RailCarriage}), and writes it into the bogey's {@link AbstractBogeyBlockEntity} NBT so the client
 * mixin ({@link com.lycoris.loconautics.mixin.client.BogeyYawVisualMixin}) can rotate the partial model.
 * This works for any bogey type registered with Create's bogey system.
 */
@EventBusSubscriber(modid = LoconauticsConstants.MOD_ID)
public final class SableTrainDriver {

    private SableTrainDriver() {
    }

    /** Wire the physics-tick pinning. Call once from common setup. */
    public static void register() {
        SableEventPlatform.INSTANCE.onPhysicsTick(SableTrainDriver::onPhysicsTick);
        SableEventPlatform.INSTANCE.onPostPhysicsTick(SableTrainDriver::onPostPhysicsTick);
        LoconauticsConstants.LOGGER.info("Loconautics: registered all-Sable custom-train driver");
    }

    // ---------------------------------------------------------------------------------------------
    // Physics tick: pin each car's sub-level pose to its RailCarriage.
    // ---------------------------------------------------------------------------------------------

    // Bogey spring (legacy free/derail "physics" mode): acceleration coefficients (force is mass-normalised).
    private static final double SPRING_K = 150.0;
    private static final double SPRING_C = 25.0;

    // Linear-bearing drive (DEFAULT trains): prismatic joint along the rail.
    private static final double DRIVE_K = 6.0;
    private static final double MAX_DRIVE_ACCEL = 20.0;

    /** Live constraints, keyed by the body sub-level UUID they glue to the rail. */
    private static final java.util.Map<UUID, PhysicsConstraintHandle> CONSTRAINTS = new java.util.HashMap<>();

    /** Debug escape hatch: force the legacy kinematic teleport-pin instead of the constraint-glued drive. */
    private static final boolean USE_KINEMATIC_PIN = false;

    /** Pre-step: drive each non-legacy car; legacy cars get springs. */
    public static void onPhysicsTick(SubLevelPhysicsSystem system, double timeStep) {
        forEachCar(system, (pipeline, container, sub, train, car) -> {
            // Global speed cap FIRST, so it applies no matter what is pushing the car — the bearing-axle drive,
            // thrusters, a physics wand, collisions, gravity on a slope, anything — and regardless of drive mode.
            capTrainSpeed(sub);
            // Derailed cars (e.g. all wheels destroyed) are released from the rail and left as a free physics
            // body — no constraint, no spring, no pin. Detection happens once per game tick in checkDerailment().
            if (train.isDerailed()) {
                releaseConstraint(sub.getUniqueId());
                return;
            }
            if (train.isPhysics()) {
                applyBogeySprings(sub, car, timeStep, system.getLevel());
                return;
            }
            if (useForceGlue(car)) {
                driveConstraintGlued(pipeline, sub, car, timeStep, train);
            } else {
                pinCar(pipeline, sub, car, false);
            }
        });
        sweepStaleConstraints();
    }

    /** Post-step: only the kinematic-pin fallback needs a re-pin. */
    public static void onPostPhysicsTick(SubLevelPhysicsSystem system, double timeStep) {
        forEachCar(system, (pipeline, container, sub, train, car) -> {
            if (train.isDerailed() || train.isPhysics() || useForceGlue(car)) {
                return;
            }
            pinCar(pipeline, sub, car, true);
        });
    }

    private static boolean useForceGlue(Car car) {
        return !USE_KINEMATIC_PIN && car.localLead() != null && car.localTrail() != null;
    }

    // ---------------------------------------------------------------------------------------------
    // Linear-bearing drive.
    // ---------------------------------------------------------------------------------------------

    private static void driveConstraintGlued(PhysicsPipeline pipeline, ServerSubLevel bodySub,
                                             Car car, double dt, SableTrain train) {
        RailCarriage carriage = car.carriage();
        Vector3d railPoint = carriage.center();
        if (railPoint == null) {
            return;
        }
        Vector3d tangent = forwardUnit(carriage);
        Vector3dc localAnchor = new Vector3d(car.localLead()).add(car.localTrail()).mul(0.5);

        // Measure the body's slide along the tangent and advance the rail by it.
        Vector3d anchorWorld = bodySub.logicalPose().transformPosition(localAnchor, new Vector3d());
        double slide = anchorWorld.sub(railPoint, new Vector3d()).dot(tangent);
        advanceRail(car, slide);

        railPoint = carriage.center();
        if (railPoint == null) {
            return;
        }
        tangent = forwardUnit(carriage);

        // Body: prismatic joint — perpendicular + rotation hard-locked, tangent free.
        glueBodyPrismatic(pipeline, bodySub, localAnchor, railPoint, tangent, carriage.orientation());

        // Bearing Axle propulsion.
        if (train.isPowered()) {
            applyTangentialDrive(bodySub, tangent, dt, train);
        }

        RigidBodyHandle bodyHandle = RigidBodyHandle.of(bodySub);
        if (bodyHandle != null) {
            train.setSpeed(bodyHandle.getLinearVelocity(new Vector3d()).dot(tangent) / 20.0);
        }
    }

    private static void glueBodyPrismatic(PhysicsPipeline pipeline, ServerSubLevel sub, Vector3dc localAnchor,
                                          Vector3d railPoint, Vector3d tangent, Quaterniond q) {
        UUID id = sub.getUniqueId();
        releaseConstraint(id);
        Vector3d up = perpendicularUp(tangent);
        Vector3d lateral = new Vector3d(tangent).cross(up).normalize();
        Quaterniond frame1 = basisQuaternion(tangent, up, lateral);
        Quaterniond frame2 = new Quaterniond(q).invert().mul(frame1);
        java.util.Set<ConstraintJointAxis> locked = java.util.EnumSet.of(
                ConstraintJointAxis.LINEAR_Y, ConstraintJointAxis.LINEAR_Z,
                ConstraintJointAxis.ANGULAR_X, ConstraintJointAxis.ANGULAR_Y, ConstraintJointAxis.ANGULAR_Z);
        GenericConstraintConfiguration config = new GenericConstraintConfiguration(
                new Vector3d(railPoint), new Vector3d(localAnchor), frame1, frame2, locked);
        PhysicsConstraintHandle handle = pipeline.addConstraint(null, sub, config);
        if (handle != null) {
            CONSTRAINTS.put(id, handle);
        }
    }

    private static void applyTangentialDrive(ServerSubLevel sub, Vector3d tangent, double dt, SableTrain train) {
        RigidBodyHandle handle = RigidBodyHandle.of(sub);
        if (handle == null) {
            return;
        }
        MassData md = sub.getMassTracker();
        double locoMass = (md != null && !md.isInvalid()) ? md.getMass() : 1.0;
        // The WHOLE hauled consist's weight governs how briskly the train can change speed; the impulse itself is
        // applied to the driven body's own mass (the rope constraints drag the rest along).
        double consistMass = train.haulMass() > 0.0 ? train.haulMass() : locoMass;

        Vector3d vel = handle.getLinearVelocity(new Vector3d());
        // Downhill: free-roll. The engine neither pulls nor brakes against gravity — exactly how a real train
        // coasts down a grade. Sable's gravity accelerates it, held in check by the global speed cap (capTrainSpeed).
        if (vel.y < -1.0e-3) {
            return;
        }
        double vTarget = train.targetSpeed() * 20.0;
        double vCurrent = vel.dot(tangent);
        double desiredAccel = DRIVE_K * (vTarget - vCurrent);

        // Speeding up uses the weight-scaled traction ceiling (heavier accelerates slower); slowing down uses the
        // braking ceiling, whose GRIP scales with weight — lighter consists slip and slide further, heavier ones
        // grip and stop predictably.
        boolean braking = Math.abs(vTarget) < Math.abs(vCurrent);
        double limit = braking ? brakeDecelLimit(consistMass) : tractionAccelLimit(consistMass);
        double accel = Math.max(-limit, Math.min(limit, desiredAccel));

        Vector3d worldImpulse = new Vector3d(tangent).mul(locoMass * accel * dt);
        ForceTotal forces = new ForceTotal();
        forces.applyLinearImpulse(sub.logicalPose().transformNormalInverse(worldImpulse, new Vector3d()));
        handle.applyForcesAndReset(forces);
    }

    // ---------------------------------------------------------------------------------------------
    // Weight-scaled acceleration / braking / wheel-slip (train.realism config).
    // ---------------------------------------------------------------------------------------------

    /** Max acceleration (m/s²) the consist can pull, scaled inversely with its weight and capped. */
    private static double tractionAccelLimit(double consistMass) {
        if (!Config.REALISM_ENABLED.get() || !Config.WEIGHT_SCALES_ACCELERATION.get()) {
            return MAX_DRIVE_ACCEL;
        }
        double scaled = Config.REALISM_BASE_ACCELERATION.get()
                * (Config.REALISM_REFERENCE_MASS.get() / Math.max(1.0, consistMass));
        return Math.min(scaled, Config.REALISM_MAX_ACCELERATION.get());
    }

    /**
     * Max braking deceleration (m/s²): the base deceleration scaled by weight-based braking GRIP (adhesion), capped.
     * Heavier consists grip toward full adhesion and stop predictably; lighter ones slip (lower adhesion → weaker
     * braking → they slide further). Braking is NOT scaled inversely with mass — weight changes grip, not raw force.
     */
    private static double brakeDecelLimit(double consistMass) {
        if (!Config.REALISM_ENABLED.get()) {
            return MAX_DRIVE_ACCEL;
        }
        double decel = Config.REALISM_BASE_DECELERATION.get() * brakingAdhesion(consistMass);
        return Math.min(decel, Config.REALISM_MAX_ACCELERATION.get());
    }

    /** Braking grip (0..1): lighter consists slip (apply less braking force), heavier ones grip fully. */
    private static double brakingAdhesion(double consistMass) {
        if (!Config.REALISM_ENABLED.get() || !Config.WEIGHT_SCALES_BRAKING_SLIP.get()) {
            return 1.0;
        }
        double minAdhesion = Config.REALISM_MIN_BRAKING_ADHESION.get();
        double full = Config.REALISM_FULL_ADHESION_MASS.get();
        double adhesion = minAdhesion + (1.0 - minAdhesion) * (Math.max(1.0, consistMass) / full);
        return Math.max(minAdhesion, Math.min(1.0, adhesion));
    }

    /**
     * Absolute incline of the body at the train, in degrees (0 = level). Read from the sub-level's real physics
     * pose — the angle its (originally world-up) local up-axis is tilted away from vertical — so it reflects exactly
     * how Sable is sitting the body, on a rail slope or any other tilt. Direction-independent: steeper = more stress.
     */
    private static double inclineDegrees(ServerSubLevel sub) {
        Quaterniond q = sub.logicalPose().orientation();
        Vector3d up = q.transform(new Vector3d(0.0, 1.0, 0.0)); // body up in world space
        double cos = Math.max(-1.0, Math.min(1.0, up.y));        // cos(tilt) = up · worldUp
        return Math.toDegrees(Math.acos(cos));
    }

    /** True if the train is heading up the incline (so the slope stress term applies). Uses the live travel
     *  direction when moving, else the throttle direction; downhill/level returns false (least stress). */
    private static boolean isClimbing(SableTrain train, double rpm) {
        Vec3 forward = train.car().carriage().forward(); // unit rail tangent (trailing -> leading)
        double travelDir = Math.abs(train.speed()) > 1.0e-4 ? Math.signum(train.speed()) : (rpm >= 0.0 ? 1.0 : -1.0);
        return travelDir * forward.y > 1.0e-4;
    }

    private static Vector3d forwardUnit(RailCarriage carriage) {
        Vec3 f = carriage.forward();
        Vector3d v = new Vector3d(f.x, f.y, f.z);
        return v.lengthSquared() < 1.0e-9 ? new Vector3d(1, 0, 0) : v.normalize();
    }

    private static Vector3d perpendicularUp(Vector3d forward) {
        Vector3d up = new Vector3d(0, 1, 0).sub(new Vector3d(forward).mul(forward.y));
        if (up.lengthSquared() < 1.0e-6) {
            up = new Vector3d(1, 0, 0).sub(new Vector3d(forward).mul(forward.x));
        }
        return up.normalize();
    }

    private static Quaterniond basisQuaternion(Vector3d forward, Vector3d up, Vector3d lateral) {
        org.joml.Matrix3d m = new org.joml.Matrix3d();
        m.setColumn(0, forward);
        m.setColumn(1, up);
        m.setColumn(2, lateral);
        return m.getNormalizedRotation(new Quaterniond());
    }

    /** Removes (if present) the prismatic constraint gluing this body sub-level to the rail. Idempotent. */
    private static void releaseConstraint(UUID id) {
        PhysicsConstraintHandle handle = CONSTRAINTS.remove(id);
        if (handle != null) {
            handle.remove();
        }
    }

    /** Releases joints whose body sub-level is no longer part of any live train. */
    private static void sweepStaleConstraints() {
        if (CONSTRAINTS.isEmpty()) {
            return;
        }
        java.util.Set<UUID> live = new java.util.HashSet<>();
        for (SableTrain train : SableTrainRegistry.all()) {
            UUID id = train.car().subLevelId();
            if (id != null) {
                live.add(id);
            }
        }
        var it = CONSTRAINTS.entrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            if (!live.contains(entry.getKey())) {
                entry.getValue().remove();
                it.remove();
            }
        }
    }

    /**
     * Advances the body carriage and every bogey's individual rail by {@code ds} blocks this substep.
     * Each bogey keeps its own {@link RailCarriage} so it tracks the tangent at its own position on the
     * track — necessary for correct yaw on cars where front and rear bogeys are on different parts of a curve.
     */
    private static void advanceRail(Car car, double ds) {
        car.carriage().setSpeed(ds);
        car.carriage().tick();
        for (SableTrain.Bogey bogey : car.bogeys()) {
            bogey.rail().setSpeed(ds);
            bogey.rail().tick();
        }
    }

    /** Pins the body sub-level to its rail pose (kinematic-pin fallback path). */
    private static void pinCar(PhysicsPipeline pipeline, ServerSubLevel bodySub, Car car, boolean post) {
        Vector3d bodyCenter = new Vector3d();
        Quaterniond bodyQ = bodyPose(car, bodyCenter);
        if (bodyQ != null) {
            pin(pipeline, bodySub, bodyCenter, bodyQ, post);
        }
    }

    private static Quaterniond bodyPose(Car car, Vector3d centerDst) {
        var bogeys = car.bogeys();
        if (bogeys.size() >= 2) {
            Vector3d first = bogeys.get(0).rail().center();
            Vector3d last = bogeys.get(bogeys.size() - 1).rail().center();
            if (first != null && last != null) {
                Vector3d forward = new Vector3d(last).sub(first);
                centerDst.set(first).add(last).mul(0.5);
                return car.carriage().orientationTo(forward);
            }
        }
        Vector3d c = car.carriage().center();
        if (c == null) {
            return null;
        }
        centerDst.set(c);
        return car.carriage().orientation();
    }

    private static void pin(PhysicsPipeline pipeline, ServerSubLevel sub, Vector3d center, Quaterniond q, boolean post) {
        Vector3d target = targetPosition(sub, center, q);
        if (post) {
            Pose3d pose = sub.logicalPose();
            pose.position().set(target);
            pose.orientation().set(q);
        }
        pipeline.teleport(sub, target, q);
        pipeline.resetVelocity(sub);
    }

    /** Physics-mode hold: bogey springs pull the free body toward the rail. */
    private static void applyBogeySprings(ServerSubLevel sub, Car car, double dt, ServerLevel level) {
        if (car.localLead() == null || car.localTrail() == null) {
            return;
        }
        RigidBodyHandle handle = RigidBodyHandle.of(sub);
        if (handle == null) {
            return;
        }
        MassData md = sub.getMassTracker();
        double mass = (md != null && !md.isInvalid()) ? md.getMass() : 1.0;
        ForceTotal forces = new ForceTotal();
        bogeySpring(forces, sub, car.localLead(), car.carriage().leadingPos(), mass, dt, level);
        bogeySpring(forces, sub, car.localTrail(), car.carriage().trailingPos(), mass, dt, level);
        handle.applyForcesAndReset(forces);
    }

    private static void bogeySpring(ForceTotal forces, ServerSubLevel sub, Vector3dc localPoint, Vec3 targetVec,
                                    double mass, double dt, ServerLevel level) {
        if (targetVec == null) {
            return;
        }
        Vector3d worldPoint = sub.logicalPose().transformPosition(localPoint, new Vector3d());
        Vector3d delta = new Vector3d(targetVec.x, targetVec.y, targetVec.z).sub(worldPoint);
        Vector3d vel = Sable.HELPER.getVelocity(level, worldPoint, new Vector3d());
        Vector3d worldImpulse = new Vector3d(delta).mul(SPRING_K).fma(-SPRING_C, vel).mul(mass * dt);
        Vector3d localImpulse = sub.logicalPose().transformNormalInverse(worldImpulse, new Vector3d());
        forces.applyImpulseAtPoint(sub, localPoint, localImpulse);
    }

    private static void forEachCar(SubLevelPhysicsSystem system, CarAction action) {
        if (SableTrainRegistry.isEmpty()) {
            return;
        }
        ServerLevel level = system.getLevel();
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) {
            return;
        }
        PhysicsPipeline pipeline = system.getPipeline();

        for (SableTrain train : SableTrainRegistry.all()) {
            if (train.level() != level) {
                continue;
            }
            Car car = train.car();
            if (car.subLevelId() == null) {
                continue;
            }
            if (!(container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub) || sub.isRemoved()) {
                continue;
            }
            try {
                action.run(pipeline, container, sub, train, car);
            } catch (Throwable t) {
                LoconauticsConstants.LOGGER.error("[sabletrain] error driving sub-level {}", car.subLevelId(), t);
            }
        }
    }

    @FunctionalInterface
    private interface CarAction {
        void run(PhysicsPipeline pipeline, ServerSubLevelContainer container, ServerSubLevel sub, SableTrain train, Car car);
    }

    private static Vector3d targetPosition(ServerSubLevel sub, Vector3d railCenter, Quaterniond orientation) {
        Vector3dc rotationPoint = sub.logicalPose().rotationPoint();
        BlockPos plotAnchor = sub.getPlot().getCenterBlock();
        Vector3d offset = new Vector3d(
                rotationPoint.x() - (plotAnchor.getX() + 0.5),
                rotationPoint.y() - plotAnchor.getY(),
                rotationPoint.z() - (plotAnchor.getZ() + 0.5));
        orientation.transform(offset);
        return new Vector3d(railCenter).add(offset);
    }

    // ---------------------------------------------------------------------------------------------
    // Game tick: advance train motion + yaw visuals.
    // ---------------------------------------------------------------------------------------------

    private static int diagCounter = 0;
    private static int massCounter = 0;

    /** Full-syncs every active train sub-level's relocation marker to a player when they join (so the wrench
     *  flow recognises trains that were assembled before they connected). */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SableTrainSyncPacket.syncAllTo(player);
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (SableTrainRegistry.isEmpty()) {
            return;
        }
        boolean diag = (diagCounter++ % 20) == 0;
        boolean updateMass = (massCounter++ % 10) == 0;
        for (SableTrain train : SableTrainRegistry.all()) {
            try {
                checkDerailment(train);
                applyPropulsion(train);
                train.tickMotion();
                updateBogeyYaw(train);
                if (updateMass) {
                    updateAxleMass(train);
                }
                if (diag) {
                    logOrientation(train);
                }
            } catch (Throwable t) {
                LoconauticsConstants.LOGGER.error("[sabletrain] error ticking motion for {}", train.id(), t);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Bogey yaw: compute the visual yaw for every bogey in the car and push it into the BE.
    // ---------------------------------------------------------------------------------------------

    /**
     * For each bogey in the car, computes the angle between the body's forward direction and the rail
     * tangent at that bogey's position, then writes it via {@link BogeyYawVisual} so the client mixin
     * can rotate the bogey's partial model. Works for any bogey type because it targets
     * {@link AbstractBogeyBlockEntity}, which all Create-compatible bogeys extend.
     */
    private static void updateBogeyYaw(SableTrain train) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(train.level());
        if (container == null) {
            return;
        }
        Car car = train.car();
        if (car.subLevelId() == null
                || !(container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub)
                || sub.isRemoved()) {
            return;
        }
        if (car.bogeys().isEmpty()) {
            return;
        }

        // Body forward in world space. Create renders each bogey at its OWN absolute track angle, after undoing
        // the carriage's render orientation (see CarriageContraptionEntityRenderer.translateBogey). The analog
        // here is to measure the bogey's yaw against the body's ACTUAL physics orientation, not the rail chord:
        // we take the two bogey attachment points and transform them through the body sub-level's live pose, so
        // the wheels align to the rail relative to how the body is really sitting this frame (robust to any lag
        // between the physics pose and the rail). Falls back to the rail chord for legacy pin-mode cars.
        Vector3d bodyForward;
        if (car.localLead() != null && car.localTrail() != null) {
            Vector3d leadW = sub.logicalPose().transformPosition(car.localLead(), new Vector3d());
            Vector3d trailW = sub.logicalPose().transformPosition(car.localTrail(), new Vector3d());
            bodyForward = leadW.sub(trailW); // leading - trailing = forward, matching Create's CarriageBogey
        } else {
            Vec3 f = car.carriage().forward();
            bodyForward = new Vector3d(f.x, f.y, f.z);
        }
        if (bodyForward.lengthSquared() < 1.0e-9) {
            return;
        }
        bodyForward.normalize();

        ServerLevel subLevel = sub.getLevel();

        for (SableTrain.Bogey bogey : car.bogeys()) {
            // Rail tangent at this specific bogey's position on the track.
            Vec3 bogeyFwd = bogey.rail().forward();
            Vector3d bogeyTangent = new Vector3d(bogeyFwd.x, bogeyFwd.y, bogeyFwd.z);
            if (bogeyTangent.lengthSquared() < 1.0e-9) {
                continue;
            }
            bogeyTangent.normalize();

            // Yaw angle: signed angle around Y between body forward and bogey tangent.
            // atan2 of the cross product (Y component) vs dot product gives the signed angle.
            double cross = bodyForward.x * bogeyTangent.z - bodyForward.z * bogeyTangent.x;
            double dot   = bodyForward.x * bogeyTangent.x + bodyForward.z * bogeyTangent.z;
            // Negated: Minecraft's Y rotation (Axis.YP, applied by BogeyYawVisualMixin) turns
            // clockwise-when-viewed-from-above — the opposite sense to this math-convention atan2 angle.
            // Without the flip the wheels pivot AWAY from the rail instead of turning to follow it.
            float yawDeg = -(float) Math.toDegrees(Math.atan2(cross, dot));

            // Find the bogey's BlockEntity inside the sub-level and push the yaw into it.
            BlockPos worldPos = sub.getPlot().getCenterBlock().offset(bogey.localPos());
            if (subLevel.getBlockEntity(worldPos) instanceof AbstractBogeyBlockEntity be) {
                BogeyYawVisual.setLocalYaw(be, yawDeg);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Derailment: a car that has lost all its bogeys leaves the rail and becomes a free physics body.
    // ---------------------------------------------------------------------------------------------

    /**
     * Once per game tick, checks whether the car still has any wheels. If its body sub-level no longer contains a
     * single Create bogey block, the car is flagged {@linkplain SableTrain#setDerailed derailed}; the next physics
     * tick then {@linkplain #releaseConstraint releases its rail constraint} so it falls/collides like any free
     * sub-level. This is the one place that decides "is this car still on the rail?", so future derailment rules
     * (over-speed on curves, collisions, manual uncoupling) only need to call {@code train.setDerailed(true)}.
     */
    private static void checkDerailment(SableTrain train) {
        if (train.isDerailed() || train.isPhysics()) {
            return; // already free, or a legacy free-physics car that was never rail-bound
        }
        ServerSubLevelContainer container = SubLevelContainer.getContainer(train.level());
        if (container == null) {
            return;
        }
        Car car = train.car();
        if (car.subLevelId() == null
                || !(container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub) || sub.isRemoved()) {
            return;
        }
        if (!hasBogeyBlock(sub)) {
            train.setDerailed(true);
            LoconauticsConstants.LOGGER.info(
                    "[sabletrain] car {} lost all its bogeys — released from the rail (now a free sub-level)",
                    train.id());
        }
    }

    /** True if the body sub-level still contains at least one Create bogey block (the car's wheels). */
    private static boolean hasBogeyBlock(ServerSubLevel sub) {
        ServerLevel subLevel = sub.getLevel();
        var bounds = sub.getPlot().getBoundingBox();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
            for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
                for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                    if (subLevel.getBlockState(pos.set(x, y, z)).getBlock() instanceof AbstractBogeyBlock) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // ---------------------------------------------------------------------------------------------
    // Mass tracking.
    // ---------------------------------------------------------------------------------------------

    private static void updateAxleMass(SableTrain train) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(train.level());
        if (container == null) {
            return;
        }
        Car car = train.car();
        if (car.subLevelId() == null
                || !(container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub) || sub.isRemoved()) {
            return;
        }
        // Only the carriage that actually carries a Bearing Axle needs a weight — and that weight is the WHOLE
        // consist it is hauling (its own blocks + every carriage coupled behind it), resolved modularly across all
        // registered couplers by TrainConsist. Carriages without an axle skip the (now consist-wide) computation.
        BearingAxleBlockEntity axle = bearingAxleIn(sub);
        if (axle == null) {
            return;
        }
        double total = TrainConsist.totalMass(train.level(), sub.getUniqueId());
        train.setHaulMass(total); // cache for the physics-tick drive (weight-scaled accel/braking)
        if (total != axle.getTrainMass()) {
            axle.setTrainMass(total);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Diagnostics.
    // ---------------------------------------------------------------------------------------------

    private static void logOrientation(SableTrain train) {
        String axleRpm = "no-axle";
        String axleMass = "n/a";
        double subMass = 0.0;
        ServerSubLevelContainer container = SubLevelContainer.getContainer(train.level());
        Car car = train.car();
        if (container != null) {
            if (car.subLevelId() != null
                    && container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub && !sub.isRemoved()) {
                subMass += liveMass(sub);
            }
            BearingAxleBlockEntity axle = findBearingAxle(train, container);
            if (axle != null) {
                axleRpm = f(axle.getSpeed());
                axleMass = f(axle.getTrainMass());
            }
        }
        RailCarriage c = car.carriage();
        var centre = c.center();
        var fwd = c.forward();
        String cart = (centre == null ? "NULL" : "(" + f(centre.x) + "," + f(centre.y) + "," + f(centre.z) + ")")
                + " fwd=(" + f(fwd.x) + "," + f(fwd.z) + ")" + (c.stopped() ? "[STOPPED]" : "");
        LoconauticsConstants.LOGGER.info("[sabletrain] diag speed={} target={} axleRPM={} subMass={} axleTrainMass={} cart={}",
                f(train.speed()), f(train.targetSpeed()), axleRpm, f(subMass), axleMass, cart);
    }

    private static String f(double d) {
        return String.format("%.2f", d);
    }

    // ---------------------------------------------------------------------------------------------
    // Propulsion.
    // ---------------------------------------------------------------------------------------------

    private static void applyPropulsion(SableTrain train) {
        if (train.isDerailed()) {
            train.setPowered(false); // a free, off-rail car can't be driven by its Bearing Axle
            return;
        }
        ServerSubLevelContainer container = SubLevelContainer.getContainer(train.level());
        if (container == null) {
            return;
        }
        Car car = train.car();
        if (car.subLevelId() == null
                || !(container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub) || sub.isRemoved()) {
            train.setPowered(false);
            return;
        }
        BearingAxleBlockEntity axle = bearingAxleIn(sub);
        if (axle == null) {
            train.setPowered(false);
            return;
        }
        // An axle present means this car is the locomotive: it both drives and brakes (rear cars have no axle and
        // are merely dragged). Downhill free-rolling is handled in applyTangentialDrive, not by withholding power.
        double rpm = axle.getSpeed();
        train.setPowered(true);

        double maxSpeed = bearingAxleCapBpt();
        double target = (rpm / 256.0) * maxSpeed;
        target = Math.max(-maxSpeed, Math.min(maxSpeed, target));
        train.setTargetSpeed(target);

        // Slope stress applies ONLY while climbing — hauling the consist uphill costs extra stress; descending it
        // is the least stressful (no slope term at all). Motion on slopes is left entirely to Sable's gravity.
        boolean slope = Config.REALISM_ENABLED.get() && Config.SLOPE_EFFECTS_ENABLED.get();
        axle.setSlopeAngle(slope && isClimbing(train, rpm) ? inclineDegrees(sub) : 0.0);
    }

    /** Create's configured train top speed (m/s) — the value both Loconautics caps defer to when set to -1. */
    private static double createTopSpeedMps() {
        return AllConfigs.server().trains.trainTopSpeed.get();
    }

    /** Bearing-axle speed cap in m/s: the configured value, or Create's train top speed when -1. */
    private static double bearingAxleCapMps() {
        double cfg = Config.BEARING_AXLE_MAX_SPEED.get();
        return cfg < 0.0 ? createTopSpeedMps() : cfg;
    }

    /** Bearing-axle speed cap in blocks/tick (the unit the drive target uses). 1 m/s = 1 block / 20 ticks. */
    private static double bearingAxleCapBpt() {
        return bearingAxleCapMps() / 20.0;
    }

    /** Global train speed cap in m/s (matches Sable's velocity units): the configured value, or Create's when -1. */
    private static double trainSpeedCapMps() {
        double cfg = Config.TRAIN_MAX_SPEED.get();
        return cfg < 0.0 ? createTopSpeedMps() : cfg;
    }

    /**
     * Hard-limits a sub-level's linear speed to the global train cap ({@link #trainSpeedCapMps}). Sable reports
     * velocity in m/s, so we compare directly and, if over the cap, add a velocity delta that scales it straight
     * back down to the cap (direction preserved, angular velocity untouched). Runs every physics substep for every
     * car, so the cap holds against any source of motion.
     */
    private static void capTrainSpeed(ServerSubLevel sub) {
        double capMps = trainSpeedCapMps();
        if (capMps < 0.0) {
            return; // negative cap = disabled (resolvers never return this, but stay safe)
        }
        RigidBodyHandle handle = RigidBodyHandle.of(sub);
        if (handle == null) {
            return;
        }
        Vector3d velocity = handle.getLinearVelocity(new Vector3d());
        double speed = velocity.length();
        if (speed > capMps && speed > 1.0e-6) {
            Vector3d delta = new Vector3d(velocity).mul(capMps / speed - 1.0);
            handle.addLinearAndAngularVelocity(delta, new Vector3d());
        }
    }

    static BearingAxleBlockEntity findBearingAxle(SableTrain train, ServerSubLevelContainer container) {
        Car car = train.car();
        if (car.subLevelId() == null
                || !(container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub) || sub.isRemoved()) {
            return null;
        }
        return bearingAxleIn(sub);
    }

    /** Summed physics block mass (kg) of one sub-level. Delegates to {@link TrainConsist#subLevelMass} so the
     *  per-sub-level weight calculation lives in exactly one place. */
    private static double liveMass(ServerSubLevel sub) {
        return TrainConsist.subLevelMass(sub);
    }

    private static BearingAxleBlockEntity bearingAxleIn(ServerSubLevel sub) {
        ServerLevel subLevel = sub.getLevel();
        var bounds = sub.getPlot().getBoundingBox();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
            for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
                for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                    pos.set(x, y, z);
                    if (subLevel.getBlockState(pos).getBlock() instanceof BearingAxleBlock
                            && subLevel.getBlockEntity(pos) instanceof BearingAxleBlockEntity axle) {
                        return axle;
                    }
                }
            }
        }
        return null;
    }
}