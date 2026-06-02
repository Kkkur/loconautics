/*
 * Decompiled with CFR 0.152.
 */
package dev.simulated_team.simulated.index.sounds;

import java.util.function.Supplier;

public static class SimLazySoundType.LazySupplier<T> {
    T nullableLazy;
    Supplier<T> lazyGetter;

    public static <T> SimLazySoundType.LazySupplier<T> of(Supplier<T> getter) {
        return new SimLazySoundType.LazySupplier<T>(getter);
    }

    public SimLazySoundType.LazySupplier(Supplier<T> getter) {
        this.lazyGetter = getter;
    }

    public T cast() {
        if (this.nullableLazy == null) {
            this.nullableLazy = this.lazyGetter.get();
        }
        return this.nullableLazy;
    }
}
