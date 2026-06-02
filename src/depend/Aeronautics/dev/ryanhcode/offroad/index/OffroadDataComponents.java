/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.platform.registry.RegistrationProvider
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.component.DataComponentType$Builder
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 */
package dev.ryanhcode.offroad.index;

import dev.ryanhcode.offroad.content.components.TireLike;
import foundry.veil.platform.registry.RegistrationProvider;
import java.util.function.UnaryOperator;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

public class OffroadDataComponents {
    private static final RegistrationProvider<DataComponentType<?>> REGISTRY = RegistrationProvider.get((ResourceKey)Registries.DATA_COMPONENT_TYPE, (String)"offroad");
    public static final DataComponentType<TireLike> TIRE = OffroadDataComponents.create("tire", builder -> builder.persistent(TireLike.CODEC));

    private static <T> DataComponentType<T> create(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DataComponentType type = ((DataComponentType.Builder)builder.apply(DataComponentType.builder())).build();
        REGISTRY.register(name, () -> type);
        return type;
    }

    public static void init() {
    }
}
