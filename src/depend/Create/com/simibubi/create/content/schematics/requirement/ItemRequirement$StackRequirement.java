/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.schematics.requirement;

import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import net.minecraft.world.item.ItemStack;

public static class ItemRequirement.StackRequirement {
    public final ItemStack stack;
    public final ItemRequirement.ItemUseType usage;

    public ItemRequirement.StackRequirement(ItemStack stack, ItemRequirement.ItemUseType usage) {
        this.stack = stack;
        this.usage = usage;
    }

    public boolean matches(ItemStack other) {
        return ItemStack.isSameItem((ItemStack)this.stack, (ItemStack)other);
    }
}
