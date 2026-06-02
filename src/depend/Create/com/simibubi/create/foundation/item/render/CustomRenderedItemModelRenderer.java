/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.foundation.item.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public abstract class CustomRenderedItemModelRenderer
extends BlockEntityWithoutLevelRenderer {
    public CustomRenderedItemModelRenderer() {
        super(null, null);
    }

    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        CustomRenderedItemModel mainModel = (CustomRenderedItemModel)Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 0);
        PartialItemModelRenderer renderer = PartialItemModelRenderer.of(stack, transformType, ms, buffer, overlay);
        ms.pushPose();
        ms.translate(0.5f, 0.5f, 0.5f);
        this.render(stack, mainModel, renderer, transformType, ms, buffer, light, overlay);
        ms.popPose();
    }

    protected abstract void render(ItemStack var1, CustomRenderedItemModel var2, PartialItemModelRenderer var3, ItemDisplayContext var4, PoseStack var5, MultiBufferSource var6, int var7, int var8);
}
