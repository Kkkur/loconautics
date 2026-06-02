/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.StateHolder
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.api.registry;

import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.impl.registry.SimpleRegistryImpl;
import java.util.List;
import net.minecraft.world.level.block.state.StateHolder;
import org.jetbrains.annotations.NotNull;

public static interface SimpleRegistry.Multi<K, V>
extends SimpleRegistry<K, List<V>> {
    public void add(K var1, V var2);

    public void addProvider(SimpleRegistry.Provider<K, V> var1);

    @Override
    @NotNull
    public List<V> get(K var1);

    @Override
    @NotNull
    public List<V> get(StateHolder<K, ?> var1);

    public static <K, V> SimpleRegistry.Multi<K, V> create() {
        return SimpleRegistryImpl.multi();
    }
}
