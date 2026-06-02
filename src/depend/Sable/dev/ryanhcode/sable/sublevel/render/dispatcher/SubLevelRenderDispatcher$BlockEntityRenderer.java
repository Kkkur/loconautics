/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.ryanhcode.sable.sublevel.render.dispatcher;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;

public static interface SubLevelRenderDispatcher.BlockEntityRenderer {
    default public void renderBlockEntities(Collection<BlockEntity> blockEntities, PoseStack poseStack, float partialTick, double cameraX, double cameraY, double cameraZ) {
        for (BlockEntity blockEntity : blockEntities) {
            this.renderSingleBE(blockEntity, poseStack, partialTick, cameraX, cameraY, cameraZ);
        }
    }

    public void renderSingleBE(BlockEntity var1, PoseStack var2, float var3, double var4, double var6, double var8);

    public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher();
}
