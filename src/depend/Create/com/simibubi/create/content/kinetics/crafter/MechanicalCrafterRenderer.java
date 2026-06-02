/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.Pointing
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.crafter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlock;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.simibubi.create.content.kinetics.crafter.RecipeGridHandler;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MechanicalCrafterRenderer
extends SafeBlockEntityRenderer<MechanicalCrafterBlockEntity> {
    public MechanicalCrafterRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(MechanicalCrafterBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ms.pushPose();
        Direction facing = (Direction)be.getBlockState().getValue(HorizontalKineticBlock.HORIZONTAL_FACING);
        Vec3 vec = Vec3.atLowerCornerOf((Vec3i)facing.getNormal()).scale(0.58).add(0.5, 0.5, 0.5);
        if (be.phase == MechanicalCrafterBlockEntity.Phase.EXPORTING) {
            Direction targetDirection = MechanicalCrafterBlock.getTargetDirection(be.getBlockState());
            float progress = Mth.clamp((float)(((float)(1000 - be.countDown) + (float)be.getCountDownSpeed() * partialTicks) / 1000.0f), (float)0.0f, (float)1.0f);
            vec = vec.add(Vec3.atLowerCornerOf((Vec3i)targetDirection.getNormal()).scale((double)(progress * 0.75f)));
        }
        ms.translate(vec.x, vec.y, vec.z);
        ms.scale(0.5f, 0.5f, 0.5f);
        float yRot = AngleHelper.horizontalAngle((Direction)facing);
        ms.mulPose(Axis.YP.rotationDegrees(yRot));
        this.renderItems(be, partialTicks, ms, buffer, light, overlay);
        ms.popPose();
        this.renderFast(be, partialTicks, ms, buffer, light);
    }

    public void renderItems(MechanicalCrafterBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (be.phase == MechanicalCrafterBlockEntity.Phase.IDLE) {
            ItemStack stack2 = be.getInventory().getItem(0);
            if (!stack2.isEmpty()) {
                ms.pushPose();
                ms.translate(0.0f, 0.0f, -0.00390625f);
                ms.mulPose(Axis.YP.rotationDegrees(180.0f));
                Minecraft.getInstance().getItemRenderer().renderStatic(stack2, ItemDisplayContext.FIXED, light, overlay, ms, buffer, be.getLevel(), 0);
                ms.popPose();
            }
        } else {
            RecipeGridHandler.GroupedItems items = be.groupedItems;
            float distance = 0.5f;
            ms.pushPose();
            if (be.phase == MechanicalCrafterBlockEntity.Phase.CRAFTING) {
                items = be.groupedItemsBeforeCraft;
                items.calcStats();
                float progress = Mth.clamp((float)(((float)(2000 - be.countDown) + (float)be.getCountDownSpeed() * partialTicks) / 1000.0f), (float)0.0f, (float)1.0f);
                float earlyProgress = Mth.clamp((float)(progress * 2.0f), (float)0.0f, (float)1.0f);
                float lateProgress = Mth.clamp((float)(progress * 2.0f - 1.0f), (float)0.0f, (float)1.0f);
                ms.scale(1.0f - lateProgress, 1.0f - lateProgress, 1.0f - lateProgress);
                Vec3 centering = new Vec3((double)((float)(-items.minX) + (float)(-items.width + 1) / 2.0f), (double)((float)(-items.minY) + (float)(-items.height + 1) / 2.0f), 0.0).scale((double)earlyProgress);
                ms.translate(centering.x * 0.5, centering.y * 0.5, 0.0);
                distance += (-4.0f * (progress - 0.5f) * (progress - 0.5f) + 1.0f) * 0.25f;
            }
            boolean onlyRenderFirst = be.phase == MechanicalCrafterBlockEntity.Phase.INSERTING || be.phase == MechanicalCrafterBlockEntity.Phase.CRAFTING && be.countDown < 1000;
            float spacing = distance;
            items.grid.forEach((pair, stack) -> {
                if (onlyRenderFirst && ((Integer)pair.getLeft() != 0 || (Integer)pair.getRight() != 0)) {
                    return;
                }
                ms.pushPose();
                Integer x = (Integer)pair.getKey();
                Integer y = (Integer)pair.getValue();
                ms.translate((float)x.intValue() * spacing, (float)y.intValue() * spacing, 0.0f);
                int offset = 0;
                if (be.phase == MechanicalCrafterBlockEntity.Phase.EXPORTING && be.getBlockState().hasProperty(MechanicalCrafterBlock.POINTING)) {
                    Pointing value = (Pointing)be.getBlockState().getValue(MechanicalCrafterBlock.POINTING);
                    offset = value == Pointing.UP ? -1 : (value == Pointing.LEFT ? 2 : (value == Pointing.RIGHT ? -2 : 1));
                }
                ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(180.0f)).translate(0.0f, 0.0f, (float)(x + y * 3 + offset * 9) / 1024.0f);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, ms, buffer, be.getLevel(), 0);
                ms.popPose();
            });
            ms.popPose();
            if (be.phase == MechanicalCrafterBlockEntity.Phase.CRAFTING) {
                items = be.groupedItems;
                float progress = Mth.clamp((float)(((float)(1000 - be.countDown) + (float)be.getCountDownSpeed() * partialTicks) / 1000.0f), (float)0.0f, (float)1.0f);
                float earlyProgress = Mth.clamp((float)(progress * 2.0f), (float)0.0f, (float)1.0f);
                float lateProgress = Mth.clamp((float)(progress * 2.0f - 1.0f), (float)0.0f, (float)1.0f);
                ms.mulPose(Axis.ZP.rotationDegrees(earlyProgress * 2.0f * 360.0f));
                float upScaling = earlyProgress * 1.125f;
                float downScaling = 1.0f + (1.0f - lateProgress) * 0.125f;
                ms.scale(upScaling, upScaling, upScaling);
                ms.scale(downScaling, downScaling, downScaling);
                items.grid.forEach((pair, stack) -> {
                    if ((Integer)pair.getLeft() != 0 || (Integer)pair.getRight() != 0) {
                        return;
                    }
                    ms.pushPose();
                    ms.mulPose(Axis.YP.rotationDegrees(180.0f));
                    Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, ms, buffer, be.getLevel(), 0);
                    ms.popPose();
                });
            }
        }
    }

    public void renderFast(MechanicalCrafterBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light) {
        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        if (!VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            SuperByteBuffer superBuffer = CachedBuffers.partial((PartialModel)AllPartialModels.SHAFTLESS_COGWHEEL, (BlockState)blockState);
            KineticBlockEntityRenderer.standardKineticRotationTransform(superBuffer, be, light);
            superBuffer.rotateCentered((float)(((Direction)blockState.getValue(HorizontalKineticBlock.HORIZONTAL_FACING)).getAxis() != Direction.Axis.X ? 0.0 : 1.5707963267948966), Direction.UP);
            superBuffer.rotateCentered(1.5707964f, Direction.EAST);
            superBuffer.renderInto(ms, vb);
        }
        Direction targetDirection = MechanicalCrafterBlock.getTargetDirection(blockState);
        BlockPos pos = be.getBlockPos();
        if ((be.covered || be.phase != MechanicalCrafterBlockEntity.Phase.IDLE) && be.phase != MechanicalCrafterBlockEntity.Phase.CRAFTING && be.phase != MechanicalCrafterBlockEntity.Phase.INSERTING) {
            SuperByteBuffer lidBuffer = this.renderAndTransform(AllPartialModels.MECHANICAL_CRAFTER_LID, blockState);
            lidBuffer.light(light).renderInto(ms, vb);
        }
        if (MechanicalCrafterBlock.isValidTarget(be.getLevel(), pos.relative(targetDirection), blockState)) {
            SuperByteBuffer beltBuffer = this.renderAndTransform(AllPartialModels.MECHANICAL_CRAFTER_BELT, blockState);
            SuperByteBuffer beltFrameBuffer = this.renderAndTransform(AllPartialModels.MECHANICAL_CRAFTER_BELT_FRAME, blockState);
            if (be.phase == MechanicalCrafterBlockEntity.Phase.EXPORTING) {
                int textureIndex = (int)((float)be.getCountDownSpeed() / 128.0f * (float)AnimationTickHolder.getTicks());
                beltBuffer.shiftUVtoSheet(AllSpriteShifts.CRAFTER_THINGIES, (float)(textureIndex % 4) / 4.0f, 0.0f, 1);
            }
            beltBuffer.light(light).renderInto(ms, vb);
            beltFrameBuffer.light(light).renderInto(ms, vb);
        } else {
            SuperByteBuffer arrowBuffer = this.renderAndTransform(AllPartialModels.MECHANICAL_CRAFTER_ARROW, blockState);
            arrowBuffer.light(light).renderInto(ms, vb);
        }
    }

    private SuperByteBuffer renderAndTransform(PartialModel renderBlock, BlockState crafterState) {
        SuperByteBuffer buffer = CachedBuffers.partial((PartialModel)renderBlock, (BlockState)crafterState);
        float xRot = ((Pointing)crafterState.getValue(MechanicalCrafterBlock.POINTING)).getXRotation();
        float yRot = AngleHelper.horizontalAngle((Direction)((Direction)crafterState.getValue(HorizontalKineticBlock.HORIZONTAL_FACING)));
        buffer.rotateCentered((float)((double)((yRot + 90.0f) / 180.0f) * Math.PI), Direction.UP);
        buffer.rotateCentered((float)((double)(xRot / 180.0f) * Math.PI), Direction.EAST);
        return buffer;
    }
}
