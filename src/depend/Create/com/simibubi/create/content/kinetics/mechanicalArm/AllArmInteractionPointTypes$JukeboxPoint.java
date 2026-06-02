/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.JukeboxBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.JukeboxBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public static class AllArmInteractionPointTypes.JukeboxPoint
extends AllArmInteractionPointTypes.TopFaceArmInteractionPoint {
    public AllArmInteractionPointTypes.JukeboxPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    public int getSlotCount(ArmBlockEntity armBlockEntity) {
        return 1;
    }

    @Override
    public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
        if (stack.get(DataComponents.JUKEBOX_PLAYABLE) == null) {
            return stack;
        }
        if (this.cachedState.getOptionalValue((Property)JukeboxBlock.HAS_RECORD).orElse(true).booleanValue()) {
            return stack;
        }
        BlockEntity blockEntity = this.level.getBlockEntity(this.pos);
        if (!(blockEntity instanceof JukeboxBlockEntity)) {
            return stack;
        }
        JukeboxBlockEntity jukeboxBE = (JukeboxBlockEntity)blockEntity;
        if (!jukeboxBE.getTheItem().isEmpty()) {
            return stack;
        }
        ItemStack remainder = stack.copy();
        ItemStack toInsert = remainder.split(1);
        if (!simulate) {
            jukeboxBE.setTheItem(toInsert);
        }
        return remainder;
    }

    @Override
    public ItemStack extract(ArmBlockEntity armBlockEntity, int slot, int amount, boolean simulate) {
        if (!this.cachedState.getOptionalValue((Property)JukeboxBlock.HAS_RECORD).orElse(false).booleanValue()) {
            return ItemStack.EMPTY;
        }
        BlockEntity blockEntity = this.level.getBlockEntity(this.pos);
        if (!(blockEntity instanceof JukeboxBlockEntity)) {
            return ItemStack.EMPTY;
        }
        JukeboxBlockEntity jukeboxBE = (JukeboxBlockEntity)blockEntity;
        if (!simulate) {
            return jukeboxBE.removeItem(slot, amount);
        }
        return jukeboxBE.getTheItem();
    }
}
