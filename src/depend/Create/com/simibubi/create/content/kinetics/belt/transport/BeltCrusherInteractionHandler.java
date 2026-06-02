/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 */
package com.simibubi.create.content.kinetics.belt.transport;

import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class BeltCrusherInteractionHandler {
    public static boolean checkForCrushers(BeltInventory beltInventory, TransportedItemStack currentItem, float nextOffset) {
        boolean beltMovementPositive = beltInventory.beltMovementPositive;
        int firstUpcomingSegment = (int)Math.floor(currentItem.beltPosition);
        int step = beltMovementPositive ? 1 : -1;
        int segment = firstUpcomingSegment = Mth.clamp((int)firstUpcomingSegment, (int)0, (int)(beltInventory.belt.beltLength - 1));
        while (beltMovementPositive ? (float)segment <= nextOffset : (float)(segment + 1) >= nextOffset) {
            Direction movementFacing;
            Direction crusherFacing;
            BlockPos crusherPos = BeltHelper.getPositionForOffset(beltInventory.belt, segment).above();
            Level world = beltInventory.belt.getLevel();
            BlockState crusherState = world.getBlockState(crusherPos);
            if (crusherState.getBlock() instanceof CrushingWheelControllerBlock && (crusherFacing = (Direction)crusherState.getValue((Property)CrushingWheelControllerBlock.FACING)) == (movementFacing = beltInventory.belt.getMovementFacing())) {
                boolean hasCrossed;
                float crusherEntry = (float)segment + 0.5f;
                float postCrusherEntry = (crusherEntry += 0.399f * (float)(beltMovementPositive ? -1 : 1)) + 0.799f * (float)(!beltMovementPositive ? -1 : 1);
                boolean bl = hasCrossed = nextOffset > crusherEntry && nextOffset < postCrusherEntry && beltMovementPositive || nextOffset < crusherEntry && nextOffset > postCrusherEntry && !beltMovementPositive;
                if (!hasCrossed) {
                    return false;
                }
                currentItem.beltPosition = crusherEntry;
                BlockEntity be = world.getBlockEntity(crusherPos);
                if (!(be instanceof CrushingWheelControllerBlockEntity)) {
                    return true;
                }
                CrushingWheelControllerBlockEntity crusherBE = (CrushingWheelControllerBlockEntity)be;
                ItemStack toInsert = currentItem.stack.copy();
                ItemStack remainder = ItemHandlerHelper.insertItemStacked((IItemHandler)crusherBE.inventory, (ItemStack)toInsert, (boolean)false);
                if (ItemStack.matches((ItemStack)toInsert, (ItemStack)remainder)) {
                    return true;
                }
                int notFilled = currentItem.stack.getCount() - toInsert.getCount();
                if (!remainder.isEmpty()) {
                    remainder.grow(notFilled);
                } else if (notFilled > 0) {
                    remainder = currentItem.stack.copyWithCount(notFilled);
                }
                currentItem.stack = remainder;
                beltInventory.belt.notifyUpdate();
                return true;
            }
            segment += step;
        }
        return false;
    }
}
