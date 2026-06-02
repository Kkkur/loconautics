/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.logistics.tableCloth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.depot.DepotRenderer;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.List;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TableClothRenderer
extends SmartBlockEntityRenderer<TableClothBlockEntity> {
    public TableClothRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(TableClothBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);
        List<ItemStack> stacks = blockEntity.getItemsForRender();
        float rotationInRadians = (float)Math.PI / 180 * (180.0f - blockEntity.facing.toYRot());
        if (blockEntity.isShop()) {
            ((SuperByteBuffer)CachedBuffers.partial((PartialModel)(blockEntity.sideOccluded ? AllPartialModels.TABLE_CLOTH_PRICE_TOP : AllPartialModels.TABLE_CLOTH_PRICE_SIDE), (BlockState)blockEntity.getBlockState()).rotateCentered(rotationInRadians, Direction.UP)).light(light).overlay(overlay).renderInto(ms, buffer.getBuffer(RenderType.cutout()));
        }
        ms.pushPose();
        TransformStack.of((PoseStack)ms).rotateCentered(rotationInRadians, Direction.UP);
        for (int i = 0; i < stacks.size(); ++i) {
            BakedModel bakedModel;
            boolean blockItem;
            ItemStack entry = stacks.get(i);
            ms.pushPose();
            ms.translate(0.5f, 0.1875f, 0.5f);
            if (stacks.size() > 1) {
                ms.mulPose(Axis.YP.rotationDegrees((float)i * (360.0f / (float)stacks.size()) + 45.0f));
                ms.translate(0.0, i % 2 == 0 ? -0.005 : 0.0, 0.3125);
                ms.mulPose(Axis.YP.rotationDegrees((float)(-i) * (360.0f / (float)stacks.size()) - 45.0f));
            }
            if (!(blockItem = (bakedModel = Minecraft.getInstance().getItemRenderer().getModel(entry, null, null, 0)).isGui3d())) {
                TransformStack.of((PoseStack)ms).rotate(-rotationInRadians + (float)Math.PI, Direction.UP);
            }
            DepotRenderer.renderItem(ms, buffer, light, OverlayTexture.NO_OVERLAY, entry, 0, null, Vec3.atCenterOf((Vec3i)blockEntity.getBlockPos()), true);
            ms.popPose();
        }
        ms.popPose();
    }
}
