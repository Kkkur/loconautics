/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.RegisterEvent$RegisterHelper
 */
package com.simibubi.create;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.AllSoundEvents;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegisterEvent;

private static class AllSoundEvents.CustomSoundEntry
extends AllSoundEvents.SoundEntry {
    protected List<ResourceLocation> variants;
    protected DeferredHolder<SoundEvent, SoundEvent> event;

    public AllSoundEvents.CustomSoundEntry(ResourceLocation id, List<ResourceLocation> variants, String subtitle, SoundSource category, int attenuationDistance) {
        super(id, subtitle, category, attenuationDistance);
        this.variants = variants;
    }

    @Override
    public void prepare() {
        this.event = DeferredHolder.create((ResourceKey)Registries.SOUND_EVENT, (ResourceLocation)this.id);
    }

    @Override
    public void register(RegisterEvent.RegisterHelper<SoundEvent> helper) {
        ResourceLocation location = this.event.getId();
        helper.register(location, (Object)SoundEvent.createVariableRangeEvent((ResourceLocation)location));
    }

    @Override
    public Holder<SoundEvent> getMainEventHolder() {
        return this.event;
    }

    @Override
    public SoundEvent getMainEvent() {
        return (SoundEvent)this.event.get();
    }

    @Override
    public void write(JsonObject json) {
        JsonObject entry = new JsonObject();
        JsonArray list = new JsonArray();
        JsonObject s = new JsonObject();
        s.addProperty("name", this.id.toString());
        s.addProperty("type", "file");
        if (this.attenuationDistance != 0) {
            s.addProperty("attenuation_distance", (Number)this.attenuationDistance);
        }
        list.add((JsonElement)s);
        for (ResourceLocation variant : this.variants) {
            s = new JsonObject();
            s.addProperty("name", variant.toString());
            s.addProperty("type", "file");
            if (this.attenuationDistance != 0) {
                s.addProperty("attenuation_distance", (Number)this.attenuationDistance);
            }
            list.add((JsonElement)s);
        }
        entry.add("sounds", (JsonElement)list);
        if (this.hasSubtitle()) {
            entry.addProperty("subtitle", this.getSubtitleKey());
        }
        json.add(this.id.getPath(), (JsonElement)entry);
    }

    @Override
    public void play(Level world, Player entity, double x, double y, double z, float volume, float pitch) {
        world.playSound(entity, x, y, z, (SoundEvent)this.event.get(), this.category, volume, pitch);
    }

    @Override
    public void playAt(Level world, double x, double y, double z, float volume, float pitch, boolean fade) {
        world.playLocalSound(x, y, z, (SoundEvent)this.event.get(), this.category, volume, pitch, fade);
    }
}
