/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
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
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.contraptions.actors.harvester;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlock;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlockEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
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
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class HarvesterRenderer
extends SafeBlockEntityRenderer<HarvesterBlockEntity> {
    private static final Vec3 PIVOT = new Vec3(0.0, 6.0, 9.0);

    public HarvesterRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(HarvesterBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        SuperByteBuffer superBuffer = CachedBuffers.partial((PartialModel)AllPartialModels.HARVESTER_BLADE, (BlockState)blockState);
        HarvesterRenderer.transform(be.getLevel(), (Direction)blockState.getValue((Property)HarvesterBlock.FACING), superBuffer, be.getAnimatedSpeed(), PIVOT);
        superBuffer.light(light).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
    }

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffers) {
        float speed;
        BlockState blockState = context.state;
        Direction facing = (Direction)blockState.getValue((Property)BlockStateProperties.HORIZONTAL_FACING);
        SuperByteBuffer superBuffer = CachedBuffers.partial((PartialModel)AllPartialModels.HARVESTER_BLADE, (BlockState)blockState);
        float f = speed = !VecHelper.isVecPointingTowards((Vec3)context.relativeMotion, (Direction)facing.getOpposite()) ? context.getAnimationSpeed() : 0.0f;
        if (context.contraption.stalled) {
            speed = 0.0f;
        }
        superBuffer.transform(matrices.getModel());
        HarvesterRenderer.transform(context.world, facing, superBuffer, speed, PIVOT);
        superBuffer.light(LevelRenderer.getLightColor((BlockAndTintGetter)renderWorld, (BlockPos)context.localPos)).useLevelLight((BlockAndTintGetter)context.world, matrices.getWorld()).renderInto(matrices.getViewProjection(), buffers.getBuffer(RenderType.cutoutMipped()));
    }

    public static void transform(Level world, Direction facing, SuperByteBuffer superBuffer, float speed, Vec3 pivot) {
        float originOffset = 0.0625f;
        Vec3 rotOffset = new Vec3(0.0, pivot.y * (double)originOffset, pivot.z * (double)originOffset);
        float time = AnimationTickHolder.getRenderTime((LevelAccessor)world) / 20.0f;
        float angle = time * speed % 360.0f;
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)superBuffer.rotateCentered(AngleHelper.rad((double)AngleHelper.horizontalAngle((Direction)facing)), Direction.UP)).translate(rotOffset.x, rotOffset.y, rotOffset.z)).rotate(AngleHelper.rad((double)angle), Direction.WEST)).translate(-rotOffset.x, -rotOffset.y, -rotOffset.z);
    }
}
