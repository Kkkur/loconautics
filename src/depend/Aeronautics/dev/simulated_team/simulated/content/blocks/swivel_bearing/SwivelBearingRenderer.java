/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.simulated_team.simulated.content.blocks.swivel_bearing;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.SwivelBearingBlock;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.SwivelBearingBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class SwivelBearingRenderer
extends KineticBlockEntityRenderer<SwivelBearingBlockEntity> {
    public SwivelBearingRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(SwivelBearingBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        super.renderSafe((KineticBlockEntity)be, partialTicks, ms, buffer, light, overlay);
        BlockState state = be.getBlockState();
        Direction.Axis axis = ((IRotate)state.getBlock()).getRotationAxis(state);
        SuperByteBuffer cogwheel = SwivelBearingRenderer.kineticRotationTransform((SuperByteBuffer)CachedBuffers.partialFacingVertical((PartialModel)SimPartialModels.SWIVEL_BEARING_COG, (BlockState)state, (Direction)((Direction)state.getValue((Property)SwivelBearingBlock.FACING)).getOpposite()), (KineticBlockEntity)be.getExtraKinetics(), (Direction.Axis)axis, (float)SwivelBearingRenderer.getAngleForBe((KineticBlockEntity)be.getExtraKinetics(), (BlockPos)be.getBlockPos(), (Direction.Axis)axis), (int)light);
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        cogwheel.renderInto(ms, vb);
        if (!be.isAssembled()) {
            SwivelBearingRenderer.renderRotatingBuffer((KineticBlockEntity)be, (SuperByteBuffer)CachedBuffers.partialFacing((PartialModel)SimPartialModels.SHAFT_SIXTEENTH, (BlockState)state, (Direction)((Direction)state.getValue((Property)SwivelBearingBlock.FACING))), (PoseStack)ms, (VertexConsumer)vb, (int)light);
        }
    }

    protected SuperByteBuffer getRotatedModel(SwivelBearingBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacing((PartialModel)SimPartialModels.SHAFT_SIXTEENTH, (BlockState)state, (Direction)((Direction)state.getValue((Property)SwivelBearingBlock.FACING)).getOpposite());
    }
}
