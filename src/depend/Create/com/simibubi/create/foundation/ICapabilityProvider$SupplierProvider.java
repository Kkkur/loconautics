/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation;

import com.simibubi.create.foundation.ICapabilityProvider;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public static class ICapabilityProvider.SupplierProvider<T>
implements ICapabilityProvider<T> {
    private final Supplier<T> inner;

    private ICapabilityProvider.SupplierProvider(Supplier<T> inner) {
        this.inner = inner;
    }

    @Override
    @Nullable
    public T getCapability() {
        return this.inner == null ? null : (T)this.inner.get();
    }
}
