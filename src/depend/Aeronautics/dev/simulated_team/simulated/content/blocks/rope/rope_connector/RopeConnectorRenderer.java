/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
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
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.rope.rope_connector;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import dev.simulated_team.simulated.content.blocks.rope.rope_connector.RopeConnectorBlock;
import dev.simulated_team.simulated.content.blocks.rope.rope_connector.RopeConnectorBlockEntity;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.RopeStrandRenderer;
import dev.simulated_team.simulated.index.SimPartialModels;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class RopeConnectorRenderer
extends SafeBlockEntityRenderer<RopeConnectorBlockEntity> {
    public RopeConnectorRenderer(BlockEntityRendererProvider.Context context) {
    }

    public boolean shouldRenderOffScreen(RopeConnectorBlockEntity blockEntity) {
        return true;
    }

    public boolean shouldRender(RopeConnectorBlockEntity blockEntity, Vec3 cameraPos) {
        return true;
    }

    protected void renderSafe(RopeConnectorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        RopeStrandRenderer.render(be, be.getRopeHolder(), partialTicks, ms, buffer);
        RopeStrandHolderBehavior holder = be.getRopeHolder();
        if (!(holder.isAttached() || be.isVirtual() && be.getRopeHolder().renderAttached)) {
            return;
        }
        SuperByteBuffer knot = CachedBuffers.partialFacing((PartialModel)SimPartialModels.ROPE_CONNECTOR_KNOT, (BlockState)AllBlocks.ROPE.getDefaultState(), (Direction)Direction.NORTH);
        BlockPos blockPos = be.getBlockPos();
        BlockState state = be.getBlockState();
        Vec3 attachmentPoint = be.getVisualAttachmentPoint(blockPos, state);
        Direction facing = (Direction)state.getValue((Property)RopeConnectorBlock.FACING);
        SuperByteBuffer knotBuffer = knot.light(light);
        boolean axisAlongFirstCoordinate = (Boolean)state.getValue((Property)RopeConnectorBlock.AXIS_ALONG_FIRST_COORDINATE);
        float zRotLast = axisAlongFirstCoordinate ^ facing.getAxis() == Direction.Axis.Z ? 90.0f : 0.0f;
        float yRot = AngleHelper.horizontalAngle((Direction)facing) + (axisAlongFirstCoordinate || facing.getAxis() != Direction.Axis.Y ? 0.0f : 90.0f);
        float zRot = facing == Direction.UP ? 270.0f : (facing == Direction.DOWN ? 90.0f : 0.0f);
        knotBuffer.translate(attachmentPoint.subtract(blockPos.getCenter()));
        knotBuffer.rotateCentered((float)((double)(zRot / 180.0f) * Math.PI), Direction.SOUTH);
        knotBuffer.rotateCentered((float)((double)(yRot / 180.0f) * Math.PI), Direction.UP);
        knotBuffer.rotateCentered((float)((double)(zRotLast / 180.0f) * Math.PI), Direction.SOUTH);
        knotBuffer.rotateCentered(1.5707964f, Direction.UP);
        knotBuffer.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }
}
