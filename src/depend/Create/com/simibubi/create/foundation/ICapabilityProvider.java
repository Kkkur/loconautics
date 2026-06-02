/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.capabilities.BlockCapabilityCache
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation;

import java.util.function.Function;
import java.util.function.Supplier;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public interface ICapabilityProvider<T> {
    @Nullable
    public T getCapability();

    public static <T, C> ICapabilityProvider<T> of(Function<Runnable, BlockCapabilityCache<T, C>> cacheFactory) {
        return new BlockCapabilityCacheProvider<T, C>(cacheFactory);
    }

    public static <T> ICapabilityProvider<T> of(Supplier<T> supplier) {
        return new SupplierProvider<T>(supplier);
    }

    public static <T> ICapabilityProvider<T> of(T cap) {
        return new SimpleProvider<T>(cap);
    }

    @ApiStatus.Internal
    public static class BlockCapabilityCacheProvider<T, C>
    implements ICapabilityProvider<T> {
        private final BlockCapabilityCache<T, C> inner;
        private volatile boolean invalid = false;

        private BlockCapabilityCacheProvider(Function<Runnable, BlockCapabilityCache<T, C>> cacheFactory) {
            this.inner = cacheFactory == null ? null : cacheFactory.apply(() -> {
                this.invalid = true;
            });
        }

        @Override
        @Nullable
        public T getCapability() {
            return (T)(this.inner == null || this.invalid ? null : this.inner.getCapability());
        }
    }

    public static class SupplierProvider<T>
    implements ICapabilityProvider<T> {
        private final Supplier<T> inner;

        private SupplierProvider(Supplier<T> inner) {
            this.inner = inner;
        }

        @Override
        @Nullable
        public T getCapability() {
            return this.inner == null ? null : (T)this.inner.get();
        }
    }

    @ApiStatus.Internal
    public static class SimpleProvider<T>
    implements ICapabilityProvider<T> {
        private final T inner;

        private SimpleProvider(T inner) {
            this.inner = inner;
        }

        @Override
        @Nullable
        public T getCapability() {
            return this.inner;
        }
    }
}
