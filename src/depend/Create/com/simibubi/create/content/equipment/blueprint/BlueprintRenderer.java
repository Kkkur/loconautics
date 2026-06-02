/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.Sheets
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4fc
 */
package com.simibubi.create.content.equipment.blueprint;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.equipment.blueprint.BlueprintEntity;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4fc;

public class BlueprintRenderer
extends EntityRenderer<BlueprintEntity> {
    public BlueprintRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(BlueprintEntity entity, float yaw, float pt, PoseStack ms, MultiBufferSource buffer, int light) {
        boolean vertical;
        PartialModel partialModel = entity.size == 3 ? AllPartialModels.CRAFTING_BLUEPRINT_3x3 : (entity.size == 2 ? AllPartialModels.CRAFTING_BLUEPRINT_2x2 : AllPartialModels.CRAFTING_BLUEPRINT_1x1);
        SuperByteBuffer sbb = CachedBuffers.partial((PartialModel)partialModel, (BlockState)Blocks.AIR.defaultBlockState());
        ((SuperByteBuffer)((SuperByteBuffer)sbb.rotateYDegrees(-yaw)).rotateXDegrees(90.0f + entity.getXRot())).translate(-0.5, -0.03125, -0.5);
        if (entity.size == 2) {
            sbb.translate(0.5, 0.0, -0.5);
        }
        sbb.disableDiffuse().light(light).renderInto(ms, buffer.getBuffer(Sheets.solidBlockSheet()));
        super.render((Entity)entity, yaw, pt, ms, buffer, light);
        ms.pushPose();
        float fakeNormalXRotation = -15.0f;
        int bl = light >> 4 & 0xF;
        int sl = light >> 20 & 0xF;
        boolean bl2 = vertical = entity.getXRot() != 0.0f;
        if (entity.getXRot() == -90.0f) {
            fakeNormalXRotation = -45.0f;
        } else if (entity.getXRot() == 90.0f || yaw % 180.0f != 0.0f) {
            bl = (int)((double)bl / 1.35);
            sl = (int)((double)sl / 1.35);
        }
        int itemLight = Mth.floor((double)((double)sl + 0.5)) << 20 | (Mth.floor((double)((double)bl + 0.5)) & 0xF) << 4;
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(vertical ? 0.0f : -yaw)).rotateXDegrees(fakeNormalXRotation);
        Matrix3f copy = new Matrix3f((Matrix3fc)ms.last().normal());
        ms.popPose();
        ms.pushPose();
        ((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(-yaw)).rotateXDegrees(entity.getXRot())).translate(0.0, 0.0, 0.03225);
        if (entity.size == 3) {
            ms.translate(-1.0f, -1.0f, 0.0f);
        }
        PoseStack squashedMS = new PoseStack();
        squashedMS.last().pose().mul((Matrix4fc)ms.last().pose());
        for (int x = 0; x < entity.size; ++x) {
            squashedMS.pushPose();
            for (int y = 0; y < entity.size; ++y) {
                BlueprintEntity.BlueprintSection section = entity.getSection(x * entity.size + y);
                Couple<ItemStack> displayItems = section.getDisplayItems();
                squashedMS.pushPose();
                squashedMS.scale(0.5f, 0.5f, 9.765625E-4f);
                displayItems.forEachWithContext((stack, primary) -> {
                    if (stack.isEmpty()) {
                        return;
                    }
                    squashedMS.pushPose();
                    if (!primary.booleanValue()) {
                        squashedMS.translate(0.325f, -0.325f, 1.0f);
                        squashedMS.scale(0.625f, 0.625f, 1.0f);
                    }
                    squashedMS.last().normal().set((Matrix3fc)copy);
                    Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GUI, itemLight, OverlayTexture.NO_OVERLAY, squashedMS, buffer, entity.level(), 0);
                    squashedMS.popPose();
                });
                squashedMS.popPose();
                squashedMS.translate(1.0f, 0.0f, 0.0f);
            }
            squashedMS.popPose();
            squashedMS.translate(0.0f, 1.0f, 0.0f);
        }
        ms.popPose();
    }

    public ResourceLocation getTextureLocation(BlueprintEntity entity) {
        return null;
    }
}
