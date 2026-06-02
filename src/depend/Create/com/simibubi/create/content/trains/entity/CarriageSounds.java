/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.SubtitleOverlay
 *  net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance$Attenuation
 *  net.minecraft.client.sounds.WeighedSoundEvents
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.mixin.accessor.GuiAccessor;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Couple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CarriageSounds {
    CarriageContraptionEntity entity;
    LerpedFloat distanceFactor;
    LerpedFloat speedFactor;
    LerpedFloat approachFactor;
    LerpedFloat seatCrossfade;
    LoopingSound minecartEsqueSound;
    LoopingSound sharedWheelSound;
    LoopingSound sharedWheelSoundSeated;
    LoopingSound sharedHonkSound;
    Couple<SoundEvent> bogeySounds;
    SoundEvent closestBogeySound;
    boolean arrived;
    int tick;
    int prevSharedTick;

    public CarriageSounds(CarriageContraptionEntity entity) {
        this.entity = entity;
        this.bogeySounds = entity.getCarriage().bogeys.map(bogey -> bogey != null && bogey.getStyle() != null ? bogey.getStyle().soundEvent.get() : AllSoundEvents.TRAIN2.getMainEvent());
        this.closestBogeySound = (SoundEvent)this.bogeySounds.getFirst();
        this.distanceFactor = LerpedFloat.linear();
        this.speedFactor = LerpedFloat.linear();
        this.approachFactor = LerpedFloat.linear();
        this.seatCrossfade = LerpedFloat.linear();
        this.arrived = true;
    }

    public void tick(Carriage.DimensionalCarriageEntity dce) {
        AllSoundEvents.SoundEntry continuousSound;
        double distance2;
        Minecraft mc = Minecraft.getInstance();
        Entity camEntity = mc.cameraEntity;
        if (camEntity == null) {
            return;
        }
        Vec3 leadingAnchor = dce.leadingAnchor();
        Vec3 trailingAnchor = dce.trailingAnchor();
        if (leadingAnchor == null || trailingAnchor == null) {
            return;
        }
        ++this.tick;
        Vec3 cam = camEntity.getEyePosition();
        Vec3 contraptionMotion = this.entity.position().subtract(this.entity.getPrevPositionVec());
        Vec3 combinedMotion = contraptionMotion.subtract(camEntity.getDeltaMovement());
        Train train = this.entity.getCarriage().train;
        if (this.arrived && contraptionMotion.length() > (double)0.01f) {
            this.arrived = false;
        }
        if (this.arrived && this.entity.carriageIndex == 0) {
            train.accumulatedSteamRelease /= 2.0f;
        }
        this.arrived |= this.entity.isStalled();
        if (this.entity.carriageIndex == 0) {
            train.accumulatedSteamRelease = (float)Math.min((double)train.accumulatedSteamRelease + Math.min(0.5, Math.abs(contraptionMotion.length() / 10.0)), 10.0);
        }
        Vec3 toBogey1 = leadingAnchor.subtract(cam);
        Vec3 toBogey2 = trailingAnchor.subtract(cam);
        Couple<CarriageBogey> bogeys = this.entity.getCarriage().bogeys;
        double distance1 = toBogey1.length();
        CarriageBogey relevantBogey = (CarriageBogey)bogeys.get(distance1 > (distance2 = toBogey2.length()));
        if (relevantBogey == null) {
            relevantBogey = (CarriageBogey)bogeys.getFirst();
        }
        if (relevantBogey != null) {
            this.closestBogeySound = relevantBogey.getStyle().soundEvent.get();
        }
        Vec3 toCarriage = distance1 > distance2 ? toBogey2 : toBogey1;
        double distance = Math.min(distance1, distance2);
        Vec3 soundLocation = cam.add(toCarriage);
        double dot = toCarriage.normalize().dot(combinedMotion.normalize());
        this.speedFactor.chase(contraptionMotion.length(), 0.25, LerpedFloat.Chaser.exp((double)0.05f));
        this.distanceFactor.chase(Mth.clampedLerp((double)100.0, (double)0.0, (double)((distance - 3.0) / 64.0)), 0.25, LerpedFloat.Chaser.exp((double)50.0));
        this.approachFactor.chase(Mth.clampedLerp((double)50.0, (double)200.0, (double)(0.5 * (dot + 1.0))), 0.25, LerpedFloat.Chaser.exp((double)10.0));
        this.seatCrossfade.chase(camEntity.getVehicle() instanceof CarriageContraptionEntity ? 1.0 : 0.0, (double)0.1f, LerpedFloat.Chaser.EXP);
        this.speedFactor.tickChaser();
        this.distanceFactor.tickChaser();
        this.approachFactor.tickChaser();
        this.seatCrossfade.tickChaser();
        this.minecartEsqueSound = this.playIfMissing(mc, this.minecartEsqueSound, AllSoundEvents.TRAIN.getMainEvent());
        this.sharedWheelSound = this.playIfMissing(mc, this.sharedWheelSound, this.closestBogeySound);
        this.sharedWheelSoundSeated = this.playIfMissing(mc, this.sharedWheelSoundSeated, AllSoundEvents.TRAIN3.getMainEvent());
        float volume = Math.min(Math.min(this.speedFactor.getValue(), this.distanceFactor.getValue() / 100.0f), this.approachFactor.getValue() / 300.0f + 0.0125f);
        if (this.entity.carriageIndex == 0) {
            float v = volume * (1.0f - this.seatCrossfade.getValue() * 0.35f) * 0.75f;
            if ((3 + this.tick) % 4 == 0) {
                AllSoundEvents.STEAM.playAt(this.entity.level(), soundLocation, v * ((this.tick + 7) % 8 == 0 ? 0.75f : 0.45f), 1.17f, false);
            }
            if (this.tick % 16 == 0) {
                AllSoundEvents.STEAM.playAt(this.entity.level(), soundLocation, v * 1.5f, 0.8f, false);
            }
        }
        if (!this.arrived && this.speedFactor.getValue() < 0.002f && train.accumulatedSteamRelease > 1.0f) {
            this.arrived = true;
            float releaseVolume = train.accumulatedSteamRelease / 10.0f;
            this.entity.level().playLocalSound(soundLocation.x, soundLocation.y, soundLocation.z, SoundEvents.LAVA_EXTINGUISH, SoundSource.NEUTRAL, 0.25f * releaseVolume, 0.78f, false);
            this.entity.level().playLocalSound(soundLocation.x, soundLocation.y, soundLocation.z, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.NEUTRAL, 0.2f * releaseVolume, 1.5f, false);
            AllSoundEvents.STEAM.playAt(this.entity.level(), soundLocation, 0.75f * releaseVolume, 0.5f, false);
        }
        float pitchModifier = (float)(this.entity.getId() * 10 % 13) / 36.0f;
        volume = Math.min(volume, this.distanceFactor.getValue() / 800.0f);
        float pitch = Mth.clamp((float)(this.speedFactor.getValue() * 2.0f + 0.25f), (float)0.75f, (float)1.95f) - pitchModifier;
        this.minecartEsqueSound.setPitch(pitch * 1.5f);
        volume = Math.min(volume, this.distanceFactor.getValue() / 1000.0f);
        for (Carriage carriage : train.carriages) {
            CarriageContraptionEntity mainEntity;
            Carriage.DimensionalCarriageEntity mainDCE = carriage.getDimensionalIfPresent((ResourceKey<Level>)this.entity.level().dimension());
            if (mainDCE == null || (mainEntity = (CarriageContraptionEntity)((Object)mainDCE.entity.get())) == null) continue;
            if (mainEntity.sounds == null) {
                mainEntity.sounds = new CarriageSounds(mainEntity);
            }
            mainEntity.sounds.submitSharedSoundVolume(soundLocation, volume);
            if (carriage == this.entity.getCarriage()) break;
            this.finalizeSharedVolume(0.0f);
            return;
        }
        if (train.honkTicks == 0) {
            if (this.sharedHonkSound != null) {
                this.sharedHonkSound.stopSound();
                this.sharedHonkSound = null;
            }
            return;
        }
        --train.honkTicks;
        train.determineHonk(this.entity.level());
        if (train.lowHonk == null) {
            return;
        }
        boolean low = train.lowHonk;
        float honkPitch = (float)Math.pow(2.0, (double)train.honkPitch / 12.0);
        AllSoundEvents.SoundEntry endSound = !low ? AllSoundEvents.WHISTLE_TRAIN_MANUAL_END : AllSoundEvents.WHISTLE_TRAIN_MANUAL_LOW_END;
        AllSoundEvents.SoundEntry soundEntry = continuousSound = !low ? AllSoundEvents.WHISTLE_TRAIN_MANUAL : AllSoundEvents.WHISTLE_TRAIN_MANUAL_LOW;
        if (train.honkTicks == 5) {
            endSound.playAt((Level)mc.level, soundLocation, 1.0f, honkPitch, false);
        }
        if (train.honkTicks == 19) {
            endSound.playAt((Level)mc.level, soundLocation, 0.5f, honkPitch, false);
        }
        this.sharedHonkSound = this.playIfMissing(mc, this.sharedHonkSound, continuousSound.getMainEvent(), true);
        this.sharedHonkSound.setLocation(soundLocation);
        float fadeout = Mth.clamp((float)((float)(3 - train.honkTicks) / 3.0f), (float)0.0f, (float)1.0f);
        float fadein = Mth.clamp((float)((float)(train.honkTicks - 17) / 3.0f), (float)0.0f, (float)1.0f);
        this.sharedHonkSound.setVolume(1.0f - fadeout - fadein);
        this.sharedHonkSound.setPitch(honkPitch);
    }

    private LoopingSound playIfMissing(Minecraft mc, LoopingSound loopingSound, SoundEvent sound) {
        return this.playIfMissing(mc, loopingSound, sound, false);
    }

    private LoopingSound playIfMissing(Minecraft mc, LoopingSound loopingSound, SoundEvent sound, boolean continuouslyShowSubtitle) {
        if (loopingSound == null) {
            loopingSound = new LoopingSound(sound, SoundSource.NEUTRAL, continuouslyShowSubtitle);
            mc.getSoundManager().play((SoundInstance)loopingSound);
        }
        return loopingSound;
    }

    public void submitSharedSoundVolume(Vec3 location, float volume) {
        Minecraft mc = Minecraft.getInstance();
        this.minecartEsqueSound = this.playIfMissing(mc, this.minecartEsqueSound, AllSoundEvents.TRAIN.getMainEvent());
        this.sharedWheelSound = this.playIfMissing(mc, this.sharedWheelSound, this.closestBogeySound);
        this.sharedWheelSoundSeated = this.playIfMissing(mc, this.sharedWheelSoundSeated, AllSoundEvents.TRAIN3.getMainEvent());
        boolean approach = true;
        if (this.tick != this.prevSharedTick) {
            this.prevSharedTick = this.tick;
            approach = false;
        } else if (this.sharedWheelSound.getVolume() > volume) {
            return;
        }
        Vec3 currentLoc = new Vec3(this.minecartEsqueSound.getX(), this.minecartEsqueSound.getY(), this.minecartEsqueSound.getZ());
        Vec3 newLoc = approach ? currentLoc.add(location.subtract(currentLoc).scale(0.125)) : location;
        this.minecartEsqueSound.setLocation(newLoc);
        this.sharedWheelSound.setLocation(newLoc);
        this.sharedWheelSoundSeated.setLocation(newLoc);
        this.finalizeSharedVolume(volume);
    }

    public void finalizeSharedVolume(float volume) {
        float crossfade = this.seatCrossfade.getValue();
        this.minecartEsqueSound.setVolume((1.0f - crossfade * 0.65f) * volume / 2.0f);
        volume = Math.min(volume, Math.max((this.speedFactor.getValue() - 0.25f) / 4.0f + 0.01f, 0.0f));
        this.sharedWheelSoundSeated.setVolume(volume * crossfade);
        this.sharedWheelSound.setVolume(volume * (1.0f - crossfade) * 1.5f);
    }

    public void stop() {
        if (this.minecartEsqueSound != null) {
            this.minecartEsqueSound.stopSound();
        }
        if (this.sharedWheelSound != null) {
            this.sharedWheelSound.stopSound();
        }
        if (this.sharedWheelSoundSeated != null) {
            this.sharedWheelSoundSeated.stopSound();
        }
        if (this.sharedHonkSound != null) {
            this.sharedHonkSound.stopSound();
        }
    }

    static class LoopingSound
    extends AbstractTickableSoundInstance {
        private static final SubtitleOverlay OVERLAY = ((GuiAccessor)Minecraft.getInstance().gui).create$getSubtitleOverlay();
        private final boolean repeatSubtitle;
        private final WeighedSoundEvents weighedSoundEvents = this.resolve(Minecraft.getInstance().getSoundManager());
        private byte subtitleTimer = 0;

        protected LoopingSound(SoundEvent soundEvent, SoundSource source) {
            this(soundEvent, source, false);
        }

        protected LoopingSound(SoundEvent soundEvent, SoundSource source, boolean repeatSubtitle) {
            super(soundEvent, source, SoundInstance.createUnseededRandom());
            this.attenuation = SoundInstance.Attenuation.LINEAR;
            this.looping = true;
            this.delay = 0;
            this.volume = 1.0E-4f;
            this.repeatSubtitle = repeatSubtitle;
        }

        public void tick() {
            if (this.repeatSubtitle) {
                this.subtitleTimer = (byte)(this.subtitleTimer + 1);
                if (this.subtitleTimer == 20) {
                    OVERLAY.onPlaySound((SoundInstance)this, this.weighedSoundEvents, (float)this.sound.getAttenuationDistance());
                    this.subtitleTimer = 0;
                }
            }
        }

        public void setVolume(float volume) {
            this.volume = volume;
        }

        public float getVolume() {
            return this.volume;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
        }

        public float getPitch() {
            return this.pitch;
        }

        public void setLocation(Vec3 location) {
            this.x = location.x;
            this.y = location.y;
            this.z = location.z;
        }

        public void stopSound() {
            Minecraft.getInstance().getSoundManager().stop((SoundInstance)this);
        }
    }
}
