/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.caffeinemc.mods.sodium.client.gl.device.CommandList
 *  net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices
 *  net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager
 *  net.caffeinemc.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses
 *  net.caffeinemc.mods.sodium.client.render.viewport.CameraTransform
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.RenderType
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionf
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 */
package dev.ryanhcode.sable.sublevel.render.sodium;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.mixinterface.sublevel_render.sodium.DefaultChunkRendererExtension;
import dev.ryanhcode.sable.mixinterface.sublevel_render.sodium.OcclusionCullerExtension;
import dev.ryanhcode.sable.mixinterface.sublevel_render.sodium.RenderSectionManagerExtension;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.render.SubLevelRenderData;
import net.caffeinemc.mods.sodium.client.gl.device.CommandList;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses;
import net.caffeinemc.mods.sodium.client.render.viewport.CameraTransform;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;

public class SubLevelRenderSectionManager
extends RenderSectionManager {
    private final Vector3d chunkOffset = new Vector3d();
    private final Matrix4f projection = new Matrix4f();
    private final Matrix4f modelView = new Matrix4f();
    private final ClientSubLevel subLevel;
    private CameraTransform cameraTransform;

    public SubLevelRenderSectionManager(ClientSubLevel subLevel, ClientLevel level, int renderDistance, CommandList commandList) {
        super(level, renderDistance, commandList);
        this.subLevel = subLevel;
        OcclusionCullerExtension culler = (OcclusionCullerExtension)((RenderSectionManagerExtension)((Object)this)).sable$getOcclusionCuller();
        culler.sable$setSubLevel(subLevel);
    }

    public void apply(ChunkRenderMatrices matrices, double camX, double camY, double camZ) {
        SubLevelRenderData renderer = this.subLevel.getRenderData();
        this.modelView.set(matrices.modelView());
        this.projection.set((Matrix4fc)RenderSystem.getProjectionMatrix());
        renderer.getChunkOffset(this.chunkOffset);
        Vector3f pos = new Vector3f((float)camX, (float)camY, (float)camZ);
        renderer.getTransformation(0.0, 0.0, 0.0).invert().transformPosition(pos);
        this.cameraTransform = new CameraTransform((double)pos.x - this.chunkOffset.x, (double)pos.y - this.chunkOffset.y, (double)pos.z - this.chunkOffset.z);
    }

    public void render(ChunkRenderMatrices originalMatrices, RenderType layer, double camX, double camY, double camZ) {
        DefaultChunkRendererExtension chunkRenderer = (DefaultChunkRendererExtension)((RenderSectionManagerExtension)((Object)this)).sable$getChunkRenderer();
        chunkRenderer.sable$setCameraTransform(this.cameraTransform);
        PoseStack matrixStack = new PoseStack();
        matrixStack.mulPose(new Matrix4f(originalMatrices.modelView()));
        matrixStack.pushPose();
        Pose3dc pose = this.subLevel.renderPose();
        Vector3dc spos = pose.position();
        Vector3dc scale = pose.scale();
        Quaterniondc orientation = pose.orientation();
        matrixStack.translate(spos.x() - camX, spos.y() - camY, spos.z() - camZ);
        matrixStack.mulPose(new Quaternionf(orientation));
        matrixStack.scale((float)scale.x(), (float)scale.y(), (float)scale.z());
        this.modelView.set((Matrix4fc)matrixStack.last().pose());
        matrixStack.popPose();
        ChunkRenderMatrices matrices = new ChunkRenderMatrices((Matrix4fc)RenderSystem.getProjectionMatrix(), (Matrix4fc)this.modelView);
        if (layer == RenderType.solid()) {
            this.renderLayer(matrices, DefaultTerrainRenderPasses.SOLID, -this.chunkOffset.x, -this.chunkOffset.y, -this.chunkOffset.z);
            this.renderLayer(matrices, DefaultTerrainRenderPasses.CUTOUT, -this.chunkOffset.x, -this.chunkOffset.y, -this.chunkOffset.z);
        } else if (layer == RenderType.translucent()) {
            this.renderLayer(matrices, DefaultTerrainRenderPasses.TRANSLUCENT, -this.chunkOffset.x, -this.chunkOffset.y, -this.chunkOffset.z);
        }
        chunkRenderer.sable$setCameraTransform(null);
    }

    public boolean shouldDisableOcclusionCulling() {
        return true;
    }
}
