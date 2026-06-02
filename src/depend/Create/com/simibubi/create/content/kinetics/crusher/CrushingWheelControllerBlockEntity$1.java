/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.kinetics.crusher;

import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;

class CrushingWheelControllerBlockEntity.1
extends ProcessingInventory {
    CrushingWheelControllerBlockEntity.1(Consumer callback) {
        super(callback);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return super.isItemValid(slot, stack) && CrushingWheelControllerBlockEntity.this.processingEntity == null;
    }
}
