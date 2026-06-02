/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.client.render.CullFrustum
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.server.packs.resources.ResourceManager
 *  org.joml.Matrix4f
 */
package dev.ryanhcode.sable.sublevel.render.dispatcher;

import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.render.SubLevelRenderData;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelRenderDispatcher;
import dev.ryanhcode.sable.sublevel.render.sodium.SodiumSubLevelRenderData;
import foundry.veil.api.client.render.CullFrustum;
import java.util.function.Consumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import org.joml.Matrix4f;

public class SodiumSubLevelRenderDispatcher
implements SubLevelRenderDispatcher {
    @Override
    public SubLevelRenderData resize(ClientSubLevel subLevel, SubLevelRenderData renderData) {
        ((SodiumSubLevelRenderData)renderData).resize();
        return renderData;
    }

    @Override
    public SubLevelRenderData createRenderData(ClientSubLevel subLevel) {
        return new SodiumSubLevelRenderData(subLevel);
    }

    @Override
    public void updateCulling(Iterable<ClientSubLevel> sublevels, double cameraX, double cameraY, double cameraZ, CullFrustum cullFrustum, boolean isSpectator) {
    }

    @Override
    public void renderSectionLayer(Iterable<ClientSubLevel> sublevels, RenderType renderType, ShaderInstance shader, double cameraX, double cameraY, double cameraZ, Matrix4f modelView, Matrix4f projection, float partialTicks) {
    }

    @Override
    public void renderAfterSections(Iterable<ClientSubLevel> sublevels, double cameraX, double cameraY, double cameraZ, Matrix4f modelView, Matrix4f projection, float partialTicks) {
    }

    @Override
    public void renderBlockEntities(Iterable<ClientSubLevel> sublevels, SubLevelRenderDispatcher.BlockEntityRenderer blockEntityRenderer, double cameraX, double cameraY, double cameraZ, float partialTick) {
    }

    @Override
    public void addDebugInfo(Consumer<String> consumer) {
    }

    public void onResourceManagerReload(ResourceManager resourceManager) {
    }

    public void free() {
    }
}
