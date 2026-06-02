/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.saveddata.maps.MapDecorationType
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package com.simibubi.create;

import com.simibubi.create.Create;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

public class AllMapDecorationTypes {
    private static final DeferredRegister<MapDecorationType> DECORATION_TYPES = DeferredRegister.create((ResourceKey)Registries.MAP_DECORATION_TYPE, (String)"create");
    public static final Holder<MapDecorationType> STATION_MAP_DECORATION = DECORATION_TYPES.register("station", () -> new MapDecorationType(Create.asResource("station"), true, -1, false, true));

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        DECORATION_TYPES.register(modEventBus);
    }
}
