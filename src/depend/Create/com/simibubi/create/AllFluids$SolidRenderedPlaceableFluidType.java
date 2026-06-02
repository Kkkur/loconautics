/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.builders.FluidBuilder$FluidTypeFactory
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.material.FluidState
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.FluidType$Properties
 *  org.joml.Vector3f
 */
package com.simibubi.create;

import com.simibubi.create.AllFluids;
import com.tterrag.registrate.builders.FluidBuilder;
import java.util.function.Supplier;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Vector3f;

private static class AllFluids.SolidRenderedPlaceableFluidType
extends AllFluids.TintedFluidType {
    private Vector3f fogColor;
    private Supplier<Float> fogDistance;

    public static FluidBuilder.FluidTypeFactory create(int fogColor, Supplier<Float> fogDistance) {
        return (p, s, f) -> {
            AllFluids.SolidRenderedPlaceableFluidType fluidType = new AllFluids.SolidRenderedPlaceableFluidType(p, s, f);
            fluidType.fogColor = new Color(fogColor, false).asVectorF();
            fluidType.fogDistance = fogDistance;
            return fluidType;
        };
    }

    private AllFluids.SolidRenderedPlaceableFluidType(FluidType.Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        super(properties, stillTexture, flowingTexture);
    }

    @Override
    protected int getTintColor(FluidStack stack) {
        return -1;
    }

    @Override
    public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
        return 0xFFFFFF;
    }

    @Override
    protected Vector3f getCustomFogColor() {
        return this.fogColor;
    }

    @Override
    protected float getFogDistanceModifier() {
        return this.fogDistance.get().floatValue();
    }
}
