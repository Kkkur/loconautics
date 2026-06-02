/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.AABB
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.gui.AllIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public static class ValueBox.ItemValueBox
extends ValueBox {
    ItemStack stack;
    MutableComponent count;

    public ValueBox.ItemValueBox(Component label, AABB bb, BlockPos pos, ItemStack stack, MutableComponent count) {
        super(label, bb, pos);
        this.stack = stack;
        this.count = count;
    }

    @Override
    public AllIcons getOutline() {
        if (!this.stack.isEmpty()) {
            return AllIcons.VALUE_BOX_HOVER_6PX;
        }
        return super.getOutline();
    }

    @Override
    public void renderContents(PoseStack ms, MultiBufferSource buffer) {
        super.renderContents(ms, buffer);
        if (this.count == null) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        ms.translate(17.5, -5.0, 7.0);
        boolean isFilter = this.stack.getItem() instanceof FilterItem;
        boolean isEmpty = this.stack.isEmpty();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel modelWithOverrides = itemRenderer.getModel(this.stack, null, null, 0);
        boolean blockItem = modelWithOverrides.isGui3d();
        float scale = 1.5f;
        ms.translate((float)(-font.width((FormattedText)this.count)), 0.0f, 0.0f);
        if (isFilter) {
            ms.translate(-5.0f, 8.0f, 0.0f);
        } else if (isEmpty) {
            ms.translate(-15.0, -1.0, -2.75);
            scale = 1.65f;
        } else {
            ms.translate(-7.0f, 10.0f, blockItem ? 10.25f : 0.0f);
        }
        if (this.count.getString().equals("*")) {
            ms.translate(-1.0f, 3.0f, 0.0f);
        }
        ms.scale(scale, scale, scale);
        ValueBox.drawString8x(ms, buffer, (Component)this.count, 0.0f, 0.0f, isFilter ? 0xFFFFFF : 0xEDEDED);
    }
}
