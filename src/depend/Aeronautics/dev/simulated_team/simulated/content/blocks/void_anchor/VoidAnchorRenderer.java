/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.void_anchor;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.simulated_team.simulated.content.blocks.void_anchor.VoidAnchorBlockEntity;
import dev.simulated_team.simulated.content.end_sea.EndSeaShadowRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;

public class VoidAnchorRenderer
implements BlockEntityRenderer<VoidAnchorBlockEntity> {
    public VoidAnchorRenderer(BlockEntityRendererProvider.Context context) {
    }

    public boolean shouldRender(VoidAnchorBlockEntity blockEntity, Vec3 vec3) {
        return true;
    }

    public int getViewDistance() {
        return 512;
    }

    public boolean shouldRenderOffScreen(VoidAnchorBlockEntity blockEntity) {
        return true;
    }

    public void render(VoidAnchorBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        if (EndSeaShadowRenderer.renderingShadowMap()) {
            return;
        }
        EndSeaShadowRenderer.addVoidAnchor(blockEntity);
    }
}
