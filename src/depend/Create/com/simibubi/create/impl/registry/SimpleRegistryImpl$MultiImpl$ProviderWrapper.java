/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.impl.registry;

import com.simibubi.create.api.registry.SimpleRegistry;
import java.util.List;

private record SimpleRegistryImpl.MultiImpl.ProviderWrapper<K, V>(SimpleRegistry.Provider<K, V> wrapped) implements SimpleRegistry.Provider<K, List<V>>
{
    @Override
    public List<V> get(K object) {
        V value = this.wrapped.get(object);
        return value == null ? null : List.of(value);
    }

    @Override
    public void onRegister(Runnable invalidate) {
        this.wrapped.onRegister(invalidate);
    }
}
