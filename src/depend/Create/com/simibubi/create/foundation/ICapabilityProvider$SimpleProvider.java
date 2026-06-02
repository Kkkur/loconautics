/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation;

import com.simibubi.create.foundation.ICapabilityProvider;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public static class ICapabilityProvider.SimpleProvider<T>
implements ICapabilityProvider<T> {
    private final T inner;

    private ICapabilityProvider.SimpleProvider(T inner) {
        this.inner = inner;
    }

    @Override
    @Nullable
    public T getCapability() {
        return this.inner;
    }
}
