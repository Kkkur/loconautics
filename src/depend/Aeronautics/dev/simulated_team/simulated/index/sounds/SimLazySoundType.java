/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.level.block.SoundType
 */
package dev.simulated_team.simulated.index.sounds;

import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

public class SimLazySoundType
extends SoundType {
    private final LazySupplier<SoundEvent> lazyBreak;
    private final LazySupplier<SoundEvent> lazyStep;
    private final LazySupplier<SoundEvent> lazyPlace;
    private final LazySupplier<SoundEvent> lazyHit;
    private final LazySupplier<SoundEvent> lazyFall;

    public SimLazySoundType(float volume, float pitch, Supplier<SoundEvent> lazyBreak, Supplier<SoundEvent> lazyStep, Supplier<SoundEvent> lazyPlace, Supplier<SoundEvent> lazyHit, Supplier<SoundEvent> lazyFall) {
        super(volume, pitch, null, null, null, null, null);
        this.lazyBreak = LazySupplier.of(lazyBreak);
        this.lazyStep = LazySupplier.of(lazyStep);
        this.lazyPlace = LazySupplier.of(lazyPlace);
        this.lazyHit = LazySupplier.of(lazyHit);
        this.lazyFall = LazySupplier.of(lazyFall);
    }

    public SoundEvent getBreakSound() {
        return this.lazyBreak.cast();
    }

    public SoundEvent getStepSound() {
        return this.lazyStep.cast();
    }

    public SoundEvent getPlaceSound() {
        return this.lazyPlace.cast();
    }

    public SoundEvent getHitSound() {
        return this.lazyHit.cast();
    }

    public SoundEvent getFallSound() {
        return this.lazyFall.cast();
    }

    public static class LazySupplier<T> {
        T nullableLazy;
        Supplier<T> lazyGetter;

        public static <T> LazySupplier<T> of(Supplier<T> getter) {
            return new LazySupplier<T>(getter);
        }

        public LazySupplier(Supplier<T> getter) {
            this.lazyGetter = getter;
        }

        public T cast() {
            if (this.nullableLazy == null) {
                this.nullableLazy = this.lazyGetter.get();
            }
            return this.nullableLazy;
        }
    }
}
