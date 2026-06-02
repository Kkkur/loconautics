/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.content.contraptions.AbstractContraptionEntity
 *  com.simibubi.create.content.contraptions.render.ContraptionVisual
 *  dev.engine_room.flywheel.api.visualization.VisualEmbedding
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.lib.visual.AbstractEntityVisual
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.ChunkPos
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.contraptions;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import dev.engine_room.flywheel.api.visualization.VisualEmbedding;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visual.AbstractEntityVisual;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.neoforge.compatibility.flywheel.FlywheelCompatNeoForge;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.flywheel.EmbeddedEnvironmentExtension;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.joml.Matrix3fc;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ContraptionVisual.class})
public abstract class ContraptionVisualMixin
extends AbstractEntityVisual<AbstractContraptionEntity> {
    @Shadow
    @Final
    protected VisualEmbedding embedding;
    @Shadow
    @Final
    private PoseStack contraptionMatrix;

    public ContraptionVisualMixin(VisualizationContext ctx, AbstractContraptionEntity entity, float partialTick) {
        super(ctx, (Entity)entity, partialTick);
    }

    @Inject(method={"setEmbeddingMatrices"}, at={@At(value="HEAD")}, cancellable=true)
    private void sable$setEmbeddingMatrices(float partialTick, CallbackInfo ci) {
        int plotZ;
        SubLevelContainer container = SubLevelContainer.getContainer(((AbstractContraptionEntity)this.entity).level());
        if (container == null) {
            return;
        }
        ChunkPos chunkPos = ((AbstractContraptionEntity)this.entity).chunkPosition();
        boolean inBounds = container.inBounds(chunkPos);
        if (!inBounds) {
            return;
        }
        int plotX = (chunkPos.x >> container.getLogPlotSize()) - container.getOrigin().x;
        FlywheelCompatNeoForge.SubLevelFlwRenderState state = FlywheelCompatNeoForge.getInfo(ChunkPos.asLong((int)plotX, (int)(plotZ = (chunkPos.z >> container.getLogPlotSize()) - container.getOrigin().y)));
        if (state == null) {
            return;
        }
        Vec3i origin = this.renderOrigin();
        Vector3d pos = new Vector3d();
        if (((AbstractContraptionEntity)this.entity).isPrevPosInvalid()) {
            pos.x = ((AbstractContraptionEntity)this.entity).getX();
            pos.y = ((AbstractContraptionEntity)this.entity).getY();
            pos.z = ((AbstractContraptionEntity)this.entity).getZ();
        } else {
            pos.x = Mth.lerp((double)partialTick, (double)((AbstractContraptionEntity)this.entity).xo, (double)((AbstractContraptionEntity)this.entity).getX());
            pos.y = Mth.lerp((double)partialTick, (double)((AbstractContraptionEntity)this.entity).yo, (double)((AbstractContraptionEntity)this.entity).getY());
            pos.z = Mth.lerp((double)partialTick, (double)((AbstractContraptionEntity)this.entity).zo, (double)((AbstractContraptionEntity)this.entity).getZ());
        }
        ChunkPos centerChunk = state.centerChunk;
        PoseStack sceneMatrix = new PoseStack();
        sceneMatrix.translate((float)(pos.x - (double)centerChunk.getMinBlockX()), (float)pos.y, (float)(pos.z - (double)centerChunk.getMinBlockZ()));
        ((AbstractContraptionEntity)this.entity).applyLocalTransforms(sceneMatrix, partialTick);
        Pose3d renderPose = state.renderPose;
        renderPose.transformPosition(pos).sub((double)origin.getX(), (double)origin.getY(), (double)origin.getZ());
        this.contraptionMatrix.setIdentity();
        this.contraptionMatrix.translate(pos.x, pos.y, pos.z);
        this.contraptionMatrix.mulPose(new Quaternionf(renderPose.orientation()));
        ((AbstractContraptionEntity)this.entity).applyLocalTransforms(this.contraptionMatrix, partialTick);
        this.embedding.transforms((Matrix4fc)this.contraptionMatrix.last().pose(), (Matrix3fc)this.contraptionMatrix.last().normal());
        VisualEmbedding visualEmbedding = this.embedding;
        if (visualEmbedding instanceof EmbeddedEnvironmentExtension) {
            EmbeddedEnvironmentExtension extension = (EmbeddedEnvironmentExtension)visualEmbedding;
            extension.sable$setLightingInfo((Matrix4fc)sceneMatrix.last().pose(), state.sceneID, state.latestSkyLightScale / 15.0f);
        }
        ci.cancel();
    }
}
