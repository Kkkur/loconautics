/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.render.ShadedBlockSbbBuilder
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.createmod.catnip.render.SuperByteBufferCache
 *  net.createmod.catnip.render.SuperByteBufferCache$Compartment
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.BlockRenderDispatcher
 *  net.minecraft.client.renderer.block.ModelBlockRenderer
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateHolder
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.simibubi.create.content.contraptions.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ClientContraption;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.render.BlockEntityRenderHelper;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.BitSet;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.ShadedBlockSbbBuilder;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.apache.commons.lang3.tuple.Pair;

public class ContraptionEntityRenderer<C extends AbstractContraptionEntity>
extends EntityRenderer<C> {
    public static final SuperByteBufferCache.Compartment<Pair<Contraption, RenderType>> CONTRAPTION = new SuperByteBufferCache.Compartment();
    private static final ThreadLocal<ThreadLocalObjects> THREAD_LOCAL_OBJECTS = ThreadLocal.withInitial(ThreadLocalObjects::new);

    public ContraptionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public static SuperByteBuffer getBuffer(Contraption contraption, VirtualRenderWorld renderWorld, RenderType renderType) {
        return SuperByteBufferCache.getInstance().get(CONTRAPTION, (Object)Pair.of((Object)contraption, (Object)renderType), () -> ContraptionEntityRenderer.buildStructureBuffer(contraption, renderWorld, renderType));
    }

    private static SuperByteBuffer buildStructureBuffer(Contraption contraption, VirtualRenderWorld renderWorld, RenderType layer) {
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer renderer = dispatcher.getModelRenderer();
        ThreadLocalObjects objects = THREAD_LOCAL_OBJECTS.get();
        PoseStack poseStack = objects.poseStack;
        RandomSource random = objects.random;
        ClientContraption clientContraption = contraption.getOrCreateClientContraptionLazy();
        ClientContraption.RenderedBlocks blocks = clientContraption.getRenderedBlocks();
        ShadedBlockSbbBuilder sbbBuilder = objects.sbbBuilder;
        sbbBuilder.begin();
        ModelBlockRenderer.enableCaching();
        for (BlockPos pos : blocks.positions()) {
            BlockState state = blocks.lookup().apply(pos);
            if (state.getRenderShape() != RenderShape.MODEL) continue;
            BakedModel model = dispatcher.getBlockModel(state);
            ModelData modelData = renderWorld.getModelData(pos);
            modelData = model.getModelData((BlockAndTintGetter)renderWorld, pos, state, modelData);
            long randomSeed = state.getSeed(pos);
            random.setSeed(randomSeed);
            if (!model.getRenderTypes(state, random, modelData).contains(layer)) continue;
            poseStack.pushPose();
            poseStack.translate((float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
            renderer.tesselateBlock((BlockAndTintGetter)renderWorld, model, state, pos, poseStack, (VertexConsumer)sbbBuilder, true, random, randomSeed, OverlayTexture.NO_OVERLAY, modelData, layer);
            poseStack.popPose();
        }
        ModelBlockRenderer.clearCache();
        return sbbBuilder.end();
    }

    public ResourceLocation getTextureLocation(C entity) {
        return null;
    }

    public boolean shouldRender(C entity, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
        if (((AbstractContraptionEntity)((Object)entity)).getContraption() == null) {
            return false;
        }
        if (!((AbstractContraptionEntity)((Object)entity)).isAliveOrStale()) {
            return false;
        }
        if (!((AbstractContraptionEntity)((Object)entity)).isReadyForRender()) {
            return false;
        }
        return super.shouldRender(entity, frustum, cameraX, cameraY, cameraZ);
    }

    public void render(C entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int overlay) {
        super.render(entity, yaw, partialTicks, poseStack, buffers, overlay);
        Contraption contraption = ((AbstractContraptionEntity)((Object)entity)).getContraption();
        if (contraption == null) {
            return;
        }
        Level level = entity.level();
        ClientContraption clientContraption = contraption.getOrCreateClientContraptionLazy();
        VirtualRenderWorld renderWorld = clientContraption.getRenderLevel();
        ContraptionMatrices matrices = clientContraption.getMatrices();
        matrices.setup(poseStack, (AbstractContraptionEntity)((Object)entity));
        if (!VisualizationManager.supportsVisualization((LevelAccessor)level)) {
            for (RenderType renderType : RenderType.chunkBufferLayers()) {
                SuperByteBuffer sbb = ContraptionEntityRenderer.getBuffer(contraption, renderWorld, renderType);
                if (sbb.isEmpty()) continue;
                VertexConsumer vc = buffers.getBuffer(renderType);
                ((SuperByteBuffer)sbb.transform(matrices.getModel())).useLevelLight((BlockAndTintGetter)level, matrices.getWorld()).renderInto(poseStack, vc);
            }
        }
        BitSet adjustRenderedBlockEntities = clientContraption.getAndAdjustShouldRenderBlockEntities();
        clientContraption.scratchErroredBlockEntities.clear();
        BlockEntityRenderHelper.renderBlockEntities(clientContraption.renderedBlockEntityView, adjustRenderedBlockEntities, clientContraption.scratchErroredBlockEntities, renderWorld, level, matrices.getModelViewProjection(), matrices.getLight(), buffers, AnimationTickHolder.getPartialTicks());
        clientContraption.shouldRenderBlockEntities.andNot(clientContraption.scratchErroredBlockEntities);
        ContraptionEntityRenderer.renderActors(level, renderWorld, contraption, matrices, buffers);
        matrices.clear();
    }

    private static void renderActors(Level level, VirtualRenderWorld renderWorld, Contraption c, ContraptionMatrices matrices, MultiBufferSource buffer) {
        PoseStack m = matrices.getModel();
        for (Pair pair : c.getActors()) {
            StructureTemplate.StructureBlockInfo blockInfo;
            MovementBehaviour movementBehaviour;
            MovementContext context = (MovementContext)pair.getRight();
            if (context == null) continue;
            if (context.world == null) {
                context.world = level;
            }
            if ((movementBehaviour = MovementBehaviour.REGISTRY.get((StateHolder<Block, ?>)(blockInfo = (StructureTemplate.StructureBlockInfo)pair.getLeft()).state())) == null || c.isHiddenInPortal(blockInfo.pos())) continue;
            m.pushPose();
            TransformStack.of((PoseStack)m).translate((Vec3i)blockInfo.pos());
            movementBehaviour.renderInContraption(context, renderWorld, matrices, buffer);
            m.popPose();
        }
    }

    private static class ThreadLocalObjects {
        public final PoseStack poseStack = new PoseStack();
        public final RandomSource random = RandomSource.createNewThreadLocalInstance();
        public final ShadedBlockSbbBuilder sbbBuilder = ShadedBlockSbbBuilder.create();

        private ThreadLocalObjects() {
        }
    }
}
