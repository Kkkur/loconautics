/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package dev.eriksonn.aeronautics.content.components;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.eriksonn.aeronautics.index.AeroDataComponents;
import dev.eriksonn.aeronautics.index.AeroItems;
import dev.eriksonn.aeronautics.index.AeroSoundEvents;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record Converter(ItemStack item, int ticks, Optional<ResourceLocation> sound, Optional<ResourceLocation> particle) {
    public static final Codec<Converter> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ItemStack.CODEC.fieldOf("item").forGetter(Converter::item), (App)Codec.INT.fieldOf("ticks").forGetter(Converter::ticks), (App)ResourceLocation.CODEC.optionalFieldOf("sound").forGetter(Converter::sound), (App)ResourceLocation.CODEC.optionalFieldOf("particle").forGetter(Converter::particle)).apply((Applicative)instance, Converter::new));

    public Converter(Converter converter, int ticks) {
        this(converter.item(), ticks, converter.sound(), converter.particle());
    }

    public static Converter cloudSkipper() {
        return new Converter(AeroItems.MUSIC_DISC_CLOUD_SKIPPER.asStack(), 60, Optional.of(AeroSoundEvents.CLOUD_SKIPPER_TRANSFORM.id()), Optional.of(ResourceLocation.withDefaultNamespace((String)"white_smoke")));
    }

    public static void tick(Level level, ItemEntity entity, ItemStack stack, Converter converter) {
        if (converter.item().isEmpty()) {
            return;
        }
        if (converter.ticks() > 0) {
            stack.set(AeroDataComponents.CONVERTER, (Object)new Converter(converter, converter.ticks() - 1));
        } else {
            ResourceLocation soundLocation;
            SoundEvent sound;
            int count = stack.getCount();
            entity.setItem(converter.item().copy());
            ItemStack newItem = entity.getItem();
            newItem.remove(AeroDataComponents.CONVERTER);
            newItem.setCount(count);
            if (converter.sound().isPresent() && (sound = (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(soundLocation = converter.sound().get())) != null) {
                level.playSound((Entity)entity, entity.blockPosition(), sound, SoundSource.AMBIENT, 5.0f, 1.0f);
            }
            if (converter.particle().isPresent() && level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                ParticleType particle = (ParticleType)BuiltInRegistries.PARTICLE_TYPE.get(converter.particle().get());
                if (particle instanceof ParticleOptions) {
                    ParticleOptions particleOptions = (ParticleOptions)particle;
                    Vec3 pos = entity.position();
                    float offset = entity.getBbHeight() + entity.getBbHeight() / 2.0f;
                    serverLevel.sendParticles(particleOptions, pos.x(), pos.y() + (double)offset, pos.z(), 20, 0.0, 0.0, 0.0, 0.05);
                }
            }
        }
    }
}
