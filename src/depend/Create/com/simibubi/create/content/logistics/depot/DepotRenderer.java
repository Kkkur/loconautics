/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.logistics.depot;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.Random;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class DepotRenderer
extends SafeBlockEntityRenderer<DepotBlockEntity> {
    public DepotRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(DepotBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        DepotRenderer.renderItemsOf(be, partialTicks, ms, buffer, light, overlay, be.depotBehaviour);
    }

    public static void renderItemsOf(SmartBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, DepotBehaviour depotBehaviour) {
        TransportedItemStack transported = depotBehaviour.heldItem;
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        Vec3 itemPosition = VecHelper.getCenterOf((Vec3i)be.getBlockPos());
        ms.pushPose();
        ms.translate(0.5f, 0.9375f, 0.5f);
        if (transported != null) {
            depotBehaviour.incoming.add(transported);
        }
        for (TransportedItemStack tis : depotBehaviour.incoming) {
            ms.pushPose();
            msr.nudge(0);
            float offset = Mth.lerp((float)partialTicks, (float)tis.prevBeltPosition, (float)tis.beltPosition);
            float sideOffset = Mth.lerp((float)partialTicks, (float)tis.prevSideOffset, (float)tis.sideOffset);
            if (tis.insertedFrom.getAxis().isHorizontal()) {
                boolean alongX;
                Vec3 offsetVec = Vec3.atLowerCornerOf((Vec3i)tis.insertedFrom.getOpposite().getNormal()).scale((double)(0.5f - offset));
                ms.translate(offsetVec.x, offsetVec.y, offsetVec.z);
                boolean bl = alongX = tis.insertedFrom.getClockWise().getAxis() == Direction.Axis.X;
                if (!alongX) {
                    sideOffset *= -1.0f;
                }
                ms.translate(alongX ? sideOffset : 0.0f, 0.0f, alongX ? 0.0f : sideOffset);
            }
            ItemStack itemStack = tis.stack;
            int angle = tis.angle;
            Random r = new Random(0L);
            DepotRenderer.renderItem(ms, buffer, light, overlay, itemStack, angle, r, itemPosition, false);
            ms.popPose();
        }
        if (transported != null) {
            depotBehaviour.incoming.remove(transported);
        }
        for (int i = 0; i < depotBehaviour.processingOutputBuffer.getSlots(); ++i) {
            ItemStack stack = depotBehaviour.processingOutputBuffer.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            ms.pushPose();
            msr.nudge(i);
            boolean renderUpright = BeltHelper.isItemUpright(stack);
            msr.rotateYDegrees(45.0f * (float)i);
            ms.translate(0.35f, 0.0f, 0.0f);
            if (renderUpright) {
                msr.rotateYDegrees(-(45.0f * (float)i));
            }
            Random r = new Random(i + 1);
            int angle = (int)(360.0f * r.nextFloat());
            DepotRenderer.renderItem(ms, buffer, light, overlay, stack, renderUpright ? angle + 90 : angle, r, itemPosition, false);
            ms.popPose();
        }
        ms.popPose();
    }

    public static void renderItem(PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack itemStack, int angle, Random r, Vec3 itemPosition, boolean alwaysUpright) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        int count = Mth.log2((int)itemStack.getCount()) / 2;
        BakedModel bakedModel = itemRenderer.getModel(itemStack, null, null, 0);
        boolean blockItem = bakedModel.isGui3d();
        boolean renderUpright = BeltHelper.isItemUpright(itemStack) || alwaysUpright && !blockItem;
        ms.pushPose();
        msr.rotateYDegrees((float)angle);
        if (renderUpright) {
            Vec3 cameraPosition = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            Vec3 diff = itemPosition.subtract(cameraPosition);
            float yRot = (float)(Mth.atan2((double)diff.x, (double)diff.z) + Math.PI);
            ms.mulPose(Axis.YP.rotation(yRot));
            ms.translate(0.0, 0.09375, -0.0625);
        }
        for (int i = 0; i <= count; ++i) {
            ms.pushPose();
            if (blockItem && r != null) {
                ms.translate(r.nextFloat() * 0.0625f * (float)i, 0.0f, r.nextFloat() * 0.0625f * (float)i);
            }
            if (PackageItem.isPackage(itemStack) && !alwaysUpright) {
                ms.translate(0.0f, 0.25f, 0.0f);
                ms.scale(1.5f, 1.5f, 1.5f);
            } else if (blockItem && alwaysUpright) {
                ms.translate(0.0f, 0.0625f, 0.0f);
                ms.scale(0.755f, 0.755f, 0.755f);
            } else {
                ms.scale(0.5f, 0.5f, 0.5f);
            }
            if (!blockItem && !renderUpright) {
                ms.translate(0.0f, -0.1875f, 0.0f);
                msr.rotateXDegrees(90.0f);
            }
            itemRenderer.render(itemStack, ItemDisplayContext.FIXED, false, ms, buffer, light, overlay, bakedModel);
            ms.popPose();
            if (!renderUpright) {
                if (!blockItem) {
                    msr.rotateYDegrees(10.0f);
                }
                ms.translate(0.0, blockItem ? 0.015625 : 0.0625, 0.0);
                continue;
            }
            ms.translate(0.0f, 0.0f, -0.0625f);
        }
        ms.popPose();
    }
}
