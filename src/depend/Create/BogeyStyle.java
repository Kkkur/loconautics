/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.bogey;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyVisual;
import com.simibubi.create.content.trains.bogey.BogeyVisualizer;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class BogeyStyle {
    public final ResourceLocation id;
    public final ResourceLocation cycleGroup;
    public final Component displayName;
    public final Supplier<SoundEvent> soundEvent;
    public final ParticleOptions contactParticle;
    public final ParticleOptions smokeParticle;
    public final CompoundTag defaultData;
    private final Map<BogeySizes.BogeySize, Supplier<? extends AbstractBogeyBlock<?>>> sizes;
    @OnlyIn(value=Dist.CLIENT)
    private Map<BogeySizes.BogeySize, SizeRenderer> sizeRenderers;

    public BogeyStyle(ResourceLocation id, ResourceLocation cycleGroup, Component displayName, Supplier<SoundEvent> soundEvent, ParticleOptions contactParticle, ParticleOptions smokeParticle, CompoundTag defaultData, Map<BogeySizes.BogeySize, Supplier<? extends AbstractBogeyBlock<?>>> sizes, Map<BogeySizes.BogeySize, Supplier<Supplier<? extends SizeRenderer>>> sizeRenderers) {
        this.id = id;
        this.cycleGroup = cycleGroup;
        this.displayName = displayName;
        this.soundEvent = soundEvent;
        this.contactParticle = contactParticle;
        this.smokeParticle = smokeParticle;
        this.defaultData = defaultData;
        this.sizes = sizes;
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> {
            this.sizeRenderers = new HashMap<BogeySizes.BogeySize, SizeRenderer>();
            sizeRenderers.forEach((k, v) -> this.sizeRenderers.put((BogeySizes.BogeySize)k, (SizeRenderer)((Supplier)v.get()).get()));
        });
    }

    public Map<ResourceLocation, BogeyStyle> getCycleGroup() {
        return AllBogeyStyles.getCycleGroup(this.cycleGroup);
    }

    public Set<BogeySizes.BogeySize> validSizes() {
        return this.sizes.keySet();
    }

    public AbstractBogeyBlock<?> getBlockForSize(BogeySizes.BogeySize size) {
        return this.sizes.get(size).get();
    }

    public AbstractBogeyBlock<?> getNextBlock(BogeySizes.BogeySize currentSize) {
        return Stream.iterate(currentSize.nextBySize(), BogeySizes.BogeySize::nextBySize).filter(this.sizes::containsKey).findFirst().map(this::getBlockForSize).orElse(this.getBlockForSize(currentSize));
    }

    @OnlyIn(value=Dist.CLIENT)
    public void render(BogeySizes.BogeySize size, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay, float wheelAngle, @Nullable CompoundTag bogeyData, boolean inContraption) {
        if (bogeyData == null) {
            bogeyData = new CompoundTag();
        }
        poseStack.translate(0.0, -1.5078125, 0.0);
        SizeRenderer renderer = this.sizeRenderers.get(size);
        if (renderer != null) {
            renderer.renderer.render(bogeyData, wheelAngle, partialTick, poseStack, buffers, light, overlay, inContraption);
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    @Nullable
    public BogeyVisual createVisual(BogeySizes.BogeySize size, VisualizationContext ctx, float partialTick, boolean inContraption) {
        SizeRenderer renderer = this.sizeRenderers.get(size);
        if (renderer != null) {
            return renderer.visualizer.createVisual(ctx, partialTick, inContraption);
        }
        return null;
    }

    @OnlyIn(value=Dist.CLIENT)
    public record SizeRenderer(BogeyRenderer renderer, BogeyVisualizer visualizer) {
    }

    public static class Builder {
        protected final ResourceLocation id;
        protected final ResourceLocation cycleGroup;
        protected final Map<BogeySizes.BogeySize, Supplier<? extends AbstractBogeyBlock<?>>> sizes = new HashMap();
        protected Component displayName = CreateLang.translateDirect("bogey.style.invalid", new Object[0]);
        protected Supplier<SoundEvent> soundEvent = AllSoundEvents.TRAIN2::getMainEvent;
        protected ParticleOptions contactParticle = ParticleTypes.CRIT;
        protected ParticleOptions smokeParticle = ParticleTypes.POOF;
        protected CompoundTag defaultData = new CompoundTag();
        protected final Map<BogeySizes.BogeySize, Supplier<Supplier<? extends SizeRenderer>>> sizeRenderers = new HashMap<BogeySizes.BogeySize, Supplier<Supplier<? extends SizeRenderer>>>();

        public Builder(ResourceLocation id, ResourceLocation cycleGroup) {
            this.id = id;
            this.cycleGroup = cycleGroup;
        }

        public Builder displayName(Component displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder soundEvent(Supplier<SoundEvent> soundEvent) {
            this.soundEvent = soundEvent;
            return this;
        }

        public Builder contactParticle(ParticleOptions contactParticle) {
            this.contactParticle = contactParticle;
            return this;
        }

        public Builder smokeParticle(ParticleOptions smokeParticle) {
            this.smokeParticle = smokeParticle;
            return this;
        }

        public Builder defaultData(CompoundTag defaultData) {
            this.defaultData = defaultData;
            return this;
        }

        public Builder size(BogeySizes.BogeySize size, Supplier<? extends AbstractBogeyBlock<?>> block, Supplier<Supplier<? extends SizeRenderer>> renderer) {
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
}
