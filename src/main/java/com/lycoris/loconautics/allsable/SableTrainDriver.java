package com.lycoris.loconautics.allsable;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.lycoris.loconautics.Config;
import com.lycoris.loconautics.allsable.SableTrain.Car;
import com.lycoris.loconautics.content.bearingaxle.BearingAxleBlock;
import com.lycoris.loconautics.content.bearingaxle.BearingAxleBlockEntity;
import com.lycoris.loconautics.core.LoconauticsConstants;

import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.platform.SableEventPlatform;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
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

    /** Pre-step: teleport the body (Rapier + logicalPose) to the rail pose and kill its velocity. */
    public static void onPhysicsTick(SubLevelPhysicsSystem system, double timeStep) {
        forEachCar(system, (pipeline, sub, car) -> {
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

    /** Post-step: re-pin logicalPose (only) to the rail pose, removing the per-substep gravity sag. */
    public static void onPostPhysicsTick(SubLevelPhysicsSystem system, double timeStep) {
        forEachCar(system, (pipeline, sub, car) -> {
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
                    action.run(pipeline, sub, car);
                } catch (Throwable t) {
                    LoconauticsConstants.LOGGER.error("[sabletrain] error pinning sub-level {}", car.subLevelId(), t);
                }
            }
        }
    }

    @FunctionalInterface
    private interface CarAction {
        void run(PhysicsPipeline pipeline, ServerSubLevel sub, Car car);
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

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (SableTrainRegistry.isEmpty()) {
            return;
        }
        for (SableTrain train : SableTrainRegistry.all()) {
            try {
                applyPropulsion(train);
                train.tickMotion();
            } catch (Throwable t) {
                LoconauticsConstants.LOGGER.error("[sabletrain] error ticking motion for {}", train.id(), t);
            }
        }
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
        BearingAxleBlockEntity axle = null;
        for (Car car : train.cars()) {
            if (car.subLevelId() != null
                    && container.getSubLevel(car.subLevelId()) instanceof ServerSubLevel sub) {
                axle = findBearingAxle(sub);
                if (axle != null) {
                    break;
                }
            }
        }
        if (axle == null) {
            return; // no powered axle — keep current target (debug constant speed)
        }
        double maxSpeed = maxSpeedBpt();
        double rpm = axle.getSpeed();
        double target = (rpm / 256.0) * maxSpeed;
        target = Math.max(-maxSpeed, Math.min(maxSpeed, target));
        train.setTargetSpeed(target);
    }

    /** Configured speed cap in blocks/tick, or a 1.0 default when unset (0). */
    private static double maxSpeedBpt() {
        double cfg = Config.PHYSICS_TRAIN_MAX_SPEED.get();
        return cfg > 0.0 ? cfg : 1.0;
    }

    /** Scans a sub-level's own level for a {@link BearingAxleBlockEntity}. */
    private static BearingAxleBlockEntity findBearingAxle(ServerSubLevel sub) {
        BoundingBox3ic bounds = sub.getPlot().getBoundingBox();
        ServerLevel subLevel = sub.getLevel();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = bounds.minX(); x <= bounds.maxX(); x++) {
            for (int y = bounds.minY(); y <= bounds.maxY(); y++) {
                for (int z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                    pos.set(x, y, z);
                    if (!(subLevel.getBlockState(pos).getBlock() instanceof BearingAxleBlock)) {
                        continue;
                    }
                    BlockEntity be = subLevel.getBlockEntity(pos);
                    if (be instanceof BearingAxleBlockEntity axle) {
                        return axle;
                    }
                }
            }
        }
        return null;
    }
}
