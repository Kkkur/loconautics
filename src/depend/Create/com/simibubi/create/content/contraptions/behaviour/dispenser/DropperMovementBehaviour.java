/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  net.minecraft.Util
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.items.IItemHandler
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.behaviour.dispenser;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.contraption.dispenser.DefaultMountedDispenseBehavior;
import com.simibubi.create.api.contraption.dispenser.MountedDispenseBehavior;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.item.ItemHelper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class DropperMovementBehaviour
implements MovementBehaviour {
    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        if (context.world.isClientSide) {
            return;
        }
        MountedItemStorage storage = context.getItemStorage();
        if (storage == null) {
            return;
        }
        int slot = DropperMovementBehaviour.getSlot(storage, context.world.random, (IItemHandler)context.contraption.getStorage().getAllItems());
        if (slot == -1) {
            DropperMovementBehaviour.failDispense(context, pos);
            return;
        }
        ItemStack stack = storage.getStackInSlot(slot).copy();
        MountedDispenseBehavior behavior = this.getDispenseBehavior(context, pos, stack);
        ItemStack remainder = behavior.dispense(stack, context, pos);
        storage.setStackInSlot(slot, remainder);
    }

    protected MountedDispenseBehavior getDispenseBehavior(MovementContext context, BlockPos pos, ItemStack stack) {
        return DefaultMountedDispenseBehavior.INSTANCE;
    }

    private static int getSlot(MountedItemStorage storage, RandomSource random, IItemHandler contraptionInventory) {
        IntArrayList filledSlots = new IntArrayList();
        for (int i = 0; i < storage.getSlots(); ++i) {
            ItemStack stack = storage.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (stack.getCount() == 1 && stack.getMaxStackSize() != 1) {
                if ((stack = DropperMovementBehaviour.tryTopOff(stack, contraptionInventory)) == null) continue;
                storage.setStackInSlot(i, stack);
            }
            filledSlots.add(i);
        }
        return switch (filledSlots.size()) {
            case 0 -> -1;
            case 1 -> filledSlots.getInt(0);
            default -> (Integer)Util.getRandom((List)filledSlots, (RandomSource)random);
        };
    }

    @Nullable
    private static ItemStack tryTopOff(ItemStack stack, IItemHandler from) {
        Predicate<ItemStack> test = otherStack -> ItemStack.isSameItemSameComponents((ItemStack)stack, (ItemStack)otherStack);
        int needed = stack.getMaxStackSize() - stack.getCount();
        ItemStack extracted = ItemHelper.extract(from, test, ItemHelper.ExtractionCountMode.UPTO, needed, false);
        return extracted.isEmpty() ? null : stack.copyWithCount(stack.getCount() + extracted.getCount());
    }

    private static void failDispense(MovementContext ctx, BlockPos pos) {
        ctx.world.levelEvent(1001, pos, 0);
    }
}
