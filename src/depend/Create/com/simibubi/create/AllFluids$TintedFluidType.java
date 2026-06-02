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
 *  net.neoforged.neoforge.fluids.FluidType
 *  net.neoforged.neoforge.fluids.FluidType$Properties
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Vector3f
 */
package com.simibubi.create;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public static abstract class AllFluids.TintedFluidType
extends FluidType {
    protected static final int NO_TINT = -1;
    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;

    public AllFluids.TintedFluidType(FluidType.Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        super(properties);
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
    }

    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions(){

            public ResourceLocation getStillTexture() {
                return stillTexture;
            }

            public ResourceLocation getFlowingTexture() {
                return flowingTexture;
            }

            public int getTintColor(FluidStack stack) {
                return this.getTintColor(stack);
            }

            public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                return this.getTintColor(state, getter, pos);
            }

            @NotNull
            public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                Vector3f customFogColor = this.getCustomFogColor();
                return customFogColor == null ? fluidFogColor : customFogColor;
            }

            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                float modifier = this.getFogDistanceModifier();
                float baseWaterFog = 96.0f;
                if (modifier != 1.0f) {
                    RenderSystem.setShaderFogShape((FogShape)FogShape.CYLINDER);
                    RenderSystem.setShaderFogStart((float)-8.0f);
                    RenderSystem.setShaderFogEnd((float)(baseWaterFog * modifier));
                }
            }
        });
    }

    protected abstract int getTintColor(FluidStack var1);

    protected abstract int getTintColor(FluidState var1, BlockAndTintGetter var2, BlockPos var3);

    protected Vector3f getCustomFogColor() {
        return null;
    }

    protected float getFogDistanceModifier() {
        return 1.0f;
    }
}
