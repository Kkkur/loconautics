/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.content.redstone.analogLever.AnalogLeverBlock
 *  com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.simulated_team.simulated.content.blocks.throttle_lever;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlock;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlock;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlockEntity;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.mixin.accessor.LevelRendererAccessor;
import dev.simulated_team.simulated.util.SimColors;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ThrottleLeverRenderer
extends SafeBlockEntityRenderer<ThrottleLeverBlockEntity> {
    protected static final double ANGLE_LIMIT = 40.0;

    public ThrottleLeverRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static void transformHandleExternal(ThrottleLeverBlockEntity blockEntity, float partialTicks, PoseStack ms) {
        float state = blockEntity.clientAngle.getValue(partialTicks);
        AttachFace face = (AttachFace)blockEntity.getBlockState().getValue((Property)FaceAttachedHorizontalDirectionalBlock.FACE);
        float angle = (float)(((double)(state / 15.0f) * 80.0 - 40.0) / 180.0 * Math.PI);
        if (face == AttachFace.WALL) {
            angle = -angle;
        }
        PoseTransformStack stack = TransformStack.of((PoseStack)ms);
        ThrottleLeverRenderer.transform(stack, blockEntity.getBlockState());
        ((PoseTransformStack)((PoseTransformStack)stack.translate(0.5, 0.1875, 0.5)).rotateX(angle)).translateBack(0.5, 0.1875, 0.5);
    }

    protected void renderSafe(ThrottleLeverBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        BlockHitResult hitResult;
        HitResult hitResult2;
        BlockState leverState = be.getBlockState();
        float state = be.clientAngle.getValue(partialTicks);
        AttachFace face = (AttachFace)be.getBlockState().getValue((Property)FaceAttachedHorizontalDirectionalBlock.FACE);
        float angle = (float)(((double)(state / 15.0f) * 80.0 - 40.0) / 180.0 * Math.PI);
        if (face == AttachFace.WALL) {
            angle = -angle;
        }
        if (!VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            VertexConsumer vb = bufferSource.getBuffer(RenderType.cutoutMipped());
            SuperByteBuffer handle = CachedBuffers.partial((PartialModel)SimPartialModels.THROTTLE_LEVER_HANDLE, (BlockState)leverState);
            SuperByteBuffer button = CachedBuffers.partial((PartialModel)SimPartialModels.THROTTLE_LEVER_BUTTON, (BlockState)leverState);
            float signalStrength = Math.max(0.0f, (float)be.state / 15.0f);
            SuperByteBuffer diode = CachedBuffers.partial((PartialModel)SimPartialModels.THROTTLE_LEVER_DIODE, (BlockState)leverState);
            int color = SimColors.redstone(signalStrength);
            double buttonAngle = be.clientPressedLerp.getValue(partialTicks) * -7.0f;
            ThrottleLeverRenderer.transform(handle, leverState);
            ThrottleLeverRenderer.transform(button, leverState);
            ThrottleLeverRenderer.transform(diode, leverState);
            this.transformHandleExternal((TransformStack)handle, angle, face);
            handle.light(light).renderInto(ms, vb);
            ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)this.transformHandleExternal((TransformStack)button, angle, face).translate(0.0f, 0.875f, 0.5f)).rotateXDegrees((float)buttonAngle)).translateBack(0.0f, 0.875f, 0.5f)).light(light).renderInto(ms, vb);
            diode.light(light).color(color).renderInto(ms, vb);
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!be.isVirtual() && (hitResult2 = minecraft.hitResult) instanceof BlockHitResult && (hitResult = (BlockHitResult)hitResult2).getBlockPos().equals((Object)be.getBlockPos())) {
            ThrottleLeverRenderer.renderOutline(be, ms, bufferSource, angle);
        }
    }

    private static void renderOutline(ThrottleLeverBlockEntity be, PoseStack ms, MultiBufferSource bufferSource, float angle) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        VoxelShape leverShape = ((ThrottleLeverBlock)SimBlocks.THROTTLE_LEVER.get()).getHandleShape(SimBlocks.THROTTLE_LEVER.getDefaultState());
        ms.pushPose();
        PoseTransformStack stack = TransformStack.of((PoseStack)ms);
        ThrottleLeverRenderer.transform(stack, be.getBlockState());
        ((PoseTransformStack)((PoseTransformStack)stack.translate(0.5, 0.1875, 0.5)).rotateX(angle)).translateBack(0.5, 0.1875, 0.5);
        LevelRendererAccessor.invokeRenderShape(ms, consumer, leverShape, 0.0, 0.0, 0.0, 0.0f, 0.0f, 0.0f, 0.4f);
        ms.popPose();
    }

    private <T extends TransformStack<T>> TransformStack<T> transformHandleExternal(TransformStack<T> buffer, float angle, AttachFace face) {
        return (TransformStack)((TransformStack)((TransformStack)((TransformStack)buffer.translate(0.5f, 0.1875f, 0.5f)).rotateX(angle)).translateBack(0.5f, 0.1875f, 0.5f)).rotateCentered(face == AttachFace.WALL ? (float)Math.PI : 0.0f, Direction.UP);
    }

    private static <T extends TransformStack<T>> TransformStack<T> transform(TransformStack<T> buffer, BlockState leverState) {
        AttachFace attached = (AttachFace)leverState.getValue((Property)AnalogLeverBlock.FACE);
        Direction facing = (Direction)leverState.getValue((Property)AnalogLeverBlock.FACING);
        float rX = switch (attached) {
            case AttachFace.FLOOR -> 0.0f;
            case AttachFace.WALL -> 90.0f;
            default -> 180.0f;
        };
        float rY = AngleHelper.horizontalAngle((Direction)facing);
        buffer.rotateCentered((float)((double)(rY / 180.0f) * Math.PI), Direction.UP);
        buffer.rotateCentered((float)((double)(rX / 180.0f) * Math.PI), Direction.EAST);
        buffer.rotateCentered(attached == AttachFace.CEILING ? (float)Math.PI : 0.0f, Direction.UP);
        return buffer;
    }
}
