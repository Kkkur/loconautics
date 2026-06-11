package com.lycoris.loconautics.allsable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;

import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Wheel/steam particles for Sable train sub-levels — a port of Create's {@code CarriageParticles}: per
 * bogey, contact sparks ({@code BogeyStyle.contactParticle}) fly from the four wheel-rail corners while
 * rolling, a shower of them while braking hard, and steam puffs ({@code BogeyStyle.smokeParticle}) vent
 * during the ~1&nbsp;second depressurise after the train stops. Same emission probabilities, offsets and
 * particle motions as Create, with the speed scaled into Create's band (same {@code SPEED_SCALE} idea as
 * {@link SableTrainSounds}).
 *
 * <p>Extra (no Create equivalent): a derailed car that is still sliding grinds out a spark shower at its
 * wheel line, matching the derail sound, fading as the free body stops.
 *
 * <p>Bogey positions/orientations come from the client render state ({@link BogeyWheelAnimator#bogeysOf}
 * indexes the rendered bogey BEs), so no extra sync is needed. Ticked from
 * {@code LoconauticsClient.onClientTick}.
 */
@OnlyIn(Dist.CLIENT)
public final class SableTrainParticles {

    private static final Map<UUID, SableTrainParticles> ACTIVE = new HashMap<>();

    /** Same gear-range mapping as {@link SableTrainSounds}: our blocks/tick × 3 ≈ Create's curve band. */
    private static final double SPEED_SCALE = 3.0;
    /** Create only emits carriage particles within 64 blocks of the camera. */
    private static final double MAX_DISTANCE = 64.0;

    /** Advances every train's particle emitter; called once per client tick. */
    public static void tickAll() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            ACTIVE.clear();
            return;
        }
        if (mc.isPaused() || mc.cameraEntity == null) {
            return;
        }
        Set<UUID> ids = SableTrainClientRegistry.ids();
        ACTIVE.keySet().removeIf(id -> !ids.contains(id));
        if (ids.isEmpty()) {
            return;
        }
        SubLevelContainer container = SubLevelContainer.getContainer(mc.level);
        if (container == null) {
            return;
        }
        for (UUID id : ids) {
            SubLevel sub = container.getSubLevel(id);
            if (!(sub instanceof ClientSubLevel clientSub) || sub.isRemoved()) {
                ACTIVE.remove(id);
                continue;
            }
            ACTIVE.computeIfAbsent(id, k -> new SableTrainParticles()).tick(mc, clientSub);
        }
    }

    // ---- per-train state (mirrors Create's CarriageParticles fields) ----

    private final LerpedFloat brakes = LerpedFloat.linear();
    private boolean arrived = true;
    private int depressurise;
    private double prevMotion;
    private float accumulatedSteamRelease;
    private Vector3d lastPos;
    private int diagTick;

    private SableTrainParticles() {
    }

    private void tick(Minecraft mc, ClientSubLevel sub) {
        Vector3dc p = sub.renderPose().position();
        Vector3d pos = new Vector3d(p);
        Vector3d motion = lastPos == null ? new Vector3d() : pos.sub(lastPos, new Vector3d());
        lastPos = pos;

        Vec3 camPos = mc.cameraEntity.position();
        if (pos.distanceSquared(camPos.x, camPos.y, camPos.z) > MAX_DISTANCE * MAX_DISTANCE) {
            return;
        }

        SableTrainClientRegistry.TrainMarker marker = SableTrainClientRegistry.get(sub.getUniqueId());
        double rawSpeed = marker != null ? Math.abs(marker.speed()) : motion.length();
        double length = Math.min(rawSpeed * SPEED_SCALE, 2.0); // Create's "contraptionMotion.length()"
        RandomSource r = mc.level.random;
        List<AbstractBogeyBlockEntity> bogeys = BogeyWheelAnimator.bogeysOf(sub.getUniqueId());

        // DERAILED: grinding spark shower at the wheel line while the free body slides (pairs the sound).
        if (marker != null && marker.derailed()) {
            double sliding = motion.length();
            if (sliding > 0.02 && !bogeys.isEmpty()) {
                float cutoff = (float) Math.min(1.0, sliding * 3.0 * 1.2);
                Vec3 drift = new Vec3(motion.x, motion.y, motion.z).scale(0.75);
                for (AbstractBogeyBlockEntity be : bogeys) {
                    emitCorners(mc, sub, be, true, cutoff, false, drift, r);
                    if (r.nextInt(3) == 0) { // dust kicked up alongside the sparks
                        Vec3 c = bogeyCenter(sub, be);
                        mc.level.addParticle(ParticleTypes.POOF,
                                c.x + (r.nextDouble() - 0.5) * 2.0, c.y - 1.2, c.z + (r.nextDouble() - 0.5) * 2.0,
                                drift.x, 0.05, drift.z);
                    }
                }
            }
            return;
        }

        // ---- from here on: Create's CarriageParticles.tick, with `length` as the motion magnitude ----

        if (arrived && length > 0.01f) {
            arrived = false;
        }
        accumulatedSteamRelease = (float) Math.min(accumulatedSteamRelease + Math.min(0.5, length / 10.0), 10.0);
        boolean stopped = length < 0.002f;
        if (stopped) {
            if (!arrived) {
                arrived = true;
                depressurise = (int) (20.0f * accumulatedSteamRelease / 10.0f);
                accumulatedSteamRelease = 0.0f;
            }
        } else {
            depressurise = 0;
        }
        if (depressurise > 0) {
            depressurise--;
        }

        brakes.chase(prevMotion > length + length / 512.0 ? 1.0 : 0.0, 0.25, LerpedFloat.Chaser.exp(0.625));
        brakes.tickChaser();
        prevMotion = length;

        Vec3 drift = new Vec3(motion.x, motion.y, motion.z).scale(0.75);
        boolean spark = depressurise == 0 || depressurise > 10;
        float cutoff = length < 0.125 ? 0.0f : 0.125f;
        if (length > 0.1666666716337204) {
            cutoff = Math.max(cutoff, brakes.getValue() * 1.15f);
        }

        if (++diagTick % 20 == 0) {
            com.lycoris.loconautics.core.LoconauticsConstants.LOGGER.info(
                    "[sabletrain] particles: train {} len={} cutoff={} bogeys={} spark={} depres={} brakes={}",
                    sub.getUniqueId().toString().substring(0, 8), String.format("%.3f", length),
                    String.format("%.3f", cutoff), bogeys.size(), spark, depressurise,
                    String.format("%.2f", brakes.getValue()));
        }

        if (bogeys.isEmpty()) {
            return;
        }
        for (AbstractBogeyBlockEntity be : bogeys) {
            emitCorners(mc, sub, be, spark, cutoff, !spark, drift, r);
        }
    }

    /**
     * Create's four-corner emission for one bogey: particles at the wheel-rail contact corners
     * (±lateral, ±along the local rail tangent), kicked outward and dragged by the train's motion.
     * {@code smokeLeniency} reproduces Create's depressurise behaviour (smoke ignores the speed cutoff
     * three times out of four, so the stopped train still vents).
     */
    private static void emitCorners(Minecraft mc, ClientSubLevel sub, AbstractBogeyBlockEntity be,
                                    boolean spark, float cutoff, boolean smokeLeniency,
                                    Vec3 drift, RandomSource r) {
        Vec3 center = bogeyCenter(sub, be);

        // Bogey forward = block axis through the body pose, turned by the visual yaw (the rail tangent).
        BlockState state = be.getBlockState();
        Direction.Axis axis = state.hasProperty(AbstractBogeyBlock.AXIS)
                ? state.getValue(AbstractBogeyBlock.AXIS) : Direction.Axis.X;
        Vector3d f = axis == Direction.Axis.X ? new Vector3d(1, 0, 0) : new Vector3d(0, 0, 1);
        sub.renderPose().orientation().transform(f);
        double yaw = Math.toRadians(BogeyYawVisual.getLocalYaw(be));
        double cos = Math.cos(yaw);
        double sin = Math.sin(yaw);
        double fx = f.x * cos + f.z * sin;   // rotY(yaw), same sense as the render mixin's Axis.YP
        double fz = -f.x * sin + f.z * cos;
        double len = Math.sqrt(fx * fx + fz * fz);
        if (len < 1.0e-6) {
            return;
        }
        fx /= len;
        fz /= len;
        double lx = -fz; // lateral = forward rotated 90°
        double lz = fx;

        ParticleOptions contact = be.getStyle().contactParticle;
        ParticleOptions smoke = be.getStyle().smokeParticle;
        double yOff = spark ? -1.4 : -0.5;   // wheel-rail contact vs just above the axle (block centre frame)

        for (int j : new int[] {1, -1}) {
            if (r.nextFloat() > cutoff && (!smokeLeniency || r.nextInt(4) == 0)) {
                continue;
            }
            for (int i : new int[] {1, -1}) {
                if (r.nextFloat() > cutoff && (!smokeLeniency || r.nextInt(4) == 0)) {
                    continue;
                }
                double px = center.x + lx * (j * 1.15) + fx * i;
                double py = center.y + yOff;
                double pz = center.z + lz * (j * 1.15) + fz * i;
                double kick = spark ? 0.5 : 0.25;
                double mx = lx * (j * kick) + drift.x;
                double my = (spark ? 0.49 : -0.29) + drift.y;
                double mz = lz * (j * kick) + drift.z;
                mc.level.addParticle(spark ? contact : smoke, px, py, pz, mx, my, mz);
            }
        }
    }

    /** The bogey block's centre in world space, from the sub-level's interpolated render pose. */
    private static Vec3 bogeyCenter(ClientSubLevel sub, AbstractBogeyBlockEntity be) {
        return sub.renderPose().transformPosition(Vec3.atCenterOf(be.getBlockPos()));
    }
}
