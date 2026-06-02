/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.logistics.box;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.box.PackageItem;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PackageRenderer
extends EntityRenderer<PackageEntity> {
    public PackageRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.shadowRadius = 0.5f;
    }

    public void render(PackageEntity entity, float yaw, float pt, PoseStack ms, MultiBufferSource buffer, int light) {
        if (!VisualizationManager.supportsVisualization((LevelAccessor)entity.level())) {
            ItemStack box = entity.box;
            if (box.isEmpty() || !PackageItem.isPackage(box)) {
                box = AllBlocks.CARDBOARD_BLOCK.asStack();
            }
            PartialModel model = AllPartialModels.PACKAGES.get(BuiltInRegistries.ITEM.getKey((Object)box.getItem()));
            PackageRenderer.renderBox((Entity)entity, yaw, ms, buffer, light, model);
        }
        super.render((Entity)entity, yaw, pt, ms, buffer, light);
    }

    public static void renderBox(Entity entity, float yaw, PoseStack ms, MultiBufferSource buffer, int light, PartialModel model) {
        if (model == null) {
            return;
        }
        SuperByteBuffer sbb = CachedBuffers.partial((PartialModel)model, (BlockState)Blocks.AIR.defaultBlockState());
        ((SuperByteBuffer)((SuperByteBuffer)sbb.translate(-0.5, 0.0, -0.5)).rotateCentered(-AngleHelper.rad((double)(yaw + 90.0f)), Direction.UP)).light(light).nudge(entity.getId());
        sbb.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    public ResourceLocation getTextureLocation(PackageEntity pEntity) {
        return null;
    }
}
