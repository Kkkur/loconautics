/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.StateHolder
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.impl.registry;

import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.foundation.mixin.accessor.StateHolderAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.world.level.block.state.StateHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract sealed class SimpleRegistryImpl<K, V>
implements SimpleRegistry<K, V> {
    protected final Map<K, V> registrations = new IdentityHashMap();
    protected final List<SimpleRegistry.Provider<K, V>> providers = new ArrayList<SimpleRegistry.Provider<K, V>>();

    @Override
    public synchronized void register(K object, V value) {
        Objects.requireNonNull(object, "object");
        Objects.requireNonNull(value, "value");
        V existing = this.registrations.get(object);
        if (existing != null) {
            throw new IllegalArgumentException(String.format("Tried to register duplicate values for object %s (%s): old=%s, new=%s", object, object.getClass(), existing, value));
        }
        this.registrations.put(object, value);
    }

    @Override
    public synchronized void registerProvider(SimpleRegistry.Provider<K, V> provider) {
        Objects.requireNonNull(provider);
        if (this.providers.contains(provider)) {
            throw new IllegalArgumentException("Tried to register provider twice: " + String.valueOf(provider));
        }
        this.providers.add(0, provider);
        provider.onRegister(this::invalidate);
    }

    @Override
    @Nullable
    public synchronized V get(StateHolder<K, ?> state) {
        Objects.requireNonNull(state, "state");
        Object owner = ((StateHolderAccessor)state).getOwner();
        return this.get(owner);
    }

    public static <K, V> SimpleRegistry<K, V> single() {
        return new SingleImpl();
    }

    public static <K, V> SimpleRegistry.Multi<K, V> multi() {
        return new MultiImpl();
    }

    static final class SingleImpl<K, V>
    extends SimpleRegistryImpl<K, V> {
        private static final Object nullMarker = new Object();
        private final Map<K, V> providedValues = new IdentityHashMap();

        SingleImpl() {
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
            this.providedValues.put(object, SingleImpl.nullMarker());
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

    static final class MultiImpl<K, V>
    extends SimpleRegistryImpl<K, List<V>>
    implements SimpleRegistry.Multi<K, V> {
        private final Map<K, List<V>> totals = new IdentityHashMap<K, List<V>>();

        MultiImpl() {
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
}
