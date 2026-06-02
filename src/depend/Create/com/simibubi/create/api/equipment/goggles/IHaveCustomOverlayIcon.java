/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.api.equipment.goggles;

import com.simibubi.create.AllItems;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import net.minecraft.world.item.ItemStack;

public sealed interface IHaveCustomOverlayIcon
permits IHaveGoggleInformation, IHaveHoveringInformation {
    default public ItemStack getIcon(boolean isPlayerSneaking) {
        return AllItems.GOGGLES.asStack();
    }
}
