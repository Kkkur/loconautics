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
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing.PropellerBearingBlockEntity;
import dev.eriksonn.aeronautics.index.AeroPartialModels;
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

public class PropellerBearingRenderer
extends KineticBlockEntityRenderer<PropellerBearingBlockEntity> {
    public PropellerBearingRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(PropellerBearingBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        super.renderSafe((KineticBlockEntity)be, partialTicks, ms, buffer, light, overlay);
        Direction facing = (Direction)be.getBlockState().getValue((Property)BlockStateProperties.FACING);
        PartialModel top = AeroPartialModels.BEARING_PLATE;
        SuperByteBuffer superBuffer = CachedBuffers.partial((PartialModel)top, (BlockState)be.getBlockState());
        float interpolatedAngle = be.getInterpolatedAngle(partialTicks - 1.0f);
        PropellerBearingRenderer.kineticRotationTransform((SuperByteBuffer)superBuffer, (KineticBlockEntity)be, (Direction.Axis)facing.getAxis(), (float)((float)((double)(interpolatedAngle / 180.0f) * Math.PI)), (int)light);
        if (facing.getAxis().isHorizontal()) {
            superBuffer.rotateCentered(AngleHelper.rad((double)AngleHelper.horizontalAngle((Direction)facing.getOpposite())), Direction.UP);
        }
        superBuffer.rotateCentered(AngleHelper.rad((double)(-90.0f - AngleHelper.verticalAngle((Direction)facing))), Direction.EAST);
        superBuffer.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    protected SuperByteBuffer getRotatedModel(PropellerBearingBlockEntity te, BlockState state) {
        return CachedBuffers.partialFacing((PartialModel)AllPartialModels.SHAFT_HALF, (BlockState)state, (Direction)((Direction)state.getValue((Property)BearingBlock.FACING)).getOpposite());
    }
}
