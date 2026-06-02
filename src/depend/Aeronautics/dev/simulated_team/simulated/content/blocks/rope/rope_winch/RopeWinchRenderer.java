/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.content.contraptions.pulley.AbstractPulleyRenderer
 *  com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock
 *  com.simibubi.create.content.kinetics.base.DirectionalKineticBlock
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer
 *  com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SpriteShiftEntry
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.rope.rope_winch;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.contraptions.pulley.AbstractPulleyRenderer;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.blocks.rope.rope_winch.RopeWinchBlockEntity;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.RopeStrandRenderer;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.index.SimSpriteShifts;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class RopeWinchRenderer
extends SafeBlockEntityRenderer<RopeWinchBlockEntity> {
    public RopeWinchRenderer(BlockEntityRendererProvider.Context context) {
    }

    private static SuperByteBuffer transform(SuperByteBuffer buffer, BlockState state, boolean axisDirectionMatters) {
        Direction facing = (Direction)state.getValue((Property)DirectionalKineticBlock.FACING);
        float zRotLast = axisDirectionMatters && (Boolean)state.getValue((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE) ^ facing.getAxis() == Direction.Axis.Z ? 90.0f : 0.0f;
        float yRot = AngleHelper.horizontalAngle((Direction)facing) + ((Boolean)state.getValue((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE) != false || facing.getAxis() != Direction.Axis.Y ? 0.0f : 90.0f);
        float zRot = facing == Direction.UP ? 270.0f : (facing == Direction.DOWN ? 90.0f : 0.0f);
        buffer.rotateCentered((float)((double)(zRot / 180.0f) * Math.PI), Direction.SOUTH);
        buffer.rotateCentered((float)((double)(yRot / 180.0f) * Math.PI), Direction.UP);
        buffer.rotateCentered((float)((double)(zRotLast / 180.0f) * Math.PI), Direction.SOUTH);
        return buffer;
    }

    public boolean shouldRenderOffScreen(RopeWinchBlockEntity be) {
        return true;
    }

    public boolean shouldRender(RopeWinchBlockEntity pBlockEntity, Vec3 pCameraPos) {
        return true;
    }

    protected void renderSafe(RopeWinchBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        FilteringRenderer.renderOnBlockEntity((SmartBlockEntity)be, (float)partialTicks, (PoseStack)ms, (MultiBufferSource)buffer, (int)light, (int)overlay);
        this.renderComponents(be, partialTicks, ms, buffer, light, overlay);
    }

    protected void renderComponents(RopeWinchBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ms.pushPose();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        BlockState state = be.getBlockState();
        SuperByteBuffer shaft = CachedBuffers.partial((PartialModel)SimPartialModels.ROPE_WINCH_SHAFT, (BlockState)state);
        SuperByteBuffer ropeCoil = CachedBuffers.partial((PartialModel)SimPartialModels.ROPE_WINCH_ROPE_COIL, (BlockState)state);
        Direction.Axis axis = KineticBlockEntityRenderer.getRotationAxisOf((KineticBlockEntity)be);
        float angle = KineticBlockEntityRenderer.getAngleForBe((KineticBlockEntity)be, (BlockPos)be.getBlockPos(), (Direction.Axis)axis);
        KineticBlockEntityRenderer.kineticRotationTransform((SuperByteBuffer)shaft, (KineticBlockEntity)be, (Direction.Axis)axis, (float)angle, (int)light);
        RopeWinchRenderer.transform(shaft, state, true).renderInto(ms, vb);
        if (be.getRopeHolder().isAttached() || be.isVirtual() && be.getRopeHolder().renderAttached) {
            ropeCoil.light(light);
            Direction facing = (Direction)state.getValue((Property)DirectionalKineticBlock.FACING);
            float speed = facing == Direction.DOWN ? (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 1.0f : -1.0f) : (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE == (Boolean)state.getValue((Property)DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE) ? 1.0f : -1.0f);
            AbstractPulleyRenderer.scrollCoil((SuperByteBuffer)ropeCoil, (SpriteShiftEntry)this.getCoilShift(), (float)be.clientAngle.getValue(partialTicks), (float)speed);
            RopeWinchRenderer.transform(ropeCoil, state, true).renderInto(ms, vb);
        }
        ms.popPose();
        RopeStrandRenderer.render((SmartBlockEntity)be, be.getRopeHolder(), partialTicks, ms, buffer);
    }

    protected SpriteShiftEntry getCoilShift() {
        return SimSpriteShifts.ROPE_WINCH_COIL;
    }
}
