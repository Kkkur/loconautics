/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.content.redstone.analogLever.AnalogLeverBlock
 *  com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.simulated_team.simulated.content.blocks.physics_assembler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlock;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.blocks.physics_assembler.PhysicsAssemblerBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;

public class PhysicsAssemblerRenderer
extends SmartBlockEntityRenderer<PhysicsAssemblerBlockEntity> {
    public PhysicsAssemblerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(PhysicsAssemblerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        SuperByteBuffer handle = CachedBuffers.partial((PartialModel)SimPartialModels.ASSEMBLER_LEVER, (BlockState)blockState);
        float angle = PhysicsAssemblerRenderer.getRenderAngle(be, partialTicks);
        ((SuperByteBuffer)((SuperByteBuffer)this.transform(handle, blockState).translate(0.5f, 0.4375f, 0.5f)).rotate(angle, Direction.EAST)).translate(-0.5f, -0.4375f, -0.5f);
        handle.light(light).renderInto(ms, vb);
    }

    public static float getRenderAngle(PhysicsAssemblerBlockEntity be, float partialTicks) {
        if (!be.isVirtual()) {
            be.initializeLeverPosition();
        }
        return (float)Math.toRadians(be.getClientAngle(partialTicks));
    }

    private SuperByteBuffer transform(SuperByteBuffer buffer, BlockState leverState) {
        AttachFace face = (AttachFace)leverState.getValue((Property)AnalogLeverBlock.FACE);
        float rX = face == AttachFace.FLOOR ? 0.0f : (face == AttachFace.WALL ? 90.0f : 180.0f);
        float rY = AngleHelper.horizontalAngle((Direction)((Direction)leverState.getValue((Property)AnalogLeverBlock.FACING)));
        buffer.rotateCentered((float)((double)(rY / 180.0f) * Math.PI), Direction.UP);
        buffer.rotateCentered((float)((double)(rX / 180.0f) * Math.PI), Direction.EAST);
        return buffer;
    }
}
