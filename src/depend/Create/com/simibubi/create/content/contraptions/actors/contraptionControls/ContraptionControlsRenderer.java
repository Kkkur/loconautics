/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.actors.contraptionControls;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlock;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsMovement;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.utility.DyeHelper;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class ContraptionControlsRenderer
extends SmartBlockEntityRenderer<ContraptionControlsBlockEntity> {
    public ContraptionControlsRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(ContraptionControlsBlockEntity blockEntity, float pt, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = blockEntity.getBlockState();
        Direction facing = ((Direction)blockState.getValue((Property)ContraptionControlsBlock.FACING)).getOpposite();
        Vec3 buttonMovementAxis = VecHelper.rotate((Vec3)new Vec3(0.0, 1.0, -0.325), (double)AngleHelper.horizontalAngle((Direction)facing), (Direction.Axis)Direction.Axis.Y);
        Vec3 buttonMovement = buttonMovementAxis.scale((double)(-0.07f + -0.041666668f * blockEntity.button.getValue(pt)));
        Vec3 buttonOffset = buttonMovementAxis.scale((double)0.07f);
        ms.pushPose();
        ms.translate(buttonMovement.x, buttonMovement.y, buttonMovement.z);
        super.renderSafe(blockEntity, pt, ms, buffer, light, overlay);
        ms.translate(buttonOffset.x, buttonOffset.y, buttonOffset.z);
        VertexConsumer vc = buffer.getBuffer(RenderType.solid());
        CachedBuffers.partialFacing((PartialModel)AllPartialModels.CONTRAPTION_CONTROLS_BUTTON, (BlockState)blockState, (Direction)facing).light(light).renderInto(ms, vc);
        ms.popPose();
        int i = (int)blockEntity.indicator.getValue(pt) / 45 % 8 + 8;
        CachedBuffers.partialFacing((PartialModel)AllPartialModels.CONTRAPTION_CONTROLS_INDICATOR.get(i % 8), (BlockState)blockState, (Direction)facing).light(light).renderInto(ms, vc);
    }

    public static void renderInContraption(MovementContext ctx, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        float heightCentering;
        float scale;
        int width;
        Object object = ctx.temporaryData;
        if (!(object instanceof ContraptionControlsMovement.ElevatorFloorSelection)) {
            return;
        }
        ContraptionControlsMovement.ElevatorFloorSelection efs = (ContraptionControlsMovement.ElevatorFloorSelection)object;
        if (!AllBlocks.CONTRAPTION_CONTROLS.has(ctx.state)) {
            return;
        }
        Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
        float playerDistance = (float)(ctx.position == null || cameraEntity == null ? 0.0 : ctx.position.distanceToSqr(cameraEntity.getEyePosition()));
        float flicker = renderWorld.random.nextFloat();
        Couple<Integer> couple = DyeHelper.getDyeColors(efs.targetYEqualsSelection ? DyeColor.WHITE : DyeColor.ORANGE);
        int brightColor = (Integer)couple.getFirst();
        int darkColor = (Integer)couple.getSecond();
        int flickeringBrightColor = Color.mixColors((int)brightColor, (int)darkColor, (float)(flicker / 4.0f));
        Font fontRenderer = Minecraft.getInstance().font;
        float shadowOffset = 0.5f;
        String text = efs.currentShortName;
        String description = efs.currentLongName;
        PoseStack ms = matrices.getViewProjection();
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        float buttondepth = 0.0f;
        BlockEntity blockEntity = ctx.contraption.getBlockEntityClientSide(ctx.localPos);
        if (blockEntity instanceof ContraptionControlsBlockEntity) {
            ContraptionControlsBlockEntity cbe = (ContraptionControlsBlockEntity)blockEntity;
            buttondepth = -0.041666668f * cbe.button.getValue(AnimationTickHolder.getPartialTicks((LevelAccessor)renderWorld));
        }
        ms.pushPose();
        msr.translate((Vec3i)ctx.localPos);
        ms.translate(0.0f, buttondepth, 0.0f);
        VertexConsumer vc = buffer.getBuffer(RenderType.solid());
        CachedBuffers.partialFacing((PartialModel)AllPartialModels.CONTRAPTION_CONTROLS_BUTTON, (BlockState)ctx.state, (Direction)((Direction)ctx.state.getValue((Property)ContraptionControlsBlock.FACING)).getOpposite()).light(LevelRenderer.getLightColor((BlockAndTintGetter)renderWorld, (BlockPos)ctx.localPos)).useLevelLight((BlockAndTintGetter)ctx.world, matrices.getWorld()).renderInto(ms, vc);
        ms.popPose();
        ms.pushPose();
        msr.translate((Vec3i)ctx.localPos);
        msr.rotateCentered(AngleHelper.rad((double)AngleHelper.horizontalAngle((Direction)((Direction)ctx.state.getValue((Property)ContraptionControlsBlock.FACING)))), Direction.UP);
        ms.translate(0.4f, 1.125f, 0.5f);
        msr.rotate(AngleHelper.rad((double)67.5), Direction.WEST);
        if (!text.isBlank() && playerDistance < 100.0f) {
            int actualWidth = fontRenderer.width(text);
            width = Math.max(actualWidth, 12);
            scale = 1.0f / (5.0f * ((float)width - 0.5f));
            heightCentering = ((float)width - 8.0f) / 2.0f;
            ms.pushPose();
            ms.translate(0.0f, 0.15f, buttondepth - 0.25f);
            ms.scale(scale, -scale, scale);
            ms.translate((float)Math.max(0, width - actualWidth) / 2.0f, heightCentering, 0.0f);
            NixieTubeRenderer.drawInWorldString(ms, buffer, text, flickeringBrightColor);
            ms.translate(shadowOffset, shadowOffset, -0.0625f);
            NixieTubeRenderer.drawInWorldString(ms, buffer, text, Color.mixColors((int)darkColor, (int)0, (float)0.35f));
            ms.popPose();
        }
        if (!description.isBlank() && playerDistance < 20.0f) {
            int actualWidth = fontRenderer.width(description);
            width = Math.max(actualWidth, 55);
            scale = 1.0f / (3.0f * ((float)width - 0.5f));
            heightCentering = ((float)width - 8.0f) / 2.0f;
            ms.pushPose();
            ms.translate(-0.0635f, 0.06f, buttondepth - 0.25f);
            ms.scale(scale, -scale, scale);
            ms.translate((float)Math.max(0, width - actualWidth) / 2.0f, heightCentering, 0.0f);
            NixieTubeRenderer.drawInWorldString(ms, buffer, description, flickeringBrightColor);
            ms.popPose();
        }
        ms.popPose();
    }
}
