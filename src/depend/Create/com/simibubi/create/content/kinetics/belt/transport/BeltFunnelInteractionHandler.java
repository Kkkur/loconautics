/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.belt.transport;

import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.Property;

public class BeltFunnelInteractionHandler {
    /*
     * Unable to fully structure code
     */
    public static boolean checkForFunnels(BeltInventory beltInventory, TransportedItemStack currentItem, float nextOffset) {
        beltMovementPositive = beltInventory.beltMovementPositive;
        firstUpcomingSegment = (int)Math.floor(currentItem.beltPosition);
        step = beltMovementPositive != false ? 1 : -1;
        segment = firstUpcomingSegment = Mth.clamp((int)firstUpcomingSegment, (int)0, (int)(beltInventory.belt.beltLength - 1));
        while (beltMovementPositive != false ? (float)segment <= nextOffset : (float)(segment + 1) >= nextOffset) {
            block18: {
                block21: {
                    block20: {
                        block19: {
                            funnelPos = BeltHelper.getPositionForOffset(beltInventory.belt, segment).above();
                            world = beltInventory.belt.getLevel();
                            funnelState = world.getBlockState(funnelPos);
                            if (!(funnelState.getBlock() instanceof BeltFunnelBlock)) break block18;
                            funnelFacing = (Direction)funnelState.getValue((Property)BeltFunnelBlock.HORIZONTAL_FACING);
                            v0 = blocking = funnelFacing == (movementFacing = beltInventory.belt.getMovementFacing()).getOpposite();
                            if (funnelFacing == movementFacing || funnelState.getValue(BeltFunnelBlock.SHAPE) == BeltFunnelBlock.Shape.PUSHING) break block18;
                            funnelEntry = (float)segment + 0.5f;
                            if (funnelState.getValue(BeltFunnelBlock.SHAPE) == BeltFunnelBlock.Shape.EXTENDED) {
                                funnelEntry += 0.499f * (float)(beltMovementPositive != false ? -1 : 1);
                            }
                            v1 = hasCrossed = nextOffset > funnelEntry && beltMovementPositive != false || nextOffset < funnelEntry && beltMovementPositive == false;
                            if (!hasCrossed) {
                                return false;
                            }
                            if (blocking) {
                                currentItem.beltPosition = funnelEntry;
                            }
                            if (!world.isClientSide && !funnelState.getOptionalValue((Property)BeltFunnelBlock.POWERED).orElse(false).booleanValue()) break block19;
                            if (blocking) {
                                return true;
                            }
                            break block18;
                        }
                        be = world.getBlockEntity(funnelPos);
                        if (!(be instanceof FunnelBlockEntity)) {
                            return true;
                        }
                        funnelBE = (FunnelBlockEntity)be;
                        inserting = funnelBE.getBehaviour(InvManipulationBehaviour.TYPE);
                        filtering = funnelBE.getBehaviour(FilteringBehaviour.TYPE);
                        if (inserting != null && (filtering == null || filtering.test(currentItem.stack))) break block20;
                        if (blocking) {
                            return true;
                        }
                        break block18;
                    }
                    if (beltInventory.belt.invVersionTracker.stillWaiting(inserting)) break block18;
                    amountToExtract = funnelBE.getAmountToExtract();
                    modeToExtract = funnelBE.getModeToExtract();
                    toInsert = currentItem.stack.copy();
                    if (amountToExtract <= toInsert.getCount() || modeToExtract == ItemHelper.ExtractionCountMode.UPTO) break block21;
                    if (blocking) {
                        return true;
                    }
                    break block18;
                }
                if (amountToExtract == -1 || modeToExtract == ItemHelper.ExtractionCountMode.UPTO) ** GOTO lbl54
                toInsert.setCount(Math.min(amountToExtract, toInsert.getCount()));
                remainder = ((InvManipulationBehaviour)inserting.simulate()).insert(toInsert);
                if (!remainder.isEmpty()) {
                    if (blocking) {
                        return true;
                    }
                } else {
                    beltInventory.belt.invVersionTracker.awaitNewVersion(inserting);
lbl54:
                    // 2 sources

                    if (ItemStack.matches((ItemStack)toInsert, (ItemStack)(remainder = inserting.insert(toInsert)))) {
                        beltInventory.belt.invVersionTracker.awaitNewVersion(inserting);
                        if (blocking) {
                            return true;
                        }
                    } else {
                        notFilled = currentItem.stack.getCount() - toInsert.getCount();
                        if (!remainder.isEmpty()) {
                            remainder.grow(notFilled);
                        } else if (notFilled > 0) {
                            remainder = currentItem.stack.copyWithCount(notFilled);
                        }
                        funnelBE.flap(true);
                        funnelBE.onTransfer(toInsert);
                        currentItem.stack = remainder;
                        beltInventory.belt.notifyUpdate();
                        if (blocking) {
                            return true;
                        }
                    }
                }
            }
            segment += step;
        }
        return false;
    }
}
