/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.neoforged.neoforge.registries.datamaps.DataMapType
 */
package com.simibubi.create.api.registry;

import com.simibubi.create.Create;
import com.simibubi.create.api.data.datamaps.BlazeBurnerFuel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public class CreateDataMaps {
    public static final DataMapType<Item, BlazeBurnerFuel> REGULAR_BLAZE_BURNER_FUELS = DataMapType.builder((ResourceLocation)Create.asResource("regular_blaze_burner_fuels"), (ResourceKey)Registries.ITEM, BlazeBurnerFuel.CODEC).build();
    public static final DataMapType<Item, BlazeBurnerFuel> SUPERHEATED_BLAZE_BURNER_FUELS = DataMapType.builder((ResourceLocation)Create.asResource("superheated_blaze_burner_fuels"), (ResourceKey)Registries.ITEM, BlazeBurnerFuel.CODEC).build();

    private CreateDataMaps() {
        throw new AssertionError((Object)"This class should not be instantiated");
    }
}
