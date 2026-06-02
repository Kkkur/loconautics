/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.schematics.requirement;

import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import net.minecraft.world.item.ItemStack;

public static class ItemRequirement.StrictNbtStackRequirement
extends ItemRequirement.StackRequirement {
    public ItemRequirement.StrictNbtStackRequirement(ItemStack stack, ItemRequirement.ItemUseType usage) {
        super(stack, usage);
    }

    @Override
    public boolean matches(ItemStack other) {
        return ItemStack.isSameItemSameComponents((ItemStack)this.stack, (ItemStack)other);
    }
}
