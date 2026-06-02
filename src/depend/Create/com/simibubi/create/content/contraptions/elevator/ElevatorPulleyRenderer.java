/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.contraptions.elevator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.contraptions.elevator.ElevatorPulleyBlock;
import com.simibubi.create.content.contraptions.elevator.ElevatorPulleyBlockEntity;
import com.simibubi.create.content.contraptions.pulley.AbstractPulleyRenderer;
import com.simibubi.create.content.contraptions.pulley.PulleyRenderer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class ElevatorPulleyRenderer
extends KineticBlockEntityRenderer<ElevatorPulleyBlockEntity> {
    public ElevatorPulleyRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(ElevatorPulleyBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        float offset = PulleyRenderer.getBlockEntityOffset(partialTicks, be);
        boolean running = PulleyRenderer.isPulleyRunning(be);
        SpriteShiftEntry beltShift = AllSpriteShifts.ELEVATOR_BELT;
        SpriteShiftEntry coilShift = AllSpriteShifts.ELEVATOR_COIL;
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        Level world = be.getLevel();
        BlockState blockState = be.getBlockState();
        BlockPos pos = be.getBlockPos();
        float blockStateAngle = 180.0f + AngleHelper.horizontalAngle((Direction)((Direction)blockState.getValue(ElevatorPulleyBlock.HORIZONTAL_FACING)));
        SuperByteBuffer magnet = CachedBuffers.partial((PartialModel)AllPartialModels.ELEVATOR_MAGNET, (BlockState)blockState);
        if (running || offset == 0.0f) {
            AbstractPulleyRenderer.renderAt((LevelAccessor)world, (SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)magnet.center()).rotateYDegrees(blockStateAngle)).uncenter(), offset, pos, ms, vb);
        }
        SuperByteBuffer rotatedCoil = this.getRotatedCoil(be);
        if (offset == 0.0f) {
            rotatedCoil.light(light).renderInto(ms, vb);
            return;
        }
        AbstractPulleyRenderer.scrollCoil(rotatedCoil, coilShift, offset, 2.0f).light(light).renderInto(ms, vb);
        float spriteSize = beltShift.getTarget().getV1() - beltShift.getTarget().getV0();
        double beltScroll = (-((double)offset + 0.5) - Math.floor(-((double)offset + 0.5))) / 2.0;
        SuperByteBuffer halfRope = CachedBuffers.partial((PartialModel)AllPartialModels.ELEVATOR_BELT_HALF, (BlockState)blockState);
        SuperByteBuffer rope = CachedBuffers.partial((PartialModel)AllPartialModels.ELEVATOR_BELT, (BlockState)blockState);
        float f = offset % 1.0f;
        if (f < 0.25f || f > 0.75f) {
            ((SuperByteBuffer)((SuperByteBuffer)halfRope.center()).rotateYDegrees(blockStateAngle)).uncenter();
            AbstractPulleyRenderer.renderAt((LevelAccessor)world, halfRope.shiftUVScrolling(beltShift, (float)beltScroll * spriteSize), f > 0.75f ? f - 1.0f : f, pos, ms, vb);
        }
        if (!running) {
            return;
        }
        int i = 0;
        while ((float)i < offset - 0.25f) {
            ((SuperByteBuffer)((SuperByteBuffer)rope.center()).rotateYDegrees(blockStateAngle)).uncenter();
            AbstractPulleyRenderer.renderAt((LevelAccessor)world, rope.shiftUVScrolling(beltShift, (float)beltScroll * spriteSize), offset - (float)i, pos, ms, vb);
            ++i;
        }
    }

    @Override
    protected BlockState getRenderedBlockState(ElevatorPulleyBlockEntity be) {
        return ElevatorPulleyRenderer.shaft(ElevatorPulleyRenderer.getRotationAxisOf(be));
    }

    protected SuperByteBuffer getRotatedCoil(KineticBlockEntity be) {
        BlockState blockState = be.getBlockState();
        return CachedBuffers.partialFacing((PartialModel)AllPartialModels.ELEVATOR_COIL, (BlockState)blockState, (Direction)((Direction)blockState.getValue(ElevatorPulleyBlock.HORIZONTAL_FACING)));
    }

    public boolean shouldRenderOffScreen(ElevatorPulleyBlockEntity p_188185_1_) {
        return true;
    }
}
