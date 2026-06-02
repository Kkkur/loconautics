/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 */
package com.simibubi.create.compat.computercraft;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.minecraft.client.gui.GuiGraphics;

@FunctionalInterface
public static interface ComputerScreen.RenderWindowFunction {
    public void render(GuiGraphics var1, int var2, int var3, float var4, int var5, int var6, AllGuiTextures var7);
}
