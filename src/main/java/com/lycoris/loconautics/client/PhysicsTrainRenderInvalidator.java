package com.lycoris.loconautics.client;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.lycoris.loconautics.core.LoconauticsConstants;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.render.ClientContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Forces Create's Flywheel contraption visual to rebuild after a train is marked as a physics train.
 *
 * <p>The contraption's <i>structure</i> and child block-entity visuals are built from
 * {@code ClientContraption} state that is captured at first render — which can happen <i>before</i> our
 * {@code PhysicsTrainSyncPacket} arrives, so at build time the train isn't yet known to be physical and
 * the visuals are created normally. {@link ClientContraption#resetRenderLevel()} clears that state and
 * bumps the structure/children versions, so on the next frame the visual rebuilds — and this time
 * {@code ClientContraptionRenderMixin} / {@code ContraptionVisualChildrenMixin} see a physics train and
 * suppress it. (The travelling bogey is handled per-frame by {@code CarriageBogeyVisualMixin} and needs
 * no rebuild.)
 *
 * <p>The carriage entities may not have spawned on the client at sync time, so we retry for a short
 * window on the client tick until the train's carriages are found.
 */
@EventBusSubscriber(modid = LoconauticsConstants.MOD_ID, value = Dist.CLIENT)
public final class PhysicsTrainRenderInvalidator {

    /** trainId -> client ticks remaining to keep retrying the invalidation. */
    private static final Map<UUID, Integer> PENDING = new ConcurrentHashMap<>();
    private static final int RETRY_TICKS = 60;

    private PhysicsTrainRenderInvalidator() {
    }

    /** Request a render rebuild for the given train (call when it enters physics mode). */
    public static void request(UUID trainId) {
        if (trainId != null) {
            PENDING.put(trainId, RETRY_TICKS);
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            PENDING.clear();
            return;
        }
        if (PENDING.isEmpty()) {
            return;
        }

        var it = PENDING.entrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            UUID trainId = entry.getKey();
            boolean invalidatedAny = false;

            for (Entity e : mc.level.entitiesForRendering()) {
                if (e instanceof CarriageContraptionEntity cce && trainId.equals(cce.trainId)) {
                    Contraption contraption = cce.getContraption();
                    if (contraption != null) {
                        contraption.getOrCreateClientContraptionLazy().resetRenderLevel();
                        invalidatedAny = true;
                    }
                }
            }

            int remaining = entry.getValue() - 1;
            if (invalidatedAny || remaining <= 0) {
                it.remove();
            } else {
                entry.setValue(remaining);
            }
        }
    }
}
