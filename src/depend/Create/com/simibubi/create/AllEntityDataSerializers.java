/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.resources.ResourceKey
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  net.neoforged.neoforge.registries.NeoForgeRegistries$Keys
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package com.simibubi.create;

import com.simibubi.create.content.trains.entity.CarriageSyncDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

public class AllEntityDataSerializers {
    private static final DeferredRegister<EntityDataSerializer<?>> REGISTER = DeferredRegister.create((ResourceKey)NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, (String)"create");
    public static final CarriageSyncDataSerializer CARRIAGE_DATA = new CarriageSyncDataSerializer();
    public static final DeferredHolder<EntityDataSerializer<?>, CarriageSyncDataSerializer> CARRIAGE_DATA_ENTRY = REGISTER.register("carriage_data", () -> CARRIAGE_DATA);

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
