/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.common.SoundActions
 *  net.neoforged.neoforge.fluids.BaseFlowingFluid
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler$FluidAction
 *  net.neoforged.neoforge.fluids.capability.IFluidHandlerItem
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.fluid;

import com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.createmod.catnip.data.Pair;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

public class FluidHelper {
    public static boolean isWater(Fluid fluid) {
        return FluidHelper.convertToStill(fluid) == Fluids.WATER;
    }

    public static boolean isLava(Fluid fluid) {
        return FluidHelper.convertToStill(fluid) == Fluids.LAVA;
    }

    public static boolean isSame(FluidStack fluidStack, FluidStack fluidStack2) {
        return fluidStack.getFluid() == fluidStack2.getFluid();
    }

    public static boolean isSame(FluidStack fluidStack, Fluid fluid) {
        return fluidStack.getFluid() == fluid;
    }

    public static boolean isTag(Fluid fluid, TagKey<Fluid> tag) {
        return fluid.is(tag);
    }

    public static boolean isTag(FluidState fluid, TagKey<Fluid> tag) {
        return fluid.is(tag);
    }

    public static boolean isTag(FluidStack fluid, TagKey<Fluid> tag) {
        return FluidHelper.isTag(fluid.getFluid(), tag);
    }

    public static SoundEvent getFillSound(FluidStack fluid) {
        SoundEvent soundevent = fluid.getFluid().getFluidType().getSound(fluid, SoundActions.BUCKET_FILL);
        if (soundevent == null) {
            soundevent = FluidHelper.isTag(fluid, (TagKey<Fluid>)FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
        }
        return soundevent;
    }

    public static SoundEvent getEmptySound(FluidStack fluid) {
        SoundEvent soundevent = fluid.getFluid().getFluidType().getSound(fluid, SoundActions.BUCKET_EMPTY);
        if (soundevent == null) {
            soundevent = FluidHelper.isTag(fluid, (TagKey<Fluid>)FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        }
        return soundevent;
    }

    public static boolean hasBlockState(Fluid fluid) {
        BlockState blockState = fluid.defaultFluidState().createLegacyBlock();
        return blockState != null && blockState != Blocks.AIR.defaultBlockState();
    }

    public static FluidStack copyStackWithAmount(FluidStack fs, int amount) {
        if (amount <= 0) {
            return FluidStack.EMPTY;
        }
        if (fs.isEmpty()) {
            return FluidStack.EMPTY;
        }
        FluidStack copy = fs.copy();
        copy.setAmount(amount);
        return copy;
    }

    public static Fluid convertToFlowing(Fluid fluid) {
        if (fluid == Fluids.WATER) {
            return Fluids.FLOWING_WATER;
        }
        if (fluid == Fluids.LAVA) {
            return Fluids.FLOWING_LAVA;
        }
        if (fluid instanceof BaseFlowingFluid) {
            return ((BaseFlowingFluid)fluid).getFlowing();
        }
        return fluid;
    }

    public static Fluid convertToStill(Fluid fluid) {
        if (fluid == Fluids.FLOWING_WATER) {
            return Fluids.WATER;
        }
        if (fluid == Fluids.FLOWING_LAVA) {
            return Fluids.LAVA;
        }
        if (fluid instanceof BaseFlowingFluid) {
            return ((BaseFlowingFluid)fluid).getSource();
        }
        return fluid;
    }

    public static boolean tryEmptyItemIntoBE(Level worldIn, Player player, InteractionHand handIn, ItemStack heldItem, SmartBlockEntity be) {
        if (!GenericItemEmptying.canItemBeEmptied(worldIn, heldItem)) {
            return false;
        }
        Pair<FluidStack, ItemStack> emptyingResult = GenericItemEmptying.emptyItem(worldIn, heldItem, true);
        IFluidHandler capability = (IFluidHandler)worldIn.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), null);
        FluidStack fluidStack = (FluidStack)emptyingResult.getFirst();
        if (capability == null || fluidStack.getAmount() != capability.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE)) {
            return false;
        }
        if (worldIn.isClientSide) {
            return true;
        }
        ItemStack copyOfHeld = heldItem.copy();
        emptyingResult = GenericItemEmptying.emptyItem(worldIn, copyOfHeld, false);
        capability.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
        if (!player.isCreative() && !(be instanceof CreativeFluidTankBlockEntity)) {
            if (copyOfHeld.isEmpty()) {
                player.setItemInHand(handIn, (ItemStack)emptyingResult.getSecond());
            } else {
                player.setItemInHand(handIn, copyOfHeld);
                player.getInventory().placeItemBackInInventory((ItemStack)emptyingResult.getSecond());
            }
        }
        return true;
    }

    public static boolean tryFillItemFromBE(Level world, Player player, InteractionHand handIn, ItemStack heldItem, SmartBlockEntity be) {
        if (!GenericItemFilling.canItemBeFilled(world, heldItem)) {
            return false;
        }
        IFluidHandler capability = (IFluidHandler)world.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), null);
        if (capability == null) {
            return false;
        }
        for (int i = 0; i < capability.getTanks(); ++i) {
            int requiredAmountForItem;
            FluidStack fluid = capability.getFluidInTank(i);
            if (fluid.isEmpty() || (requiredAmountForItem = GenericItemFilling.getRequiredAmountForItem(world, heldItem, fluid.copy())) == -1 || requiredAmountForItem > fluid.getAmount()) continue;
            if (world.isClientSide) {
                return true;
            }
            if (player.isCreative() || be instanceof CreativeFluidTankBlockEntity) {
                heldItem = heldItem.copy();
            }
            ItemStack out = GenericItemFilling.fillItem(world, requiredAmountForItem, heldItem, fluid.copy());
            FluidStack copy = fluid.copy();
            copy.setAmount(requiredAmountForItem);
            capability.drain(copy, IFluidHandler.FluidAction.EXECUTE);
            if (!player.isCreative()) {
                player.getInventory().placeItemBackInInventory(out);
            }
            be.notifyUpdate();
            return true;
        }
        return false;
    }

    @Nullable
    public static FluidExchange exchange(IFluidHandler fluidTank, IFluidHandlerItem fluidItem, FluidExchange preferred, int maxAmount) {
        return FluidHelper.exchange(fluidTank, fluidItem, preferred, true, maxAmount);
    }

    @Nullable
    public static FluidExchange exchangeAll(IFluidHandler fluidTank, IFluidHandlerItem fluidItem, FluidExchange preferred) {
        return FluidHelper.exchange(fluidTank, fluidItem, preferred, false, Integer.MAX_VALUE);
    }

    @Nullable
    private static FluidExchange exchange(IFluidHandler fluidTank, IFluidHandlerItem fluidItem, FluidExchange preferred, boolean singleOp, int maxTransferAmountPerTank) {
        FluidExchange lockedExchange = null;
        for (int tankSlot = 0; tankSlot < fluidTank.getTanks(); ++tankSlot) {
            for (int slot = 0; slot < fluidItem.getTanks(); ++slot) {
                int amount;
                boolean canMoveToItem;
                FluidStack fluidInTank = fluidTank.getFluidInTank(tankSlot);
                int tankCapacity = fluidTank.getTankCapacity(tankSlot) - fluidInTank.getAmount();
                boolean tankEmpty = fluidInTank.isEmpty();
                FluidStack fluidInItem = fluidItem.getFluidInTank(tankSlot);
                int itemCapacity = fluidItem.getTankCapacity(tankSlot) - fluidInItem.getAmount();
                boolean itemEmpty = fluidInItem.isEmpty();
                boolean undecided = lockedExchange == null;
                boolean canMoveToTank = (undecided || lockedExchange == FluidExchange.ITEM_TO_TANK) && tankCapacity > 0;
                boolean bl = canMoveToItem = (undecided || lockedExchange == FluidExchange.TANK_TO_ITEM) && itemCapacity > 0;
                if (!tankEmpty && !itemEmpty && !FluidStack.isSameFluidSameComponents((FluidStack)fluidInItem, (FluidStack)fluidInTank)) continue;
                if (((tankEmpty || itemCapacity <= 0) && canMoveToTank || undecided && preferred == FluidExchange.ITEM_TO_TANK) && (amount = fluidTank.fill(fluidItem.drain(Math.min(maxTransferAmountPerTank, tankCapacity), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE)) > 0) {
                    lockedExchange = FluidExchange.ITEM_TO_TANK;
                    if (!singleOp) continue;
                    return lockedExchange;
                }
                if ((!itemEmpty && tankCapacity > 0 || !canMoveToItem) && (!undecided || preferred != FluidExchange.TANK_TO_ITEM) || (amount = fluidItem.fill(fluidTank.drain(Math.min(maxTransferAmountPerTank, itemCapacity), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE)) <= 0) continue;
                lockedExchange = FluidExchange.TANK_TO_ITEM;
                if (!singleOp) continue;
                return lockedExchange;
            }
        }
        return null;
    }

    public static enum FluidExchange {
        ITEM_TO_TANK,
        TANK_TO_ITEM;

    }
}
