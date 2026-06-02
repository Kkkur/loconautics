/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.contraptions.bearing;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.contraptions.bearing.IBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class BearingRenderer<T extends KineticBlockEntity>
extends KineticBlockEntityRenderer<T> {
    public BearingRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(T be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        Direction facing = (Direction)be.getBlockState().getValue((Property)BlockStateProperties.FACING);
        PartialModel top = ((IBearingBlockEntity)be).isWoodenTop() ? AllPartialModels.BEARING_TOP_WOODEN : AllPartialModels.BEARING_TOP;
        SuperByteBuffer superBuffer = CachedBuffers.partial((PartialModel)top, (BlockState)be.getBlockState());
        float interpolatedAngle = ((IBearingBlockEntity)be).getInterpolatedAngle(partialTicks - 1.0f);
        BearingRenderer.kineticRotationTransform(superBuffer, be, facing.getAxis(), (float)((double)(interpolatedAngle / 180.0f) * Math.PI), light);
        if (facing.getAxis().isHorizontal()) {
            superBuffer.rotateCentered(AngleHelper.rad((double)AngleHelper.horizontalAngle((Direction)facing.getOpposite())), Direction.UP);
        }
        superBuffer.rotateCentered(AngleHelper.rad((double)(-90.0f - AngleHelper.verticalAngle((Direction)facing))), Direction.EAST);
        superBuffer.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    @Override
    protected SuperByteBuffer getRotatedModel(KineticBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacing((PartialModel)AllPartialModels.SHAFT_HALF, (BlockState)state, (Direction)((Direction)state.getValue((Property)BearingBlock.FACING)).getOpposite());
    }
}
