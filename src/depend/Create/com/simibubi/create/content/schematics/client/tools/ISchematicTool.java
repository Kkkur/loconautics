/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.render.SuperRenderTypeBuffer
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.schematics.client.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec3;

public interface ISchematicTool {
    public void init();

    public void updateSelection();

    public boolean handleRightClick();

    public boolean handleMouseWheel(double var1);

    public void renderTool(PoseStack var1, SuperRenderTypeBuffer var2, Vec3 var3);

    public void renderOverlay(Gui var1, GuiGraphics var2, float var3, int var4, int var5);

    public void renderOnSchematic(PoseStack var1, SuperRenderTypeBuffer var2);
}
