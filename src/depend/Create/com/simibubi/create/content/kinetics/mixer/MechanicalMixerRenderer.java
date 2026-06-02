/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.mixer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class MechanicalMixerRenderer
extends KineticBlockEntityRenderer<MechanicalMixerBlockEntity> {
    public MechanicalMixerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    public boolean shouldRenderOffScreen(MechanicalMixerBlockEntity be) {
        return true;
    }

    @Override
    protected void renderSafe(MechanicalMixerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        SuperByteBuffer superBuffer = CachedBuffers.partial((PartialModel)AllPartialModels.SHAFTLESS_COGWHEEL, (BlockState)blockState);
        MechanicalMixerRenderer.standardKineticRotationTransform(superBuffer, be, light).renderInto(ms, vb);
        float renderedHeadOffset = be.getRenderedHeadOffset(partialTicks);
        float speed = be.getRenderedHeadRotationSpeed(partialTicks);
        float time = AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel());
        float angle = time * speed * 6.0f / 10.0f % 360.0f / 180.0f * (float)Math.PI;
        SuperByteBuffer poleRender = CachedBuffers.partial((PartialModel)AllPartialModels.MECHANICAL_MIXER_POLE, (BlockState)blockState);
        ((SuperByteBuffer)poleRender.translate(0.0f, -renderedHeadOffset, 0.0f)).light(light).renderInto(ms, vb);
        VertexConsumer vbCutout = buffer.getBuffer(RenderType.cutoutMipped());
        SuperByteBuffer headRender = CachedBuffers.partial((PartialModel)AllPartialModels.MECHANICAL_MIXER_HEAD, (BlockState)blockState);
        ((SuperByteBuffer)((SuperByteBuffer)headRender.rotateCentered(angle, Direction.UP)).translate(0.0f, -renderedHeadOffset, 0.0f)).light(light).renderInto(ms, vbCutout);
    }
}
