/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.tags.TagKey
 */
package com.simibubi.create.api.registry.registrate;

import com.simibubi.create.api.registry.SimpleRegistry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;

protected record SimpleBuilder.SimpleRegistryAccess<K, V>(BiConsumer<K, V> adder, BiConsumer<TagKey<K>, V> tagAdder) {
    public static <K, V> SimpleBuilder.SimpleRegistryAccess<K, V> of(SimpleRegistry<K, V> registry, Function<K, Holder<K>> holderGetter) {
        return new SimpleBuilder.SimpleRegistryAccess<Object, Object>(registry::register, (tag, value) -> registry.registerProvider(SimpleRegistry.Provider.forTag(tag, holderGetter, value)));
    }

    public static <K, V> SimpleBuilder.SimpleRegistryAccess<K, V> of(SimpleRegistry.Multi<K, V> registry, Function<K, Holder<K>> holderGetter) {
        return new SimpleBuilder.SimpleRegistryAccess<Object, Object>(registry::add, (tag, value) -> registry.addProvider(SimpleRegistry.Provider.forTag(tag, holderGetter, value)));
    }
}
