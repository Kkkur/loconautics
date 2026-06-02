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
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.fan;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class EncasedFanRenderer
extends KineticBlockEntityRenderer<EncasedFanBlockEntity> {
    public EncasedFanRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(EncasedFanBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        Direction direction = (Direction)be.getBlockState().getValue((Property)BlockStateProperties.FACING);
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());
        int lightBehind = LevelRenderer.getLightColor((BlockAndTintGetter)be.getLevel(), (BlockPos)be.getBlockPos().relative(direction.getOpposite()));
        int lightInFront = LevelRenderer.getLightColor((BlockAndTintGetter)be.getLevel(), (BlockPos)be.getBlockPos().relative(direction));
        SuperByteBuffer shaftHalf = CachedBuffers.partialFacing((PartialModel)AllPartialModels.SHAFT_HALF, (BlockState)be.getBlockState(), (Direction)direction.getOpposite());
        SuperByteBuffer fanInner = CachedBuffers.partialFacing((PartialModel)AllPartialModels.ENCASED_FAN_INNER, (BlockState)be.getBlockState(), (Direction)direction.getOpposite());
        float time = AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel());
        float speed = be.getSpeed() * 5.0f;
        if (speed > 0.0f) {
            speed = Mth.clamp((float)speed, (float)80.0f, (float)1280.0f);
        }
        if (speed < 0.0f) {
            speed = Mth.clamp((float)speed, (float)-1280.0f, (float)-80.0f);
        }
        float angle = time * speed * 3.0f / 10.0f % 360.0f;
        angle = angle / 180.0f * (float)Math.PI;
        EncasedFanRenderer.standardKineticRotationTransform(shaftHalf, be, lightBehind).renderInto(ms, vb);
        EncasedFanRenderer.kineticRotationTransform(fanInner, be, direction.getAxis(), angle, lightInFront).renderInto(ms, vb);
    }
}
