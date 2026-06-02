/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity
 *  com.simibubi.create.content.kinetics.transmission.SplitShaftRenderer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
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
package dev.simulated_team.simulated.content.blocks.directional_gearshift;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.content.kinetics.transmission.SplitShaftRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.blocks.directional_gearshift.DirectionalGearshiftBlock;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.SimPartialModels;
import net.createmod.catnip.animation.AnimationTickHolder;
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

public class DirectionalGearshiftRenderer
extends SplitShaftRenderer {
    public DirectionalGearshiftRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(SplitShaftBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        Direction.Axis axis = ((DirectionalGearshiftBlock)((Object)SimBlocks.DIRECTIONAL_GEARSHIFT.get())).getRotationAxis(blockState);
        float time = AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel());
        float angle = time * be.getSpeed() * 3.0f / 10.0f % 360.0f;
        float shaftAngle = 0.0f;
        float modifier = 0.0f;
        float offset = 0.0f;
        if (be.hasSource() && !((Boolean)blockState.getValue((Property)DirectionalGearshiftBlock.LEFT_POWERED)).booleanValue() && ((Boolean)blockState.getValue((Property)DirectionalGearshiftBlock.RIGHT_POWERED)).booleanValue()) {
            shaftAngle = angle;
        }
        if (be.hasSource() && ((Boolean)blockState.getValue((Property)DirectionalGearshiftBlock.LEFT_POWERED)).booleanValue() && !((Boolean)blockState.getValue((Property)DirectionalGearshiftBlock.RIGHT_POWERED)).booleanValue()) {
            modifier = be.getRotationSpeedModifier(be.getSourceFacing().getOpposite());
            offset = DirectionalGearshiftRenderer.getRotationOffsetForPosition((KineticBlockEntity)be, (BlockPos)be.getBlockPos(), (Direction.Axis)axis);
        }
        angle *= modifier;
        angle += offset;
        angle = angle / 180.0f * (float)Math.PI;
        shaftAngle = shaftAngle / 180.0f * (float)Math.PI;
        Direction direction = (Direction)blockState.getValue((Property)DirectionalGearshiftBlock.FACING);
        boolean vertical = axis.isVertical() || direction.getAxis().isVertical() && (Boolean)blockState.getValue((Property)DirectionalGearshiftBlock.AXIS_ALONG_FIRST_COORDINATE) == false;
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.solid());
        SuperByteBuffer barrel = CachedBuffers.partial((PartialModel)SimPartialModels.DIRECTIONAL_GEARSHIFT_CENTER, (BlockState)blockState);
        SuperByteBuffer barrelShaftA = CachedBuffers.partial((PartialModel)SimPartialModels.DIRECTIONAL_GEARSHIFT_BARREL_SHAFT, (BlockState)blockState);
        DirectionalGearshiftRenderer.kineticRotationTransform((SuperByteBuffer)barrelShaftA, (KineticBlockEntity)be, (Direction.Axis)axis, (float)angle, (int)light);
        ((SuperByteBuffer)((SuperByteBuffer)barrelShaftA.center()).rotateToFace(direction)).uncenter();
        if (vertical) {
            barrelShaftA.rotateZCenteredDegrees(90.0f);
        }
        barrelShaftA.rotateZCentered((float)Math.PI);
        barrelShaftA.rotateYCentered(shaftAngle);
        barrelShaftA.light(light).renderInto(ms, consumer);
        SuperByteBuffer barrelShaftB = CachedBuffers.partial((PartialModel)SimPartialModels.DIRECTIONAL_GEARSHIFT_BARREL_SHAFT, (BlockState)blockState);
        DirectionalGearshiftRenderer.kineticRotationTransform((SuperByteBuffer)barrelShaftB, (KineticBlockEntity)be, (Direction.Axis)axis, (float)angle, (int)light);
        ((SuperByteBuffer)((SuperByteBuffer)barrelShaftB.center()).rotateToFace(direction)).uncenter();
        if (vertical) {
            barrelShaftB.rotateZCenteredDegrees(90.0f);
        }
        barrelShaftB.rotateYCentered(shaftAngle);
        barrelShaftB.light(light).renderInto(ms, consumer);
        DirectionalGearshiftRenderer.kineticRotationTransform((SuperByteBuffer)barrel, (KineticBlockEntity)be, (Direction.Axis)axis, (float)angle, (int)light);
        ((SuperByteBuffer)((SuperByteBuffer)barrel.center()).rotateToFace(direction)).uncenter();
        if (vertical) {
            barrel.rotateZCenteredDegrees(90.0f);
        }
        barrel.light(light).renderInto(ms, consumer);
        super.renderSafe(be, partialTicks, ms, bufferSource, light, overlay);
    }
}
