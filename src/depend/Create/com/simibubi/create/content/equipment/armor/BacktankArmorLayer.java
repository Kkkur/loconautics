/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.Sheets
 *  net.minecraft.client.renderer.entity.EntityRenderDispatcher
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.renderer.entity.LivingEntityRenderer
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.equipment.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.equipment.armor.BacktankBlock;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankRenderer;
import com.simibubi.create.foundation.mixin.accessor.EntityRenderDispatcherAccessor;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class BacktankArmorLayer<T extends LivingEntity, M extends EntityModel<T>>
extends RenderLayer<T, M> {
    public BacktankArmorLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    public void render(PoseStack ms, MultiBufferSource buffer, int light, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getPose() == Pose.SLEEPING) {
            return;
        }
        BacktankItem item = BacktankItem.getWornBy(entity);
        if (item == null) {
            return;
        }
        EntityModel entityModel = this.getParentModel();
        if (!(entityModel instanceof HumanoidModel)) {
            return;
        }
        HumanoidModel model = (HumanoidModel)entityModel;
        boolean hasGlint = entity.getItemBySlot(BacktankItem.SLOT).hasFoil();
        VertexConsumer vc = ItemRenderer.getFoilBuffer((MultiBufferSource)buffer, (RenderType)Sheets.cutoutBlockSheet(), (boolean)false, (boolean)true);
        BlockState renderedState = (BlockState)item.getBlock().defaultBlockState().setValue(BacktankBlock.HORIZONTAL_FACING, (Comparable)Direction.SOUTH);
        SuperByteBuffer backtank = CachedBuffers.block((BlockState)renderedState);
        SuperByteBuffer cogs = CachedBuffers.partial((PartialModel)BacktankRenderer.getCogsModel(renderedState), (BlockState)renderedState);
        SuperByteBuffer nob = CachedBuffers.partial((PartialModel)BacktankRenderer.getShaftModel(renderedState), (BlockState)renderedState);
        ms.pushPose();
        model.body.translateAndRotate(ms);
        ms.translate(-0.5f, 0.625f, 1.0f);
        ms.scale(1.0f, -1.0f, -1.0f);
        backtank.disableDiffuse().light(light).renderInto(ms, vc);
        ((SuperByteBuffer)nob.disableDiffuse().translate(0.0f, -0.1875f, 0.0f)).light(light).renderInto(ms, vc);
        ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)cogs.center()).rotateYDegrees(180.0f)).uncenter()).translate(0.0f, 0.40625f, 0.6875f)).rotate(AngleHelper.rad((double)(2.0f * AnimationTickHolder.getRenderTime((LevelAccessor)entity.level()) % 360.0f)), Direction.EAST)).translate(0.0f, -0.40625f, -0.6875f);
        cogs.disableDiffuse().light(light).renderInto(ms, vc);
        ms.popPose();
    }

    public static void registerOnAll(EntityRenderDispatcher renderManager) {
        for (EntityRenderer entityRenderer : renderManager.getSkinMap().values()) {
            BacktankArmorLayer.registerOn(entityRenderer);
        }
        for (EntityRenderer entityRenderer : ((EntityRenderDispatcherAccessor)renderManager).create$getRenderers().values()) {
            BacktankArmorLayer.registerOn(entityRenderer);
        }
    }

    public static void registerOn(EntityRenderer<?> entityRenderer) {
        if (!(entityRenderer instanceof LivingEntityRenderer)) {
            return;
        }
        LivingEntityRenderer livingRenderer = (LivingEntityRenderer)entityRenderer;
        if (!(livingRenderer.getModel() instanceof HumanoidModel)) {
            return;
        }
        BacktankArmorLayer layer = new BacktankArmorLayer(livingRenderer);
        livingRenderer.addLayer(layer);
    }
}
