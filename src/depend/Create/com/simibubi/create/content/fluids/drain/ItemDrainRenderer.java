/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.platform.NeoForgeCatnipServices
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.fluids.FluidStack
 */
package com.simibubi.create.content.fluids.drain;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.fluids.drain.ItemDrainBlockEntity;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.Random;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;

public class ItemDrainRenderer
extends SmartBlockEntityRenderer<ItemDrainBlockEntity> {
    public ItemDrainRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(ItemDrainBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        this.renderFluid(be, partialTicks, ms, buffer, light);
        this.renderItem(be, partialTicks, ms, buffer, light, overlay);
    }

    protected void renderItem(ItemDrainBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        boolean alongX;
        TransportedItemStack transported = be.heldItem;
        if (transported == null) {
            return;
        }
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        Vec3 itemPosition = VecHelper.getCenterOf((Vec3i)be.getBlockPos());
        Direction insertedFrom = transported.insertedFrom;
        if (!insertedFrom.getAxis().isHorizontal()) {
            return;
        }
        ms.pushPose();
        ms.translate(0.5f, 0.9375f, 0.5f);
        msr.nudge(0);
        float offset = Mth.lerp((float)partialTicks, (float)transported.prevBeltPosition, (float)transported.beltPosition);
        float sideOffset = Mth.lerp((float)partialTicks, (float)transported.prevSideOffset, (float)transported.sideOffset);
        Vec3 offsetVec = Vec3.atLowerCornerOf((Vec3i)insertedFrom.getOpposite().getNormal()).scale((double)(0.5f - offset));
        ms.translate(offsetVec.x, offsetVec.y, offsetVec.z);
        boolean bl = alongX = insertedFrom.getClockWise().getAxis() == Direction.Axis.X;
        if (!alongX) {
            sideOffset *= -1.0f;
        }
        ms.translate(alongX ? sideOffset : 0.0f, 0.0f, alongX ? 0.0f : sideOffset);
        ItemStack itemStack = transported.stack;
        Random r = new Random(0L);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        int count = Mth.log2((int)itemStack.getCount()) / 2;
        boolean renderUpright = BeltHelper.isItemUpright(itemStack);
        BakedModel bakedModel = itemRenderer.getModel(itemStack, null, null, 0);
        boolean blockItem = bakedModel.isGui3d();
        if (renderUpright) {
            ms.translate(0.0, 0.09375, 0.0);
        }
        int positive = insertedFrom.getAxisDirection().getStep();
        float verticalAngle = (float)positive * offset * 360.0f;
        if (insertedFrom.getAxis() != Direction.Axis.X) {
            msr.rotateXDegrees(verticalAngle);
        }
        if (insertedFrom.getAxis() != Direction.Axis.Z) {
            msr.rotateZDegrees(-verticalAngle);
        }
        if (renderUpright) {
            Vec3 cameraPosition = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            Vec3 vectorForOffset = itemPosition.add(offsetVec);
            Vec3 diff = vectorForOffset.subtract(cameraPosition);
            if (insertedFrom.getAxis() != Direction.Axis.X) {
                diff = VecHelper.rotate((Vec3)diff, (double)verticalAngle, (Direction.Axis)Direction.Axis.X);
            }
            if (insertedFrom.getAxis() != Direction.Axis.Z) {
                diff = VecHelper.rotate((Vec3)diff, (double)(-verticalAngle), (Direction.Axis)Direction.Axis.Z);
            }
            float yRot = (float)Mth.atan2((double)diff.z, (double)(-diff.x));
            ms.mulPose(Axis.YP.rotation((float)((double)yRot - 1.5707963267948966)));
            ms.translate(0.0f, 0.0f, -0.0625f);
        }
        for (int i = 0; i <= count; ++i) {
            ms.pushPose();
            if (blockItem) {
                ms.translate(r.nextFloat() * 0.0625f * (float)i, 0.0f, r.nextFloat() * 0.0625f * (float)i);
            }
            ms.scale(0.5f, 0.5f, 0.5f);
            if (!blockItem && !renderUpright) {
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

    protected void renderFluid(ItemDrainBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light) {
        ItemStack heldItemStack;
        SmartFluidTankBehaviour tank = be.internalTank;
        if (tank == null) {
            return;
        }
        SmartFluidTankBehaviour.TankSegment primaryTank = tank.getPrimaryTank();
        FluidStack fluidStack = primaryTank.getRenderedFluid();
        float level = primaryTank.getFluidLevel().getValue(partialTicks);
        if (!fluidStack.isEmpty() && level != 0.0f) {
            float yMin = 0.3125f;
            float min = 0.125f;
            float max = min + 0.75f;
            float yOffset = 0.4375f * level;
            ms.pushPose();
            ms.translate(0.0f, yOffset, 0.0f);
            NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox((Object)fluidStack, min, yMin - yOffset, min, max, yMin, max, buffer, ms, light, false, false);
            ms.popPose();
        }
        if ((heldItemStack = be.getHeldItemStack()).isEmpty()) {
            return;
        }
        FluidStack fluidStack2 = (FluidStack)GenericItemEmptying.emptyItem(be.getLevel(), heldItemStack, true).getFirst();
        if (fluidStack2.isEmpty()) {
            if (fluidStack.isEmpty()) {
                return;
            }
            fluidStack2 = fluidStack;
        }
        int processingTicks = be.processingTicks;
        float processingPT = (float)be.processingTicks - partialTicks;
        float processingProgress = 1.0f - (processingPT - 5.0f) / 10.0f;
        processingProgress = Mth.clamp((float)processingProgress, (float)0.0f, (float)1.0f);
        float radius = 0.0f;
        if (processingTicks != -1) {
            radius = (float)(Math.pow(2.0f * processingProgress - 1.0f, 2.0) - 1.0);
            AABB bb = new AABB(0.5, 1.0, 0.5, 0.5, 0.25, 0.5).inflate((double)(radius / 32.0f));
            NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox((Object)fluidStack2, (float)bb.minX, (float)bb.minY, (float)bb.minZ, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ, buffer, ms, light, true, false);
        }
    }
}
