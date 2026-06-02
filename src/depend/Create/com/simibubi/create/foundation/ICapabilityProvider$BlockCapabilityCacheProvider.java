/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.neoforge.capabilities.BlockCapabilityCache
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation;

import com.simibubi.create.foundation.ICapabilityProvider;
import java.util.function.Function;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public static class ICapabilityProvider.BlockCapabilityCacheProvider<T, C>
implements ICapabilityProvider<T> {
    private final BlockCapabilityCache<T, C> inner;
    private volatile boolean invalid = false;

    private ICapabilityProvider.BlockCapabilityCacheProvider(Function<Runnable, BlockCapabilityCache<T, C>> cacheFactory) {
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
