package com.lycoris.loconautics.allsable;

import java.util.UUID;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.lycoris.loconautics.Config;
import com.lycoris.loconautics.allsable.SableTrain.Car;
import com.lycoris.loconautics.allsable.SableTrain.StationState;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerBlock;
import com.lycoris.loconautics.content.analogcontroller.AnalogControllerBlockEntity;
import com.lycoris.loconautics.content.bearingaxle.BearingAxleBlock;
import com.lycoris.loconautics.content.bearingaxle.BearingAxleBlockEntity;
import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.network.LoconauticsNetwork;
import com.lycoris.loconautics.network.packets.SableTrainSyncPacket;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
// (AbstractBogeyBlock is used both for the yaw visual target and to detect when a car has lost all its wheels.)
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.entity.TravellingPoint.SteerDirection;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.createmod.catnip.data.Couple;
import net.minecraft.network.chat.Component;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.generic.GenericConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.generic.GenericConstraintHandle;
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

    /** Live constraints, keyed by the body sub-level UUID they glue to the rail. Reused across substeps (frames
     *  updated in place) — never rebuilt per tick — so Sable's solver keeps its warm-start state. */
    private static final java.util.Map<UUID, GenericConstraintHandle> CONSTRAINTS = new java.util.HashMap<>();

    /** Debug escape hatch: force the legacy kinematic teleport-pin instead of the constraint-glued drive. */
    private static final boolean USE_KINEMATIC_PIN = false;

    /** Cap (blocks) on the bogey visual rail-snap offset, so a desynced rail follower can't fling the model. */
    private static final double MAX_BOGEY_VISUAL_OFFSET = 1.5;

    /** Pre-step: drive each non-legacy car; legacy cars get springs. */
    public static void onPhysicsTick(SubLevelPhysicsSystem system, double timeStep) {
        substepSeq++;
        forEachCar(system, (pipeline, container, sub, train, car) -> {
            // Velocity BEFORE Sable's collision/constraint solve this substep (pairs with the POST log below).
            if (Config.BOUNCE_DEBUG.get()) {
                logBounceVel("PRE ", sub, train, car);
            }
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
            // Velocity AFTER Sable's solve. KE_post > KE_pre on a bounce substep = energy was ADDED, not conserved.
            if (Config.BOUNCE_DEBUG.get()) {
                logBounceVel("POST", sub, train, car);
            }
            if (train.isDerailed() || train.isPhysics()) {
                return;
            }
            if (useForceGlue(car)) {
                // Remove any velocity the solve added OFF the rail line. A rail-glued body may only move along its
                // tangent — the prismatic joint forbids perpendicular translation and all rotation. When the body
                // is rammed into an obstacle, Sable's contact solver can leave large perpendicular/vertical (and
                // angular) velocity, which the magnitude-only speed cap merely rescales — preserving its direction
                // and sustaining the bounce. Projecting the velocity back onto the tangent (and zeroing spin)
                // dissipates that energy instead of recycling it.
                projectVelocityOntoRail(sub, car);
                return;
            }
            pinCar(pipeline, sub, car, true);
        });
    }

    /**
     * Constrains a rail-glued body's velocity to what the prismatic joint actually allows: motion only along the
     * rail tangent, no spin. Keeps the along-rail component (so the train still moves and gravity still pulls it
     * down a grade) clamped to the global speed cap, and removes the perpendicular/vertical and angular components
     * that a collision can inject. This is what stops injected energy from being recycled into a sustained bounce.
     */
    private static void projectVelocityOntoRail(ServerSubLevel sub, Car car) {
        RigidBodyHandle handle = RigidBodyHandle.of(sub);
        if (handle == null) {
            return;
        }
        Vector3d tangent = railTangent(car);
        Vector3d vel = handle.getLinearVelocity(new Vector3d());
        double vTan = vel.dot(tangent);
        double capMps = trainSpeedCapMps();
        if (capMps >= 0.0) {
            vTan = Math.max(-capMps, Math.min(capMps, vTan));
        }
        Vector3d desired = new Vector3d(tangent).mul(vTan);
        Vector3d deltaLinear = desired.sub(vel, new Vector3d());
        Vector3d angVel = handle.getAngularVelocity(new Vector3d());
        handle.addLinearAndAngularVelocity(deltaLinear, angVel.negate());
    }

    /** Unit rail tangent for a car: the chord between its front and rear bogey rail points (matching the pose the
     *  prismatic joint is built on), falling back to the body carriage's own tangent. */
    private static Vector3d railTangent(Car car) {
        var bogeys = car.bogeys();
        if (bogeys.size() >= 2) {
            Vector3d rear = bogeys.get(0).rail().center();
            Vector3d front = bogeys.get(bogeys.size() - 1).rail().center();
            if (rear != null && front != null) {
                Vector3d chord = new Vector3d(front).sub(rear);
                if (chord.lengthSquared() > 1.0e-9) {
                    return chord.normalize();
                }
            }
        }
        return forwardUnit(car.carriage());
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
        animateBogeyWheels(car, bodySub, slide);

        railPoint = carriage.center();
        if (railPoint == null) {
            return;
        }
        tangent = forwardUnit(carriage);
        Quaterniond q = carriage.orientation();

        // THE SUB-LEVEL ADAPTS TO THE BOGEYS (Create-style), not the other way around: with 2+ bogeys, the
        // body's constraint pose is derived FROM the bogeys' own rail points — pinned at their midpoint and
        // oriented along the chord between them, exactly how Create stretches a carriage between its bogey
        // anchors. The rigid bogey blocks then land on their rail points by construction, instead of the
        // bogeys being visually dragged to wherever the body's own follower put them.
        var bogeys = car.bogeys();
        if (bogeys.size() >= 2) {
            Vector3d rear = bogeys.get(0).rail().center();
            Vector3d front = bogeys.get(bogeys.size() - 1).rail().center();
            if (rear != null && front != null) {
                Vector3d chord = new Vector3d(front).sub(rear);
                if (chord.lengthSquared() > 1.0e-9) {
                    railPoint = new Vector3d(front).add(rear).mul(0.5);
                    tangent = new Vector3d(chord).normalize();
                    q = carriage.orientationTo(chord);
                }
            }
        }

        // Body: prismatic joint — perpendicular + rotation hard-locked, tangent free.
        glueBodyPrismatic(pipeline, bodySub, localAnchor, railPoint, tangent, q);

        // Bearing Axle propulsion.
        if (train.isPowered()) {
            applyTangentialDrive(bodySub, tangent, dt, train);
        }

        RigidBodyHandle bodyHandle = RigidBodyHandle.of(bodySub);
        if (bodyHandle != null) {
            double bodyVelBpt = bodyHandle.getLinearVelocity(new Vector3d()).dot(tangent) / 20.0;
            train.setSpeed(bodyVelBpt);
            // #3: does the rail advancement (slide) agree with the physics velocity? A persistent gap after a
            // collision means the rail follower and the body have desynced.
            if (Config.BOUNCE_DEBUG.get()) {
                LoconauticsConstants.LOGGER.info(
                        "[bounce] seq={} car={} slide={} speed={} bodyVelBpt={} slide-vel={}",
                        substepSeq, train.id(), f(slide), f(train.speed()), f(bodyVelBpt), f(slide - bodyVelBpt));
            }
        }
    }

    private static void glueBodyPrismatic(PhysicsPipeline pipeline, ServerSubLevel sub, Vector3dc localAnchor,
                                          Vector3d railPoint, Vector3d tangent, Quaterniond q) {
        UUID id = sub.getUniqueId();
        Vector3d up = perpendicularUp(tangent);
        Vector3d lateral = new Vector3d(tangent).cross(up).normalize();
        Quaterniond frame1 = basisQuaternion(tangent, up, lateral);
        Quaterniond frame2 = new Quaterniond(q).invert().mul(frame1);

        // Reuse the existing joint and just MOVE its frames to the new rail pose. Tearing the constraint down and
        // re-adding it every substep (the old behaviour) discarded the solver's accumulated impulse / warm-start
        // state, so a body in contact with an obstacle and a freshly-rebuilt rail joint over-corrected against each
        // other and pumped kinetic energy into the body each step — the runaway "bounce". Updating in place keeps
        // the solve stable; we only build a fresh joint when there isn't a valid one yet.
        GenericConstraintHandle existing = CONSTRAINTS.get(id);
        if (existing != null && existing.isValid()) {
            existing.setFrame1(new Vector3d(railPoint), frame1);
            existing.setFrame2(new Vector3d(localAnchor), frame2);
            return;
        }
        releaseConstraint(id);
        java.util.Set<ConstraintJointAxis> locked = java.util.EnumSet.of(
                ConstraintJointAxis.LINEAR_Y, ConstraintJointAxis.LINEAR_Z,
                ConstraintJointAxis.ANGULAR_X, ConstraintJointAxis.ANGULAR_Y, ConstraintJointAxis.ANGULAR_Z);
        GenericConstraintConfiguration config = new GenericConstraintConfiguration(
                new Vector3d(railPoint), new Vector3d(localAnchor), frame1, frame2, locked);
        GenericConstraintHandle handle = pipeline.addConstraint(null, sub, config);
        if (handle != null) {
            CONSTRAINTS.put(id, handle);
        }
    }

    private static void applyTangentialDrive(ServerSubLevel sub, Vector3d tangent, double dt, SableTrain train) {
        RigidBodyHandle handle = RigidBodyHandle.of(sub);
        if (handle == null) {
            return;
        }
        // REALISTIC over-cap: the consist is too heavy for the axle (and its gear) to pull, so the axle produces no
        // tractive effort. We apply NO impulse here — the prismatic constraint still holds the car to the rail and
        // Sable's gravity still acts, so the train can roll/coast downhill but cannot be driven (intended).
        if (train.isOverloaded()) {
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
        GenericConstraintHandle handle = CONSTRAINTS.remove(id);
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
    /** Per-tick clamp on the follow correction, so a badly-off follower glides back instead of jumping. */
    private static final double MAX_FOLLOW_CORRECTION = 0.5;

    private static void advanceRail(Car car, double ds) {
        // Unstick the body carriage before each advance (like the bogey followers below). Once it parks at a dead
        // end it sets stopped=true and, without this, would stay frozen forever — so a consist driven INTO the end
        // could never be reversed back out, and the rail point stayed pinned at the end while propulsion kept
        // ramming the body into it. Re-trying each substep lets it leave when driven away; a true dead end simply
        // re-blocks (stays put) when driven further into it, so the dead-end derail check still works.
        car.carriage().unstick();
        car.carriage().setSpeed(ds);
        car.carriage().tick();

        var bogeys = car.bogeys();
        if (bogeys.isEmpty()) {
            return;
        }
        Vec3 bodyFwd = car.carriage().forward();

        // Create-style follow: bogey followers do NOT advance independently. The leading bogey (last in
        // front-to-back order) advances by ds; every other follower additionally corrects its travel so the
        // 3D CHORD distance to the bogey ahead stays equal to their rigid block distance — exactly Create's
        // Carriage "stress" mechanism (CarriageBogey.getStress / TravellingPoint.follow). This continuously
        // heals any arc error (coarse seating, reversed axis, past dead-end parks), pinning each bogey to its
        // own point on the curve instead of letting it hang off the body pose.
        int n = bogeys.size();
        SableTrain.Bogey lead = bogeys.get(n - 1);
        lead.rail().unstick();
        lead.rail().setSpeed(ds * travelSign(lead, bodyFwd));
        lead.rail().tick();

        for (int i = n - 2; i >= 0; i--) {
            SableTrain.Bogey ahead = bogeys.get(i + 1);
            SableTrain.Bogey bogey = bogeys.get(i);
            bogey.rail().unstick();
            double move = ds * travelSign(bogey, bodyFwd);

            Vector3d cAhead = ahead.rail().center();
            Vector3d cThis = bogey.rail().center();
            if (cAhead != null && cThis != null) {
                double chord = cAhead.distance(cThis);
                double rigid = Math.sqrt(bogey.localPos().distSqr(ahead.localPos()));
                double err = chord - rigid; // >0: lagging too far behind the bogey ahead
                Vec3 f = bogey.rail().forward();
                double toward = (cAhead.x - cThis.x) * f.x + (cAhead.z - cThis.z) * f.z;
                double corr = Math.max(-MAX_FOLLOW_CORRECTION, Math.min(MAX_FOLLOW_CORRECTION, err));
                move += toward >= 0.0 ? corr : -corr;
            }

            bogey.rail().setSpeed(move);
            bogey.rail().tick();
        }
    }

    /**
     * Drives wheel-rotation animation on every bogey in the car for this substep.
     *
     * <p>Delegates to {@link AbstractBogeyBlockEntity#animate(float)}, which converts the arc-length
     * {@code ds} into degrees using each bogey block's own {@link AbstractBogeyBlock#getWheelRadius()},
     * accumulates the result into its {@code virtualAnimation} {@code LerpedFloat}, and marks the BE
     * changed so the new angle is sent to clients. On the client, {@link
     * com.simibubi.create.content.trains.bogey.BogeyBlockEntityVisual} picks up the interpolated angle
     * via {@code getVirtualAngle(partialTick)} and passes it straight to the style's {@link
     * com.simibubi.create.content.trains.bogey.BogeyVisual#update} — so every registered bogey style
     * (standard small/large, mod-added styles) spins its wheels correctly with no extra code here.
     *
     * @param car the car whose bogey BEs should be animated
     * @param sub the server sub-level that owns the car's body blocks
     * @param ds  arc-length moved this substep (blocks, may be negative when reversing)
     */
    private static void animateBogeyWheels(Car car, ServerSubLevel sub, double ds) {
        if (ds == 0.0) return;
        float distanceMoved = (float) Math.abs(ds);
        ServerLevel subLevel = sub.getLevel();
        for (SableTrain.Bogey bogey : car.bogeys()) {
            BlockPos worldPos = sub.getPlot().getCenterBlock().offset(bogey.localPos());
            if (subLevel.getBlockEntity(worldPos) instanceof AbstractBogeyBlockEntity be) {
                be.animate(distanceMoved);
            }
        }
    }

    /** +1/−1 so a follower seated facing the other way still advances along the body's travel direction. */
    private static double travelSign(SableTrain.Bogey bogey, Vec3 bodyFwd) {
        Vec3 f = bogey.rail().forward();
        return (f.x * bodyFwd.x + f.z * bodyFwd.z) < 0.0 ? -1.0 : 1.0;
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
    /** Monotonic physics-substep counter, so [bounce] substep logs correlate with each other (and with ticks). */
    private static long substepSeq = 0;

    /** Last speed broadcast to clients per train, so the marker only re-syncs when the speed actually moves. */
    private static final java.util.Map<UUID, Double> SYNCED_SPEED = new java.util.HashMap<>();
    /** Trains whose last broadcast marker said "derailed" (so a derail flip always re-syncs immediately). */
    private static final java.util.Set<UUID> SYNCED_DERAILED = new java.util.HashSet<>();

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
        stationTickCounter++;
        for (SableTrain train : SableTrainRegistry.all()) {
            try {
                checkDerailment(train);
                applyPropulsion(train);
                tickStation(train, operatedControllerIn(train));
                train.tickMotion();
                updateBogeyYaw(train, diag);
                updateBogeyWheels(train);
                syncSpeed(train);
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
        // #2: per-tick rope coupling state, once per distinct level (the manager is per-level, not per-train).
        if (Config.BOUNCE_DEBUG.get()) {
            java.util.Set<ServerLevel> levels = new java.util.HashSet<>();
            for (SableTrain train : SableTrainRegistry.all()) {
                if (levels.add(train.level())) {
                    logCouplingState(train.level());
                }
            }
        }
    }

    /**
     * Re-broadcasts the train's client marker when its speed has moved meaningfully since the last sync, so
     * clients always know the authoritative speed (drives the train sounds) without per-tick packet spam:
     * while cruising at constant speed nothing is sent.
     */
    private static void syncSpeed(SableTrain train) {
        double speed = train.speed();
        boolean derailed = train.isDerailed();
        Double last = SYNCED_SPEED.get(train.id());
        boolean lastDerailed = SYNCED_DERAILED.contains(train.id());
        if (last != null && Math.abs(last - speed) < 0.004 && derailed == lastDerailed) {
            return;
        }
        SYNCED_SPEED.put(train.id(), speed);
        if (derailed) {
            SYNCED_DERAILED.add(train.id());
        } else {
            SYNCED_DERAILED.remove(train.id());
        }
        SableTrainSyncPacket.broadcast(train);
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
    private static void updateBogeyYaw(SableTrain train, boolean diag) {
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
        if (train.isDerailed()) {
            return; // off the rails — the rail followers are stale; the wheels freeze in their last visual pose
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

        int index = 0;
        for (SableTrain.Bogey bogey : car.bogeys()) {
            // Rail tangent at this specific bogey's position on the track.
            Vec3 bogeyFwd = bogey.rail().forward();
            Vector3d bogeyTangent = new Vector3d(bogeyFwd.x, bogeyFwd.y, bogeyFwd.z);
            if (bogeyTangent.lengthSquared() < 1.0e-9) {
                index++;
                continue;
            }
            bogeyTangent.normalize();
            // The bogey model is front-back symmetric: measure against the nearest half-turn of the tangent,
            // so a follower seated facing the other way doesn't read as a ±180° yaw (wrong pivot direction).
            if (bodyForward.dot(bogeyTangent) < 0.0) {
                bogeyTangent.mul(-1.0);
            }

            // Yaw angle: signed angle around Y between body forward and bogey tangent.
            // atan2 of the cross product (Y component) vs dot product gives the signed angle.
            double cross = bodyForward.x * bogeyTangent.z - bodyForward.z * bogeyTangent.x;
            double dot   = bodyForward.x * bogeyTangent.x + bodyForward.z * bogeyTangent.z;
            // Negated: Minecraft's Y rotation (Axis.YP, applied by BogeyYawVisualMixin) turns
            // clockwise-when-viewed-from-above — the opposite sense to this math-convention atan2 angle.
            // Without the flip the wheels pivot AWAY from the rail instead of turning to follow it.
            float yawDeg = -(float) Math.toDegrees(Math.atan2(cross, dot));

            // Visual position correction: the bogey block is carried rigidly by the body, which sits on the
            // CHORD between its bogeys — on curves the rigid position drifts off the rail (inward sagitta +
            // along-track). Create renders each bogey at its own point ON the rail; reproduce that by offsetting
            // the rendered model from its rigid position to the rail point under it. Horizontal only — the mount
            // height above the rail stays rigid. Expressed in the body sub-level's local axes, the frame the BE
            // renderer's pose stack lives in.
            BlockPos worldPos = sub.getPlot().getCenterBlock().offset(bogey.localPos());
            float offX = 0.0f, offY = 0.0f, offZ = 0.0f;
            Vector3d railC = bogey.rail().center();
            if (railC != null) {
                Vector3d rigidW = sub.logicalPose().transformPosition(
                        new Vector3d(worldPos.getX() + 0.5, worldPos.getY() + 0.5, worldPos.getZ() + 0.5),
                        new Vector3d());
                Vector3d offW = new Vector3d(railC).sub(rigidW);
                offW.y = 0.0; // keep the rigid mount height; correct the rail-plane drift only
                // LATERAL correction only: project out the along-track component. The follower's arc position
                // is only block-accurate, so the longitudinal part of (rail − rigid) is noise that would drag
                // the wheels along the track — the perpendicular part is what centres them on the rail line.
                Vector3d tanH = new Vector3d(bogeyTangent.x, 0.0, bogeyTangent.z);
                if (tanH.lengthSquared() > 1.0e-9) {
                    tanH.normalize();
                    offW.sub(tanH.mul(offW.dot(tanH)));
                }
                if (offW.lengthSquared() > MAX_BOGEY_VISUAL_OFFSET * MAX_BOGEY_VISUAL_OFFSET) {
                    offW.normalize().mul(MAX_BOGEY_VISUAL_OFFSET); // follower glitch — don't fling the model
                }
                Vector3d offL = sub.logicalPose().orientation().transformInverse(offW, new Vector3d());
                offX = (float) offL.x;
                offY = (float) offL.y;
                offZ = (float) offL.z;
            }

            // Find the bogey's BlockEntity inside the sub-level and push the visual pose into it.
            boolean beFound = false;
            if (subLevel.getBlockEntity(worldPos) instanceof AbstractBogeyBlockEntity be) {
                BogeyYawVisual.setLocalVisual(be, yawDeg, offX, offY, offZ);
                beFound = true;
            }

            if (diag) {
                double fwdDot = bogeyFwd.x * bodyForward.x + bogeyFwd.z * bodyForward.z;
                LoconauticsConstants.LOGGER.info(
                        "[sabletrain] bogey[{}] yaw={} latOff=({}, {}) fwdDotBody={} stopped={} beFound={} railC={}",
                        index, String.format("%.1f", yawDeg), String.format("%.2f", offX),
                        String.format("%.2f", offZ), String.format("%.2f", fwdDot),
                        bogey.rail().stopped(), beFound,
                        railC == null ? "null" : String.format("(%.1f, %.1f, %.1f)", railC.x, railC.y, railC.z));
            }
            index++;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Bogey wheel animation: drive virtualAnimation on each bogey BE (kinematic-pin path).
    // ---------------------------------------------------------------------------------------------

    /**
     * Called once per game tick for kinematic-pin trains (cars without captured bogey attachment
     * points). For the constraint-glued path, {@link #animateBogeyWheels} is called from inside
     * {@link #driveConstraintGlued} each physics substep instead, where the actual slide distance
     * ({@code ds}) is measured directly from the body's motion.
     *
     * <p>This method uses {@code train.speed()} (blocks/tick) as a proxy, which is accurate enough
     * for the pin path since it is also what {@link SableTrain#tickMotion()} advances the rail by.
     */
    private static void updateBogeyWheels(SableTrain train) {
        // Constraint-glued cars are animated per substep in driveConstraintGlued; skip them here.
        Car car = train.car();
        if (useForceGlue(car)) return;

        double ds = train.speed();
        if (ds == 0.0) return;

        ServerSubLevelContainer container = SubLevelContainer.getContainer(train.level());
        if (container == null) return;
        if (car.subLevelId() == null
                || !(container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub)
                || sub.isRemoved()) return;

        animateBogeyWheels(car, sub, ds);
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
            return;
        }
        // Cornering load: derail when mass × speed × turn-rate exceeds the limit (heavier/faster/sharper = worse).
        if (Config.DERAIL_ON_CURVE.get()) {
            double lateral = lateralLoad(train, sub);
            if (lateral > Config.DERAIL_MAX_LATERAL_FORCE.get()) {
                train.setDerailed(true);
                LAST_FORWARD.remove(train.id());
                LoconauticsConstants.LOGGER.info(
                        "[sabletrain] car {} derailed cornering: load {} > {} (mass*speed*turn)",
                        train.id(), f(lateral), f(Config.DERAIL_MAX_LATERAL_FORCE.get()));
                return;
            }
        }
        // Off the end of the track: the carriage's rail point hit a dead end. If it arrives with enough momentum
        // (mass × speed) and there's no buffer, it flies off the rail instead of parking.
        if (Config.DERAIL_AT_TRACK_END.get() && car.carriage().stopped()) {
            double impact = trainMass(sub) * Math.abs(train.speed()) * 20.0; // mass × m/s
            if (impact > Config.DERAIL_MAX_END_IMPACT.get()) {
                train.setDerailed(true);
                LAST_FORWARD.remove(train.id());
                LoconauticsConstants.LOGGER.info(
                        "[sabletrain] car {} ran off the track end with impact {} > {} (no buffer) — derailed",
                        train.id(), f(impact), f(Config.DERAIL_MAX_END_IMPACT.get()));
            }
        }
    }

    /** Body forward (horizontal) from the previous tick, per train — used to measure the turn rate. */
    private static final java.util.Map<UUID, Vector3d> LAST_FORWARD = new java.util.HashMap<>();

    /**
     * Cornering load on the rails this tick: {@code mass × speed(m/s) × turnRate(rad/s)}. The turn rate is how
     * fast the body's heading is rotating (this tick's forward vs last tick's), so it is 0 on straight track and
     * grows with sharper, faster turns. Multiplying by mass means heavier trains push harder — matching the
     * "velocity × mass" rule. Returns 0 when there's no previous heading yet (first tick).
     */
    private static double lateralLoad(SableTrain train, ServerSubLevel sub) {
        Vec3 f = train.car().carriage().forward();
        Vector3d fwd = new Vector3d(f.x, 0.0, f.z);
        if (fwd.lengthSquared() < 1.0e-9) {
            return 0.0;
        }
        fwd.normalize();
        Vector3d last = LAST_FORWARD.put(train.id(), new Vector3d(fwd));
        if (last == null) {
            return 0.0; // no previous heading to compare against yet
        }
        double dYaw = Math.acos(Math.max(-1.0, Math.min(1.0, last.dot(fwd)))); // radians turned this tick
        double turnRate = dYaw * 20.0;                       // rad/s
        double speed = Math.abs(train.speed()) * 20.0;       // m/s
        return trainMass(sub) * speed * turnRate;
    }

    /** Train mass (kg) from the sub-level's mass tracker, falling back to a live block-sum if unavailable. */
    private static double trainMass(ServerSubLevel sub) {
        MassData md = sub.getMassTracker();
        double m = (md != null && !md.isInvalid()) ? md.getMass() : 0.0;
        return m > 0.0 ? m : liveMass(sub);
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
        // #5: every-10-tick refresh. Compare the freshly-summed consist mass with the value the physics tick has
        // been driving with (cachedHaul) to spot a stale mass during the bounce window.
        if (Config.BOUNCE_DEBUG.get()) {
            LoconauticsConstants.LOGGER.info("[bounce] mass car={} consistTotal={} cachedHaul={} axleMass={}",
                    train.id(), f(total), f(train.haulMass()), f(axle.getTrainMass()));
        }
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
    // Bounce diagnostics (gated behind Config.BOUNCE_DEBUG). All lines carry the [bounce] tag.
    // ---------------------------------------------------------------------------------------------

    /**
     * #1: logs one car's velocity along its rail tangent and its kinetic energy (½·m·|v|²), tagged with the current
     * substep and {@code phase}. Logging the same car at PRE (before Sable's solve) and POST (after) brackets the
     * collision/constraint solve, so a {@code KE} that rises across the step proves energy is being added.
     */
    private static void logBounceVel(String phase, ServerSubLevel sub, SableTrain train, Car car) {
        RigidBodyHandle handle = RigidBodyHandle.of(sub);
        if (handle == null) {
            return;
        }
        Vector3d v = handle.getLinearVelocity(new Vector3d());
        MassData md = sub.getMassTracker();
        double m = (md != null && !md.isInvalid()) ? md.getMass() : 1.0;
        double vTan = v.dot(forwardUnit(car.carriage()));
        double ke = 0.5 * m * v.lengthSquared();
        LoconauticsConstants.LOGGER.info("[bounce] seq={} {} car={} vTan={} |v|={} KE={} m={}",
                substepSeq, phase, train.id(), f(vTan), f(v.length()), f(ke), f(m));
    }

    /**
     * #2: logs every rope strand's current vs rest extension (the closest available signal to coupling "stretch").
     * NOTE: the steel-cable coupling is, at the Sable level, a bidirectional velocity-damped joint with no length
     * stop (see {@code ServerRopeStrand.reattachConstraints} — linear motors, stiffness 0, no force limit), NOT a
     * tension-only rope. So there is no taut/slack switching to observe; this stretch value is informational.
     */
    private static void logCouplingState(ServerLevel level) {
        var manager = dev.simulated_team.simulated.content.blocks.rope.strand.server.ServerLevelRopeManager
                .getOrCreate(level);
        if (manager == null) {
            return;
        }
        for (var strand : manager.getAllStrands()) {
            double cur = strand.getCurrentExtension();
            double goal = strand.getExtension();
            LoconauticsConstants.LOGGER.info("[bounce] rope cur={} goal={} stretch={}",
                    f(cur), f(goal), f(cur - goal));
        }
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

    // ---------------------------------------------------------------------------------------------
    // Station stopping (mirrors Create's manual driving): an "approach" prompt appears when a station is on the
    // leading bogey's edge ahead; the operator holds Space to engage a distance-based braking curve that parks the
    // train exactly at the marker; pressing W departs. Runs between applyPropulsion (which writes targetSpeed from
    // the Bearing Axle RPM) and tickMotion (which ramps speed toward targetSpeed), so braking can own targetSpeed.
    // ---------------------------------------------------------------------------------------------

    /** Constant deceleration used by the station braking curve (blocks/tick²). Tunable. */
    private static final double STATION_DECELERATION = 0.003;
    /** Park when the nearest bogey is within this many blocks of the marker (the braking curve has crept it in). */
    private static final double STATION_PARK_EPSILON = 0.4;
    /** Fixed back-shift of the bogey "front detector" along the approach (blocks): the detector sits this far behind
     *  the bogey, so the train always parks at the same offset relative to the marker. */
    private static final double STATION_FRONT_OFFSET = -3.5;
    /** Amber/brass colour Create uses for station names in prompts. */
    private static final int STATION_NAME_COLOR = 7358000;
    /** Shared cadence (in game ticks) for re-sending a live banner so its client keepalive never lapses. */
    private static int stationTickCounter = 0;

    /** A reachable station: the {@code GlobalStation}, the along-rail distance to it, and the travel direction
     *  toward it ({@code +1} = node1→node2, {@code -1} = reverse). */
    private record StationTarget(GlobalStation station, double distance, double direction) {}

    /**
     * Drives the station-stop state machine for one train, reading the mounted operator's controls
     * ({@code controller}, may be {@code null} when nobody is driving) and pushing HUD prompts as Create does.
     */
    private static void tickStation(SableTrain train, AnalogControllerBlockEntity controller) {
        // Parking is a BEARING AXLE feature: only a powered locomotive (the car carrying a Bearing Axle) can approach
        // and park at stations — exactly like a Bearing-Axle-driven train. A car that is derailed, a free physics
        // body, or has no axle (isPowered is set false by applyPropulsion when no axle is present) never enters the
        // station state machine, and any car that loses its axle mid-stop is released back to RUNNING.
        if (train.isDerailed() || train.isPhysics() || !train.isPowered()) {
            if (train.stationState() != StationState.RUNNING) {
                train.setStationState(StationState.RUNNING);
                train.setCurrentStation(null);
                train.setDistanceToStation(Double.MAX_VALUE);
            }
            return;
        }

        boolean spaceHeld = controller != null && controller.isSpaceHeld();
        boolean forwardHeld = controller != null && controller.isForwardHeld();
        ServerPlayer operator = controller != null ? operatorOf(train, controller) : null;
        boolean resend = (stationTickCounter % 10) == 0; // keep the banner's client keepalive (30t) from lapsing

        switch (train.stationState()) {
            case RUNNING -> {
                // Speed-and-distance gate (mirrors Navigation.findNearestApproachable): the prompt/arming only
                // become available once the station is within braking range for the CURRENT speed — so it appears
                // later when you're slow, earlier when you're fast — and not while still too close to stop cleanly.
                double brakingDistance = (train.speed() * train.speed()) / (2.0 * STATION_DECELERATION);
                double minDistance = 0.75 * brakingDistance;
                double maxDistance = Math.max(32.0, 1.5 * brakingDistance);
                StationTarget target = scanStation(train, maxDistance, null);
                if (target == null || target.distance() < minDistance) {
                    return;
                }
                if (spaceHeld) {
                    // Operator committed: arm the approach — the curve drives the train toward the marker (forward
                    // or backward, whichever side the station is on) and parks it there.
                    train.setCurrentStation(target.station());
                    train.setDistanceToStation(target.distance());
                    train.setStationDirection(target.direction());
                    train.setStationState(StationState.STOPPING);
                } else if (operator != null && resend) {
                    // "Hold [Jump] to approach <station>" — the prompt the operator acts on.
                    sendApproachPrompt(operator, target.station());
                }
            }
            case STOPPING -> {
                if (!spaceHeld) {
                    // Released before parking: abort and hand control back (matches Create cancelling navigation).
                    train.setStationState(StationState.RUNNING);
                    train.setCurrentStation(null);
                    train.setDistanceToStation(Double.MAX_VALUE);
                    return;
                }
                // Re-MEASURE the live along-rail distance from the nearest bogey to the committed station every tick.
                // Dead-reckoning (distance -= speed) drifts several blocks because the sampled speed never matches the
                // sub-step movement exactly; re-scanning ties the stop to the real bogey↔marker geometry, so the train
                // parks at the SAME spot every time. Search a little past the last known distance to stay bounded.
                double searchRange = Math.max(32.0, train.distanceToStation() + 8.0);
                StationTarget live = scanStation(train, searchRange, train.currentStation());
                if (live == null) {
                    // Lost sight of the station (rail/switch changed) — release control gracefully.
                    train.setStationState(StationState.RUNNING);
                    train.setCurrentStation(null);
                    train.setDistanceToStation(Double.MAX_VALUE);
                    return;
                }
                double distance = live.distance();
                train.setDistanceToStation(distance);
                train.setStationDirection(live.direction());
                if (distance <= STATION_PARK_EPSILON) {
                    // A bogey is on the marker: stop and park.
                    train.setSpeed(0.0);
                    train.setTargetSpeed(0.0);
                    train.setStationState(StationState.STOPPED);
                    return;
                }
                // direction × sqrt(2·decel·distance): a speed profile that DRIVES the train toward the marker (it
                // accelerates from rest up to this, then the shrinking distance tapers it to zero on arrival).
                // Capped to the train's top speed so it never commands more than the axle can deliver.
                double mag = Math.min(Math.sqrt(2.0 * STATION_DECELERATION * distance), bearingAxleCapBpt());
                train.setTargetSpeed(live.direction() * mag); // overrides whatever applyPropulsion just wrote
                if (operator != null && resend) {
                    sendStationPrompt(operator, "loconautics.train.approaching_station", train.currentStation());
                }
            }
            case STOPPED -> {
                train.setTargetSpeed(0.0);
                if (forwardHeld) {
                    // Operator pulls away: depart and release the controls lockout next tick.
                    if (operator != null) {
                        sendStationPrompt(operator, "loconautics.train.departing_from", train.currentStation());
                    }
                    train.setStationState(StationState.DEPARTING);
                    train.setCurrentStation(null);
                    train.setDistanceToStation(Double.MAX_VALUE);
                } else if (operator != null && resend) {
                    sendStationPrompt(operator, "loconautics.train.arrived_at", train.currentStation());
                }
            }
            case DEPARTING -> {
                if (Math.abs(train.speed()) > 0.001) {
                    train.setStationState(StationState.RUNNING);
                }
            }
        }
    }

    /**
     * Walks the rail for the nearest approachable {@link GlobalStation}, mirroring how Create's station detects a
     * train: the <b>front of any bogey is eligible, from either side</b> (Create checks the track one block under a
     * bogey, forward and backward — see {@code StationBlockEntity.trackClicked}). Because these carriages have no
     * inherent front, it scouts from every bogey's rail point in <b>both</b> directions, across edges up to
     * {@code maxDistance}, and returns the nearest station with the travel direction needed to reach it. Steers
     * straight through junctions and applies the same {@code canApproachFrom} platform-side check. Uses throwaway
     * copies of each {@link TravellingPoint} so the live carriage is never moved. {@code null} if none in range.
     */
    private static StationTarget scanStation(SableTrain train, double maxDistance, GlobalStation target) {
        RailCarriage carriage = train.car().carriage();
        StationTarget best = null;
        var bogeys = train.car().bogeys();
        if (bogeys.isEmpty()) {
            best = scoutBothWays(carriage, carriage.leading(), maxDistance, target);
        } else {
            for (SableTrain.Bogey bogey : bogeys) {
                best = nearer(best, scoutBothWays(carriage, bogey.rail().leading(), maxDistance, target));
            }
        }
        return best;
    }

    /**
     * Scouts forward and backward from one origin point, returning the nearer approachable station. When
     * {@code target} is non-null only that station qualifies (used to keep re-measuring the committed stop);
     * when null any approachable station qualifies (used to find one to approach).
     */
    private static StationTarget scoutBothWays(RailCarriage carriage, TravellingPoint origin, double maxDistance,
                                               GlobalStation target) {
        if (origin == null || origin.edge == null) {
            return null;
        }
        return nearer(scoutDirection(carriage, origin, maxDistance, 1.0, target),
                scoutDirection(carriage, origin, maxDistance, -1.0, target));
    }

    /** Returns whichever {@link StationTarget} is closer (handling nulls). */
    private static StationTarget nearer(StationTarget a, StationTarget b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.distance() <= b.distance() ? a : b;
    }

    /**
     * Scouts one direction ({@code direction} = +1 forward / -1 backward) for the nearest approachable station.
     * If {@code target} is non-null, only that station qualifies (others are passed over); otherwise any does.
     */
    private static StationTarget scoutDirection(RailCarriage carriage, TravellingPoint origin,
                                                double maxDistance, double direction, GlobalStation target) {
        TravellingPoint scout = new TravellingPoint(
                origin.node1, origin.node2, origin.edge, origin.position, origin.upsideDown);
        // Shift the detector a fixed distance back along the approach (opposite the scan direction) so the train
        // always parks at the same spot relative to the marker. This back-move ignores edge points.
        scout.travel(carriage.graph(), -direction * STATION_FRONT_OFFSET,
                scout.steer(SteerDirection.NONE, carriage.upNormal()),
                scout.ignoreEdgePoints(), scout.ignoreTurns(), scout.ignorePortals());
        GlobalStation[] found = { null };
        double[] foundDistance = { 0.0 };
        TravellingPoint.IEdgePointListener listener = (distance, pair) -> {
            if (!(pair.getFirst() instanceof GlobalStation station)) {
                return false; // skip signals/observers — keep scanning
            }
            if (target != null && station != target) {
                return false; // looking for a specific station — keep scanning past others
            }
            Couple<TrackNode> nodes = pair.getSecond(); // already oriented to travel direction by travel()
            if (!station.canApproachFrom(nodes.getSecond())) {
                return false;
            }
            found[0] = station;
            foundDistance[0] = Math.abs(distance);
            return true; // stop the scout at the first approachable station
        };
        scout.travel(carriage.graph(), direction * maxDistance,
                scout.steer(SteerDirection.NONE, carriage.upNormal()),
                listener, scout.ignoreTurns(), scout.ignorePortals());
        if (found[0] == null) {
            return null;
        }
        return new StationTarget(found[0], foundDistance[0], direction);
    }

    // ---------------------------------------------------------------------------------------------
    // Station prompts: push the operator's Analog Controller HUD banner (same wording/colour as Create).
    // ---------------------------------------------------------------------------------------------

    /** "Hold [Jump] to approach <station>" — the call-to-action prompt while a station is reachable ahead. */
    private static void sendApproachPrompt(ServerPlayer player, GlobalStation station) {
        if (station == null) {
            return;
        }
        Component name = Component.literal(station.name).withStyle(s -> s.withColor(STATION_NAME_COLOR));
        Component text = Component.translatable("loconautics.train.approach_station",
                Component.keybind("key.jump"), name);
        LoconauticsNetwork.sendStationPrompt(player, text, false);
    }

    /** Builds the amber-named banner Component (matching Create's wording/colour) and sends it to the operator. */
    private static void sendStationPrompt(ServerPlayer player, String key, GlobalStation station) {
        if (station == null) {
            return;
        }
        Component name = Component.literal(station.name).withStyle(s -> s.withColor(STATION_NAME_COLOR));
        LoconauticsNetwork.sendStationPrompt(player, Component.translatable(key, name), false);
    }

    /** Resolves the {@link ServerPlayer} mounted on the given controller, or {@code null}. */
    private static ServerPlayer operatorOf(SableTrain train, AnalogControllerBlockEntity controller) {
        UUID userId = controller.getCurrentUser();
        if (userId == null) {
            return null;
        }
        return train.level().getPlayerByUUID(userId) instanceof ServerPlayer sp ? sp : null;
    }

    /** The {@link AnalogControllerBlockEntity} with an active user inside this train's sub-level, or {@code null}. */
    private static AnalogControllerBlockEntity operatedControllerIn(SableTrain train) {
        ServerSubLevelContainer container = SubLevelContainer.getContainer(train.level());
        if (container == null) {
            return null;
        }
        Car car = train.car();
        if (car.subLevelId() == null
                || !(container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub) || sub.isRemoved()) {
            return null;
        }
        AnalogControllerBlockEntity controller = analogControllerIn(sub);
        return (controller != null && controller.hasUser()) ? controller : null;
    }

    /** Scans a sub-level for an {@link AnalogControllerBlock}'s block entity (mirrors {@link #bearingAxleIn}). */
    private static AnalogControllerBlockEntity analogControllerIn(ServerSubLevel sub) {
        ServerLevel subLevel = sub.getLevel();
        var bounds = sub.getPlot().getBoundingBox();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
            for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
                for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                    pos.set(x, y, z);
                    if (subLevel.getBlockState(pos).getBlock() instanceof AnalogControllerBlock
                            && subLevel.getBlockEntity(pos) instanceof AnalogControllerBlockEntity controller) {
                        return controller;
                    }
                }
            }
        }
        return null;
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
            // #4: the cap only ever rescales magnitude DOWN to the global cap (mass-independent) — it never removes
            // a collision's energy. Repeated firings during a bounce mean it is sustaining, not damping, the loop.
            if (Config.BOUNCE_DEBUG.get()) {
                LoconauticsConstants.LOGGER.info("[bounce] seq={} CAP fired speed={} -> cap={} dir=({}, {}, {})",
                        substepSeq, f(speed), f(capMps), f(velocity.x), f(velocity.y), f(velocity.z));
            }
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