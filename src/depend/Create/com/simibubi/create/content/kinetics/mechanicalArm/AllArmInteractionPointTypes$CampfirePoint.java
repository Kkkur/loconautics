/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CampfireCookingRecipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.CampfireBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public static class AllArmInteractionPointTypes.CampfirePoint
extends AllArmInteractionPointTypes.DepositOnlyArmInteractionPoint {
    public AllArmInteractionPointTypes.CampfirePoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
        BlockEntity blockEntity = this.level.getBlockEntity(this.pos);
        if (!(blockEntity instanceof CampfireBlockEntity)) {
            return stack;
        }
        CampfireBlockEntity campfireBE = (CampfireBlockEntity)blockEntity;
        Optional recipe = campfireBE.getCookableRecipe(stack);
        if (recipe.isEmpty()) {
            return stack;
        }
        if (simulate) {
            boolean hasSpace = false;
            for (ItemStack campfireStack : campfireBE.getItems()) {
                if (!campfireStack.isEmpty()) continue;
                hasSpace = true;
                break;
            }
            if (!hasSpace) {
                return stack;
            }
            ItemStack remainder = stack.copy();
            remainder.shrink(1);
            return remainder;
        }
        ItemStack remainder = stack.copy();
        campfireBE.placeFood(null, remainder, ((CampfireCookingRecipe)((RecipeHolder)recipe.get()).value()).getCookingTime());
        return remainder;
    }
}
