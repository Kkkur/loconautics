/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.math.OrientedBoundingBox3d
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  dev.ryanhcode.sable.sublevel.ClientSubLevel
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Matrix4f
 *  org.joml.Vector2d
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.merging_glue;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.merging_glue.MergingGlueBlockEntity;
import dev.simulated_team.simulated.content.blocks.spring.SpringBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class MergingGlueRenderer
extends SmartBlockEntityRenderer<MergingGlueBlockEntity> {
    public MergingGlueRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(MergingGlueBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        Vector3d upB;
        if (!be.isController()) {
            return;
        }
        MergingGlueBlockEntity other = be.getPartnerGlue();
        if (other == null) {
            return;
        }
        ClientSubLevel otherSubLevel = Sable.HELPER.getContainingClient((BlockEntity)other);
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((BlockEntity)be);
        BlockPos blockPos = be.getBlockPos();
        Vector3d center = be.getCenter(new Vector3d());
        Vector3d otherCenter = other.getCenter(new Vector3d());
        BlockState state = be.getBlockState();
        Direction facing = (Direction)state.getValue((Property)SpringBlock.FACING);
        Direction otherFacing = (Direction)other.getBlockState().getValue((Property)SpringBlock.FACING);
        Vector3d normalA = JOMLConversion.atLowerCornerOf((Vec3i)facing.getNormal());
        Vector3d normalB = JOMLConversion.atLowerCornerOf((Vec3i)otherFacing.getNormal());
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutout((ResourceLocation)Simulated.path("textures/block/merging_glue/strand.png")));
        Pose3dc renderPose = subLevel != null ? subLevel.renderPose() : null;
        Pose3dc otherRenderPose = otherSubLevel != null ? otherSubLevel.renderPose() : null;
        boolean horizontal = facing.getAxis().isHorizontal();
        Vector3dc rightA = horizontal ? JOMLConversion.atLowerCornerOf((Vec3i)facing.getClockWise().getNormal(), (Vector3d)new Vector3d()) : OrientedBoundingBox3d.FORWARD;
        Vector3d rightB = horizontal ? JOMLConversion.atLowerCornerOf((Vec3i)otherFacing.getCounterClockWise().getNormal(), (Vector3d)new Vector3d()) : new Vector3d(OrientedBoundingBox3d.FORWARD);
        Vector3dc upA = horizontal ? new Vector3d(0.0, 1.0, 0.0) : OrientedBoundingBox3d.RIGHT;
        Vector3d vector3d = upB = horizontal ? new Vector3d(0.0, 1.0, 0.0) : new Vector3d(OrientedBoundingBox3d.RIGHT);
        if (otherRenderPose != null) {
            otherRenderPose.transformNormal(normalB);
            otherRenderPose.transformNormal(rightB);
            otherRenderPose.transformNormal(upB);
            otherRenderPose.transformPosition(otherCenter);
        }
        if (renderPose != null) {
            renderPose.transformNormalInverse(normalB);
            renderPose.transformNormalInverse(rightB);
            renderPose.transformNormalInverse(upB);
            renderPose.transformPositionInverse(otherCenter);
        }
        Vector3d strandCenterA = center.sub((Vector3dc)JOMLConversion.atLowerCornerOf((Vec3i)blockPos), new Vector3d());
        Vector3d strandCenterB = otherCenter.sub((Vector3dc)JOMLConversion.atLowerCornerOf((Vec3i)blockPos), new Vector3d());
        Vector3d strandPosA = new Vector3d();
        Vector3d strandPosB = new Vector3d();
        Vector2d[] strandPositions = new Vector2d[]{new Vector2d(0.25, 0.25), new Vector2d(0.45, 0.3), new Vector2d(0.6, 0.6), new Vector2d(0.65, 0.7)};
        for (int i = 0; i < 2; ++i) {
            Vector2d strandA = strandPositions[i * 2].sub(0.5, 0.5, new Vector2d()).mul(0.75);
            Vector2d strandB = strandPositions[i * 2 + 1].sub(0.5, 0.5, new Vector2d()).mul(0.75);
            MergingGlueRenderer.renderGlueCross((Vector3dc)strandPosA.set((Vector3dc)strandCenterA).fma(strandA.x, rightA).fma(strandA.y, upA), upA, rightA, (Vector3dc)strandPosB.set((Vector3dc)strandCenterB).fma(strandB.x, (Vector3dc)rightB).fma(strandB.y, (Vector3dc)upB), (Vector3dc)upB, (Vector3dc)rightB, buffer, ms, light);
        }
    }

    private static VertexConsumer addVertex(VertexConsumer buffer, Matrix4f pose, Vector3dc pos) {
        return buffer.addVertex(pose, (float)pos.x(), (float)pos.y(), (float)pos.z());
    }

    private static void renderGlueCross(Vector3dc posA, Vector3dc upA, Vector3dc rightA, Vector3dc posB, Vector3dc upB, Vector3dc rightB, VertexConsumer buffer, PoseStack ms, int light) {
        Matrix4f pose = ms.last().pose();
        Vector3d vertex = new Vector3d();
        MergingGlueRenderer.addVertex(buffer, pose, (Vector3dc)posA.fma(-0.5, upA, vertex)).setColor(-1).setUv(0.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(ms.last(), 0.0f, 1.0f, 0.0f);
        MergingGlueRenderer.addVertex(buffer, pose, (Vector3dc)posA.fma(0.5, upA, vertex)).setColor(-1).setUv(0.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(ms.last(), 0.0f, 1.0f, 0.0f);
        MergingGlueRenderer.addVertex(buffer, pose, (Vector3dc)posB.fma(0.5, upB, vertex)).setColor(-1).setUv(1.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(ms.last(), 0.0f, 1.0f, 0.0f);
        MergingGlueRenderer.addVertex(buffer, pose, (Vector3dc)posB.fma(-0.5, upB, vertex)).setColor(-1).setUv(1.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(ms.last(), 0.0f, 1.0f, 0.0f);
        MergingGlueRenderer.addVertex(buffer, pose, (Vector3dc)posB.fma(-0.5, upB, vertex)).setColor(-1).setUv(1.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(ms.last(), 0.0f, 1.0f, 0.0f);
        MergingGlueRenderer.addVertex(buffer, pose, (Vector3dc)posB.fma(0.5, upB, vertex)).setColor(-1).setUv(1.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(ms.last(), 0.0f, 1.0f, 0.0f);
        MergingGlueRenderer.addVertex(buffer, pose, (Vector3dc)posA.fma(0.5, upA, vertex)).setColor(-1).setUv(0.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(ms.last(), 0.0f, 1.0f, 0.0f);
        MergingGlueRenderer.addVertex(buffer, pose, (Vector3dc)posA.fma(-0.5, upA, vertex)).setColor(-1).setUv(0.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(ms.last(), 0.0f, 1.0f, 0.0f);
        MergingGlueRenderer.addVertex(buffer, pose, (Vector3dc)posA.fma(-0.5, rightA, vertex)).setColor(-1).setUv(0.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(ms.last(), 0.0f, 1.0f, 0.0f);
        MergingGlueRenderer.addVertex(buffer, pose, (Vector3dc)posA.fma(0.5, rightA, vertex)).setColor(-1).setUv(0.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(ms.last(), 0.0f, 1.0f, 0.0f);
        MergingGlueRenderer.addVertex(buffer, pose, (Vector3dc)posB.fma(0.5, rightB, vertex)).setColor(-1).setUv(1.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(ms.last(), 0.0f, 1.0f, 0.0f);
        MergingGlueRenderer.addVertex(buffer, pose, (Vector3dc)posB.fma(-0.5, rightB, vertex)).setColor(-1).setUv(1.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(ms.last(), 0.0f, 1.0f, 0.0f);
        MergingGlueRenderer.addVertex(buffer, pose, (Vector3dc)posB.fma(-0.5, rightB, vertex)).setColor(-1).setUv(1.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(ms.last(), 0.0f, 1.0f, 0.0f);
        MergingGlueRenderer.addVertex(buffer, pose, (Vector3dc)posB.fma(0.5, rightB, vertex)).setColor(-1).setUv(1.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(ms.last(), 0.0f, 1.0f, 0.0f);
        MergingGlueRenderer.addVertex(buffer, pose, (Vector3dc)posA.fma(0.5, rightA, vertex)).setColor(-1).setUv(0.0f, 1.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(ms.last(), 0.0f, 1.0f, 0.0f);
        MergingGlueRenderer.addVertex(buffer, pose, (Vector3dc)posA.fma(-0.5, rightA, vertex)).setColor(-1).setUv(0.0f, 0.0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(ms.last(), 0.0f, 1.0f, 0.0f);
    }
}
