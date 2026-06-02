/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.core.Position
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.AABB
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.block_entity_visible;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.Position;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={LevelRenderer.class}, priority=2000)
public class LevelRendererMixin {
    @Redirect(method={"renderLevel"}, at=@At(value="INVOKE", target="Lnet/neoforged/neoforge/client/ClientHooks;isBlockEntityRendererVisible(Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/client/renderer/culling/Frustum;)Z"), require=0)
    private static <T extends BlockEntity> boolean isBlockEntityRendererVisible(BlockEntityRenderDispatcher dispatcher, BlockEntity blockEntity, Frustum frustum) {
        BlockEntityRenderer renderer = dispatcher.getRenderer(blockEntity);
        if (renderer == null) {
            return false;
        }
        AABB renderBounds = renderer.getRenderBoundingBox(blockEntity);
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((Position)renderBounds.getCenter());
        if (subLevel != null) {
            BoundingBox3d bb = new BoundingBox3d(renderBounds);
            renderBounds = bb.transform((Pose3dc)subLevel.logicalPose(), bb).toMojang();
        }
        return frustum.isVisible(renderBounds);
    }
}
