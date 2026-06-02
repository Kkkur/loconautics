/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.sounds.SoundManager
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.foundation.sound;

import com.simibubi.create.foundation.sound.ContinuousSound;
import com.simibubi.create.foundation.sound.RepeatingSound;
import com.simibubi.create.foundation.sound.SoundScapes;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

class SoundScape {
    List<ContinuousSound> continuous;
    List<RepeatingSound> repeating;
    private float pitch;
    private SoundScapes.AmbienceGroup group;
    private Vec3 meanPos;
    private SoundScapes.PitchGroup pitchGroup;

    public SoundScape(float pitch, SoundScapes.AmbienceGroup group) {
        this.pitchGroup = SoundScapes.getGroupFromPitch(pitch);
        this.pitch = pitch;
        this.group = group;
        this.continuous = new ArrayList<ContinuousSound>();
        this.repeating = new ArrayList<RepeatingSound>();
    }

    public SoundScape continuous(SoundEvent sound, float relativeVolume, float relativePitch) {
        return this.add(new ContinuousSound(sound, this, this.pitch * relativePitch, relativeVolume));
    }

    public SoundScape repeating(SoundEvent sound, float relativeVolume, float relativePitch, int delay) {
        return this.add(new RepeatingSound(sound, this, this.pitch * relativePitch, relativeVolume, delay));
    }

    public SoundScape add(ContinuousSound continuousSound) {
        this.continuous.add(continuousSound);
        return this;
    }

    public SoundScape add(RepeatingSound repeatingSound) {
        this.repeating.add(repeatingSound);
        return this;
    }

    public void play() {
        this.continuous.forEach(arg_0 -> ((SoundManager)Minecraft.getInstance().getSoundManager()).play(arg_0));
    }

    public void tick() {
        if (AnimationTickHolder.getTicks() % 5 == 0) {
            this.meanPos = null;
        }
        this.repeating.forEach(RepeatingSound::tick);
    }

    public void remove() {
        this.continuous.forEach(ContinuousSound::remove);
    }

    public Vec3 getMeanPos() {
        return this.meanPos == null ? (this.meanPos = this.determineMeanPos()) : this.meanPos;
    }

    private Vec3 determineMeanPos() {
        this.meanPos = Vec3.ZERO;
        int amount = 0;
        for (BlockPos blockPos : SoundScapes.getAllLocations(this.group, this.pitchGroup)) {
            this.meanPos = this.meanPos.add(VecHelper.getCenterOf((Vec3i)blockPos));
            ++amount;
        }
        if (amount == 0) {
            return this.meanPos;
        }
        return this.meanPos.scale((double)(1.0f / (float)amount));
    }

    public float getVolume() {
        Entity renderViewEntity = Minecraft.getInstance().cameraEntity;
        float distanceMultiplier = 0.0f;
        if (renderViewEntity != null) {
            double distanceTo = renderViewEntity.position().distanceTo(this.getMeanPos());
            distanceMultiplier = (float)Mth.lerp((double)(distanceTo / 16.0), (double)2.0, (double)0.0);
        }
        int soundCount = SoundScapes.getSoundCount(this.group, this.pitchGroup);
        float max = AllConfigs.client().ambientVolumeCap.getF();
        float argMax = 15.0f;
        return Mth.clamp((float)((float)soundCount / (argMax * 10.0f)), (float)0.025f, (float)max) * distanceMultiplier;
    }
}
