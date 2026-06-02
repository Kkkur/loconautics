/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.foundation.blockEntity.renderer.ColoredOverlayBlockEntityRenderer
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
package dev.simulated_team.simulated.content.blocks.redstone.redstone_inductor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.ColoredOverlayBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.blocks.redstone.redstone_inductor.RedstoneInductorBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.util.SimColors;
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

public class RedstoneInductorRenderer
extends ColoredOverlayBlockEntityRenderer<RedstoneInductorBlockEntity> {
    public RedstoneInductorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(RedstoneInductorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        SuperByteBuffer render = RedstoneInductorRenderer.render((SuperByteBuffer)this.getOverlayBuffer(be), (int)this.getColor(be, partialTicks), (int)light);
        Direction facing = (Direction)be.getBlockState().getValue((Property)BlockStateProperties.HORIZONTAL_FACING);
        render.translate(0.5, 0.0, 0.5);
        ((SuperByteBuffer)render.rotateYDegrees(AngleHelper.horizontalAngle((Direction)facing))).pushPose();
        render.renderInto(ms, buffer.getBuffer(RenderType.cutout()));
    }

    protected int getColor(RedstoneInductorBlockEntity te, float partialTicks) {
        float state = te.lerpedState.getValue(partialTicks);
        return SimColors.redstone(state / 15.0f);
    }

    protected SuperByteBuffer getOverlayBuffer(RedstoneInductorBlockEntity te) {
        return CachedBuffers.partial((PartialModel)SimPartialModels.REDSTONE_INDUCTOR_INDICATOR, (BlockState)te.getBlockState());
    }
}
