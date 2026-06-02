/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.processing.sequenced;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import net.createmod.catnip.theme.Color;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SequencedAssemblyItem
extends Item {
    public SequencedAssemblyItem(Item.Properties p_i48487_1_) {
        super(p_i48487_1_.stacksTo(1));
    }

    public float getProgress(ItemStack stack) {
        if (!stack.has(AllDataComponents.SEQUENCED_ASSEMBLY)) {
            return 0.0f;
        }
        return ((SequencedAssemblyRecipe.SequencedAssembly)stack.get(AllDataComponents.SEQUENCED_ASSEMBLY)).progress();
    }

    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    public int getBarWidth(ItemStack stack) {
        return Math.round(this.getProgress(stack) * 13.0f);
    }

    public int getBarColor(ItemStack stack) {
        return Color.mixColors((int)-16268, (int)-12124192, (float)this.getProgress(stack));
    }
}
