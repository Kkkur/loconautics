/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.contraptions.pulley;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
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

public abstract class AbstractPulleyRenderer<T extends KineticBlockEntity>
extends KineticBlockEntityRenderer<T> {
    private PartialModel halfRope;
    private PartialModel halfMagnet;

    public AbstractPulleyRenderer(BlockEntityRendererProvider.Context context, PartialModel halfRope, PartialModel halfMagnet) {
        super(context);
        this.halfRope = halfRope;
        this.halfMagnet = halfMagnet;
    }

    public boolean shouldRenderOffScreen(T p_188185_1_) {
        return true;
    }

    @Override
    protected void renderSafe(T be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        float offset = this.getOffset(be, partialTicks);
        boolean running = this.isRunning(be);
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        AbstractPulleyRenderer.scrollCoil(this.getRotatedCoil(be), this.getCoilShift(), offset, 1.0f).light(light).renderInto(ms, vb);
        Level world = be.getLevel();
        BlockState blockState = be.getBlockState();
        BlockPos pos = be.getBlockPos();
        SuperByteBuffer halfMagnet = CachedBuffers.partial((PartialModel)this.halfMagnet, (BlockState)blockState);
        SuperByteBuffer halfRope = CachedBuffers.partial((PartialModel)this.halfRope, (BlockState)blockState);
        SuperByteBuffer magnet = this.renderMagnet(be);
        SuperByteBuffer rope = this.renderRope(be);
        if (running || offset == 0.0f) {
            AbstractPulleyRenderer.renderAt((LevelAccessor)world, offset > 0.25f ? magnet : halfMagnet, offset, pos, ms, vb);
        }
        float f = offset % 1.0f;
        if (offset > 0.75f && (f < 0.25f || f > 0.75f)) {
            AbstractPulleyRenderer.renderAt((LevelAccessor)world, halfRope, f > 0.75f ? f - 1.0f : f, pos, ms, vb);
        }
        if (!running) {
            return;
        }
        int i = 0;
        while ((float)i < offset - 1.25f) {
            AbstractPulleyRenderer.renderAt((LevelAccessor)world, rope, offset - (float)i - 1.0f, pos, ms, vb);
            ++i;
        }
    }

    public static void renderAt(LevelAccessor world, SuperByteBuffer partial, float offset, BlockPos pulleyPos, PoseStack ms, VertexConsumer buffer) {
        BlockPos actualPos = pulleyPos.below((int)offset);
        int light = LevelRenderer.getLightColor((BlockAndTintGetter)world, (BlockState)world.getBlockState(actualPos), (BlockPos)actualPos);
        ((SuperByteBuffer)partial.translate(0.0f, -offset, 0.0f)).light(light).renderInto(ms, buffer);
    }

    protected abstract Direction.Axis getShaftAxis(T var1);

    protected abstract PartialModel getCoil();

    protected abstract SpriteShiftEntry getCoilShift();

    protected abstract SuperByteBuffer renderRope(T var1);

    protected abstract SuperByteBuffer renderMagnet(T var1);

    protected abstract float getOffset(T var1, float var2);

    protected abstract boolean isRunning(T var1);

    @Override
    protected BlockState getRenderedBlockState(T be) {
        return AbstractPulleyRenderer.shaft(this.getShaftAxis(be));
    }

    protected SuperByteBuffer getRotatedCoil(T be) {
        BlockState blockState = be.getBlockState();
        return CachedBuffers.partialFacing((PartialModel)this.getCoil(), (BlockState)blockState, (Direction)Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)this.getShaftAxis(be)));
    }

    public static SuperByteBuffer scrollCoil(SuperByteBuffer sbb, SpriteShiftEntry coilShift, float offset, float speedModifier) {
        if (offset == 0.0f) {
            return sbb;
        }
        float spriteSize = coilShift.getTarget().getV1() - coilShift.getTarget().getV0();
        double coilScroll = (double)(-((offset *= speedModifier / 2.0f) + 0.1875f)) - Math.floor((offset + 0.1875f) * -2.0f) / 2.0;
        return sbb.shiftUVScrolling(coilShift, (float)coilScroll * spriteSize);
    }

    public int getViewDistance() {
        return (Integer)AllConfigs.server().kinetics.maxRopeLength.get();
    }
}
