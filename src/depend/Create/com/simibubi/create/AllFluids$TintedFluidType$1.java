/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.FogShape
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Camera
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.FogRenderer$FogMode
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.material.FluidState
 *  net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
 *  net.neoforged.neoforge.fluids.FluidStack
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Vector3f
 */
package com.simibubi.create;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

class AllFluids.TintedFluidType.1
implements IClientFluidTypeExtensions {
    AllFluids.TintedFluidType.1() {
    }

    public ResourceLocation getStillTexture() {
        return TintedFluidType.this.stillTexture;
    }

    public ResourceLocation getFlowingTexture() {
        return TintedFluidType.this.flowingTexture;
    }

    public int getTintColor(FluidStack stack) {
        return TintedFluidType.this.getTintColor(stack);
    }

    public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return TintedFluidType.this.getTintColor(state, getter, pos);
    }

    @NotNull
    public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
        Vector3f customFogColor = TintedFluidType.this.getCustomFogColor();
        return customFogColor == null ? fluidFogColor : customFogColor;
    }

    public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
        float modifier = TintedFluidType.this.getFogDistanceModifier();
        float baseWaterFog = 96.0f;
        if (modifier != 1.0f) {
            RenderSystem.setShaderFogShape((FogShape)FogShape.CYLINDER);
            RenderSystem.setShaderFogStart((float)-8.0f);
            RenderSystem.setShaderFogEnd((float)(baseWaterFog * modifier));
        }
    }
}
