/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 */
package com.simibubi.create.content.schematics.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class SchematicHotbarSlotOverlay {
    public void renderOn(GuiGraphics graphics, int slot) {
        Window mainWindow = Minecraft.getInstance().getWindow();
        int x = mainWindow.getGuiScaledWidth() / 2 - 88;
        int y = mainWindow.getGuiScaledHeight() - 19;
        RenderSystem.enableDepthTest();
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(0.0f, 0.0f, -300.0f);
        AllGuiTextures.SCHEMATIC_SLOT.render(graphics, x + 20 * slot, y);
        ms.popPose();
    }
}
