/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.equipment.potatoCannon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PotatoProjectileRenderer
extends EntityRenderer<PotatoProjectileEntity> {
    public PotatoProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(PotatoProjectileEntity entity, float yaw, float pt, PoseStack ms, MultiBufferSource buffer, int light) {
        ItemStack item = entity.getItem();
        if (item.isEmpty()) {
            return;
        }
        ms.pushPose();
        ms.translate(0.0, entity.getBoundingBox().getYsize() / 2.0 - 0.125, 0.0);
        entity.getRenderMode().transform(ms, entity, pt);
        Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY, ms, buffer, entity.level(), 0);
        ms.popPose();
    }

    public ResourceLocation getTextureLocation(PotatoProjectileEntity entity) {
        return null;
    }
}
