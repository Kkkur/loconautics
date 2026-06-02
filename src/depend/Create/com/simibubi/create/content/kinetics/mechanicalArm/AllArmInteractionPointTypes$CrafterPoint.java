/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlock;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public static class AllArmInteractionPointTypes.CrafterPoint
extends ArmInteractionPoint {
    public AllArmInteractionPointTypes.CrafterPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    protected Direction getInteractionDirection() {
        return this.cachedState.getOptionalValue(MechanicalCrafterBlock.HORIZONTAL_FACING).orElse(Direction.SOUTH).getOpposite();
    }

    @Override
    protected Vec3 getInteractionPositionVector() {
        return super.getInteractionPositionVector().add(Vec3.atLowerCornerOf((Vec3i)this.getInteractionDirection().getNormal()).scale(0.5));
    }

    @Override
    public void updateCachedState() {
        BlockState oldState = this.cachedState;
        super.updateCachedState();
        if (oldState != this.cachedState) {
            this.cachedAngles = null;
        }
    }

    @Override
    public ItemStack extract(ArmBlockEntity armBlockEntity, int slot, int amount, boolean simulate) {
        BlockEntity be = this.level.getBlockEntity(this.pos);
        if (!(be instanceof MechanicalCrafterBlockEntity)) {
            return ItemStack.EMPTY;
        }
        MechanicalCrafterBlockEntity crafter = (MechanicalCrafterBlockEntity)be;
        MechanicalCrafterBlockEntity.Inventory inventory = crafter.getInventory();
        inventory.allowExtraction();
        ItemStack extract = super.extract(armBlockEntity, slot, amount, simulate);
        inventory.forbidExtraction();
        return extract;
    }
}
