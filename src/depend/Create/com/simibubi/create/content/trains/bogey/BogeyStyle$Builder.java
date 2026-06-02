/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 */
package com.simibubi.create.content.trains.bogey;

import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public static class BogeyStyle.Builder {
    protected final ResourceLocation id;
    protected final ResourceLocation cycleGroup;
    protected final Map<BogeySizes.BogeySize, Supplier<? extends AbstractBogeyBlock<?>>> sizes = new HashMap();
    protected Component displayName = CreateLang.translateDirect("bogey.style.invalid", new Object[0]);
    protected Supplier<SoundEvent> soundEvent = AllSoundEvents.TRAIN2::getMainEvent;
    protected ParticleOptions contactParticle = ParticleTypes.CRIT;
    protected ParticleOptions smokeParticle = ParticleTypes.POOF;
    protected CompoundTag defaultData = new CompoundTag();
    protected final Map<BogeySizes.BogeySize, Supplier<Supplier<? extends BogeyStyle.SizeRenderer>>> sizeRenderers = new HashMap<BogeySizes.BogeySize, Supplier<Supplier<? extends BogeyStyle.SizeRenderer>>>();

    public BogeyStyle.Builder(ResourceLocation id, ResourceLocation cycleGroup) {
        this.id = id;
        this.cycleGroup = cycleGroup;
    }

    public BogeyStyle.Builder displayName(Component displayName) {
        this.displayName = displayName;
        return this;
    }

    public BogeyStyle.Builder soundEvent(Supplier<SoundEvent> soundEvent) {
        this.soundEvent = soundEvent;
        return this;
    }

    public BogeyStyle.Builder contactParticle(ParticleOptions contactParticle) {
        this.contactParticle = contactParticle;
        return this;
    }

    public BogeyStyle.Builder smokeParticle(ParticleOptions smokeParticle) {
        this.smokeParticle = smokeParticle;
        return this;
    }

    public BogeyStyle.Builder defaultData(CompoundTag defaultData) {
        this.defaultData = defaultData;
        return this;
    }

    public BogeyStyle.Builder size(BogeySizes.BogeySize size, Supplier<? extends AbstractBogeyBlock<?>> block, Supplier<Supplier<? extends BogeyStyle.SizeRenderer>> renderer) {
        this.sizes.put(size, block);
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.lambda$size$0(size, (Supplier)renderer));
        return this;
    }

    public BogeyStyle build() {
        BogeyStyle entry = new BogeyStyle(this.id, this.cycleGroup, this.displayName, this.soundEvent, this.contactParticle, this.smokeParticle, this.defaultData, this.sizes, this.sizeRenderers);
        AllBogeyStyles.BOGEY_STYLES.put(this.id, entry);
        AllBogeyStyles.CYCLE_GROUPS.computeIfAbsent(this.cycleGroup, l -> new HashMap()).put(this.id, entry);
        return entry;
    }

    private /* synthetic */ void lambda$size$0(BogeySizes.BogeySize size, Supplier renderer) {
        this.sizeRenderers.put(size, renderer);
    }
}
