/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.audio.Channel
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.Sound
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance$Attenuation
 *  net.minecraft.client.resources.sounds.TickableSoundInstance
 *  net.minecraft.client.sounds.SoundManager
 *  net.minecraft.client.sounds.WeighedSoundEvents
 *  net.minecraft.core.Position
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Vector3d
 *  org.lwjgl.openal.AL11
 */
package dev.ryanhcode.sable.sound;

import com.mojang.blaze3d.audio.Channel;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.mixin.sublevel_sounds.ChannelAccessor;
import dev.ryanhcode.sable.sound.SoundInstanceDelegated;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.lwjgl.openal.AL11;

public class MovingSoundInstanceDelegate
implements SoundInstance,
TickableSoundInstance {
    private SubLevel subLevel;
    private double latestX;
    private double latestY;
    private double latestZ;
    public SoundInstance instance;

    public MovingSoundInstanceDelegate(SoundInstance instance, SubLevel subLevel) {
        this.instance = instance;
        this.subLevel = subLevel;
        if (this.instance instanceof SoundInstanceDelegated) {
            ((SoundInstanceDelegated)this.instance).setDelegate(this);
        }
    }

    public void tickWithChannel(Channel channel) {
        int source = ((ChannelAccessor)channel).getSource();
        if (this.subLevel != null && this.subLevel.isRemoved()) {
            this.subLevel = null;
        }
        if (this.subLevel == null) {
            AL11.alSource3f((int)source, (int)4102, (float)0.0f, (float)0.0f, (float)0.0f);
            return;
        }
        Vector3d instancePos = new Vector3d(this.instance.getX(), this.instance.getY(), this.instance.getZ());
        Vector3d motion = Sable.HELPER.getVelocity((Level)Minecraft.getInstance().level, instancePos);
        Entity player = Minecraft.getInstance().getCameraEntity();
        if (player == null) {
            AL11.alSource3f((int)source, (int)4102, (float)0.0f, (float)0.0f, (float)0.0f);
            return;
        }
        Vector3d playerPosition = JOMLConversion.toJOML((Position)player.position());
        Vector3d playerMotion = playerPosition.sub(player.xo, player.yo, player.zo).mul(20.0);
        AL11.alSpeedOfSound((float)1800.0f);
        AL11.alDopplerFactor((float)0.4f);
        AL11.alSource3f((int)source, (int)4102, (float)((float)(motion.x - playerMotion.x)), (float)((float)(motion.y - playerMotion.y)), (float)((float)(motion.z - playerMotion.z)));
    }

    public void unload(Channel channel) {
        AL11.alSource3f((int)((ChannelAccessor)channel).getSource(), (int)4102, (float)0.0f, (float)0.0f, (float)0.0f);
    }

    @NotNull
    public ResourceLocation getLocation() {
        return this.instance.getLocation();
    }

    public WeighedSoundEvents resolve(SoundManager pManager) {
        return this.instance.resolve(pManager);
    }

    @NotNull
    public Sound getSound() {
        return this.instance.getSound();
    }

    @NotNull
    public SoundSource getSource() {
        return this.instance.getSource();
    }

    public boolean isLooping() {
        return this.instance.isLooping();
    }

    public boolean isRelative() {
        return this.instance.isRelative();
    }

    public int getDelay() {
        return this.instance.getDelay();
    }

    public float getVolume() {
        return this.instance.getVolume();
    }

    public float getPitch() {
        return this.instance.getPitch();
    }

    public double getX() {
        if (this.subLevel == null) {
            return this.latestX;
        }
        this.latestX = this.subLevel.logicalPose().transformPosition((Vec3)new Vec3((double)this.instance.getX(), (double)this.instance.getY(), (double)this.instance.getZ())).x;
        return this.latestX;
    }

    public double getY() {
        if (this.subLevel == null) {
            return this.latestY;
        }
        this.latestY = this.subLevel.logicalPose().transformPosition((Vec3)new Vec3((double)this.instance.getX(), (double)this.instance.getY(), (double)this.instance.getZ())).y;
        return this.latestY;
    }

    public double getZ() {
        if (this.subLevel == null) {
            return this.latestZ;
        }
        this.latestZ = this.subLevel.logicalPose().transformPosition((Vec3)new Vec3((double)this.instance.getX(), (double)this.instance.getY(), (double)this.instance.getZ())).z;
        return this.latestZ;
    }

    public boolean canStartSilent() {
        return this.instance.canStartSilent();
    }

    public boolean canPlaySound() {
        return this.instance.canPlaySound();
    }

    public SoundInstance.Attenuation getAttenuation() {
        return this.instance.getAttenuation();
    }

    public boolean isStopped() {
        SoundInstance soundInstance = this.instance;
        if (soundInstance instanceof TickableSoundInstance) {
            TickableSoundInstance tickable = (TickableSoundInstance)soundInstance;
            return tickable.isStopped();
        }
        return !this.instance.canPlaySound();
    }

    public void tick() {
        SoundInstance soundInstance = this.instance;
        if (soundInstance instanceof TickableSoundInstance) {
            TickableSoundInstance tickable = (TickableSoundInstance)soundInstance;
            tickable.tick();
        }
    }
}
