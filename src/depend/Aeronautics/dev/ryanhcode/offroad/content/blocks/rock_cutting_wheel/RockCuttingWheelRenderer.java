/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.content.contraptions.behaviour.MovementContext
 *  com.simibubi.create.content.contraptions.render.ContraptionMatrices
 *  com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
 *  com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.simulated_team.simulated.content.blocks.util.AbstractDirectionalAxisBlock
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Quaternionfc
 */
package dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel.RockCuttingWheelBlockEntity;
import dev.ryanhcode.offroad.index.OffroadPartialModels;
import dev.simulated_team.simulated.content.blocks.util.AbstractDirectionalAxisBlock;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Quaternionfc;

public class RockCuttingWheelRenderer
extends SafeBlockEntityRenderer<RockCuttingWheelBlockEntity> {
    public RockCuttingWheelRenderer(BlockEntityRendererProvider.Context context) {
    }

    private static void transformBuffer(Direction facing, boolean alongFirstCoords, SuperByteBuffer wheel) {
        if ((facing.getAxis() == Direction.Axis.Z || facing.getAxis() == Direction.Axis.Y) ^ alongFirstCoords) {
            ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)wheel.rotateCentered((Quaternionfc)facing.getRotation())).rotateZCenteredDegrees(90.0f)).rotateXCenteredDegrees(0.0f)).translate(0.625, 0.5, 0.0);
        } else {
            ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)wheel.rotateCentered((Quaternionfc)facing.getRotation())).rotateZCenteredDegrees(0.0f)).rotateXCenteredDegrees(90.0f)).translate(0.0, 0.5, -0.625);
        }
    }

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        BlockState state = context.state;
        Direction facing = (Direction)state.getValue((Property)BlockStateProperties.FACING);
        SuperByteBuffer wheel = CachedBuffers.partial((PartialModel)OffroadPartialModels.ROCK_CUTTING_WHEEL_WHEEL, (BlockState)state);
        wheel.transform(matrices.getModel());
        RockCuttingWheelRenderer.transformBuffer(facing, (Boolean)state.getValue((Property)AbstractDirectionalAxisBlock.AXIS_ALONG_FIRST_COORDINATE), wheel);
        wheel.rotateYCenteredDegrees(((LerpedFloat)context.temporaryData).getValue(AnimationTickHolder.getPartialTicks((LevelAccessor)context.world)));
        wheel.light(LevelRenderer.getLightColor((BlockAndTintGetter)renderWorld, (BlockPos)context.localPos)).useLevelLight((BlockAndTintGetter)context.world, matrices.getWorld()).renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.solid()));
    }

    protected void renderSafe(RockCuttingWheelBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState state = blockEntity.getBlockState();
        SuperByteBuffer wheel = CachedBuffers.partial((PartialModel)OffroadPartialModels.ROCK_CUTTING_WHEEL_WHEEL, (BlockState)state);
        ms.pushPose();
        RockCuttingWheelRenderer.transformBuffer((Direction)state.getValue((Property)BlockStateProperties.FACING), (Boolean)state.getValue((Property)AbstractDirectionalAxisBlock.AXIS_ALONG_FIRST_COORDINATE), wheel);
        if (blockEntity.isVirtual()) {
            wheel.rotateYCenteredDegrees(blockEntity.getAnimatedSpeed(partialTicks));
        }
        wheel.light(light).renderInto(ms, buffer.getBuffer(RenderType.solid()));
        ms.popPose();
    }
}
