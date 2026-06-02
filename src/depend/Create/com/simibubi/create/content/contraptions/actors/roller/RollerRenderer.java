/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.actors.roller;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterRenderer;
import com.simibubi.create.content.contraptions.actors.roller.RollerBlock;
import com.simibubi.create.content.contraptions.actors.roller.RollerBlockEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class RollerRenderer
extends SmartBlockEntityRenderer<RollerBlockEntity> {
    public RollerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(RollerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        BlockState blockState = be.getBlockState();
        VertexConsumer vc = buffer.getBuffer(RenderType.cutoutMipped());
        ms.pushPose();
        ms.translate(0.0, -0.25, 0.0);
        SuperByteBuffer superBuffer = CachedBuffers.partial((PartialModel)AllPartialModels.ROLLER_WHEEL, (BlockState)blockState);
        Direction facing = (Direction)blockState.getValue((Property)RollerBlock.FACING);
        superBuffer.translate(Vec3.atLowerCornerOf((Vec3i)facing.getNormal()).scale(1.0625));
        HarvesterRenderer.transform(be.getLevel(), facing, superBuffer, be.getAnimatedSpeed(), Vec3.ZERO);
        ((SuperByteBuffer)((SuperByteBuffer)superBuffer.translate(0.0, -0.5, 0.5)).rotateYDegrees(90.0f)).light(light).renderInto(ms, vc);
        ms.popPose();
        ((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.ROLLER_FRAME, (BlockState)blockState).rotateCentered(AngleHelper.rad((double)(AngleHelper.horizontalAngle((Direction)facing) + 180.0f)), Direction.UP)).light(light).renderInto(ms, vc);
    }

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffers) {
        float speed;
        BlockState blockState = context.state;
        Direction facing = (Direction)blockState.getValue((Property)BlockStateProperties.HORIZONTAL_FACING);
        VertexConsumer vc = buffers.getBuffer(RenderType.cutoutMipped());
        SuperByteBuffer superBuffer = CachedBuffers.partial((PartialModel)AllPartialModels.ROLLER_WHEEL, (BlockState)blockState);
        float f = speed = !VecHelper.isVecPointingTowards((Vec3)context.relativeMotion, (Direction)facing.getOpposite()) ? context.getAnimationSpeed() : -context.getAnimationSpeed();
        if (context.contraption.stalled) {
            speed = 0.0f;
        }
        ((SuperByteBuffer)superBuffer.transform(matrices.getModel())).translate(Vec3.atLowerCornerOf((Vec3i)facing.getNormal()).scale(1.0625));
        HarvesterRenderer.transform(context.world, facing, superBuffer, speed, Vec3.ZERO);
        PoseStack viewProjection = matrices.getViewProjection();
        viewProjection.pushPose();
        viewProjection.translate(0.0, -0.25, 0.0);
        int contraptionWorldLight = LevelRenderer.getLightColor((BlockAndTintGetter)renderWorld, (BlockPos)context.localPos);
        ((SuperByteBuffer)((SuperByteBuffer)superBuffer.translate(0.0, -0.5, 0.5)).rotateYDegrees(90.0f)).light(contraptionWorldLight).useLevelLight((BlockAndTintGetter)context.world, matrices.getWorld()).renderInto(viewProjection, vc);
        viewProjection.popPose();
        ((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.ROLLER_FRAME, (BlockState)blockState).transform(matrices.getModel())).rotateCentered(AngleHelper.rad((double)(AngleHelper.horizontalAngle((Direction)facing) + 180.0f)), Direction.UP)).light(contraptionWorldLight).useLevelLight((BlockAndTintGetter)context.world, matrices.getWorld()).renderInto(viewProjection, vc);
    }
}
