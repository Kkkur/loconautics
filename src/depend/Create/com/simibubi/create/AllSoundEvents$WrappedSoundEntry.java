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
import java.util.ArrayList;
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

private static class AllSoundEvents.WrappedSoundEntry
extends AllSoundEvents.SoundEntry {
    private List<AllSoundEvents.ConfiguredSoundEvent> wrappedEvents;
    private List<CompiledSoundEvent> compiledEvents;

    public AllSoundEvents.WrappedSoundEntry(ResourceLocation id, String subtitle, List<AllSoundEvents.ConfiguredSoundEvent> wrappedEvents, SoundSource category, int attenuationDistance) {
        super(id, subtitle, category, attenuationDistance);
        this.wrappedEvents = wrappedEvents;
        this.compiledEvents = new ArrayList<CompiledSoundEvent>();
    }

    @Override
    public void prepare() {
        for (int i = 0; i < this.wrappedEvents.size(); ++i) {
            AllSoundEvents.ConfiguredSoundEvent wrapped = this.wrappedEvents.get(i);
            ResourceLocation location = this.getIdOf(i);
            DeferredHolder event = DeferredHolder.create((ResourceKey)Registries.SOUND_EVENT, (ResourceLocation)location);
            this.compiledEvents.add(new CompiledSoundEvent((DeferredHolder<SoundEvent, SoundEvent>)event, wrapped.volume(), wrapped.pitch()));
        }
    }

    @Override
    public void register(RegisterEvent.RegisterHelper<SoundEvent> helper) {
        for (CompiledSoundEvent compiledEvent : this.compiledEvents) {
            ResourceLocation location = compiledEvent.event().getId();
            helper.register(location, (Object)SoundEvent.createVariableRangeEvent((ResourceLocation)location));
        }
    }

    @Override
    public Holder<SoundEvent> getMainEventHolder() {
        return this.compiledEvents.getFirst().event();
    }

    @Override
    public SoundEvent getMainEvent() {
        return (SoundEvent)this.compiledEvents.getFirst().event().get();
    }

    protected ResourceLocation getIdOf(int i) {
        return ResourceLocation.fromNamespaceAndPath((String)this.id.getNamespace(), (String)(i == 0 ? this.id.getPath() : this.id.getPath() + "_compounded_" + i));
    }

    @Override
    public void write(JsonObject json) {
        for (int i = 0; i < this.wrappedEvents.size(); ++i) {
            AllSoundEvents.ConfiguredSoundEvent event = this.wrappedEvents.get(i);
            JsonObject entry = new JsonObject();
            JsonArray list = new JsonArray();
            JsonObject s = new JsonObject();
            s.addProperty("name", event.event().get().getLocation().toString());
            s.addProperty("type", "event");
            if (this.attenuationDistance != 0) {
                s.addProperty("attenuation_distance", (Number)this.attenuationDistance);
            }
            list.add((JsonElement)s);
            entry.add("sounds", (JsonElement)list);
            if (i == 0 && this.hasSubtitle()) {
                entry.addProperty("subtitle", this.getSubtitleKey());
            }
            json.add(this.getIdOf(i).getPath(), (JsonElement)entry);
        }
    }

    @Override
    public void play(Level world, Player entity, double x, double y, double z, float volume, float pitch) {
        for (CompiledSoundEvent event : this.compiledEvents) {
            world.playSound(entity, x, y, z, (SoundEvent)event.event().get(), this.category, event.volume() * volume, event.pitch() * pitch);
        }
    }

    @Override
    public void playAt(Level world, double x, double y, double z, float volume, float pitch, boolean fade) {
        for (CompiledSoundEvent event : this.compiledEvents) {
            world.playLocalSound(x, y, z, (SoundEvent)event.event().get(), this.category, event.volume() * volume, event.pitch() * pitch, fade);
        }
    }

    private record CompiledSoundEvent(DeferredHolder<SoundEvent, SoundEvent> event, float volume, float pitch) {
    }
}
