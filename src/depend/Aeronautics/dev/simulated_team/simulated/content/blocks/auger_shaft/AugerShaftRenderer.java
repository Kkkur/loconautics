/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Quaternionfc
 */
package dev.simulated_team.simulated.content.blocks.auger_shaft;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.blocks.auger_shaft.AugerCogBlock;
import dev.simulated_team.simulated.content.blocks.auger_shaft.AugerShaftBlock;
import dev.simulated_team.simulated.content.blocks.auger_shaft.AugerShaftBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Quaternionfc;

public class AugerShaftRenderer
extends KineticBlockEntityRenderer<AugerShaftBlockEntity> {
    public AugerShaftRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(AugerShaftBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState state = this.getRenderedBlockState(be);
        if (!VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            RenderType type = this.getRenderType(be, state);
            AugerShaftRenderer.renderRotatingBuffer((KineticBlockEntity)be, (SuperByteBuffer)this.getRotatedModel(be, state), (PoseStack)ms, (VertexConsumer)buffer.getBuffer(type), (int)light);
        }
        if (be.getBlockState().getBlock() instanceof AugerCogBlock) {
            Direction facing = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)((Direction.Axis)state.getValue((Property)AugerShaftBlock.AXIS)));
            VertexConsumer solid = buffer.getBuffer(RenderType.solid());
            for (int i = 0; i < 2; ++i) {
                SuperByteBuffer redstone = CachedBuffers.partialFacing((PartialModel)(be.flowDirection == (i == 1 ? facing.getOpposite() : facing) && be.getSpeed() != 0.0f ? SimPartialModels.AUGER_REDSTONE_ON : SimPartialModels.AUGER_REDSTONE_OFF), (BlockState)state, (Direction)facing);
                ((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)redstone.getTransforms()).center()).rotateToFace(facing)).rotate((Quaternionfc)Axis.XN.rotationDegrees((float)((facing.getAxis().isHorizontal() ? 90 : 0) + i * 180))).uncenter();
                redstone.light(light).renderInto(ms, solid);
            }
        }
    }

    protected SuperByteBuffer getRotatedModel(AugerShaftBlockEntity be, BlockState state) {
        if (!(be.getBlockState().getBlock() instanceof AugerCogBlock)) {
            return super.getRotatedModel((KineticBlockEntity)be, state);
        }
        Direction facing = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)((Direction.Axis)state.getValue((Property)AugerShaftBlock.AXIS)));
        return CachedBuffers.partialDirectional((PartialModel)SimPartialModels.AUGER_COG, (BlockState)state, (Direction)facing, () -> {
            PoseStack poseStack = new PoseStack();
            ((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)poseStack).center()).rotateToFace(facing)).rotate((Quaternionfc)Axis.XN.rotationDegrees(90.0f)).uncenter();
            return poseStack;
        });
    }

    protected BlockState getRenderedBlockState(AugerShaftBlockEntity be) {
        return AugerShaftRenderer.shaft((Direction.Axis)AugerShaftRenderer.getRotationAxisOf((KineticBlockEntity)be));
    }
}
