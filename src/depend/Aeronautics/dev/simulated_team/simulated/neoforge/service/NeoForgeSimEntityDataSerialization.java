/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.resources.ResourceKey
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  net.neoforged.neoforge.registries.NeoForgeRegistries$Keys
 */
package dev.simulated_team.simulated.neoforge.service;

import dev.simulated_team.simulated.service.SimEntityDataSerialization;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class NeoForgeSimEntityDataSerialization
implements SimEntityDataSerialization {
    private static final DeferredRegister<EntityDataSerializer<?>> REGISTER = DeferredRegister.create((ResourceKey)NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, (String)"simulated");

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }

    @Override
    public <A, T extends EntityDataSerializer<A>> void registerDataSerializer(String name, T serializer) {
        REGISTER.register(name, () -> serializer);
    }
}
