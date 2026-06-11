package com.lycoris.loconautics.allsable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.simibubi.create.AllSoundEvents;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;

import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Speed-driven train audio for Sable train sub-levels — a faithful port of Create's
 * {@code CarriageSounds} to a sub-level instead of a {@code CarriageContraptionEntity}: the same looping
 * sounds (minecart-esque rumble {@code TRAIN} + wheel clatter {@code TRAIN2}), the same volume shaping
 * (speed × listener distance × approach), the same speed→pitch curve, Create's steam-chuff cadence
 * (every 4 ticks + a deep one every 16) and its arrival hiss when the train comes to a stop.
 *
 * <p>The one adaptation: the speed fed into Create's formulas is the SERVER-authoritative train speed
 * (synced on the train marker), scaled by {@link #SPEED_SCALE} so our gear range (low RPM = well under
 * Create's typical blocks-per-tick) lands in the band Create's curves were tuned for.
 *
 * <p>Derailments cut the rolling audio instantly, play a crash, and grind out sliding "sparks" until the
 * free body stops. Ticked from {@code LoconauticsClient.onClientTick}.
 */
@OnlyIn(Dist.CLIENT)
public final class SableTrainSounds {

    private static final Map<UUID, SableTrainSounds> ACTIVE = new HashMap<>();

    /** Advances every train's sound emitter; called once per client tick. */
    public static void tickAll() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            stopAll();
            return;
        }
        if (mc.isPaused()) {
            return;
        }
        Set<UUID> ids = SableTrainClientRegistry.ids();
        ACTIVE.entrySet().removeIf(e -> {
            if (!ids.contains(e.getKey())) {
                e.getValue().stop();
                return true;
            }
            return false;
        });
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
                SableTrainSounds sounds = ACTIVE.remove(id);
                if (sounds != null) {
                    sounds.stop();
                }
                continue;
            }
            ACTIVE.computeIfAbsent(id, k -> new SableTrainSounds(id)).tick(mc, clientSub);
        }
    }

    /** Silences and forgets every train (disconnect / level unload). */
    public static void stopAll() {
        ACTIVE.values().forEach(SableTrainSounds::stop);
        ACTIVE.clear();
    }

    // ---- per-train state (mirrors Create's CarriageSounds fields) ----

    /**
     * Maps our speed (blocks/tick) onto the band Create's audio curves were tuned for. Our trains spend
     * most of the RPM range below 0.3 blocks/tick, where Create's curves barely move — scaling by ~3 puts
     * a mid-gear train where a cruising Create train sits.
     */
    private static final double SPEED_SCALE = 3.0;

    private final LerpedFloat speedFactor = LerpedFloat.linear();
    private final LerpedFloat distanceFactor = LerpedFloat.linear();
    private final LerpedFloat approachFactor = LerpedFloat.linear();
    /** Create detunes each carriage slightly: (entityId*10 % 13)/36 — ours hashes the sub-level id. */
    private final float pitchModifier;
    private LoopingSound minecartEsqueSound;
    private LoopingSound wheelSound;
    private Vector3d lastPos;
    private int tick;
    private boolean arrived = true;
    private float accumulatedSteamRelease;
    private boolean wasDerailed;

    private SableTrainSounds(UUID id) {
        this.pitchModifier = (float) (Math.abs(id.hashCode()) * 10 % 13) / 36.0f;
    }

    private void tick(Minecraft mc, ClientSubLevel sub) {
        Entity camEntity = mc.cameraEntity;
        if (camEntity == null) {
            return;
        }
        Vector3dc p = sub.renderPose().position();
        Vector3d pos = new Vector3d(p);
        Vector3d motion = lastPos == null ? new Vector3d() : pos.sub(lastPos, new Vector3d());
        lastPos = pos;
        tick++;

        // AUTHORITATIVE speed (blocks/tick) from the server marker — the same value the drive controls
        // on — scaled into Create's band. Render-pose motion is only used for location/doppler/sliding.
        SableTrainClientRegistry.TrainMarker marker = SableTrainClientRegistry.get(sub.getUniqueId());
        double rawSpeed = marker != null ? Math.abs(marker.speed()) : motion.length();
        float effSpeed = (float) Math.min(rawSpeed * SPEED_SCALE, 2.0);

        // DERAILED: the rolling sounds die instantly; a crash plays on the moment of derailment and
        // grinding metal-on-ground "sparks" follow while the free body still slides.
        if (marker != null && marker.derailed()) {
            Vec3 crashLoc = new Vec3(pos.x, pos.y, pos.z);
            if (!wasDerailed) {
                wasDerailed = true;
                stopLoops();
                mc.level.playLocalSound(crashLoc.x, crashLoc.y, crashLoc.z,
                        SoundEvents.ANVIL_LAND, SoundSource.NEUTRAL, 1.0f, 0.55f, false);
                mc.level.playLocalSound(crashLoc.x, crashLoc.y, crashLoc.z,
                        SoundEvents.NETHERITE_BLOCK_BREAK, SoundSource.NEUTRAL, 1.0f, 0.7f, false);
                AllSoundEvents.STEAM.playAt(mc.level, crashLoc, 1.0f, 0.6f, false);
            }
            double sliding = motion.length();
            if (sliding > 0.02) {
                float intensity = (float) Math.min(1.0, sliding * 3.0);
                if (tick % 3 == 0) { // grinding screech (the sparks bed)
                    mc.level.playLocalSound(crashLoc.x, crashLoc.y, crashLoc.z,
                            SoundEvents.GRINDSTONE_USE, SoundSource.NEUTRAL,
                            intensity, 0.6f + intensity * 0.5f, false);
                }
            }
            return; // no rolling audio while off the rails
        }
        if (wasDerailed) {
            wasDerailed = false; // re-railed (wrench relocation) — the loops below restart automatically
        }

        // ---- from here on: Create's CarriageSounds.tick, line for line, with effSpeed as the motion ----

        if (arrived && effSpeed > 0.01f) {
            arrived = false;
        }
        accumulatedSteamRelease = (float) Math.min(accumulatedSteamRelease + Math.min(0.5, effSpeed / 10.0), 10.0);

        Vec3 cam = camEntity.getEyePosition();
        Vec3 soundLocation = new Vec3(pos.x, pos.y, pos.z);
        Vec3 toTrain = soundLocation.subtract(cam);
        double distance = toTrain.length();
        Vec3 camMotion = camEntity.getDeltaMovement();
        Vec3 combinedMotion = new Vec3(motion.x - camMotion.x, motion.y - camMotion.y, motion.z - camMotion.z);
        double dot = distance < 1.0e-3 || combinedMotion.lengthSqr() < 1.0e-9
                ? 0.0 : toTrain.normalize().dot(combinedMotion.normalize());

        speedFactor.chase(effSpeed, 0.25, LerpedFloat.Chaser.exp(0.05f));
        distanceFactor.chase(Mth.clampedLerp(100.0, 0.0, (distance - 3.0) / 64.0), 0.25, LerpedFloat.Chaser.exp(50.0));
        approachFactor.chase(Mth.clampedLerp(50.0, 200.0, 0.5 * (dot + 1.0)), 0.25, LerpedFloat.Chaser.exp(10.0));
        speedFactor.tickChaser();
        distanceFactor.tickChaser();
        approachFactor.tickChaser();

        minecartEsqueSound = playIfMissing(mc, minecartEsqueSound, AllSoundEvents.TRAIN.getMainEvent());
        wheelSound = playIfMissing(mc, wheelSound, AllSoundEvents.TRAIN2.getMainEvent());

        float volume = Math.min(Math.min(speedFactor.getValue(), distanceFactor.getValue() / 100.0f),
                approachFactor.getValue() / 300.0f + 0.0125f);

        // Steam chuffs, Create's lead-carriage cadence (seat crossfade = 0: we never sit INSIDE a carriage).
        float v = volume * 0.75f;
        if ((3 + tick) % 4 == 0) {
            AllSoundEvents.STEAM.playAt(mc.level, soundLocation, v * ((tick + 7) % 8 == 0 ? 0.75f : 0.45f), 1.17f, false);
        }
        if (tick % 16 == 0) {
            AllSoundEvents.STEAM.playAt(mc.level, soundLocation, v * 1.5f, 0.8f, false);
        }

        // Arrival hiss: pressure built while moving releases as a long "pssshh" when the train stops.
        if (!arrived && speedFactor.getValue() < 0.002f && accumulatedSteamRelease > 1.0f) {
            arrived = true;
            float releaseVolume = accumulatedSteamRelease / 10.0f;
            mc.level.playLocalSound(soundLocation.x, soundLocation.y, soundLocation.z,
                    SoundEvents.LAVA_EXTINGUISH, SoundSource.NEUTRAL, 0.25f * releaseVolume, 0.78f, false);
            mc.level.playLocalSound(soundLocation.x, soundLocation.y, soundLocation.z,
                    SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.NEUTRAL, 0.2f * releaseVolume, 1.5f, false);
            AllSoundEvents.STEAM.playAt(mc.level, soundLocation, 0.75f * releaseVolume, 0.5f, false);
        }
        if (arrived) {
            accumulatedSteamRelease /= 2.0f;
        }

        volume = Math.min(volume, distanceFactor.getValue() / 800.0f);
        float pitch = Mth.clamp(speedFactor.getValue() * 2.0f + 0.25f, 0.75f, 1.95f) - pitchModifier;
        minecartEsqueSound.setPitch(pitch * 1.5f);
        volume = Math.min(volume, distanceFactor.getValue() / 1000.0f);

        minecartEsqueSound.setLocation(soundLocation);
        wheelSound.setLocation(soundLocation);

        // Create's finalizeSharedVolume with seat crossfade 0.
        minecartEsqueSound.setVolume(volume / 2.0f);
        float wheelVolume = Math.min(volume, Math.max((speedFactor.getValue() - 0.25f) / 4.0f + 0.01f, 0.0f));
        wheelSound.setVolume(wheelVolume * 1.5f);
    }

    private LoopingSound playIfMissing(Minecraft mc, LoopingSound sound, SoundEvent event) {
        if (sound == null) {
            sound = new LoopingSound(event);
            mc.getSoundManager().play(sound);
        }
        return sound;
    }

    /** Silences the rolling loops only (derailment) — position tracking keeps running for the slide sounds. */
    private void stopLoops() {
        if (minecartEsqueSound != null) {
            minecartEsqueSound.stopSound();
            minecartEsqueSound = null;
        }
        if (wheelSound != null) {
            wheelSound.stopSound();
            wheelSound = null;
        }
    }

    private void stop() {
        stopLoops();
        lastPos = null;
    }

    /** Minimal stand-in for Create's package-private {@code CarriageSounds.LoopingSound}. */
    private static final class LoopingSound extends AbstractTickableSoundInstance {

        private LoopingSound(SoundEvent event) {
            super(event, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
            this.attenuation = SoundInstance.Attenuation.LINEAR;
            this.looping = true;
            this.delay = 0;
            this.volume = 1.0e-4f;
        }

        @Override
        public void tick() {
        }

        private void setVolume(float volume) {
            this.volume = volume;
        }

        private void setPitch(float pitch) {
            this.pitch = pitch;
        }

        private void setLocation(Vec3 location) {
            this.x = location.x;
            this.y = location.y;
            this.z = location.z;
        }

        private void stopSound() {
            Minecraft.getInstance().getSoundManager().stop(this);
        }
    }
}
