/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.DoorBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.DoorHingeSide
 *  net.minecraft.world.level.block.state.properties.DoubleBlockHalf
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.decoration.slidingDoor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class SlidingDoorRenderer
extends SafeBlockEntityRenderer<SlidingDoorBlockEntity> {
    public SlidingDoorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(SlidingDoorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        if (!be.shouldRenderSpecial(blockState)) {
            return;
        }
        Direction facing = (Direction)blockState.getValue((Property)DoorBlock.FACING);
        Direction movementDirection = facing.getClockWise();
        if (blockState.getValue((Property)DoorBlock.HINGE) == DoorHingeSide.LEFT) {
            movementDirection = movementDirection.getOpposite();
        }
        float value = be.animation.getValue(partialTicks);
        float value2 = Mth.clamp((float)(value * 10.0f), (float)0.0f, (float)1.0f);
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());
        Vec3 offset = Vec3.atLowerCornerOf((Vec3i)movementDirection.getNormal()).scale((double)(value * value * 13.0f / 16.0f)).add(Vec3.atLowerCornerOf((Vec3i)facing.getNormal()).scale((double)(value2 * 1.0f / 32.0f)));
        if (((SlidingDoorBlock)blockState.getBlock()).isFoldingDoor()) {
            Couple<PartialModel> partials = AllPartialModels.FOLDING_DOORS.get(BuiltInRegistries.BLOCK.getKey((Object)blockState.getBlock()));
            boolean flip = blockState.getValue((Property)DoorBlock.HINGE) == DoorHingeSide.RIGHT;
            for (boolean left : Iterate.trueAndFalse) {
                SuperByteBuffer partial = CachedBuffers.partial((PartialModel)((PartialModel)partials.get(left ^ flip)), (BlockState)blockState);
                float f = flip ? -1.0f : 1.0f;
                ((SuperByteBuffer)partial.translate(0.0f, -0.001953125f, 0.0f)).translate(Vec3.atLowerCornerOf((Vec3i)facing.getNormal()).scale((double)(value2 * 1.0f / 32.0f)));
                partial.rotateCentered((float)Math.PI / 180 * AngleHelper.horizontalAngle((Direction)facing.getClockWise()), Direction.UP);
                if (flip) {
                    partial.translate(0.0f, 0.0f, 1.0f);
                }
                partial.rotateYDegrees(91.0f * f * value * value);
                if (!left) {
                    ((SuperByteBuffer)partial.translate(0.0f, 0.0f, f / 2.0f)).rotateYDegrees(-181.0f * f * value * value);
                }
                if (flip) {
                    partial.translate(0.0f, 0.0f, -0.5f);
                }
                partial.light(light).renderInto(ms, vb);
            }
            return;
        }
        for (DoubleBlockHalf half : DoubleBlockHalf.values()) {
            ((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.block((BlockState)((BlockState)((BlockState)blockState.setValue((Property)DoorBlock.OPEN, (Comparable)Boolean.valueOf(false))).setValue((Property)DoorBlock.HALF, (Comparable)half))).translate(0.0f, half == DoubleBlockHalf.UPPER ? 0.9980469f : 0.0f, 0.0f)).translate(offset)).light(light).renderInto(ms, vb);
        }
    }
}
