/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexMultiConsumer
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderBuffers
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
 *  net.minecraft.client.resources.model.ModelBakery
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.BlockDestructionProgress
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.ryanhcode.sable.mixinhelpers.sublevel_render.vanilla;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelRenderDispatcher;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.SortedSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.level.block.entity.BlockEntity;

public class VanillaSubLevelBlockEntityRenderer
implements SubLevelRenderDispatcher.BlockEntityRenderer {
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    private final RenderBuffers renderBuffers;
    private final Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress;

    public VanillaSubLevelBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, RenderBuffers renderBuffers, Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress) {
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
        this.renderBuffers = renderBuffers;
        this.destructionProgress = destructionProgress;
    }

    @Override
    public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
        return this.blockEntityRenderDispatcher;
    }

    @Override
    public void renderSingleBE(BlockEntity blockEntity, PoseStack poseStack, float partialTick, double cameraX, double cameraY, double cameraZ) {
        int progress;
        BlockPos pos = blockEntity.getBlockPos();
        MultiBufferSource.BufferSource source = this.renderBuffers.bufferSource();
        poseStack.pushPose();
        poseStack.translate((double)pos.getX() - cameraX, (double)pos.getY() - cameraY, (double)pos.getZ() - cameraZ);
        SortedSet destructionProgresses = (SortedSet)this.destructionProgress.get(pos.asLong());
        if (destructionProgresses != null && !destructionProgresses.isEmpty() && (progress = ((BlockDestructionProgress)destructionProgresses.last()).getProgress()) >= 0) {
            PoseStack.Pose posestack$pose = poseStack.last();
            SheetedDecalTextureGenerator vertexconsumer = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get(progress)), posestack$pose, 1.0f);
            source = arg_0 -> this.lambda$renderSingleBE$0((VertexConsumer)vertexconsumer, arg_0);
        }
        this.blockEntityRenderDispatcher.render(blockEntity, partialTick, poseStack, (MultiBufferSource)source);
        poseStack.popPose();
    }

    private /* synthetic */ VertexConsumer lambda$renderSingleBE$0(VertexConsumer vertexconsumer, RenderType type) {
        VertexConsumer consumer = this.renderBuffers.bufferSource().getBuffer(type);
        return type.affectsCrumbling() ? VertexMultiConsumer.create((VertexConsumer)vertexconsumer, (VertexConsumer)consumer) : consumer;
    }
}
