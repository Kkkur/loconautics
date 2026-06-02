/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.AllPartialModels
 *  com.simibubi.create.content.contraptions.bearing.BearingBlock
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
package dev.eriksonn.aeronautics.content.blocks.propeller.small;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlockEntity;
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

public abstract class SimplePropellerRenderer<T extends BasePropellerBlockEntity>
extends KineticBlockEntityRenderer<T> {
    public SimplePropellerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    public void renderSafe(T be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        BlockState state = be.getBlockState();
        Direction dir = (Direction)state.getValue((Property)BlockStateProperties.FACING);
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        SuperByteBuffer propeller = CachedBuffers.partialFacing((PartialModel)this.getCurrentModel(be), (BlockState)state);
        float angle = this.getAngle(partialTicks, dir, be);
        SimplePropellerRenderer.kineticRotationTransform((SuperByteBuffer)propeller, be, (Direction.Axis)dir.getAxis(), (float)angle, (int)light);
        if (dir.getAxis().isHorizontal()) {
            propeller.rotateCentered(AngleHelper.rad((double)AngleHelper.horizontalAngle((Direction)dir.getOpposite())), Direction.UP);
        }
        if (dir.getAxis().isVertical()) {
            propeller.rotateCentered(AngleHelper.rad((double)AngleHelper.verticalAngle((Direction)dir.getOpposite())), Direction.EAST);
        }
        ((SuperByteBuffer)propeller.translate(0.0f, 0.0f, -0.1875f)).rotateCentered(AngleHelper.rad((double)(-90.0f - AngleHelper.verticalAngle((Direction)dir))), Direction.EAST);
        propeller.renderInto(ms, vb);
    }

    public abstract PartialModel getCurrentModel(T var1);

    public float getAngle(float partialTicks, Direction dir, T be) {
        float angle = ((BasePropellerBlockEntity)((Object)be)).getPreviousAngle() * (1.0f - partialTicks) + ((BasePropellerBlockEntity)((Object)be)).getAngle() * partialTicks;
        angle = angle / 180.0f * (float)Math.PI;
        return angle *= 2.0f;
    }

    protected SuperByteBuffer getRotatedModel(T be, BlockState state) {
        return CachedBuffers.partialFacing((PartialModel)AllPartialModels.SHAFT_HALF, (BlockState)state, (Direction)((Direction)state.getValue((Property)BearingBlock.FACING)).getOpposite());
    }
}
