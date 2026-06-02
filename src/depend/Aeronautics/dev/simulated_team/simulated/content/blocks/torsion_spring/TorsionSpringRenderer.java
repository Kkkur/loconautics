/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.AllPartialModels
 *  com.simibubi.create.content.contraptions.bearing.BearingBlock
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.math.AngleHelper
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
package dev.simulated_team.simulated.content.blocks.torsion_spring;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.blocks.torsion_spring.TorsionSpringBlock;
import dev.simulated_team.simulated.content.blocks.torsion_spring.TorsionSpringBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import net.createmod.catnip.math.AngleHelper;
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

public class TorsionSpringRenderer
extends KineticBlockEntityRenderer<TorsionSpringBlockEntity> {
    public TorsionSpringRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(TorsionSpringBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        super.renderSafe((KineticBlockEntity)be, partialTicks, ms, buffer, light, overlay);
        Direction facing = (Direction)be.getBlockState().getValue((Property)TorsionSpringBlock.FACING);
        SuperByteBuffer spring = CachedBuffers.partial((PartialModel)SimPartialModels.TORSION_SPRING, (BlockState)be.getBlockState());
        float angle = be.interpolatedSpring(partialTicks);
        TorsionSpringRenderer.kineticRotationTransform((SuperByteBuffer)spring, (KineticBlockEntity)be, (Direction.Axis)facing.getAxis(), (float)((float)Math.PI / 180 * angle), (int)light);
        if (facing.getAxis().isHorizontal()) {
            spring.rotateCentered(AngleHelper.rad((double)AngleHelper.horizontalAngle((Direction)facing.getOpposite())), Direction.UP);
        }
        spring.rotateCentered(AngleHelper.rad((double)(-90.0f - AngleHelper.verticalAngle((Direction)facing))), Direction.EAST);
        spring.renderInto(ms, buffer.getBuffer(RenderType.solid()));
        SuperByteBuffer shaftOut = CachedBuffers.partialFacing((PartialModel)AllPartialModels.SHAFT_HALF, (BlockState)be.getBlockState(), (Direction)facing);
        TorsionSpringRenderer.kineticRotationTransform((SuperByteBuffer)shaftOut, (KineticBlockEntity)be, (Direction.Axis)facing.getAxis(), (float)TorsionSpringRenderer.getAngleForBe((KineticBlockEntity)be.getExtraKinetics(), (BlockPos)be.getBlockPos(), (Direction.Axis)facing.getAxis()), (int)light);
        shaftOut.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    protected SuperByteBuffer getRotatedModel(TorsionSpringBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacing((PartialModel)AllPartialModels.SHAFT_HALF, (BlockState)state, (Direction)((Direction)state.getValue((Property)BearingBlock.FACING)).getOpposite());
    }
}
