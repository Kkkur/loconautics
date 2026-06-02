/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.RespawnAnchorBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public static class AllArmInteractionPointTypes.RespawnAnchorPoint
extends AllArmInteractionPointTypes.DepositOnlyArmInteractionPoint {
    public AllArmInteractionPointTypes.RespawnAnchorPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    protected Vec3 getInteractionPositionVector() {
        return Vec3.atLowerCornerOf((Vec3i)this.pos).add(0.5, 1.0, 0.5);
    }

    @Override
    public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
        if (!stack.is(Items.GLOWSTONE)) {
            return stack;
        }
        if (this.cachedState.getOptionalValue((Property)RespawnAnchorBlock.CHARGE).orElse(4) == 4) {
            return stack;
        }
        if (!simulate) {
            RespawnAnchorBlock.charge(null, (Level)this.level, (BlockPos)this.pos, (BlockState)this.cachedState);
        }
        ItemStack remainder = stack.copy();
        remainder.shrink(1);
        return remainder;
    }
}
