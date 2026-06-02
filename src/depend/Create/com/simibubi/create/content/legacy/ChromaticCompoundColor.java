/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.color.item.ItemColor
 *  net.minecraft.util.FastColor$ARGB32
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.legacy;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class ChromaticCompoundColor
implements ItemColor {
    public int getColor(ItemStack stack, int layer) {
        Minecraft mc = Minecraft.getInstance();
        float pt = AnimationTickHolder.getPartialTicks();
        float progress = (float)((double)(mc.player.getViewYRot(pt) / 180.0f) * Math.PI) + AnimationTickHolder.getRenderTime() / 10.0f;
        if (layer == 0) {
            return Color.mixColors((int)FastColor.ARGB32.color((int)110, (int)87, (int)115), (int)FastColor.ARGB32.color((int)107, (int)48, (int)116), (float)((Mth.sin((float)progress) + 1.0f) / 2.0f));
        }
        if (layer == 1) {
            return Color.mixColors((int)FastColor.ARGB32.color((int)212, (int)93, (int)121), (int)FastColor.ARGB32.color((int)110, (int)87, (int)115), (float)((Mth.sin((float)((float)((double)progress + Math.PI))) + 1.0f) / 2.0f));
        }
        if (layer == 2) {
            return Color.mixColors((int)FastColor.ARGB32.color((int)234, (int)144, (int)133), (int)FastColor.ARGB32.color((int)212, (int)93, (int)121), (float)((Mth.sin((float)((float)((double)(progress * 1.5f) + Math.PI))) + 1.0f) / 2.0f));
        }
        return 0;
    }
}
