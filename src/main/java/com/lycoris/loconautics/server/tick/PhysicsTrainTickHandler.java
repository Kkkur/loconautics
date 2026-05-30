package com.lycoris.loconautics.server.tick;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.joml.Quaterniond;
import org.joml.Vector3d;

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
 * {@link ServerSubLevel} to the carriage entity's position + orientation via
 * {@link PhysicsPipeline#teleport}.
 *
 * <p>NOTE (needs in-game tuning): the teleport position is the sub-level's rotation point (center of
 * mass), while {@code entity.position()} is the carriage anchor (leading bogey). There may be a
 * constant offset and the yaw/pitch -> quaternion convention may need a sign flip. This is isolated
 * in {@link #orientationOf} / the position calc so it is easy to adjust from visual feedback.
 */
@EventBusSubscriber(modid = LoconauticsConstants.MOD_ID)
public final class PhysicsTrainTickHandler {

    private PhysicsTrainTickHandler() {
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        PhysicsTrainRegistry registry = PhysicsTrainRegistry.get(server);
        if (registry.all().isEmpty()) {
            return;
        }
        // Copy to avoid concurrent modification if a train is unregistered mid-iteration.
        for (PhysicsTrainTag tag : new ArrayList<>(registry.all())) {
            try {
                driveTrain(tag);
            } catch (Throwable t) {
                LoconauticsConstants.LOGGER.error("Error driving physics train {}", tag.trainId(), t);
            }
        }
    }

    private static void driveTrain(PhysicsTrainTag tag) {
        Train train = Create.RAILWAYS.trains.get(tag.trainId());
        if (train == null) {
            return; // train no longer exists; cleanup handled elsewhere
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
                continue;
            }

            ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
            if (container == null) {
                continue;
            }
            SubLevel sub = container.getSubLevel(subLevelId);
            if (!(sub instanceof ServerSubLevel serverSub)) {
                continue;
            }

            PhysicsPipeline pipeline = container.physicsSystem().getPipeline();

            Vec3 pos = entity.position();
            Vector3d position = new Vector3d(pos.x, pos.y, pos.z);
            Quaterniond orientation = orientationOf(entity);

            pipeline.teleport(serverSub, position, orientation);
        }
    }

    /**
     * Converts the carriage entity's yaw/pitch into a JOML quaternion for Sable.
     * Convention may need tuning against in-game results.
     */
    private static Quaterniond orientationOf(CarriageContraptionEntity entity) {
        double yawRad = Math.toRadians(-entity.yaw);
        double pitchRad = Math.toRadians(entity.pitch);
        return new Quaterniond().rotationYXZ(yawRad, pitchRad, 0.0);
    }
}
