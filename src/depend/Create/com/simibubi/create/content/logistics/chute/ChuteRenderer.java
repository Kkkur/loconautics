/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.logistics.chute;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class ChuteRenderer
extends SafeBlockEntityRenderer<ChuteBlockEntity> {
    public ChuteRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(ChuteBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (be.item.isEmpty()) {
            return;
        }
        BlockState blockState = be.getBlockState();
        if (blockState.getValue((Property)ChuteBlock.FACING) != Direction.DOWN) {
            return;
        }
        if (blockState.getValue(ChuteBlock.SHAPE) != ChuteBlock.Shape.WINDOW && (be.bottomPullDistance == 0.0f || be.itemPosition.getValue(partialTicks) > 0.5f)) {
            return;
        }
        ChuteRenderer.renderItem(be, partialTicks, ms, buffer, light, overlay);
    }

    public static void renderItem(ChuteBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        ms.pushPose();
        msr.center();
        float itemScale = 0.5f;
        float itemPosition = be.itemPosition.getValue(partialTicks);
        ms.translate(0.0, -0.5 + (double)itemPosition, 0.0);
        if (PackageItem.isPackage(be.item)) {
            ms.scale(1.5f, 1.5f, 1.5f);
        } else {
            ms.scale(itemScale, itemScale, itemScale);
            msr.rotateXDegrees(itemPosition * 180.0f);
            msr.rotateYDegrees(itemPosition * 180.0f);
        }
        itemRenderer.renderStatic(be.item, ItemDisplayContext.FIXED, light, overlay, ms, buffer, be.getLevel(), 0);
        ms.popPose();
    }
}
