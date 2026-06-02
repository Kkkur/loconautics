/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Vec3i
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.registries.RegisterEvent$RegisterHelper
 */
package com.simibubi.create;

import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.RegisterEvent;

public static abstract class AllSoundEvents.SoundEntry {
    protected ResourceLocation id;
    protected String subtitle;
    protected SoundSource category;
    protected int attenuationDistance;

    public AllSoundEvents.SoundEntry(ResourceLocation id, String subtitle, SoundSource category, int attenuationDistance) {
        this.id = id;
        this.subtitle = subtitle;
        this.category = category;
        this.attenuationDistance = attenuationDistance;
    }

    public abstract void prepare();

    public abstract void register(RegisterEvent.RegisterHelper<SoundEvent> var1);

    public abstract void write(JsonObject var1);

    public abstract Holder<SoundEvent> getMainEventHolder();

    public abstract SoundEvent getMainEvent();

    public String getSubtitleKey() {
        return this.id.getNamespace() + ".subtitle." + this.id.getPath();
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public boolean hasSubtitle() {
        return this.subtitle != null;
    }

    public String getSubtitle() {
        return this.subtitle;
    }

    public void playOnServer(Level world, Vec3i pos) {
        this.playOnServer(world, pos, 1.0f, 1.0f);
    }

    public void playOnServer(Level world, Vec3i pos, float volume, float pitch) {
        this.play(world, null, pos, volume, pitch);
    }

    public void play(Level world, Player entity, Vec3i pos) {
        this.play(world, entity, pos, 1.0f, 1.0f);
    }

    public void playFrom(Entity entity) {
        this.playFrom(entity, 1.0f, 1.0f);
    }

    public void playFrom(Entity entity, float volume, float pitch) {
        if (!entity.isSilent()) {
            this.play(entity.level(), null, (Vec3i)entity.blockPosition(), volume, pitch);
        }
    }

    public void play(Level world, Player entity, Vec3i pos, float volume, float pitch) {
        this.play(world, entity, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, volume, pitch);
    }

    public void play(Level world, Player entity, Vec3 pos, float volume, float pitch) {
        this.play(world, entity, pos.x(), pos.y(), pos.z(), volume, pitch);
    }

    public abstract void play(Level var1, Player var2, double var3, double var5, double var7, float var9, float var10);

    public void playAt(Level world, Vec3i pos, float volume, float pitch, boolean fade) {
        this.playAt(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, volume, pitch, fade);
    }

    public void playAt(Level world, Vec3 pos, float volume, float pitch, boolean fade) {
        this.playAt(world, pos.x(), pos.y(), pos.z(), volume, pitch, fade);
    }

    public abstract void playAt(Level var1, double var2, double var4, double var6, float var8, float var9, boolean var10);
}
