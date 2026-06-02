/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.saw;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class SawRenderer
extends SafeBlockEntityRenderer<SawBlockEntity> {
    public SawRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(SawBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        this.renderBlade(be, ms, buffer, light);
        this.renderItems(be, partialTicks, ms, buffer, light, overlay);
        FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        this.renderShaft(be, ms, buffer, light, overlay);
    }

    protected void renderBlade(SawBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light) {
        PartialModel partial;
        BlockState blockState = be.getBlockState();
        float speed = be.getSpeed();
        boolean rotate = false;
        if (SawBlock.isHorizontal(blockState)) {
            partial = speed > 0.0f ? AllPartialModels.SAW_BLADE_HORIZONTAL_ACTIVE : (speed < 0.0f ? AllPartialModels.SAW_BLADE_HORIZONTAL_REVERSED : AllPartialModels.SAW_BLADE_HORIZONTAL_INACTIVE);
        } else {
            partial = be.getSpeed() > 0.0f ? AllPartialModels.SAW_BLADE_VERTICAL_ACTIVE : (speed < 0.0f ? AllPartialModels.SAW_BLADE_VERTICAL_REVERSED : AllPartialModels.SAW_BLADE_VERTICAL_INACTIVE);
            if (((Boolean)blockState.getValue((Property)SawBlock.AXIS_ALONG_FIRST_COORDINATE)).booleanValue()) {
                rotate = true;
            }
        }
        SuperByteBuffer superBuffer = CachedBuffers.partialFacing((PartialModel)partial, (BlockState)blockState);
        if (rotate) {
            superBuffer.rotateCentered(AngleHelper.rad((double)90.0), Direction.UP);
        }
        superBuffer.color(0xFFFFFF).light(light).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
    }

    protected void renderShaft(SawBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        KineticBlockEntityRenderer.renderRotatingBuffer(be, this.getRotatedModel(be), ms, buffer.getBuffer(RenderType.solid()), light);
    }

    protected void renderItems(SawBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (be.getBlockState().getValue((Property)SawBlock.FACING) != Direction.UP) {
            return;
        }
        if (be.inventory.isEmpty()) {
            return;
        }
        boolean alongZ = (Boolean)be.getBlockState().getValue((Property)SawBlock.AXIS_ALONG_FIRST_COORDINATE) == false;
        float duration = be.inventory.recipeDuration;
        boolean moving = duration != 0.0f;
        float offset = moving ? be.inventory.remainingTime / duration : 0.0f;
        float processingSpeed = Mth.clamp((float)(Math.abs(be.getSpeed()) / 32.0f), (float)1.0f, (float)128.0f);
        if (moving) {
            offset = Mth.clamp((float)(offset + (-partialTicks + 0.5f) * processingSpeed / duration), (float)0.125f, (float)1.0f);
            if (!be.inventory.appliedRecipe) {
                offset += 1.0f;
            }
            offset /= 2.0f;
        }
        if (be.getSpeed() == 0.0f) {
            offset = 0.5f;
        }
        if (be.getSpeed() < 0.0f ^ alongZ) {
            offset = 1.0f - offset;
        }
        int outputs = 0;
        for (int i = 1; i < be.inventory.getSlots(); ++i) {
            if (be.inventory.getStackInSlot(i).isEmpty()) continue;
            ++outputs;
        }
        ms.pushPose();
        if (alongZ) {
            ms.mulPose(Axis.YP.rotationDegrees(90.0f));
        }
        ms.translate(outputs <= 1 ? 0.5 : 0.25, 0.0, (double)offset);
        ms.translate(alongZ ? -1.0f : 0.0f, 0.0f, 0.0f);
        int renderedI = 0;
        for (int i = 0; i < be.inventory.getSlots(); ++i) {
            boolean box;
            ItemStack stack = be.inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            BakedModel modelWithOverrides = itemRenderer.getModel(stack, be.getLevel(), null, 0);
            boolean blockItem = modelWithOverrides.isGui3d();
            ms.pushPose();
            ms.translate(0.0f, blockItem ? 0.925f : 0.8125f, 0.0f);
            if (i > 0 && outputs > 1) {
                ms.translate(0.5 / (double)(outputs - 1) * (double)renderedI, 0.0, 0.0);
                TransformStack.of((PoseStack)ms).nudge(i * 133);
            }
            if (box = PackageItem.isPackage(stack)) {
                ms.translate(0.0f, 0.25f, 0.0f);
                ms.scale(1.5f, 1.5f, 1.5f);
            } else {
                ms.scale(0.5f, 0.5f, 0.5f);
            }
            if (!box) {
                ms.mulPose(Axis.XP.rotationDegrees(90.0f));
            }
            itemRenderer.render(stack, ItemDisplayContext.FIXED, false, ms, buffer, light, overlay, modelWithOverrides);
            ++renderedI;
            ms.popPose();
        }
        ms.popPose();
    }

    protected SuperByteBuffer getRotatedModel(KineticBlockEntity be) {
        BlockState state = be.getBlockState();
        if (((Direction)state.getValue((Property)BlockStateProperties.FACING)).getAxis().isHorizontal()) {
            return CachedBuffers.partialFacing((PartialModel)AllPartialModels.SHAFT_HALF, (BlockState)state.rotate((LevelAccessor)be.getLevel(), be.getBlockPos(), Rotation.CLOCKWISE_180));
        }
        return CachedBuffers.block(KineticBlockEntityRenderer.KINETIC_BLOCK, (BlockState)this.getRenderedBlockState(be));
    }

    protected BlockState getRenderedBlockState(KineticBlockEntity be) {
        return KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(be));
    }

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        boolean shouldAnimate;
        BlockState state = context.state;
        Direction facing = (Direction)state.getValue((Property)SawBlock.FACING);
        Vec3 facingVec = Vec3.atLowerCornerOf((Vec3i)((Direction)context.state.getValue((Property)SawBlock.FACING)).getNormal());
        facingVec = (Vec3)context.rotation.apply(facingVec);
        Direction closestToFacing = Direction.getNearest((double)facingVec.x, (double)facingVec.y, (double)facingVec.z);
        boolean horizontal = closestToFacing.getAxis().isHorizontal();
        boolean backwards = VecHelper.isVecPointingTowards((Vec3)context.relativeMotion, (Direction)facing.getOpposite());
        boolean moving = context.getAnimationSpeed() != 0.0f;
        boolean bl = shouldAnimate = context.contraption.stalled && horizontal || !context.contraption.stalled && !backwards && moving;
        SuperByteBuffer superBuffer = SawBlock.isHorizontal(state) ? (shouldAnimate ? CachedBuffers.partial((PartialModel)AllPartialModels.SAW_BLADE_HORIZONTAL_ACTIVE, (BlockState)state) : CachedBuffers.partial((PartialModel)AllPartialModels.SAW_BLADE_HORIZONTAL_INACTIVE, (BlockState)state)) : (shouldAnimate ? CachedBuffers.partial((PartialModel)AllPartialModels.SAW_BLADE_VERTICAL_ACTIVE, (BlockState)state) : CachedBuffers.partial((PartialModel)AllPartialModels.SAW_BLADE_VERTICAL_INACTIVE, (BlockState)state));
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)superBuffer.transform(matrices.getModel())).center()).rotateYDegrees(AngleHelper.horizontalAngle((Direction)facing))).rotateXDegrees(AngleHelper.verticalAngle((Direction)facing));
        if (!SawBlock.isHorizontal(state)) {
            superBuffer.rotateZDegrees((Boolean)state.getValue((Property)SawBlock.AXIS_ALONG_FIRST_COORDINATE) != false ? 90.0f : 0.0f);
        }
        ((SuperByteBuffer)superBuffer.uncenter()).light(LevelRenderer.getLightColor((BlockAndTintGetter)renderWorld, (BlockPos)context.localPos)).useLevelLight((BlockAndTintGetter)context.world, matrices.getWorld()).renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.cutoutMipped()));
    }
}
