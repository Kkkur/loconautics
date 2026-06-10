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

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.PhysicsConstraintHandle;
import dev.ryanhcode.sable.api.physics.constraint.generic.GenericConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.force.ForceTotal;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyHelper;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.platform.SableEventPlatform;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Drives the Option-B custom trains ({@link SableTrain}): advances their motion once per game tick and pins
 * each carriage sub-level's pose to its {@link RailCarriage} every physics substep.
 *
 * <p>The pinning is collision-correct (teleport + resetVelocity pre-step, re-pin {@code logicalPose} post-step),
 * with the target pose coming from our rail math (no {@code CarriageContraptionEntity}). This is the ONLY train
 * driver now — the old Create-contraption-based hybrid system was removed in favour of this all-Sable approach.
 */
@EventBusSubscriber(modid = LoconauticsConstants.MOD_ID)
public final class SableTrainDriver {

    private SableTrainDriver() {
    }

    /** Wire the physics-tick pinning. Call once from common setup (alongside the hybrid driver). */
    public static void register() {
        SableEventPlatform.INSTANCE.onPhysicsTick(SableTrainDriver::onPhysicsTick);
        SableEventPlatform.INSTANCE.onPostPhysicsTick(SableTrainDriver::onPostPhysicsTick);
        LoconauticsConstants.LOGGER.info("Loconautics: registered all-Sable custom-train driver");
    }

    // ---------------------------------------------------------------------------------------------
    // Physics tick: pin each car's sub-level pose to its RailCarriage.
    // ---------------------------------------------------------------------------------------------

    // Bogey spring (legacy free/derail "physics" mode): acceleration coefficients (force is mass-normalised).
    private static final double SPRING_K = 150.0; // stiffness: accel per metre of offset toward the rail
    private static final double SPRING_C = 25.0;  // damping: ~critical for K (2·sqrt(K) ≈ 24.5)

    // --- Linear-bearing drive (DEFAULT trains) ----------------------------------------------------
    // Each car body is threaded onto the rail by a Rapier GENERIC joint configured as a PRISMATIC (linear-bearing)
    // constraint: the two axes perpendicular to the rail and ALL THREE rotations are HARD-locked (rigid — no
    // spring, so no wobble and no escaping the rail at speed), while the one axis ALONG the rail is left FREE.
    // The body therefore cannot leave the track, but slides freely under any force — gravity on a slope, a
    // propeller, a shove, or the Bearing Axle's drive. The rail point is re-anchored each substep to wherever the
    // body has slid, so the free axis keeps pointing along the (curving) track.
    private static final double DRIVE_K = 6.0;        // axle servo gain: along-rail accel per (block/s) of speed error
    private static final double MAX_DRIVE_ACCEL = 20.0; // cap on axle servo accel (blocks/s²) so throttle isn't jerky

    /** Live constraints, keyed by the sub-level (car body OR loose bogey) they glue to the rail. */
    private static final java.util.Map<UUID, PhysicsConstraintHandle> CONSTRAINTS = new java.util.HashMap<>();

    /** Debug escape hatch: force the legacy kinematic teleport-pin instead of the constraint-glued drive. */
    private static final boolean USE_KINEMATIC_PIN = false;

    /** Pre-step: drive each non-legacy car (constraint-glue, or kinematic-pin fallback); legacy cars get springs. */
    public static void onPhysicsTick(SubLevelPhysicsSystem system, double timeStep) {
        forEachCar(system, (pipeline, container, sub, train, car) -> {
            if (train.isPhysics()) {
                applyBogeySprings(sub, car, timeStep, system.getLevel());
                return;
            }
            if (useForceGlue(car)) {
                driveConstraintGlued(pipeline, container, sub, car, timeStep, train);
            } else {
                pinCar(pipeline, container, sub, car, false); // fallback: no captured attachment points (old save)
            }
        });
        sweepStaleConstraints(); // release joints whose train/sub-level is gone (despawned, unloaded)
    }

    /** Post-step: only the kinematic-pin fallback needs a re-pin; constraint-glued cars are solved by the engine. */
    public static void onPostPhysicsTick(SubLevelPhysicsSystem system, double timeStep) {
        forEachCar(system, (pipeline, container, sub, train, car) -> {
            if (train.isPhysics() || useForceGlue(car)) {
                return;
            }
            pinCar(pipeline, container, sub, car, true);
        });
    }

    /** A car runs the constraint-glued drive when it isn't forced to pin and has its attachment points captured. */
    private static boolean useForceGlue(Car car) {
        return !USE_KINEMATIC_PIN && car.localLead() != null && car.localTrail() != null;
    }

    // ---------------------------------------------------------------------------------------------
    // Linear-bearing drive: a prismatic joint threads the body onto the rail (free along it, locked off it).
    // ---------------------------------------------------------------------------------------------

    /**
     * Drives one default car as a body threaded onto the rail by a prismatic joint, free to slide along the track.
     *
     * <p>Each physics substep we: (1) measure how far the body has slid along the rail tangent and advance the
     * rail point (and the loose bogeys) by exactly that, so the rail re-anchors under the body — this is what lets
     * external forces move it; (2) re-create the body's prismatic joint at the new rail point (perpendicular +
     * rotation hard-locked, tangent free); (3) hard-lock each loose bogey to its own rail point; (4) if a Bearing
     * Axle is driving, apply an along-rail force servoing toward the commanded speed. With no axle, nothing drives
     * the tangent, so gravity / a propeller / a shove are free to move the train along the track.
     */
    private static void driveConstraintGlued(PhysicsPipeline pipeline, ServerSubLevelContainer container,
                                             ServerSubLevel bodySub, Car car, double dt, SableTrain train) {
        RailCarriage carriage = car.carriage();
        Vector3d railPoint = carriage.center();
        if (railPoint == null) {
            return;
        }
        Vector3d tangent = forwardUnit(carriage);
        Vector3dc localAnchor = new Vector3d(car.localLead()).add(car.localTrail()).mul(0.5);

        // 1) Measure the body's slide along the tangent (how far its anchor is ahead of the current rail point) and
        //    advance the rail by it, so the rail point tracks the body. Negative = it slid backwards (also fine).
        Vector3d anchorWorld = bodySub.logicalPose().transformPosition(localAnchor, new Vector3d());
        double slide = anchorWorld.sub(railPoint, new Vector3d()).dot(tangent);
        advanceRail(car, slide);

        // Re-read the pose at the new rail point.
        railPoint = carriage.center();
        if (railPoint == null) {
            return;
        }
        tangent = forwardUnit(carriage);

        // 2) Body: prismatic joint — perpendicular + rotation hard-locked, tangent free.
        glueBodyPrismatic(pipeline, bodySub, localAnchor, railPoint, tangent, carriage.orientation());

        // 3) Each loose bogey: hard-lock fully to its own rail point (it follows the body, advanced above).
        for (SableTrain.Bogey bogey : car.bogeys()) {
            if (!(container.getSubLevel(bogey.subLevelId()) instanceof ServerSubLevel bsub) || bsub.isRemoved()) {
                continue;
            }
            Vector3d center = bogey.rail().center();
            if (center != null) {
                glueRigid(pipeline, bsub, null, center, bogey.rail().orientation());
            }
        }

        // 4) Bearing Axle propulsion: servo the along-rail speed toward the target (external forces add on top).
        if (train.isPowered()) {
            applyTangentialDrive(bodySub, tangent, dt, train);
        }
        // Record the measured along-rail speed (blocks/tick) for diagnostics / persistence.
        RigidBodyHandle bodyHandle = RigidBodyHandle.of(bodySub);
        if (bodyHandle != null) {
            train.setSpeed(bodyHandle.getLinearVelocity(new Vector3d()).dot(tangent) / 20.0);
        }
    }

    /**
     * (Re)creates the prismatic joint holding {@code sub}'s {@code localAnchor} at {@code railPoint}: the two axes
     * perpendicular to {@code tangent} and all three rotations are hard-locked (so the body can't leave the rail
     * or wobble), while the axis along {@code tangent} is left free (so the body slides under any force). We
     * recreate it each substep because the tangent/orientation rotate as the train follows the curving track.
     */
    private static void glueBodyPrismatic(PhysicsPipeline pipeline, ServerSubLevel sub, Vector3dc localAnchor,
                                          Vector3d railPoint, Vector3d tangent, Quaterniond q) {
        UUID id = sub.getUniqueId();
        PhysicsConstraintHandle prev = CONSTRAINTS.remove(id);
        if (prev != null) {
            prev.remove();
        }
        // World joint frame: local X = tangent (the FREE slide axis), Y = up (perpendicular), Z = lateral.
        Vector3d up = perpendicularUp(tangent);
        Vector3d lateral = new Vector3d(tangent).cross(up).normalize();
        Quaterniond frame1 = basisQuaternion(tangent, up, lateral);
        // Body-local joint frame so the angular lock holds the body at the rail orientation q: R2 = q⁻¹ · R1.
        Quaterniond frame2 = new Quaterniond(q).invert().mul(frame1);
        // Lock everything except LINEAR_X (the tangent): the body is rigidly held off the rail, free along it.
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

    /**
     * (Re)creates a fully-rigid joint pinning {@code sub}'s {@code localAnchor} (or its centre of mass when null)
     * to {@code worldCenter} with orientation {@code q} — all six DOF locked. Used for the loose bogeys, which
     * just ride under the body.
     */
    private static void glueRigid(PhysicsPipeline pipeline, ServerSubLevel sub, Vector3dc localAnchor,
                                  Vector3d worldCenter, Quaterniond q) {
        UUID id = sub.getUniqueId();
        PhysicsConstraintHandle prev = CONSTRAINTS.remove(id);
        if (prev != null) {
            prev.remove();
        }
        Vector3dc anchor = localAnchor;
        if (anchor == null) {
            MassData md = sub.getMassTracker();
            anchor = (md != null && !md.isInvalid()) ? md.getCenterOfMass() : new Vector3d();
        }
        // All axes locked: frame orientations need only agree, so use q on the world side and identity on the body.
        GenericConstraintConfiguration config = new GenericConstraintConfiguration(
                new Vector3d(worldCenter), new Vector3d(anchor), new Quaterniond(q), new Quaterniond(),
                java.util.EnumSet.allOf(ConstraintJointAxis.class));
        PhysicsConstraintHandle handle = pipeline.addConstraint(null, sub, config);
        if (handle != null) {
            CONSTRAINTS.put(id, handle);
        }
    }

    /** Along-rail force servo: pushes the body's tangential speed toward the train's commanded target speed. */
    private static void applyTangentialDrive(ServerSubLevel sub, Vector3d tangent, double dt, SableTrain train) {
        RigidBodyHandle handle = RigidBodyHandle.of(sub);
        if (handle == null) {
            return;
        }
        MassData md = sub.getMassTracker();
        double mass = (md != null && !md.isInvalid()) ? md.getMass() : 1.0;
        double vTarget = train.targetSpeed() * 20.0; // blocks/tick -> blocks/sec
        double vCurrent = handle.getLinearVelocity(new Vector3d()).dot(tangent);
        double accel = Math.max(-MAX_DRIVE_ACCEL, Math.min(MAX_DRIVE_ACCEL, DRIVE_K * (vTarget - vCurrent)));
        Vector3d worldImpulse = new Vector3d(tangent).mul(mass * accel * dt);
        ForceTotal forces = new ForceTotal();
        forces.applyLinearImpulse(sub.logicalPose().transformNormalInverse(worldImpulse, new Vector3d()));
        handle.applyForcesAndReset(forces);
    }

    /** Unit rail tangent (trailing bogey -> leading bogey) as a JOML vector. */
    private static Vector3d forwardUnit(RailCarriage carriage) {
        Vec3 f = carriage.forward();
        Vector3d v = new Vector3d(f.x, f.y, f.z);
        return v.lengthSquared() < 1.0e-9 ? new Vector3d(1, 0, 0) : v.normalize();
    }

    /** World-up projected perpendicular to {@code forward} (Gram-Schmidt), with a fallback near vertical. */
    private static Vector3d perpendicularUp(Vector3d forward) {
        Vector3d up = new Vector3d(0, 1, 0).sub(new Vector3d(forward).mul(forward.y));
        if (up.lengthSquared() < 1.0e-6) { // forward ~ vertical: use world +X instead
            up = new Vector3d(1, 0, 0).sub(new Vector3d(forward).mul(forward.x));
        }
        return up.normalize();
    }

    /** Quaternion of the right-handed basis whose columns are (X=forward, Y=up, Z=lateral). */
    private static Quaterniond basisQuaternion(Vector3d forward, Vector3d up, Vector3d lateral) {
        org.joml.Matrix3d m = new org.joml.Matrix3d();
        m.setColumn(0, forward);
        m.setColumn(1, up);
        m.setColumn(2, lateral);
        return m.getNormalizedRotation(new Quaterniond());
    }

    /** Releases joints whose sub-level is no longer part of any live train (despawned / unloaded), so none leak. */
    private static void sweepStaleConstraints() {
        if (CONSTRAINTS.isEmpty()) {
            return;
        }
        java.util.Set<UUID> live = new java.util.HashSet<>();
        for (SableTrain train : SableTrainRegistry.all()) {
            Car car = train.car();
            if (car.subLevelId() != null) {
                live.add(car.subLevelId());
            }
            for (SableTrain.Bogey bogey : car.bogeys()) {
                live.add(bogey.subLevelId());
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

    /** Advances a car's body carriage and every loose bogey along the rail by {@code ds} blocks (this substep). */
    private static void advanceRail(Car car, double ds) {
        car.carriage().setSpeed(ds);
        car.carriage().tick();
        for (SableTrain.Bogey bogey : car.bogeys()) {
            bogey.rail().setSpeed(ds);
            bogey.rail().tick();
        }
    }

    /** Pins the body (posed FROM its bogeys) and each loose bogey (posed to its own rail point) for one car. */
    private static void pinCar(PhysicsPipeline pipeline, ServerSubLevelContainer container, ServerSubLevel bodySub,
                               Car car, boolean post) {
        // Body: derive its pose from the bogeys so it always sits exactly on them (no independent drift / spawn TP).
        Vector3d bodyCenter = new Vector3d();
        Quaterniond bodyQ = bodyPose(car, bodyCenter);
        if (bodyQ != null) {
            pin(pipeline, bodySub, bodyCenter, bodyQ, post);
        }
        // Each loose bogey: its own rail point + local tangent (this is what makes them pivot on curves).
        for (SableTrain.Bogey bogey : car.bogeys()) {
            if (!(container.getSubLevel(bogey.subLevelId()) instanceof ServerSubLevel bsub) || bsub.isRemoved()) {
                continue;
            }
            Vector3d center = bogey.rail().center();
            if (center != null) {
                pin(pipeline, bsub, center, bogey.rail().orientation(), post);
            }
        }
    }

    /**
     * The BODY pose: midpoint of the two end bogeys, oriented to the chord between them (the body "hangs" on its
     * bogeys, like Create). Falls back to the body's own {@link RailCarriage} when there are fewer than 2 bogeys.
     * Fills {@code centerDst} and returns the orientation (or {@code null} if no pose is available).
     */
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

    /** Teleport a sub-level's physics body to a rail pose. Post also re-fixes the Java logicalPose (collision). */
    private static void pin(PhysicsPipeline pipeline, ServerSubLevel sub, Vector3d center, Quaterniond q, boolean post) {
        Vector3d target = targetPosition(sub, center, q);
        if (post) {
            Pose3d pose = sub.logicalPose();
            pose.position().set(target);
            pose.orientation().set(q);
        }
        // Teleport the physics body in BOTH phases. Sable networks the pose to clients from the rigid body, so the
        // body's orientation must be set on the body itself (pinning only the Java logicalPose left it un-rotated).
        pipeline.teleport(sub, target, q);
        pipeline.resetVelocity(sub);
    }

    /**
     * Physics-mode hold: a stiff spring at each bogey pulls that attachment point on the free car body toward
     * its advancing rail target (the WheelMount recipe). The car rides the rail by force, not teleport, so it
     * can lag, bounce and DERAIL when the lateral pull can't hold it through a curve. Drive comes from the
     * rail targets advancing (RPM/speed) — the springs drag the body along.
     */
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

    /** One bogey: spring impulse pulling the local attachment point toward the world rail target, mass-scaled. */
    private static void bogeySpring(ForceTotal forces, ServerSubLevel sub, Vector3dc localPoint, Vec3 targetVec,
                                    double mass, double dt, ServerLevel level) {
        if (targetVec == null) {
            return;
        }
        Vector3d worldPoint = sub.logicalPose().transformPosition(localPoint, new Vector3d());
        Vector3d delta = new Vector3d(targetVec.x, targetVec.y, targetVec.z).sub(worldPoint);
        Vector3d vel = Sable.HELPER.getVelocity(level, worldPoint, new Vector3d());
        // accel = K·delta − C·vel ; impulse = mass · accel · dt
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

    /**
     * The world position the sub-level's {@code logicalPose} must take so its blocks line up with the rail
     * pose. Identical construction to the hybrid driver: {@code position = railCenter + Q·(rotationPoint -
     * plotAnchorCentre)}; x/z are re-centred on the anchor block, y on its bottom (the rail height).
     */
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
    // Game tick: advance train motion (once per tick).
    // ---------------------------------------------------------------------------------------------

    private static int diagCounter = 0;
    private static int massCounter = 0;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (SableTrainRegistry.isEmpty()) {
            return;
        }
        boolean diag = (diagCounter++ % 20) == 0;
        boolean updateMass = (massCounter++ % 10) == 0; // re-poll live train weight every 10 ticks (~0.5s) so placing a block updates the axle promptly
        for (SableTrain train : SableTrainRegistry.all()) {
            try {
                applyPropulsion(train);
                train.tickMotion();
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

    /**
     * Every 30 ticks: sum the live mass of the train's car sub-levels and push it into the Bearing Axle
     * ({@code setTrainMass}) so its weight/stress reflect the cart — and update if blocks are added/removed
     * (Sable maintains the mass tracker; we never rebuild it — that's what exploded trains before).
     */
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
        // Recompute mass from the sub-level's CURRENT blocks (not Sable's mass tracker, which is frozen at
        // assembly and never updates when the player adds/removes a block in a running cart). This makes the
        // Bearing Axle's weight update live as blocks are placed/broken on the moving cart.
        double total = liveMass(sub);
        // Loose bogeys are their own sub-levels — count their blocks toward the cart weight too.
        for (SableTrain.Bogey bogey : car.bogeys()) {
            if (container.getSubLevel(bogey.subLevelId()) instanceof ServerSubLevel bsub && !bsub.isRemoved()) {
                total += liveMass(bsub);
            }
        }
        BearingAxleBlockEntity axle = bearingAxleIn(sub);
        if (axle != null && total != axle.getTrainMass()) {
            axle.setTrainMass(total);
        }
    }

    /** Diagnostics: the cart's centre/stopped state (to see why it isn't moving) + bearing-axle RPM. */
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
        var fwd = c.forward(); // chord direction (trailing->leading); shows whether the body yaws on a curve
        String cart = (centre == null ? "NULL" : "(" + f(centre.x) + "," + f(centre.y) + "," + f(centre.z) + ")")
                + " fwd=(" + f(fwd.x) + "," + f(fwd.z) + ")" + (c.stopped() ? "[STOPPED]" : "");
        LoconauticsConstants.LOGGER.info("[sabletrain] diag speed={} target={} axleRPM={} subMass={} axleTrainMass={} cart={}",
                f(train.speed()), f(train.targetSpeed()), axleRpm, f(subMass), axleMass, cart);
    }

    private static String f(double d) {
        return String.format("%.2f", d);
    }

    // ---------------------------------------------------------------------------------------------
    // Propulsion: a Bearing Axle inside a car drives the train's target speed from its shaft RPM.
    // ---------------------------------------------------------------------------------------------

    /**
     * If any car contains a {@link BearingAxleBlockEntity}, set the train's target speed from its signed RPM
     * (sign = direction). With no axle present we leave {@code targetSpeed} untouched, so a plain debug cart
     * keeps the constant speed it was spawned with.
     *
     * <p>Conversion mirrors the bearing-axle handoff: {@code targetSpeed = rpm/256 * maxSpeed}, where
     * {@code maxSpeed} is our configured cap (or 1.0 blocks/tick by default). 256 RPM = full speed.
     */
    private static void applyPropulsion(SableTrain train) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(train.level());
        if (container == null) {
            return;
        }
        BearingAxleBlockEntity axle = findBearingAxle(train, container);
        if (axle == null) {
            train.setPowered(false); // no engine on board — the train is free to be moved by external forces
            return; // keep the debug/command target speed
        }
        train.setPowered(true);
        // The Bearing Axle is the ONLY thing that drives train velocity. It reads the RPM delivered by the
        // player's Create kinetic chain: separate generators produce rotation, the Transmission lets through
        // more/less of it according to the redstone signal from the Analog Controller (1..15), and the shaft
        // carries that controlled rotation to the axle. Train speed is purely a function of that RPM
        // (256 RPM = full speed); the sign of the RPM is the travel direction.
        double maxSpeed = maxSpeedBpt();
        double target = (axle.getSpeed() / 256.0) * maxSpeed;
        target = Math.max(-maxSpeed, Math.min(maxSpeed, target));
        train.setTargetSpeed(target);
    }

    /** Configured speed cap in blocks/tick, or a 1.0 default when unset (0). */
    private static double maxSpeedBpt() {
        double cfg = Config.PHYSICS_TRAIN_MAX_SPEED.get();
        return cfg > 0.0 ? cfg : 1.0;
    }

    /** Finds the Bearing Axle inside the cart's sub-level (the propulsion engine), or null. */
    static BearingAxleBlockEntity findBearingAxle(SableTrain train, ServerSubLevelContainer container) {
        Car car = train.car();
        if (car.subLevelId() == null
                || !(container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub) || sub.isRemoved()) {
            return null;
        }
        return bearingAxleIn(sub);
    }

    /**
     * Sums the mass of all blocks currently in a car's sub-level, using Sable's own per-block weight
     * ({@link PhysicsBlockPropertyHelper#getMass}, which returns 0 for non-solid/air). Recomputed from live
     * blocks so the weight reflects blocks added/removed AFTER assembly — Sable's built-in mass tracker is frozen
     * at assembly and never picks those up. Cheap: the same plot bounds are already scanned for the axle.
     */
    private static double liveMass(ServerSubLevel sub) {
        ServerLevel subLevel = sub.getLevel();
        var bounds = sub.getPlot().getBoundingBox();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        double mass = 0.0;
        for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
            for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
                for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                    mass += PhysicsBlockPropertyHelper.getMass(subLevel, pos.set(x, y, z),
                            subLevel.getBlockState(pos));
                }
            }
        }
        return mass;
    }

    /** Scans one sub-level's own level for a {@link BearingAxleBlockEntity}, or null. */
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
