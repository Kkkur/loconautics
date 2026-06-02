/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  foundry.veil.api.client.render.CullFrustum
 *  net.minecraft.client.Camera
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
 *  net.minecraft.server.packs.resources.ResourceManagerReloadListener
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.joml.Matrix4f
 *  org.lwjgl.system.NativeResource
 */
package dev.ryanhcode.sable.sublevel.render.dispatcher;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.render.SubLevelRenderData;
import dev.ryanhcode.sable.sublevel.render.SubLevelRenderer;
import foundry.veil.api.client.render.CullFrustum;
import java.util.Collection;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.lwjgl.system.NativeResource;

@ApiStatus.Internal
public interface SubLevelRenderDispatcher
extends NativeResource,
ResourceManagerReloadListener {
    public static SubLevelRenderDispatcher get() {
        return SubLevelRenderer.getDispatcher();
    }

    public SubLevelRenderData resize(ClientSubLevel var1, SubLevelRenderData var2);

    public SubLevelRenderData createRenderData(ClientSubLevel var1);

    default public void rebuild(Iterable<ClientSubLevel> sublevels) {
        for (ClientSubLevel sublevel : sublevels) {
            sublevel.getRenderData().rebuild();
        }
    }

    public void updateCulling(Iterable<ClientSubLevel> var1, double var2, double var4, double var6, CullFrustum var8, boolean var9);

    public void renderSectionLayer(Iterable<ClientSubLevel> var1, RenderType var2, ShaderInstance var3, double var4, double var6, double var8, Matrix4f var10, Matrix4f var11, float var12);

    public void renderAfterSections(Iterable<ClientSubLevel> var1, double var2, double var4, double var6, Matrix4f var8, Matrix4f var9, float var10);

    public void renderBlockEntities(Iterable<ClientSubLevel> var1, BlockEntityRenderer var2, double var3, double var5, double var7, float var9);

    public void addDebugInfo(Consumer<String> var1);

    default public void preRenderChunks(Camera camera) {
    }

    public static interface BlockEntityRenderer {
        default public void renderBlockEntities(Collection<BlockEntity> blockEntities, PoseStack poseStack, float partialTick, double cameraX, double cameraY, double cameraZ) {
            for (BlockEntity blockEntity : blockEntities) {
                this.renderSingleBE(blockEntity, poseStack, partialTick, cameraX, cameraY, cameraZ);
            }
        }

        public void renderSingleBE(BlockEntity var1, PoseStack var2, float var3, double var4, double var6, double var8);

        public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher();
    }
}
