/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.StateHolder
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.impl.registry;

import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.impl.registry.SimpleRegistryImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.world.level.block.state.StateHolder;
import org.jetbrains.annotations.NotNull;

static final class SimpleRegistryImpl.MultiImpl<K, V>
extends SimpleRegistryImpl<K, List<V>>
implements SimpleRegistry.Multi<K, V> {
    private final Map<K, List<V>> totals = new IdentityHashMap<K, List<V>>();

    SimpleRegistryImpl.MultiImpl() {
    }

    @Override
    public synchronized void add(K object, V value) {
        Objects.requireNonNull(object, "object");
        Objects.requireNonNull(value, "value");
        if (!this.registrations.containsKey(object)) {
            this.registrations.put(object, new ArrayList());
        }
        ((List)this.registrations.get(object)).add(value);
    }

    @Override
    public void addProvider(SimpleRegistry.Provider<K, V> provider) {
        this.registerProvider(new ProviderWrapper<K, V>(provider));
    }

    @Override
    public synchronized void invalidate() {
        this.totals.clear();
    }

    @Override
    @NotNull
    public synchronized List<V> get(K object) {
        Objects.requireNonNull(object, "object");
        if (!this.totals.containsKey(object)) {
            this.totals.put(object, this.calculateTotal(object));
        }
        return this.totals.get(object);
    }

    private List<V> calculateTotal(K object) {
        List registrations = this.registrations.getOrDefault(object, List.of());
        ArrayList total = new ArrayList(registrations);
        for (SimpleRegistry.Provider provider : this.providers) {
            List values = (List)provider.get(object);
            if (values == null) continue;
            total.addAll(values);
        }
        return total.isEmpty() ? List.of() : Collections.unmodifiableList(total);
    }

    @Override
    public synchronized List<V> get(StateHolder<K, ?> state) {
        return (List)super.get(state);
    }

    private record ProviderWrapper<K, V>(SimpleRegistry.Provider<K, V> wrapped) implements SimpleRegistry.Provider<K, List<V>>
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
}
