/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.visual.BlockEntityVisual
 *  dev.engine_room.flywheel.api.visual.LightUpdatedVisual
 *  dev.engine_room.flywheel.api.visualization.BlockEntityVisualizer
 *  dev.engine_room.flywheel.api.visualization.VisualEmbedding
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.impl.visualization.storage.BlockEntityStorage
 *  dev.engine_room.flywheel.impl.visualization.storage.Storage
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.flywheel;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visual.LightUpdatedVisual;
import dev.engine_room.flywheel.api.visualization.BlockEntityVisualizer;
import dev.engine_room.flywheel.api.visualization.VisualEmbedding;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.impl.visualization.storage.BlockEntityStorage;
import dev.engine_room.flywheel.impl.visualization.storage.Storage;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.flywheel.SubLevelEmbedding;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.flywheel.BlockEntityStorageExtension;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.flywheel.EmbeddedEnvironmentExtension;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={BlockEntityStorage.class}, remap=false)
public abstract class BlockEntityStorageMixin
extends Storage<BlockEntity>
implements BlockEntityStorageExtension {
    @Unique
    private final Quaternionf sable$orientationStorage = new Quaternionf();
    @Unique
    private final Vector3d sable$localOffsetStorage = new Vector3d();
    @Unique
    private final Map<ClientSubLevel, SubLevelEmbedding> sable$subLevelEmbeddings = new Object2ObjectOpenHashMap();
    @Unique
    private final Matrix3f sable$normalMatStorage = new Matrix3f();
    @Shadow
    @Final
    private Long2ObjectMap<BlockEntityVisual<?>> posLookup;
    @Unique
    private VisualizationContext sable$planVisualizationContext;

    @Override
    public void sable$setPlanVisualizationContext(VisualizationContext visualizationContext) {
        this.sable$planVisualizationContext = visualizationContext;
    }

    @Override
    public SubLevelEmbedding sable$getEmbeddingInfo(SubLevel subLevel) {
        if (!(subLevel instanceof ClientSubLevel)) {
            throw new IllegalArgumentException("SubLevel must be a ClientSubLevel");
        }
        ClientSubLevel clientSubLevel = (ClientSubLevel)subLevel;
        return this.sable$subLevelEmbeddings.get(clientSubLevel);
    }

    @Override
    public void sable$preFlywheelFrame() {
        this.sable$updateSubLevelEmbeddingsFrame(this.sable$planVisualizationContext);
    }

    @Unique
    private void sable$updateSubLevelEmbeddingsFrame(VisualizationContext visualizationContext) {
        Iterator<Map.Entry<ClientSubLevel, SubLevelEmbedding>> iter = this.sable$subLevelEmbeddings.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<ClientSubLevel, SubLevelEmbedding> entry = iter.next();
            SubLevelEmbedding subLevelEmbedding = entry.getValue();
            ClientSubLevel subLevel = entry.getKey();
            if (subLevel.isRemoved()) {
                this.sable$onEmbeddingRemoved(subLevel);
                iter.remove();
                continue;
            }
            if (subLevel.getLatestSkyLightScale() != subLevelEmbedding.latestSkyLightScale()) {
                for (BlockEntity be : subLevelEmbedding.blockEntities()) {
                    BlockEntityVisual visual = (BlockEntityVisual)this.posLookup.get(be.getBlockPos().asLong());
                    if (!(visual instanceof LightUpdatedVisual)) continue;
                    LightUpdatedVisual lightUpdatedVisual = (LightUpdatedVisual)visual;
                    lightUpdatedVisual.updateLight(0.0f);
                }
                subLevelEmbedding.setLatestSkyLightScale(subLevel.getLatestSkyLightScale());
            }
            this.sable$updateEmbeddingTransforms(visualizationContext, subLevel, subLevelEmbedding.embedding());
        }
    }

    @Unique
    private void sable$onEmbeddingRemoved(ClientSubLevel subLevel) {
        SubLevelEmbedding subLevelEmbedding = this.sable$subLevelEmbeddings.get(subLevel);
        VisualizationManager manager = VisualizationManager.get((LevelAccessor)subLevel.getLevel());
        if (manager != null) {
            for (BlockEntity blockEntity : subLevelEmbedding.blockEntities()) {
                manager.blockEntities().queueRemove((Object)blockEntity);
            }
        }
        subLevelEmbedding.embedding().delete();
    }

    @Unique
    private void sable$updateEmbeddingTransforms(VisualizationContext visualizationContext, ClientSubLevel subLevel, VisualEmbedding embedding) {
        ClientSubLevelContainer container = SubLevelContainer.getContainer(subLevel.getLevel());
        assert (container != null);
        Pose3dc renderPose = subLevel.renderPose();
        Vector3dc rotationPoint = renderPose.rotationPoint();
        Vector3dc position = renderPose.position();
        Matrix4f transformation = new Matrix4f();
        Vec3i parentOrigin = visualizationContext.renderOrigin();
        transformation.setTranslation((float)(position.x() - (double)parentOrigin.getX()), (float)(position.y() - (double)parentOrigin.getY()), (float)(position.z() - (double)parentOrigin.getZ()));
        transformation.rotate((Quaternionfc)this.sable$orientationStorage.set(renderPose.orientation()));
        Vec3i localOrigin = embedding.renderOrigin();
        Vector3d localOffset = rotationPoint.sub((double)localOrigin.getX(), (double)localOrigin.getY(), (double)localOrigin.getZ(), this.sable$localOffsetStorage);
        transformation.translate((float)(-localOffset.x), (float)(-localOffset.y), (float)(-localOffset.z));
        Matrix3f normal = transformation.normal(this.sable$normalMatStorage);
        embedding.transforms((Matrix4fc)transformation, (Matrix3fc)normal);
        PoseStack sceneMatrix = new PoseStack();
        ChunkPos centerChunk = subLevel.getPlot().getCenterChunk();
        sceneMatrix.translate((float)(localOrigin.getX() - centerChunk.getMinBlockX()), (float)localOrigin.getY(), (float)(localOrigin.getZ() - centerChunk.getMinBlockZ()));
        if (embedding instanceof EmbeddedEnvironmentExtension) {
            EmbeddedEnvironmentExtension embeddedEnvironment = (EmbeddedEnvironmentExtension)embedding;
            embeddedEnvironment.sable$setLightingInfo((Matrix4fc)sceneMatrix.last().pose(), container.getLightingSceneId(subLevel), (float)subLevel.getLatestSkyLightScale() / 15.0f);
        }
    }

    @Unique
    private VisualEmbedding sable$getOrCreateSubLevelEmbedding(VisualizationContext visualizationContext, ClientSubLevel subLevel) {
        SubLevelEmbedding existingSubLevelEmbedding = this.sable$subLevelEmbeddings.get(subLevel);
        if (existingSubLevelEmbedding != null) {
            return existingSubLevelEmbedding.embedding();
        }
        VisualEmbedding newEmbedding = visualizationContext.createEmbedding((Vec3i)subLevel.getPlot().getCenterBlock());
        this.sable$subLevelEmbeddings.put(subLevel, new SubLevelEmbedding(newEmbedding, (List<BlockEntity>)new ObjectArrayList(), subLevel.getLatestSkyLightScale()));
        this.sable$updateEmbeddingTransforms(visualizationContext, subLevel, newEmbedding);
        return newEmbedding;
    }

    @WrapOperation(method={"createRaw(Ldev/engine_room/flywheel/api/visualization/VisualizationContext;Lnet/minecraft/world/level/block/entity/BlockEntity;F)Ldev/engine_room/flywheel/api/visual/BlockEntityVisual;"}, at={@At(value="INVOKE", target="Ldev/engine_room/flywheel/api/visualization/BlockEntityVisualizer;createVisual(Ldev/engine_room/flywheel/api/visualization/VisualizationContext;Lnet/minecraft/world/level/block/entity/BlockEntity;F)Ldev/engine_room/flywheel/api/visual/BlockEntityVisual;")})
    public BlockEntityVisual<?> sable$createVisual(BlockEntityVisualizer instance, VisualizationContext visualizationContext, BlockEntity blockEntity, float partialTick, Operation<BlockEntityVisual<?>> original) {
        SubLevel subLevel = Sable.HELPER.getContaining(blockEntity);
        if (subLevel == null) {
            return (BlockEntityVisual)original.call(new Object[]{instance, visualizationContext, blockEntity, Float.valueOf(partialTick)});
        }
        assert (subLevel instanceof ClientSubLevel);
        VisualEmbedding embedding = this.sable$getOrCreateSubLevelEmbedding(visualizationContext, (ClientSubLevel)subLevel);
        BlockEntityVisual newVisual = (BlockEntityVisual)original.call(new Object[]{instance, embedding, blockEntity, Float.valueOf(partialTick)});
        this.sable$subLevelEmbeddings.get(subLevel).blockEntities().add(blockEntity);
        return newVisual;
    }

    public void remove(BlockEntity blockEntity) {
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient(blockEntity);
        if (subLevel != null && this.sable$subLevelEmbeddings.containsKey(subLevel)) {
            this.sable$subLevelEmbeddings.get(subLevel).blockEntities().remove(blockEntity);
        }
        super.remove((Object)blockEntity);
    }

    public void recreateAll(VisualizationContext visualizationContext, float partialTick) {
        this.sable$subLevelEmbeddings.clear();
        super.recreateAll(visualizationContext, partialTick);
    }
}
