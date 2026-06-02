/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  foundry.veil.platform.registry.RegistrationProvider
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.component.DataComponentType$Builder
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 */
package dev.simulated_team.simulated.index;

import com.mojang.serialization.Codec;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.NavigationTarget;
import dev.simulated_team.simulated.index.SimRegistries;
import foundry.veil.platform.registry.RegistrationProvider;
import java.util.UUID;
import java.util.function.UnaryOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class SimDataComponents {
    private static final RegistrationProvider<DataComponentType<?>> REGISTRY = RegistrationProvider.get((ResourceKey)Registries.DATA_COMPONENT_TYPE, (String)"simulated");
    public static final DataComponentType<BlockPos> ROPE_FIRST_CONNECTION = SimDataComponents.register("rope_first_connection", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));
    public static final DataComponentType<UUID> LODESTONE_COMPASS_SUBLEVEL_TRACKER = SimDataComponents.register("lodestone_compass_tracker", uuidBuilder -> uuidBuilder.persistent(UUIDUtil.CODEC));
    public static final DataComponentType<String> COMPASS_PLACER_UUID = SimDataComponents.register("compass_placer", builder -> builder.persistent((Codec)Codec.STRING));
    public static final DataComponentType<NavigationTarget> TARGET = SimDataComponents.register("target", builder -> builder.persistent(SimRegistries.NAVIGATION_TARGET.byNameCodec()).networkSynchronized(ResourceLocation.STREAM_CODEC.map(arg_0 -> SimRegistries.NAVIGATION_TARGET.get(arg_0), arg_0 -> SimRegistries.NAVIGATION_TARGET.getKey(arg_0))));

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DataComponentType type = ((DataComponentType.Builder)builder.apply(DataComponentType.builder())).build();
        REGISTRY.register(name, () -> type);
        return type;
    }

    public static void register() {
    }
}
