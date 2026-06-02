/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.decoration.placard;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.decoration.placard.PlacardBlock;
import com.simibubi.create.content.decoration.placard.PlacardBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;

public class PlacardRenderer
extends SafeBlockEntityRenderer<PlacardBlockEntity> {
    public PlacardRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(PlacardBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ItemStack heldItem = be.getHeldItem();
        if (heldItem.isEmpty()) {
            return;
        }
        BlockState blockState = be.getBlockState();
        Direction facing = (Direction)blockState.getValue((Property)PlacardBlock.FACING);
        AttachFace face = (AttachFace)blockState.getValue((Property)PlacardBlock.FACE);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel bakedModel = itemRenderer.getModel(heldItem, null, null, 0);
        boolean blockItem = bakedModel.isGui3d();
        ms.pushPose();
        ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)ms).center()).rotate((face == AttachFace.CEILING ? (float)Math.PI : 0.0f) + AngleHelper.rad((double)(180.0f + AngleHelper.horizontalAngle((Direction)facing))), Direction.UP)).rotate(face == AttachFace.CEILING ? -1.5707964f : (face == AttachFace.FLOOR ? 1.5707964f : 0.0f), Direction.EAST)).translate(0.0, 0.0, 0.28125)).scale(blockItem ? 0.5f : 0.375f);
        itemRenderer.render(heldItem, ItemDisplayContext.FIXED, false, ms, buffer, light, overlay, bakedModel);
        ms.popPose();
    }
}
