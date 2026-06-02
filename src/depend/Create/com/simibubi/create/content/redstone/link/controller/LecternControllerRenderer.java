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
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.redstone.link.controller;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.redstone.link.controller.LecternControllerBlock;
import com.simibubi.create.content.redstone.link.controller.LecternControllerBlockEntity;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItemRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.Property;

public class LecternControllerRenderer
extends SafeBlockEntityRenderer<LecternControllerBlockEntity> {
    public LecternControllerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(LecternControllerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ItemStack stack = AllItems.LINKED_CONTROLLER.asStack();
        ItemDisplayContext transformType = ItemDisplayContext.NONE;
        CustomRenderedItemModel mainModel = (CustomRenderedItemModel)Minecraft.getInstance().getItemRenderer().getModel(stack, be.getLevel(), null, 0);
        PartialItemModelRenderer renderer = PartialItemModelRenderer.of(stack, transformType, ms, buffer, overlay);
        boolean active = be.hasUser();
        boolean renderDepression = be.isUsedBy((Player)Minecraft.getInstance().player);
        Direction facing = (Direction)be.getBlockState().getValue((Property)LecternControllerBlock.FACING);
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        ms.pushPose();
        msr.translate(0.5, 1.45, 0.5);
        msr.rotateYDegrees(AngleHelper.horizontalAngle((Direction)facing) - 90.0f);
        msr.translate(0.28, 0.0, 0.0);
        msr.rotateZDegrees(-22.0f);
        LinkedControllerItemRenderer.renderInLectern(stack, mainModel, renderer, transformType, ms, light, active, renderDepression);
        ms.popPose();
    }
}
