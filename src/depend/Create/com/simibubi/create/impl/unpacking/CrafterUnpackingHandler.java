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
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.impl.unpacking;

import com.simibubi.create.api.packager.unpacking.UnpackingHandler;
import com.simibubi.create.content.kinetics.crafter.ConnectedInputHandler;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public enum CrafterUnpackingHandler implements UnpackingHandler
{
    INSTANCE;


    @Override
    public boolean unpack(Level level, BlockPos pos, BlockState state, Direction side, List<ItemStack> items, @Nullable PackageOrderWithCrafts orderContext, boolean simulate) {
        if (!PackageOrderWithCrafts.hasCraftingInformation(orderContext)) {
            return DEFAULT.unpack(level, pos, state, side, items, null, simulate);
        }
        List<BigItemStack> craftingContext = orderContext.getCraftingInformation();
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof MechanicalCrafterBlockEntity)) {
            return false;
        }
        MechanicalCrafterBlockEntity crafter = (MechanicalCrafterBlockEntity)be;
        ConnectedInputHandler.ConnectedInput input = crafter.getInput();
        List<MechanicalCrafterBlockEntity.Inventory> inventories = input.getInventories(level, pos);
        if (inventories.isEmpty()) {
            return false;
        }
        int max = Math.min(inventories.size(), craftingContext.size());
        block0: for (int i = 0; i < max; ++i) {
            MechanicalCrafterBlockEntity.Inventory inventory;
            BigItemStack targetStack = craftingContext.get(i);
            if (targetStack.stack.isEmpty() || !(inventory = inventories.get(i)).getStackInSlot(0).isEmpty()) continue;
            for (ItemStack stack : items) {
                ItemStack toInsert;
                if (!ItemStack.isSameItemSameComponents((ItemStack)stack, (ItemStack)targetStack.stack) || !inventory.insertItem(0, toInsert = stack.copyWithCount(1), simulate).isEmpty()) continue;
                stack.shrink(1);
                continue block0;
            }
        }
        for (ItemStack item : items) {
            if (item.isEmpty()) continue;
            return false;
        }
        if (!simulate) {
            crafter.checkCompletedRecipe(true);
        }
        return true;
    }
}
