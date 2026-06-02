/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 */
package com.simibubi.create;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public static class AllSoundEvents.SoundEntryBuilder {
    protected ResourceLocation id;
    protected String subtitle = "unregistered";
    protected SoundSource category = SoundSource.BLOCKS;
    protected List<AllSoundEvents.ConfiguredSoundEvent> wrappedEvents = new ArrayList<AllSoundEvents.ConfiguredSoundEvent>();
    protected List<ResourceLocation> variants = new ArrayList<ResourceLocation>();
    protected int attenuationDistance;

    public AllSoundEvents.SoundEntryBuilder(ResourceLocation id) {
        this.id = id;
    }

    public AllSoundEvents.SoundEntryBuilder subtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public AllSoundEvents.SoundEntryBuilder attenuationDistance(int distance) {
        this.attenuationDistance = distance;
        return this;
    }

    public AllSoundEvents.SoundEntryBuilder noSubtitle() {
        this.subtitle = null;
        return this;
    }

    public AllSoundEvents.SoundEntryBuilder category(SoundSource category) {
        this.category = category;
        return this;
    }

    public AllSoundEvents.SoundEntryBuilder addVariant(String name) {
        return this.addVariant(Create.asResource(name));
    }

    public AllSoundEvents.SoundEntryBuilder addVariant(ResourceLocation id) {
        this.variants.add(id);
        return this;
    }

    public AllSoundEvents.SoundEntryBuilder playExisting(Supplier<SoundEvent> event, float volume, float pitch) {
        this.wrappedEvents.add(new AllSoundEvents.ConfiguredSoundEvent(event, volume, pitch));
        return this;
    }

    public AllSoundEvents.SoundEntryBuilder playExisting(SoundEvent event, float volume, float pitch) {
        return this.playExisting(() -> event, volume, pitch);
    }

    public AllSoundEvents.SoundEntryBuilder playExisting(SoundEvent event) {
        return this.playExisting(event, 1.0f, 1.0f);
    }

    public AllSoundEvents.SoundEntryBuilder playExisting(Holder<SoundEvent> event) {
        return this.playExisting(() -> event.value(), 1.0f, 1.0f);
    }

    public AllSoundEvents.SoundEntry build() {
        AllSoundEvents.SoundEntry entry = this.wrappedEvents.isEmpty() ? new AllSoundEvents.CustomSoundEntry(this.id, this.variants, this.subtitle, this.category, this.attenuationDistance) : new AllSoundEvents.WrappedSoundEntry(this.id, this.subtitle, this.wrappedEvents, this.category, this.attenuationDistance);
        ALL.put(entry.getId(), entry);
        return entry;
    }
}
