/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.phys.AABB
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;

public static class ValueBox.IconValueBox
extends ValueBox {
    AllIcons icon;

    public ValueBox.IconValueBox(Component label, INamedIconOptions iconValue, AABB bb, BlockPos pos) {
        super(label, bb, pos);
        this.icon = iconValue.getIcon();
    }

    @Override
    public void renderContents(PoseStack ms, MultiBufferSource buffer) {
        super.renderContents(ms, buffer);
        float scale = 32.0f;
        ms.scale(scale, scale, scale);
        ms.translate(-0.5f, -0.5f, 0.15625f);
        int overrideColor = this.transform.getOverrideColor();
        this.icon.render(ms, buffer, overrideColor != -1 ? overrideColor : 0xFFFFFF);
    }
}
