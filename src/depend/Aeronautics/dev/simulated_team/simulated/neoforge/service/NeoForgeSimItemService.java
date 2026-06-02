/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.data.datamaps.BlazeBurnerFuel
 *  com.simibubi.create.api.registry.CreateDataMaps
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.RecipeType
 */
package dev.simulated_team.simulated.neoforge.service;

import com.simibubi.create.api.data.datamaps.BlazeBurnerFuel;
import com.simibubi.create.api.registry.CreateDataMaps;
import dev.simulated_team.simulated.service.SimItemService;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class NeoForgeSimItemService
implements SimItemService {
    @Override
    public int getBurnTime(ItemStack stack) {
        return stack.getBurnTime(RecipeType.SMELTING);
    }

    @Override
    public int getSuperheatedBurnTime(ItemStack stack) {
        BlazeBurnerFuel fuel = (BlazeBurnerFuel)stack.getItem().builtInRegistryHolder().getData(CreateDataMaps.SUPERHEATED_BLAZE_BURNER_FUELS);
        if (fuel != null) {
            return fuel.burnTime();
        }
        return 0;
    }
}
