/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.item.ItemStack
 */
package com.simibubi.create.content.kinetics.crafter;

import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.simibubi.create.foundation.item.SmartInventory;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

public static class MechanicalCrafterBlockEntity.Inventory
extends SmartInventory {
    private MechanicalCrafterBlockEntity blockEntity;

    public MechanicalCrafterBlockEntity.Inventory(MechanicalCrafterBlockEntity blockEntity) {
        super(1, blockEntity, 1, false);
        this.blockEntity = blockEntity;
        this.forbidExtraction();
        this.whenContentsChanged(slot -> {
            if (this.getItem((int)slot).isEmpty()) {
                return;
            }
            if (blockEntity.phase == MechanicalCrafterBlockEntity.Phase.IDLE) {
                blockEntity.checkCompletedRecipe(false);
            }
        });
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (this.blockEntity.phase != MechanicalCrafterBlockEntity.Phase.IDLE) {
            return stack;
        }
        if (this.blockEntity.covered) {
            return stack;
        }
        ItemStack insertItem = super.insertItem(slot, stack, simulate);
        if (insertItem.getCount() != stack.getCount() && !simulate) {
            this.blockEntity.getLevel().playSound(null, this.blockEntity.getBlockPos(), SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.25f, 0.5f);
        }
        return insertItem;
    }
}
