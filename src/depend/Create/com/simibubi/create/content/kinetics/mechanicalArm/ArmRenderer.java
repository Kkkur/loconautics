/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Matrix4fc
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Matrix4fc;

public class ArmRenderer
extends KineticBlockEntityRenderer<ArmBlockEntity> {
    public ArmRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(ArmBlockEntity be, float pt, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        int color;
        float headAngle;
        float upperArmAngle;
        float lowerArmAngle;
        float baseAngle;
        boolean rave;
        super.renderSafe(be, pt, ms, buffer, light, overlay);
        ItemStack item = be.heldItem;
        boolean hasItem = !item.isEmpty();
        boolean usingFlywheel = VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel());
        if (usingFlywheel && !hasItem) {
            return;
        }
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel bakedModel = itemRenderer.getModel(item, be.getLevel(), null, 0);
        boolean isBlockItem = hasItem && item.getItem() instanceof BlockItem && bakedModel.isGui3d();
        VertexConsumer builder = buffer.getBuffer(be.goggles ? RenderType.cutout() : RenderType.solid());
        BlockState blockState = be.getBlockState();
        PoseStack msLocal = new PoseStack();
        PoseTransformStack msr = TransformStack.of((PoseStack)msLocal);
        boolean inverted = (Boolean)blockState.getValue((Property)ArmBlock.CEILING);
        boolean bl = rave = be.phase == ArmBlockEntity.Phase.DANCING && be.getSpeed() != 0.0f;
        if (rave) {
            float renderTick = AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel()) + (float)(be.hashCode() % 64);
            baseAngle = renderTick * 10.0f % 360.0f;
            lowerArmAngle = Mth.lerp((float)((Mth.sin((float)(renderTick / 4.0f)) + 1.0f) / 2.0f), (float)-45.0f, (float)15.0f);
            upperArmAngle = Mth.lerp((float)((Mth.sin((float)(renderTick / 8.0f)) + 1.0f) / 4.0f), (float)-45.0f, (float)95.0f);
            headAngle = -lowerArmAngle;
            color = Color.rainbowColor((int)(AnimationTickHolder.getTicks() * 100)).getRGB();
        } else {
            baseAngle = be.baseAngle.getValue(pt);
            lowerArmAngle = be.lowerArmAngle.getValue(pt) - 135.0f;
            upperArmAngle = be.upperArmAngle.getValue(pt) - 90.0f;
            headAngle = be.headAngle.getValue(pt);
            color = 0xFFFFFF;
        }
        msr.center();
        if (inverted) {
            msr.rotateXDegrees(180.0f);
        }
        if (usingFlywheel) {
            this.doItemTransforms((TransformStack)msr, baseAngle, lowerArmAngle, upperArmAngle, headAngle);
        } else {
            this.renderArm(builder, ms, msLocal, (TransformStack)msr, blockState, color, baseAngle, lowerArmAngle, upperArmAngle, headAngle, be.goggles, inverted && be.goggles, hasItem, isBlockItem, light);
        }
        if (hasItem) {
            ms.pushPose();
            float itemScale = isBlockItem ? 0.5f : 0.625f;
            msr.rotateXDegrees(90.0f);
            msLocal.translate(0.0f, isBlockItem ? -0.5625f : -0.625f, 0.0f);
            msLocal.scale(itemScale, itemScale, itemScale);
            ms.last().pose().mul((Matrix4fc)msLocal.last().pose());
            itemRenderer.render(item, ItemDisplayContext.FIXED, false, ms, buffer, light, overlay, bakedModel);
            ms.popPose();
        }
    }

    private void renderArm(VertexConsumer builder, PoseStack ms, PoseStack msLocal, TransformStack msr, BlockState blockState, int color, float baseAngle, float lowerArmAngle, float upperArmAngle, float headAngle, boolean goggles, boolean inverted, boolean hasItem, boolean isBlockItem, int light) {
        SuperByteBuffer base = CachedBuffers.partial((PartialModel)AllPartialModels.ARM_BASE, (BlockState)blockState).light(light);
        SuperByteBuffer lowerBody = CachedBuffers.partial((PartialModel)AllPartialModels.ARM_LOWER_BODY, (BlockState)blockState).light(light);
        SuperByteBuffer upperBody = CachedBuffers.partial((PartialModel)AllPartialModels.ARM_UPPER_BODY, (BlockState)blockState).light(light);
        SuperByteBuffer claw = CachedBuffers.partial((PartialModel)(goggles ? AllPartialModels.ARM_CLAW_BASE_GOGGLES : AllPartialModels.ARM_CLAW_BASE), (BlockState)blockState).light(light);
        SuperByteBuffer upperClawGrip = CachedBuffers.partial((PartialModel)AllPartialModels.ARM_CLAW_GRIP_UPPER, (BlockState)blockState).light(light);
        SuperByteBuffer lowerClawGrip = CachedBuffers.partial((PartialModel)AllPartialModels.ARM_CLAW_GRIP_LOWER, (BlockState)blockState).light(light);
        ArmRenderer.transformBase(msr, baseAngle);
        ((SuperByteBuffer)base.transform(msLocal)).renderInto(ms, builder);
        ArmRenderer.transformLowerArm(msr, lowerArmAngle);
        ((SuperByteBuffer)lowerBody.color(color).transform(msLocal)).renderInto(ms, builder);
        ArmRenderer.transformUpperArm(msr, upperArmAngle);
        ((SuperByteBuffer)upperBody.color(color).transform(msLocal)).renderInto(ms, builder);
        ArmRenderer.transformHead(msr, headAngle);
        if (inverted) {
            msr.rotateZDegrees(180.0f);
        }
        ((SuperByteBuffer)claw.transform(msLocal)).renderInto(ms, builder);
        if (inverted) {
            msr.rotateZDegrees(180.0f);
        }
        for (int flip : Iterate.positiveAndNegative) {
            msLocal.pushPose();
            ArmRenderer.transformClawHalf(msr, hasItem, isBlockItem, flip);
            ((SuperByteBuffer)(flip > 0 ? lowerClawGrip : upperClawGrip).transform(msLocal)).renderInto(ms, builder);
            msLocal.popPose();
        }
    }

    private void doItemTransforms(TransformStack msr, float baseAngle, float lowerArmAngle, float upperArmAngle, float headAngle) {
        ArmRenderer.transformBase(msr, baseAngle);
        ArmRenderer.transformLowerArm(msr, lowerArmAngle);
        ArmRenderer.transformUpperArm(msr, upperArmAngle);
        ArmRenderer.transformHead(msr, headAngle);
    }

    public static void transformClawHalf(TransformStack msr, boolean hasItem, boolean isBlockItem, int flip) {
        msr.translate(0.0, (double)((float)(-flip) * (hasItem ? (isBlockItem ? 0.1875f : 0.078125f) : 0.0625f)), -0.375);
    }

    public static void transformHead(TransformStack msr, float headAngle) {
        msr.translate(0.0, 0.0, -0.9375);
        msr.rotateXDegrees(headAngle - 45.0f);
    }

    public static void transformUpperArm(TransformStack msr, float upperArmAngle) {
        msr.translate(0.0, 0.0, -0.875);
        msr.rotateXDegrees(upperArmAngle - 90.0f);
    }

    public static void transformLowerArm(TransformStack msr, float lowerArmAngle) {
        msr.translate(0.0, 0.125, 0.0);
        msr.rotateXDegrees(lowerArmAngle + 135.0f);
    }

    public static void transformBase(TransformStack msr, float baseAngle) {
        msr.translate(0.0, 0.25, 0.0);
        msr.rotateYDegrees(baseAngle);
    }

    public boolean shouldRenderOffScreen(ArmBlockEntity be) {
        return true;
    }

    @Override
    protected SuperByteBuffer getRotatedModel(ArmBlockEntity be, BlockState state) {
        return CachedBuffers.partial((PartialModel)AllPartialModels.ARM_COG, (BlockState)state);
    }
}
