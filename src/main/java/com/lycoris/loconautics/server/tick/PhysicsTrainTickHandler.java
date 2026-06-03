package com.lycoris.loconautics.server.tick;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.lycoris.loconautics.core.LoconauticsConstants;
import com.lycoris.loconautics.core.PhysicsTrainPose;
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
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.platform.SableEventPlatform;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Drives every physics train's carriage sub-level so its {@code logicalPose} follows the pose Create
 * computed for the carriage.
 *
 * <p><b>Why the physics tick (the 2026-06-03 fix):</b> reading the Sable source settled where collision
 * actually lives. Player↔sub-level collision ({@code SubLevelEntityCollision.collide}) and raytracing
 * ({@code clip_overwrite.BlockGetterMixin.clip}) both transform the sub-level's plot blocks by
 * {@code subLevel.logicalPose()} — so {@code logicalPose} is the single authoritative pose for both, while
 * the visual uses {@code renderPose} (coupled to the carriage). The old driver teleported logicalPose in
 * {@link ServerTickEvent.Post} (AFTER the physics tick); the dynamic body then sagged under gravity and
 * collision read the sagged pose (~0.64 below the render → the "gray box below the body"). We now pin
 * logicalPose to the carriage pose <b>inside</b> the physics tick — teleport+resetVelocity in
 * {@link #onPhysicsTick} (keeps the Rapier body consistent) and re-pin in {@link #onPostPhysicsTick}
 * (removes the per-substep sag) — so logicalPose is un-sagged whenever collision/raytrace read it, and the
 * collision surface coincides with the visible body.
 */
@EventBusSubscriber(modid = LoconauticsConstants.MOD_ID)
public final class PhysicsTrainTickHandler {

    /** Throttle for diagnostic logging (ticks). Set high enough not to spam. */
    private static final int LOG_INTERVAL = 40;
    private static int tickCounter = 0;

    private PhysicsTrainTickHandler() {
    }

    /** Wires the physics-tick pose pinning onto Sable's pre/post substep events. Call once during setup. */
    public static void register() {
        SableEventPlatform.INSTANCE.onPhysicsTick(PhysicsTrainTickHandler::onPhysicsTick);
        SableEventPlatform.INSTANCE.onPostPhysicsTick(PhysicsTrainTickHandler::onPostPhysicsTick);
        LoconauticsConstants.LOGGER.info("Loconautics: registered physics-tick pose pinning");
    }

    // ---------------------------------------------------------------------------------------------
    // Physics tick: pin logicalPose to the carriage pose so collision/raytrace read it un-sagged.
    // ---------------------------------------------------------------------------------------------

    /** Pre-step: teleport the body (Rapier + logicalPose) to the carriage pose and kill its velocity. */
    public static void onPhysicsTick(SubLevelPhysicsSystem physicsSystem, double timeStep) {
        forEachCarriageBody(physicsSystem, (pipeline, serverSub, entity) -> {
            Quaterniond orientation = PhysicsTrainPose.orientationOf(entity);
            Vector3d target = targetPosition(entity, serverSub, orientation);
            pipeline.teleport(serverSub, target, orientation);
            pipeline.resetVelocity(serverSub);
        });
    }

    /** Post-step: re-pin logicalPose (only) to the carriage pose, removing the tiny gravity sag. */
    public static void onPostPhysicsTick(SubLevelPhysicsSystem physicsSystem, double timeStep) {
        forEachCarriageBody(physicsSystem, (pipeline, serverSub, entity) -> {
            Quaterniond orientation = PhysicsTrainPose.orientationOf(entity);
            Vector3d target = targetPosition(entity, serverSub, orientation);
            Pose3d pose = serverSub.logicalPose();
            pose.position().set(target);
            pose.orientation().set(orientation);
        });
    }

    /** Runs {@code action} for every physics-train carriage body living in this physics system's level. */
    private static void forEachCarriageBody(SubLevelPhysicsSystem physicsSystem, CarriageBodyAction action) {
        ServerLevel level = physicsSystem.getLevel();
        MinecraftServer server = level.getServer();
        if (server == null) {
            return;
        }
        PhysicsTrainRegistry registry = PhysicsTrainRegistry.get(server);
        if (registry.all().isEmpty()) {
            return;
        }
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) {
            return;
        }
        PhysicsPipeline pipeline = physicsSystem.getPipeline();

        for (PhysicsTrainTag tag : new ArrayList<>(registry.all())) {
            Train train = Create.RAILWAYS.trains.get(tag.trainId());
            if (train == null) {
                continue; // lifecycle (game tick) tears it down
            }
            List<Carriage> carriages = train.carriages;
            int count = Math.min(carriages.size(), tag.subLevelIds().size());
            for (int i = 0; i < count; i++) {
                UUID subLevelId = tag.subLevelIds().get(i);
                if (subLevelId == null) {
                    continue;
                }
                CarriageContraptionEntity entity = carriages.get(i).anyAvailableEntity();
                if (entity == null || entity.level() != level) {
                    continue;
                }
                if (!(container.getSubLevel(subLevelId) instanceof ServerSubLevel serverSub) || serverSub.isRemoved()) {
                    continue;
                }
                try {
                    action.run(pipeline, serverSub, entity);
                } catch (Throwable t) {
                    LoconauticsConstants.LOGGER.error("Error pinning physics-train sub-level {}", subLevelId, t);
                }
            }
        }
    }

    @FunctionalInterface
    private interface CarriageBodyAction {
        void run(PhysicsPipeline pipeline, ServerSubLevel serverSub, CarriageContraptionEntity entity);
    }

    // ---------------------------------------------------------------------------------------------
    // Game tick: lifecycle (orphan/teardown detection) + diagnostic logging only. No driving here.
    // ---------------------------------------------------------------------------------------------

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
                tickLifecycle(server, tag, log);
            } catch (Throwable t) {
                LoconauticsConstants.LOGGER.error("Error in lifecycle for physics train {}", tag.trainId(), t);
            }
        }
    }

    private static void tickLifecycle(MinecraftServer server, PhysicsTrainTag tag, boolean log) {
        Train train = Create.RAILWAYS.trains.get(tag.trainId());
        if (train == null) {
            PhysicsTrainDisassembler.disassemble(server, tag.trainId());
            return;
        }

        List<Carriage> carriages = train.carriages;
        int count = Math.min(carriages.size(), tag.subLevelIds().size());
        boolean sawContainer = false;
        int alive = 0;

        for (int i = 0; i < count; i++) {
            UUID subLevelId = tag.subLevelIds().get(i);
            if (subLevelId == null) {
                continue;
            }
            CarriageContraptionEntity entity = carriages.get(i).anyAvailableEntity();
            if (entity == null || !(entity.level() instanceof ServerLevel level)) {
                continue;
            }
            ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
            if (container == null) {
                continue;
            }
            sawContainer = true;
            if (container.getSubLevel(subLevelId) instanceof ServerSubLevel serverSub) {
                alive++;
                if (log && i == 0) {
                    logPose(entity, serverSub);
                }
            }
        }

        // Train exists but its sub-levels are gone (e.g. removed after a reload): drop the binding.
        if (alive == 0 && sawContainer && count > 0) {
            PhysicsTrainDisassembler.disassemble(server, tag.trainId());
        }
    }

    /** Verifies the pin held: {@code poseNow} should equal {@code target} every tick (no gravity sag). */
    private static void logPose(CarriageContraptionEntity entity, ServerSubLevel serverSub) {
        Quaterniond orientation = PhysicsTrainPose.orientationOf(entity);
        Vector3d target = targetPosition(entity, serverSub, orientation);
        Vector3dc poseNow = serverSub.logicalPose().position();
        LoconauticsConstants.LOGGER.info(
                "[poses] c0: entityPos={} | target=({},{},{}) poseNow=({},{},{}) err=({},{},{})",
                entity.position(),
                fmt(target.x), fmt(target.y), fmt(target.z),
                fmt(poseNow.x()), fmt(poseNow.y()), fmt(poseNow.z()),
                fmt(poseNow.x() - target.x), fmt(poseNow.y() - target.y), fmt(poseNow.z() - target.z));
    }

    /**
     * The world position the sub-level's rotation point must reach so its blocks line up with where
     * Create renders the carriage. {@code position = entityPos + rotate(rotationPoint - plotAnchor, Q)};
     * only x/z are re-centred on the anchor block (Create's carriage sits at the anchor's horizontal
     * centre but its y bottom — the rail height). Identical to the render coupling in
     * {@code ClientSubLevelRenderMixin}, so logicalPose (collision) == renderPose (visual).
     */
    private static Vector3d targetPosition(CarriageContraptionEntity entity, ServerSubLevel sub, Quaterniond orientation) {
        Vec3 pos = entity.position();
        Vector3d target = new Vector3d(pos.x, pos.y, pos.z);

        Vector3dc rotationPoint = sub.logicalPose().rotationPoint();
        BlockPos plotAnchor = sub.getPlot().getCenterBlock();
        Vector3d offset = new Vector3d(
                rotationPoint.x() - (plotAnchor.getX() + 0.5),
                rotationPoint.y() - plotAnchor.getY(),
                rotationPoint.z() - (plotAnchor.getZ() + 0.5));
        orientation.transform(offset); // rotate the local offset into world space
        target.add(offset);
        return target;
    }

    private static String fmt(double d) {
        return String.format("%.2f", d);
    }
}
