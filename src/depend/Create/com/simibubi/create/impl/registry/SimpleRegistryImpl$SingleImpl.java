/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.impl.registry;

import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.impl.registry.SimpleRegistryImpl;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

static final class SimpleRegistryImpl.SingleImpl<K, V>
extends SimpleRegistryImpl<K, V> {
    private static final Object nullMarker = new Object();
    private final Map<K, V> providedValues = new IdentityHashMap();

    SimpleRegistryImpl.SingleImpl() {
    }

    @Override
    @Nullable
    public synchronized V get(K object) {
        Objects.requireNonNull(object, "object");
        if (this.registrations.containsKey(object)) {
            return this.registrations.get(object);
        }
        if (this.providedValues.containsKey(object)) {
            V provided = this.providedValues.get(object);
            return provided == nullMarker ? null : (V)provided;
        }
        for (SimpleRegistry.Provider provider : this.providers) {
            Object value = provider.get(object);
            if (value == null) continue;
            this.providedValues.put(object, value);
            return value;
        }
        this.providedValues.put(object, SimpleRegistryImpl.SingleImpl.nullMarker());
        return null;
    }

    @Override
    public void invalidate() {
        this.providedValues.clear();
    }

    private static <T> T nullMarker() {
        return (T)nullMarker;
    }
}
