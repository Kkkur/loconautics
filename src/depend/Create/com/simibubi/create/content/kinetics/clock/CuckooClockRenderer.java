/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.clock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.clock.CuckooClockBlock;
import com.simibubi.create.content.kinetics.clock.CuckooClockBlockEntity;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class CuckooClockRenderer
extends KineticBlockEntityRenderer<CuckooClockBlockEntity> {
    public CuckooClockRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(CuckooClockBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        if (!(be instanceof CuckooClockBlockEntity)) {
            return;
        }
        BlockState blockState = be.getBlockState();
        Direction direction = (Direction)blockState.getValue(CuckooClockBlock.HORIZONTAL_FACING);
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        SuperByteBuffer hourHand = CachedBuffers.partial((PartialModel)AllPartialModels.CUCKOO_HOUR_HAND, (BlockState)blockState);
        SuperByteBuffer minuteHand = CachedBuffers.partial((PartialModel)AllPartialModels.CUCKOO_MINUTE_HAND, (BlockState)blockState);
        float hourAngle = be.hourHand.getValue(partialTicks);
        float minuteAngle = be.minuteHand.getValue(partialTicks);
        this.rotateHand(hourHand, hourAngle, direction).light(light).renderInto(ms, vb);
        this.rotateHand(minuteHand, minuteAngle, direction).light(light).renderInto(ms, vb);
        SuperByteBuffer leftDoor = CachedBuffers.partial((PartialModel)AllPartialModels.CUCKOO_LEFT_DOOR, (BlockState)blockState);
        SuperByteBuffer rightDoor = CachedBuffers.partial((PartialModel)AllPartialModels.CUCKOO_RIGHT_DOOR, (BlockState)blockState);
        float angle = 0.0f;
        float offset = 0.0f;
        if (be.animationType != null) {
            float value = be.animationProgress.getValue(partialTicks);
            int step = be.animationType == CuckooClockBlockEntity.Animation.SURPRISE ? 3 : 15;
            for (int phase = 30; phase <= 60; phase += step) {
                float local = value - (float)phase;
                if (local < (float)(-step / 3)) continue;
                if (local < 0.0f) {
                    angle = Mth.lerp((float)((value - (float)(phase - 5)) / 5.0f), (float)0.0f, (float)135.0f);
                    continue;
                }
                if (local < (float)(step / 3)) {
                    angle = 135.0f;
                    continue;
                }
                if (!(local < (float)(2 * step / 3))) continue;
                angle = Mth.lerp((float)((value - (float)(phase + 5)) / 5.0f), (float)135.0f, (float)0.0f);
            }
        }
        this.rotateDoor(leftDoor, angle, true, direction).light(light).renderInto(ms, vb);
        this.rotateDoor(rightDoor, angle, false, direction).light(light).renderInto(ms, vb);
        if (be.animationType != CuckooClockBlockEntity.Animation.NONE) {
            offset = -(angle / 135.0f) * 1.0f / 2.0f + 0.625f;
            PartialModel partialModel = be.animationType == CuckooClockBlockEntity.Animation.PIG ? AllPartialModels.CUCKOO_PIG : AllPartialModels.CUCKOO_CREEPER;
            SuperByteBuffer figure = CachedBuffers.partial((PartialModel)partialModel, (BlockState)blockState);
            figure.rotateCentered(AngleHelper.rad((double)AngleHelper.horizontalAngle((Direction)direction.getCounterClockWise())), Direction.UP);
            figure.translate(offset, 0.0f, 0.0f);
            figure.light(light).renderInto(ms, vb);
        }
    }

    @Override
    protected SuperByteBuffer getRotatedModel(CuckooClockBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacing((PartialModel)AllPartialModels.SHAFT_HALF, (BlockState)state, (Direction)((Direction)state.getValue(CuckooClockBlock.HORIZONTAL_FACING)).getOpposite());
    }

    private SuperByteBuffer rotateHand(SuperByteBuffer buffer, float angle, Direction facing) {
        float pivotX = 0.125f;
        float pivotY = 0.375f;
        float pivotZ = 0.5f;
        buffer.rotateCentered(AngleHelper.rad((double)AngleHelper.horizontalAngle((Direction)facing.getCounterClockWise())), Direction.UP);
        buffer.translate(pivotX, pivotY, pivotZ);
        buffer.rotate(AngleHelper.rad((double)angle), Direction.EAST);
        buffer.translate(-pivotX, -pivotY, -pivotZ);
        return buffer;
    }

    private SuperByteBuffer rotateDoor(SuperByteBuffer buffer, float angle, boolean left, Direction facing) {
        float pivotX = 0.125f;
        float pivotY = 0.0f;
        float pivotZ = (float)(left ? 6 : 10) / 16.0f;
        buffer.rotateCentered(AngleHelper.rad((double)AngleHelper.horizontalAngle((Direction)facing.getCounterClockWise())), Direction.UP);
        buffer.translate(pivotX, pivotY, pivotZ);
        buffer.rotate(AngleHelper.rad((double)angle) * (float)(left ? -1 : 1), Direction.UP);
        buffer.translate(-pivotX, -pivotY, -pivotZ);
        return buffer;
    }
}
