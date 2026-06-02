/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.alchemy.Potion
 *  net.minecraft.world.item.alchemy.PotionContents
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.material.FluidState
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.FluidType$Properties
 */
package com.simibubi.create.content.fluids.potion;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllFluids;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

public static class PotionFluid.PotionFluidType
extends AllFluids.TintedFluidType {
    public PotionFluid.PotionFluidType(FluidType.Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        super(properties, stillTexture, flowingTexture);
    }

    @Override
    public int getTintColor(FluidStack stack) {
        return ((PotionContents)stack.getOrDefault(DataComponents.POTION_CONTENTS, (Object)PotionContents.EMPTY)).getColor() | 0xFF000000;
    }

    public String getDescriptionId(FluidStack stack) {
        PotionContents contents = (PotionContents)stack.getOrDefault(DataComponents.POTION_CONTENTS, (Object)PotionContents.EMPTY);
        ItemLike itemFromBottleType = PotionFluidHandler.itemFromBottleType((PotionFluid.BottleType)((Object)stack.getOrDefault(AllDataComponents.POTION_FLUID_BOTTLE_TYPE, (Object)PotionFluid.BottleType.REGULAR)));
        return Potion.getName((Optional)contents.potion(), (String)(itemFromBottleType.asItem().getDescriptionId() + ".effect."));
    }

    @Override
    protected int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return -1;
    }
}
