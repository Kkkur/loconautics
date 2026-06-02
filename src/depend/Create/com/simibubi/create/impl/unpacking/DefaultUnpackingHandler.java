/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.impl.unpacking;

import com.simibubi.create.api.packager.unpacking.UnpackingHandler;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

public enum DefaultUnpackingHandler implements UnpackingHandler
{
    INSTANCE;


    @Override
    public boolean unpack(Level level, BlockPos pos, BlockState state, Direction side, List<ItemStack> items, @Nullable PackageOrderWithCrafts orderContext, boolean simulate) {
        BlockEntity targetBE = level.getBlockEntity(pos);
        if (targetBE == null) {
            return false;
        }
        IItemHandler targetInv = (IItemHandler)level.getCapability(Capabilities.ItemHandler.BLOCK, pos, state, targetBE, (Object)side);
        if (targetInv == null) {
            return false;
        }
        if (!simulate) {
            for (ItemStack itemStack : items) {
                ItemHandlerHelper.insertItemStacked((IItemHandler)targetInv, (ItemStack)itemStack.copy(), (boolean)false);
            }
            return true;
        }
        for (int slot = 0; slot < targetInv.getSlots(); ++slot) {
            ItemStack itemInSlot = targetInv.getStackInSlot(slot);
            int itemsAddedToSlot = 0;
            for (int boxSlot = 0; boxSlot < items.size(); ++boxSlot) {
                ItemStack toInsert = items.get(boxSlot);
                if (toInsert.isEmpty() || targetInv.insertItem(slot, toInsert, true).getCount() == toInsert.getCount()) continue;
                if (itemInSlot.isEmpty()) {
                    int maxStackSize = targetInv.getSlotLimit(slot);
                    if (maxStackSize < toInsert.getCount()) {
                        toInsert.shrink(maxStackSize);
                        toInsert = toInsert.copyWithCount(maxStackSize);
                    } else {
                        items.set(boxSlot, ItemStack.EMPTY);
                    }
                    itemInSlot = toInsert;
                    targetInv.insertItem(slot, toInsert, simulate);
                    continue;
                }
                if (!ItemStack.isSameItemSameComponents((ItemStack)toInsert, (ItemStack)itemInSlot)) continue;
                int insertedAmount = toInsert.getCount() - targetInv.insertItem(slot, toInsert, simulate).getCount();
                int slotLimit = Math.min(itemInSlot.getMaxStackSize(), targetInv.getSlotLimit(slot));
                int insertableAmountWithPreviousItems = Math.min(toInsert.getCount(), slotLimit - itemInSlot.getCount() - itemsAddedToSlot);
                int added = Math.min(insertedAmount, Math.max(0, insertableAmountWithPreviousItems));
                itemsAddedToSlot += added;
                items.set(boxSlot, toInsert.copyWithCount(toInsert.getCount() - added));
            }
        }
        for (ItemStack stack : items) {
            if (stack.isEmpty()) continue;
            return false;
        }
        return true;
    }
}
