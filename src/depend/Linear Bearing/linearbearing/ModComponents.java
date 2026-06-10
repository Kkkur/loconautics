/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.bearing.linearbearing;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create((ResourceKey)Registries.DATA_COMPONENT_TYPE, (String)"linearbearing");
    public static final Supplier<DataComponentType<List<BlockPos>>> FABRIC_POINTS = COMPONENTS.register("fabric_points", () -> DataComponentType.builder().persistent(BlockPos.CODEC.listOf()).build());

    public static void register(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }
}
