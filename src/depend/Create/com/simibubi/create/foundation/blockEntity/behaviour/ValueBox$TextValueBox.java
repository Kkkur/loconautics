/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 */
package com.simibubi.create.foundation.blockEntity.behaviour;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public static class ValueBox.TextValueBox
extends ValueBox {
    Component text;

    public ValueBox.TextValueBox(Component label, AABB bb, BlockPos pos, Component text) {
        super(label, bb, pos);
        this.text = text;
    }

    public ValueBox.TextValueBox(Component label, AABB bb, BlockPos pos, BlockState state, Component text) {
        super(label, bb, pos, state);
        this.text = text;
    }

    @Override
    public void renderContents(PoseStack ms, MultiBufferSource buffer) {
        boolean singleDigit;
        super.renderContents(ms, buffer);
        Font font = Minecraft.getInstance().font;
        float scale = 3.0f;
        ms.scale(scale, scale, 1.0f);
        ms.translate(-4.0, -3.75, 5.0);
        int stringWidth = font.width((FormattedText)this.text);
        Objects.requireNonNull(font);
        float numberScale = 9.0f / (float)stringWidth;
        boolean bl = singleDigit = stringWidth < 10;
        if (singleDigit) {
            numberScale /= 2.0f;
        }
        Objects.requireNonNull(font);
        float verticalMargin = (float)(stringWidth - 9) / 2.0f;
        ms.scale(numberScale, numberScale, numberScale);
        ms.translate(singleDigit ? (float)(stringWidth / 2) : 0.0f, singleDigit ? -verticalMargin : verticalMargin, 0.0f);
        int overrideColor = this.transform.getOverrideColor();
        if (overrideColor == -1) {
            ValueBox.drawString8x(ms, buffer, this.text, 0.0f, 0.0f, 0xEDEDED);
        } else {
            ValueBox.drawString(ms, buffer, this.text, 0.0f, 0.0f, overrideColor);
        }
    }
}
