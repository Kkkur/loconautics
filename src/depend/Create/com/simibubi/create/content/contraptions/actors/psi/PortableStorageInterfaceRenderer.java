/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.contraptions.actors.psi;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlock;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlockEntity;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceMovement;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.util.function.Consumer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class PortableStorageInterfaceRenderer
extends SafeBlockEntityRenderer<PortableStorageInterfaceBlockEntity> {
    public PortableStorageInterfaceRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(PortableStorageInterfaceBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        BlockState blockState = be.getBlockState();
        float progress = be.getExtensionDistance(partialTicks);
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        PortableStorageInterfaceRenderer.render(blockState, be.isConnected(), progress, null, sbb -> sbb.light(light).renderInto(ms, vb));
    }

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        BlockState blockState = context.state;
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        float renderPartialTicks = AnimationTickHolder.getPartialTicks();
        LerpedFloat animation = PortableStorageInterfaceMovement.getAnimation(context);
        float progress = animation.getValue(renderPartialTicks);
        boolean lit = animation.settled();
        PortableStorageInterfaceRenderer.render(blockState, lit, progress, matrices.getModel(), sbb -> sbb.light(LevelRenderer.getLightColor((BlockAndTintGetter)renderWorld, (BlockPos)context.localPos)).useLevelLight((BlockAndTintGetter)context.world, matrices.getWorld()).renderInto(matrices.getViewProjection(), vb));
    }

    private static void render(BlockState blockState, boolean lit, float progress, PoseStack local, Consumer<SuperByteBuffer> drawCallback) {
        SuperByteBuffer middle = CachedBuffers.partial((PartialModel)PortableStorageInterfaceRenderer.getMiddleForState(blockState, lit), (BlockState)blockState);
        SuperByteBuffer top = CachedBuffers.partial((PartialModel)PortableStorageInterfaceRenderer.getTopForState(blockState), (BlockState)blockState);
        if (local != null) {
            middle.transform(local);
            top.transform(local);
        }
        Direction facing = (Direction)blockState.getValue((Property)PortableStorageInterfaceBlock.FACING);
        PortableStorageInterfaceRenderer.rotateToFacing(middle, facing);
        PortableStorageInterfaceRenderer.rotateToFacing(top, facing);
        middle.translate(0.0f, progress * 0.5f + 0.375f, 0.0f);
        top.translate(0.0f, progress, 0.0f);
        drawCallback.accept(middle);
        drawCallback.accept(top);
    }

    private static void rotateToFacing(SuperByteBuffer buffer, Direction facing) {
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)buffer.center()).rotateYDegrees(AngleHelper.horizontalAngle((Direction)facing))).rotateXDegrees(facing == Direction.UP ? 0.0f : (facing == Direction.DOWN ? 180.0f : 90.0f))).uncenter();
    }

    static PortableStorageInterfaceBlockEntity getTargetPSI(MovementContext context) {
        String _workingPos_ = "WorkingPos";
        if (!context.data.contains(_workingPos_)) {
            return null;
        }
        BlockPos pos = NBTHelper.readBlockPos((CompoundTag)context.data, (String)_workingPos_);
        BlockEntity blockEntity = context.world.getBlockEntity(pos);
        if (!(blockEntity instanceof PortableStorageInterfaceBlockEntity)) {
            return null;
        }
        PortableStorageInterfaceBlockEntity psi = (PortableStorageInterfaceBlockEntity)blockEntity;
        if (!psi.isTransferring()) {
            return null;
        }
        return psi;
    }

    static PartialModel getMiddleForState(BlockState state, boolean lit) {
        if (AllBlocks.PORTABLE_FLUID_INTERFACE.has(state)) {
            return lit ? AllPartialModels.PORTABLE_FLUID_INTERFACE_MIDDLE_POWERED : AllPartialModels.PORTABLE_FLUID_INTERFACE_MIDDLE;
        }
        return lit ? AllPartialModels.PORTABLE_STORAGE_INTERFACE_MIDDLE_POWERED : AllPartialModels.PORTABLE_STORAGE_INTERFACE_MIDDLE;
    }

    static PartialModel getTopForState(BlockState state) {
        if (AllBlocks.PORTABLE_FLUID_INTERFACE.has(state)) {
            return AllPartialModels.PORTABLE_FLUID_INTERFACE_TOP;
        }
        return AllPartialModels.PORTABLE_STORAGE_INTERFACE_TOP;
    }
}
