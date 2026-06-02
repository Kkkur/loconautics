/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.dimension.DimensionType
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.foundation.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShadowRenderHelper {
    private static final RenderType SHADOW_LAYER = RenderType.entityNoOutline((ResourceLocation)ResourceLocation.withDefaultNamespace((String)"textures/misc/shadow.png"));

    public static void renderShadow(PoseStack matrixStack, MultiBufferSource buffer, float opacity, float radius) {
        PoseStack.Pose entry = matrixStack.last();
        VertexConsumer builder = buffer.getBuffer(SHADOW_LAYER);
        ShadowRenderHelper.shadowVertex(entry, builder, opacity /= 2.0f, -1.0f * radius, 0.0f, -1.0f * radius, 0.0f, 0.0f);
        ShadowRenderHelper.shadowVertex(entry, builder, opacity, -1.0f * radius, 0.0f, 1.0f * radius, 0.0f, 1.0f);
        ShadowRenderHelper.shadowVertex(entry, builder, opacity, 1.0f * radius, 0.0f, 1.0f * radius, 1.0f, 1.0f);
        ShadowRenderHelper.shadowVertex(entry, builder, opacity, 1.0f * radius, 0.0f, -1.0f * radius, 1.0f, 0.0f);
    }

    public static void renderShadow(PoseStack matrixStack, MultiBufferSource buffer, LevelReader world, Vec3 pos, float opacity, float radius) {
        float f = radius;
        double d2 = pos.x();
        double d0 = pos.y();
        double d1 = pos.z();
        int i = Mth.floor((double)(d2 - (double)f));
        int j = Mth.floor((double)(d2 + (double)f));
        int k = Mth.floor((double)(d0 - (double)f));
        int l = Mth.floor((double)d0);
        int i1 = Mth.floor((double)(d1 - (double)f));
        int j1 = Mth.floor((double)(d1 + (double)f));
        PoseStack.Pose entry = matrixStack.last();
        VertexConsumer builder = buffer.getBuffer(SHADOW_LAYER);
        for (BlockPos blockpos : BlockPos.betweenClosed((BlockPos)new BlockPos(i, k, i1), (BlockPos)new BlockPos(j, l, j1))) {
            ShadowRenderHelper.renderBlockShadow(entry, builder, world, blockpos, d2, d0, d1, f, opacity);
        }
    }

    private static void renderBlockShadow(PoseStack.Pose entry, VertexConsumer builder, LevelReader world, BlockPos pos, double x, double y, double z, float radius, float opacity) {
        VoxelShape voxelshape;
        BlockPos blockpos = pos.below();
        BlockState blockstate = world.getBlockState(blockpos);
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE && world.getMaxLocalRawBrightness(pos) > 3 && blockstate.isCollisionShapeFullBlock((BlockGetter)world, blockpos) && !(voxelshape = blockstate.getShape((BlockGetter)world, pos.below())).isEmpty()) {
            float brightness = LightTexture.getBrightness((DimensionType)world.dimensionType(), (int)world.getMaxLocalRawBrightness(pos));
            float f = (float)(((double)opacity - (y - (double)pos.getY()) / 2.0) * 0.5 * (double)brightness);
            if (f >= 0.0f) {
                if (f > 1.0f) {
                    f = 1.0f;
                }
                AABB AABB2 = voxelshape.bounds();
                double d0 = (double)pos.getX() + AABB2.minX;
                double d1 = (double)pos.getX() + AABB2.maxX;
                double d2 = (double)pos.getY() + AABB2.minY;
                double d3 = (double)pos.getZ() + AABB2.minZ;
                double d4 = (double)pos.getZ() + AABB2.maxZ;
                float f1 = (float)(d0 - x);
                float f2 = (float)(d1 - x);
                float f3 = (float)(d2 - y + 0.015625);
                float f4 = (float)(d3 - z);
                float f5 = (float)(d4 - z);
                float f6 = -f1 / 2.0f / radius + 0.5f;
                float f7 = -f2 / 2.0f / radius + 0.5f;
                float f8 = -f4 / 2.0f / radius + 0.5f;
                float f9 = -f5 / 2.0f / radius + 0.5f;
                ShadowRenderHelper.shadowVertex(entry, builder, f, f1, f3, f4, f6, f8);
                ShadowRenderHelper.shadowVertex(entry, builder, f, f1, f3, f5, f6, f9);
                ShadowRenderHelper.shadowVertex(entry, builder, f, f2, f3, f5, f7, f9);
                ShadowRenderHelper.shadowVertex(entry, builder, f, f2, f3, f4, f7, f8);
            }
        }
    }

    private static void shadowVertex(PoseStack.Pose entry, VertexConsumer builder, float alpha, float x, float y, float z, float u, float v) {
        builder.addVertex(entry.pose(), x, y, z).setColor(1.0f, 1.0f, 1.0f, alpha).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(entry.copy(), 0.0f, 1.0f, 0.0f);
    }
}
