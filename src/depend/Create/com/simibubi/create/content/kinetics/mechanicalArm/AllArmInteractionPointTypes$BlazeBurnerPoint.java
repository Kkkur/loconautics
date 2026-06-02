/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.Containers
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public static class AllArmInteractionPointTypes.BlazeBurnerPoint
extends AllArmInteractionPointTypes.DepositOnlyArmInteractionPoint {
    public AllArmInteractionPointTypes.BlazeBurnerPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
        ItemStack input = stack.copy();
        InteractionResultHolder<ItemStack> res = BlazeBurnerBlock.tryInsert(this.cachedState, this.level, this.pos, input, false, false, simulate);
        ItemStack remainder = (ItemStack)res.getObject();
        if (input.isEmpty()) {
            return remainder;
        }
        if (!simulate) {
            Containers.dropItemStack((Level)this.level, (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (ItemStack)remainder);
        }
        return input;
    }
}
