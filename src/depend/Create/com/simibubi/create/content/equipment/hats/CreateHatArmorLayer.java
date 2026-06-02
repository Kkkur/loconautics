/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.render.CachedBuffers
 *  net.minecraft.client.model.AgeableListModel
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.HierarchicalModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.model.geom.ModelPart$Cube
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.Sheets
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.LivingEntityRenderer
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.equipment.hats;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.equipment.hats.EntityHats;
import com.simibubi.create.content.trains.schedule.hat.TrainHatInfo;
import com.simibubi.create.content.trains.schedule.hat.TrainHatInfoReloadListener;
import com.simibubi.create.foundation.mixin.accessor.AgeableListModelAccessor;
import com.simibubi.create.foundation.mixin.accessor.EntityRenderDispatcherAccessor;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.ArrayList;
import java.util.Iterator;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CreateHatArmorLayer<T extends LivingEntity, M extends EntityModel<T>>
extends RenderLayer<T, M> {
    public CreateHatArmorLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    public void render(PoseStack ms, MultiBufferSource buffer, int light, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        PartialModel hat = EntityHats.getHatFor(entity);
        if (hat == null) {
            return;
        }
        EntityModel entityModel = this.getParentModel();
        ms.pushPose();
        PoseTransformStack msr = TransformStack.of((PoseStack)ms);
        TrainHatInfo info = TrainHatInfoReloadListener.getHatInfoFor((Entity)entity);
        ArrayList<ModelPart> partsToHead = new ArrayList<ModelPart>();
        if (entityModel instanceof AgeableListModel) {
            ModelPart head;
            AgeableListModel model = (AgeableListModel)entityModel;
            if (model.young) {
                if (model.scaleHead) {
                    float f = 1.5f / model.babyHeadScale;
                    ms.scale(f, f, f);
                }
                ms.translate(0.0, (double)(model.babyYHeadOffset / 16.0f), (double)(model.babyZHeadOffset / 16.0f));
            }
            if ((head = CreateHatArmorLayer.getHeadPart(model)) != null) {
                partsToHead.addAll(TrainHatInfo.getAdjustedPart(info, head, ""));
            }
        } else if (entityModel instanceof HierarchicalModel) {
            HierarchicalModel model = (HierarchicalModel)entityModel;
            partsToHead.addAll(TrainHatInfo.getAdjustedPart(info, model.root(), "head"));
        }
        if (!partsToHead.isEmpty()) {
            partsToHead.forEach(part -> part.translateAndRotate(ms));
            ModelPart lastChild = (ModelPart)partsToHead.get(partsToHead.size() - 1);
            if (!lastChild.isEmpty()) {
                ModelPart.Cube cube = (ModelPart.Cube)lastChild.cubes.get(Mth.clamp((int)info.cubeIndex(), (int)0, (int)(lastChild.cubes.size() - 1)));
                ms.translate(info.offset().x() / 16.0, ((double)(cube.minY - cube.maxY) + info.offset().y()) / 16.0, info.offset().z() / 16.0);
                float max = Math.max(cube.maxX - cube.minX, cube.maxZ - cube.minZ) / 8.0f * info.scale();
                ms.scale(max, max, max);
            }
            ms.scale(1.0f, -1.0f, -1.0f);
            ms.translate(0.0f, -0.140625f, 0.0f);
            msr.rotateXDegrees(-8.5f);
            BlockState air = Blocks.AIR.defaultBlockState();
            CachedBuffers.partial((PartialModel)hat, (BlockState)air).disableDiffuse().light(light).renderInto(ms, buffer.getBuffer(Sheets.cutoutBlockSheet()));
        }
        ms.popPose();
    }

    public static void registerOnAll(EntityRenderDispatcher renderManager) {
        for (EntityRenderer entityRenderer : renderManager.getSkinMap().values()) {
            CreateHatArmorLayer.registerOn(entityRenderer);
        }
        for (EntityRenderer entityRenderer : ((EntityRenderDispatcherAccessor)renderManager).create$getRenderers().values()) {
            CreateHatArmorLayer.registerOn(entityRenderer);
        }
    }

    public static void registerOn(EntityRenderer<?> entityRenderer) {
        if (!(entityRenderer instanceof LivingEntityRenderer)) {
            return;
        }
        LivingEntityRenderer livingRenderer = (LivingEntityRenderer)entityRenderer;
        EntityModel model = livingRenderer.getModel();
        if (!(model instanceof HierarchicalModel) && !(model instanceof AgeableListModel)) {
            return;
        }
        CreateHatArmorLayer layer = new CreateHatArmorLayer(livingRenderer);
        livingRenderer.addLayer(layer);
    }

    private static ModelPart getHeadPart(AgeableListModel<?> model) {
        Iterator<ModelPart> iterator = ((AgeableListModelAccessor)model).create$callHeadParts().iterator();
        if (iterator.hasNext()) {
            ModelPart part = iterator.next();
            return part;
        }
        iterator = ((AgeableListModelAccessor)model).create$callBodyParts().iterator();
        if (iterator.hasNext()) {
            ModelPart part = iterator.next();
            return part;
        }
        return null;
    }
}
