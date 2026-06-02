/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.instance.Instance
 *  dev.engine_room.flywheel.api.instance.Instancer
 *  dev.engine_room.flywheel.api.material.CardinalLightingMode
 *  dev.engine_room.flywheel.api.material.Material
 *  dev.engine_room.flywheel.api.model.Model
 *  dev.engine_room.flywheel.api.task.Plan
 *  dev.engine_room.flywheel.api.visual.BlockEntityVisual
 *  dev.engine_room.flywheel.api.visual.DynamicVisual
 *  dev.engine_room.flywheel.api.visual.DynamicVisual$Context
 *  dev.engine_room.flywheel.api.visual.SectionTrackedVisual$SectionCollector
 *  dev.engine_room.flywheel.api.visual.ShaderLightVisual
 *  dev.engine_room.flywheel.api.visual.TickableVisual
 *  dev.engine_room.flywheel.api.visual.TickableVisual$Context
 *  dev.engine_room.flywheel.api.visual.Visual
 *  dev.engine_room.flywheel.api.visualization.BlockEntityVisualizer
 *  dev.engine_room.flywheel.api.visualization.VisualEmbedding
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.api.visualization.VisualizerRegistry
 *  dev.engine_room.flywheel.lib.instance.InstanceTypes
 *  dev.engine_room.flywheel.lib.instance.TransformedInstance
 *  dev.engine_room.flywheel.lib.material.SimpleMaterial
 *  dev.engine_room.flywheel.lib.model.ModelUtil
 *  dev.engine_room.flywheel.lib.model.SimpleModel
 *  dev.engine_room.flywheel.lib.model.baked.BlockModelBuilder
 *  dev.engine_room.flywheel.lib.task.ForEachPlan
 *  dev.engine_room.flywheel.lib.task.NestedPlan
 *  dev.engine_room.flywheel.lib.task.PlanMap
 *  dev.engine_room.flywheel.lib.task.RunnablePlan
 *  dev.engine_room.flywheel.lib.visual.AbstractEntityVisual
 *  it.unimi.dsi.fastutil.longs.LongArraySet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateHolder
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.AABB
 *  org.apache.commons.lang3.tuple.MutablePair
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4fc
 */
package com.simibubi.create.content.contraptions.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.contraptions.render.ClientContraption;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedBlockAndTintGetter;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.material.CardinalLightingMode;
import dev.engine_room.flywheel.api.material.Material;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.task.Plan;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visual.SectionTrackedVisual;
import dev.engine_room.flywheel.api.visual.ShaderLightVisual;
import dev.engine_room.flywheel.api.visual.TickableVisual;
import dev.engine_room.flywheel.api.visual.Visual;
import dev.engine_room.flywheel.api.visualization.BlockEntityVisualizer;
import dev.engine_room.flywheel.api.visualization.VisualEmbedding;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizerRegistry;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.material.SimpleMaterial;
import dev.engine_room.flywheel.lib.model.ModelUtil;
import dev.engine_room.flywheel.lib.model.SimpleModel;
import dev.engine_room.flywheel.lib.model.baked.BlockModelBuilder;
import dev.engine_room.flywheel.lib.task.ForEachPlan;
import dev.engine_room.flywheel.lib.task.NestedPlan;
import dev.engine_room.flywheel.lib.task.PlanMap;
import dev.engine_room.flywheel.lib.task.RunnablePlan;
import dev.engine_room.flywheel.lib.visual.AbstractEntityVisual;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.MutablePair;
import org.joml.Matrix3fc;
import org.joml.Matrix4fc;

public class ContraptionVisual<E extends AbstractContraptionEntity>
extends AbstractEntityVisual<E>
implements DynamicVisual,
TickableVisual,
ShaderLightVisual {
    protected static final int DEFAULT_LIGHT_PADDING = 1;
    protected final VisualEmbedding embedding;
    protected final List<BlockEntityVisual<?>> children = new ArrayList();
    protected final List<ActorVisual> actors = new ArrayList<ActorVisual>();
    protected final PlanMap<DynamicVisual, DynamicVisual.Context> dynamicVisuals = new PlanMap();
    protected final PlanMap<TickableVisual, TickableVisual.Context> tickableVisuals = new PlanMap();
    protected TransformedInstance structure;
    protected SectionTrackedVisual.SectionCollector sectionCollector;
    protected long minSection;
    protected long maxSection;
    protected int lightPaddingBlocks = 1;
    protected int lastStructureVersion;
    protected int lastVersionChildren;
    private final PoseStack contraptionMatrix = new PoseStack();

    public ContraptionVisual(VisualizationContext ctx, E entity, float partialTick) {
        super(ctx, entity, partialTick);
        this.embedding = ctx.createEmbedding(Vec3i.ZERO);
        this.setEmbeddingMatrices(partialTick);
        Contraption contraption = ((AbstractContraptionEntity)((Object)entity)).getContraption();
        if (contraption == null) {
            return;
        }
        ClientContraption clientContraption = contraption.getOrCreateClientContraptionLazy();
        this.setupStructure(clientContraption);
        this.setupChildren(contraption, clientContraption, partialTick);
    }

    private void setupStructure(ClientContraption clientContraption) {
        VirtualRenderWorld renderLevel = clientContraption.getRenderLevel();
        final ClientContraption.RenderedBlocks blocks = clientContraption.getRenderedBlocks();
        WrappedBlockAndTintGetter modelWorld = new WrappedBlockAndTintGetter(this, (BlockAndTintGetter)renderLevel){

            @Override
            public BlockState getBlockState(BlockPos pos) {
                return blocks.lookup().apply(pos);
            }
        };
        SimpleModel model = new BlockModelBuilder((BlockAndTintGetter)modelWorld, blocks.positions()).materialFunc((renderType, shaded, ao) -> {
            Material material = ModelUtil.getMaterial((RenderType)renderType, (boolean)shaded, (boolean)ao);
            if (material != null && material.cardinalLightingMode() == CardinalLightingMode.ENTITY) {
                return SimpleMaterial.builderOf((Material)material).cardinalLightingMode(CardinalLightingMode.CHUNK).build();
            }
            return material;
        }).build();
        Instancer instancer = this.embedding.instancerProvider().instancer(InstanceTypes.TRANSFORMED, (Model)model);
        if (this.structure == null) {
            this.structure = (TransformedInstance)instancer.createInstance();
        } else {
            instancer.stealInstance((Instance)this.structure);
        }
        this.structure.setChanged();
        this.lastStructureVersion = clientContraption.structureVersion();
    }

    private void setupChildren(Contraption contraption, ClientContraption clientContraption, float partialTick) {
        this.children.forEach(Visual::delete);
        this.children.clear();
        this.dynamicVisuals.clear();
        this.tickableVisuals.clear();
        for (BlockEntity be : clientContraption.renderedBlockEntityView) {
            this.setupVisualizer(be, partialTick);
        }
        VirtualRenderWorld renderLevel = clientContraption.getRenderLevel();
        this.actors.forEach(ActorVisual::delete);
        this.actors.clear();
        for (MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor : contraption.getActors()) {
            this.setupActor(actor, renderLevel);
        }
        this.lastVersionChildren = clientContraption.childrenVersion();
    }

    protected <T extends BlockEntity> void setupVisualizer(T be, float partialTicks) {
        BlockEntityVisualizer visualizer = VisualizerRegistry.getVisualizer((BlockEntityType)be.getType());
        if (visualizer == null) {
            return;
        }
        BlockEntityVisual visual = visualizer.createVisual((VisualizationContext)this.embedding, be, partialTicks);
        this.children.add(visual);
        if (visual instanceof DynamicVisual) {
            DynamicVisual dynamic = (DynamicVisual)visual;
            this.dynamicVisuals.add((Object)dynamic, dynamic.planFrame());
        }
        if (visual instanceof TickableVisual) {
            TickableVisual tickable = (TickableVisual)visual;
            this.tickableVisuals.add((Object)tickable, tickable.planTick());
        }
    }

    protected void setupActor(MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor, VirtualRenderWorld renderLevel) {
        StructureTemplate.StructureBlockInfo blockInfo;
        MovementBehaviour movementBehaviour;
        MovementContext context = (MovementContext)actor.getRight();
        if (context == null) {
            return;
        }
        if (context.world == null) {
            context.world = this.level;
        }
        if ((movementBehaviour = MovementBehaviour.REGISTRY.get((StateHolder<Block, ?>)(blockInfo = (StructureTemplate.StructureBlockInfo)actor.getLeft()).state())) == null) {
            return;
        }
        ActorVisual visual = movementBehaviour.createVisual((VisualizationContext)this.embedding, renderLevel, context);
        if (visual == null) {
            return;
        }
        this.actors.add(visual);
    }

    public Plan<TickableVisual.Context> planTick() {
        return NestedPlan.of((Plan[])new Plan[]{ForEachPlan.of(() -> this.actors, ActorVisual::tick), this.tickableVisuals});
    }

    public Plan<DynamicVisual.Context> planFrame() {
        return RunnablePlan.of(this::beginFrame).then((Plan)NestedPlan.of((Plan[])new Plan[]{ForEachPlan.of(() -> this.actors, ActorVisual::beginFrame), this.dynamicVisuals}));
    }

    protected void beginFrame(DynamicVisual.Context context) {
        float partialTick = context.partialTick();
        this.setEmbeddingMatrices(partialTick);
        this.checkAndUpdateLightSections();
        Contraption contraption = ((AbstractContraptionEntity)this.entity).getContraption();
        ClientContraption clientContraption = contraption.getOrCreateClientContraptionLazy();
        if (this.lastStructureVersion != clientContraption.structureVersion()) {
            this.setupStructure(clientContraption);
        }
        if (this.lastVersionChildren != clientContraption.childrenVersion()) {
            this.setupChildren(contraption, clientContraption, partialTick);
        }
    }

    private void setEmbeddingMatrices(float partialTick) {
        double z;
        double y;
        double x;
        Vec3i origin = this.renderOrigin();
        if (((AbstractContraptionEntity)this.entity).isPrevPosInvalid()) {
            x = ((AbstractContraptionEntity)this.entity).getX() - (double)origin.getX();
            y = ((AbstractContraptionEntity)this.entity).getY() - (double)origin.getY();
            z = ((AbstractContraptionEntity)this.entity).getZ() - (double)origin.getZ();
        } else {
            x = Mth.lerp((double)partialTick, (double)((AbstractContraptionEntity)this.entity).xo, (double)((AbstractContraptionEntity)this.entity).getX()) - (double)origin.getX();
            y = Mth.lerp((double)partialTick, (double)((AbstractContraptionEntity)this.entity).yo, (double)((AbstractContraptionEntity)this.entity).getY()) - (double)origin.getY();
            z = Mth.lerp((double)partialTick, (double)((AbstractContraptionEntity)this.entity).zo, (double)((AbstractContraptionEntity)this.entity).getZ()) - (double)origin.getZ();
        }
        this.contraptionMatrix.setIdentity();
        this.contraptionMatrix.translate(x, y, z);
        ((AbstractContraptionEntity)this.entity).applyLocalTransforms(this.contraptionMatrix, partialTick);
        this.embedding.transforms((Matrix4fc)this.contraptionMatrix.last().pose(), (Matrix3fc)this.contraptionMatrix.last().normal());
    }

    public void setSectionCollector(SectionTrackedVisual.SectionCollector collector) {
        this.sectionCollector = collector;
        this.checkAndUpdateLightSections();
    }

    private void checkAndUpdateLightSections() {
        AABB boundingBox = ((AbstractContraptionEntity)this.entity).getBoundingBox();
        int minSectionX = SectionPos.blockToSectionCoord((int)(Mth.floor((double)boundingBox.minX) - this.lightPaddingBlocks));
        int minSectionY = SectionPos.blockToSectionCoord((int)(Mth.floor((double)boundingBox.minY) - this.lightPaddingBlocks));
        int minSectionZ = SectionPos.blockToSectionCoord((int)(Mth.floor((double)boundingBox.minZ) - this.lightPaddingBlocks));
        int maxSectionX = SectionPos.blockToSectionCoord((int)(Mth.ceil((double)boundingBox.maxX) + this.lightPaddingBlocks));
        int maxSectionY = SectionPos.blockToSectionCoord((int)(Mth.ceil((double)boundingBox.maxY) + this.lightPaddingBlocks));
        int maxSectionZ = SectionPos.blockToSectionCoord((int)(Mth.ceil((double)boundingBox.maxZ) + this.lightPaddingBlocks));
        if (this.minSection == SectionPos.asLong((int)minSectionX, (int)minSectionY, (int)minSectionZ) && this.maxSection == SectionPos.asLong((int)maxSectionX, (int)maxSectionY, (int)maxSectionZ)) {
            return;
        }
        this.minSection = SectionPos.asLong((int)minSectionX, (int)minSectionY, (int)minSectionZ);
        this.maxSection = SectionPos.asLong((int)maxSectionX, (int)maxSectionY, (int)maxSectionZ);
        LongArraySet longSet = new LongArraySet();
        for (int x = minSectionX; x <= maxSectionX; ++x) {
            for (int y = minSectionY; y <= maxSectionY; ++y) {
                for (int z = minSectionZ; z <= maxSectionZ; ++z) {
                    longSet.add(SectionPos.asLong((int)x, (int)y, (int)z));
                }
            }
        }
        this.sectionCollector.sections((LongSet)longSet);
    }

    protected void _delete() {
        this.children.forEach(Visual::delete);
        this.actors.forEach(ActorVisual::delete);
        if (this.structure != null) {
            this.structure.delete();
        }
        this.embedding.delete();
    }
}
