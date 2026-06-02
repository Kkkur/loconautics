/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.levelWrappers.SchematicLevel
 *  net.createmod.catnip.render.ShadedBlockSbbBuilder
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.createmod.catnip.render.SuperRenderTypeBuffer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.BlockRenderDispatcher
 *  net.minecraft.client.renderer.block.ModelBlockRenderer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.neoforged.neoforge.client.model.data.ModelData
 */
package com.simibubi.create.content.schematics.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.render.BlockEntityRenderHelper;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.createmod.catnip.render.ShadedBlockSbbBuilder;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.client.model.data.ModelData;

public class SchematicRenderer {
    private static final ThreadLocal<ThreadLocalObjects> THREAD_LOCAL_OBJECTS = ThreadLocal.withInitial(ThreadLocalObjects::new);
    private final Map<RenderType, SuperByteBuffer> bufferCache = new LinkedHashMap<RenderType, SuperByteBuffer>(SchematicRenderer.getLayerCount());
    private boolean changed;
    protected final SchematicLevel schematic;
    private final BlockPos anchor;
    private final List<BlockEntity> renderedBlockEntities = new ArrayList<BlockEntity>();
    private final BitSet shouldRenderBlockEntities = new BitSet();
    private final BitSet scratchErroredBlockEntities = new BitSet();

    public SchematicRenderer(SchematicLevel world) {
        this.anchor = world.anchor;
        this.schematic = world;
        this.changed = true;
        for (BlockEntity renderedBlockEntity : this.schematic.getRenderedBlockEntities()) {
            this.renderedBlockEntities.add(renderedBlockEntity);
        }
        this.shouldRenderBlockEntities.set(0, this.renderedBlockEntities.size());
    }

    public void update() {
        this.changed = true;
    }

    public void render(PoseStack ms, SuperRenderTypeBuffer buffers) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        if (this.changed) {
            this.redraw();
        }
        this.changed = false;
        this.bufferCache.forEach((layer, buffer) -> buffer.renderInto(ms, buffers.getBuffer(layer)));
        this.scratchErroredBlockEntities.clear();
        BlockEntityRenderHelper.renderBlockEntities(this.renderedBlockEntities, this.shouldRenderBlockEntities, this.scratchErroredBlockEntities, null, (Level)this.schematic, ms, null, (MultiBufferSource)buffers, AnimationTickHolder.getPartialTicks());
        this.shouldRenderBlockEntities.andNot(this.scratchErroredBlockEntities);
    }

    protected void redraw() {
        this.bufferCache.clear();
        for (RenderType layer : RenderType.chunkBufferLayers()) {
            SuperByteBuffer buffer = this.drawLayer(layer);
            if (buffer.isEmpty()) continue;
            this.bufferCache.put(layer, buffer);
        }
    }

    protected SuperByteBuffer drawLayer(RenderType layer) {
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer renderer = dispatcher.getModelRenderer();
        ThreadLocalObjects objects = THREAD_LOCAL_OBJECTS.get();
        PoseStack poseStack = objects.poseStack;
        RandomSource random = objects.random;
        BlockPos.MutableBlockPos mutableBlockPos = objects.mutableBlockPos;
        SchematicLevel renderWorld = this.schematic;
        BoundingBox bounds = renderWorld.getBounds();
        ShadedBlockSbbBuilder sbbBuilder = objects.sbbBuilder;
        sbbBuilder.begin();
        renderWorld.renderMode = true;
        ModelBlockRenderer.enableCaching();
        for (BlockPos localPos : BlockPos.betweenClosed((int)bounds.minX(), (int)bounds.minY(), (int)bounds.minZ(), (int)bounds.maxX(), (int)bounds.maxY(), (int)bounds.maxZ())) {
            BlockPos.MutableBlockPos pos = mutableBlockPos.setWithOffset((Vec3i)localPos, (Vec3i)this.anchor);
            BlockState state = renderWorld.getBlockState((BlockPos)pos);
            if (state.getRenderShape() != RenderShape.MODEL) continue;
            BakedModel model = dispatcher.getBlockModel(state);
            BlockEntity blockEntity = renderWorld.getBlockEntity(localPos);
            ModelData modelData = blockEntity != null ? blockEntity.getModelData() : ModelData.EMPTY;
            modelData = model.getModelData((BlockAndTintGetter)renderWorld, (BlockPos)pos, state, modelData);
            long seed = state.getSeed((BlockPos)pos);
            random.setSeed(seed);
            if (!model.getRenderTypes(state, random, modelData).contains(layer)) continue;
            poseStack.pushPose();
            poseStack.translate((float)localPos.getX(), (float)localPos.getY(), (float)localPos.getZ());
            renderer.tesselateBlock((BlockAndTintGetter)renderWorld, model, state, (BlockPos)pos, poseStack, (VertexConsumer)sbbBuilder, true, random, seed, OverlayTexture.NO_OVERLAY, modelData, layer);
            poseStack.popPose();
        }
        ModelBlockRenderer.clearCache();
        renderWorld.renderMode = false;
        return sbbBuilder.end();
    }

    private static int getLayerCount() {
        return RenderType.chunkBufferLayers().size();
    }

    private static class ThreadLocalObjects {
        public final PoseStack poseStack = new PoseStack();
        public final RandomSource random = RandomSource.createNewThreadLocalInstance();
        public final BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        public final ShadedBlockSbbBuilder sbbBuilder = ShadedBlockSbbBuilder.create();

        private ThreadLocalObjects() {
        }
    }
}
