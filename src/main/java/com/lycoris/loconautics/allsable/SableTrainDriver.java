package com.lycoris.loconautics.allsable;

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
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Drives the Option-B custom trains ({@link SableTrain}): advances their motion once per game tick and pins
 * each carriage sub-level's pose to its {@link RailCarriage} every physics substep.
 *
 * <p>The pinning is the exact collision-correct mechanism the hybrid {@code PhysicsTrainTickHandler} uses
 * (teleport + resetVelocity pre-step, re-pin {@code logicalPose} post-step), but the target pose comes from
 * our rail math instead of a {@code CarriageContraptionEntity}. Sable's event platform accumulates listeners
 * (it does {@code NeoForge.EVENT_BUS.addListener} per call), so registering here does not disturb the hybrid
 * driver — the two operate on disjoint sub-levels.
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

    // Bogey spring (physics mode): acceleration coefficients (force is mass-normalised). Tune in-game.
    private static final double SPRING_K = 150.0; // stiffness: accel per metre of offset toward the rail
    private static final double SPRING_C = 25.0;  // damping: ~critical for K (2·sqrt(K) ≈ 24.5)

    /** Pre-step: PIN cars teleport to the rail; PHYSICS cars get bogey spring forces (free body, can derail). */
    public static void onPhysicsTick(SubLevelPhysicsSystem system, double timeStep) {
        forEachCar(system, (pipeline, sub, train, car) -> {
            if (train.isPhysics()) {
                applyBogeySprings(sub, car, timeStep, system.getLevel());
                return;
            }
            Quaterniond q = car.carriage().orientation();
            Vector3d center = car.carriage().center();
            if (center == null) {
                return;
            }
            Vector3d target = targetPosition(sub, center, q);
            pipeline.teleport(sub, target, q);
            pipeline.resetVelocity(sub);
        });
    }

    /** Post-step: re-pin PIN cars (removes the gravity sag). PHYSICS cars simulate freely — leave them alone. */
    public static void onPostPhysicsTick(SubLevelPhysicsSystem system, double timeStep) {
        forEachCar(system, (pipeline, sub, train, car) -> {
            if (train.isPhysics()) {
                return;
            }
            Quaterniond q = car.carriage().orientation();
            Vector3d center = car.carriage().center();
            if (center == null) {
                return;
            }
            Vector3d target = targetPosition(sub, center, q);
            Pose3d pose = sub.logicalPose();
            pose.position().set(target);
            pose.orientation().set(q);
        });
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
            for (Car car : train.cars()) {
                if (car.subLevelId() == null) {
                    continue;
                }
                if (!(container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub) || sub.isRemoved()) {
                    continue;
                }
                try {
                    action.run(pipeline, sub, train, car);
                } catch (Throwable t) {
                    LoconauticsConstants.LOGGER.error("[sabletrain] error driving sub-level {}", car.subLevelId(), t);
                }
            }
        }
    }

    @FunctionalInterface
    private interface CarAction {
        void run(PhysicsPipeline pipeline, ServerSubLevel sub, SableTrain train, Car car);
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
        boolean updateMass = (massCounter++ % 30) == 0; // re-poll the Bearing Axle's train weight every 30 ticks
        for (SableTrain train : SableTrainRegistry.all()) {
            try {
                applyPropulsion(train);
                train.tickMotion();
                if (updateMass) {
                    updateAxleMass(train);
                }
                if (diag && !train.cars().isEmpty()) {
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
        double total = 0.0;
        boolean readAny = false;
        BearingAxleBlockEntity axle = null;
        for (Car car : train.cars()) {
            if (car.subLevelId() == null
                    || !(container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub) || sub.isRemoved()) {
                continue;
            }
            MassData md = sub.getMassTracker();
            if (md != null) {
                total += md.getMass();
                readAny = true;
            }
            if (axle == null) {
                axle = bearingAxleIn(sub);
            }
        }
        if (axle != null && readAny && total != axle.getTrainMass()) {
            axle.setTrainMass(total);
        }
    }

    /** Diagnostics: per-car centre/stopped state (to see why a car isn't moving) + bearing-axle RPM. */
    private static void logOrientation(SableTrain train) {
        String axleRpm = "no-axle";
        String axleMass = "n/a";
        double subMass = 0.0;
        ServerSubLevelContainer container = SubLevelContainer.getContainer(train.level());
        if (container != null) {
            for (Car car : train.cars()) {
                if (car.subLevelId() != null
                        && container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub && !sub.isRemoved()) {
                    MassData md = sub.getMassTracker();
                    if (md != null) {
                        subMass += md.getMass();
                    }
                }
            }
            BearingAxleBlockEntity axle = findBearingAxle(train, container);
            if (axle != null) {
                axleRpm = f(axle.getSpeed());
                axleMass = f(axle.getTrainMass());
            }
        }
        StringBuilder cars = new StringBuilder();
        for (int i = 0; i < train.cars().size(); i++) {
            RailCarriage c = train.cars().get(i).carriage();
            var centre = c.center();
            cars.append(" car").append(i)
                    .append(centre == null ? "=NULL" : "=(" + f(centre.x) + "," + f(centre.y) + "," + f(centre.z) + ")")
                    .append(c.stopped() ? "[STOPPED]" : "");
        }
        LoconauticsConstants.LOGGER.info("[sabletrain] diag speed={} target={} axleRPM={} subMass={} axleTrainMass={} cars={}:{}",
                f(train.speed()), f(train.targetSpeed()), axleRpm, f(subMass), axleMass, train.cars().size(), cars);
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
            return; // no engine on board — keep the debug/command target speed
        }
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

    /** Finds the Bearing Axle inside any of the train's car sub-levels (the propulsion engine), or null. */
    static BearingAxleBlockEntity findBearingAxle(SableTrain train, ServerSubLevelContainer container) {
        for (Car car : train.cars()) {
            if (car.subLevelId() == null
                    || !(container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub) || sub.isRemoved()) {
                continue;
            }
            BearingAxleBlockEntity axle = bearingAxleIn(sub);
            if (axle != null) {
                return axle;
            }
        }
        return null;
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
