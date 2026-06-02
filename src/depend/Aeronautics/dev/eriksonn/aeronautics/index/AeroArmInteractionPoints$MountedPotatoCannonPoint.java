/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes$DepositOnlyArmInteractionPoint
 *  com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity
 *  com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.eriksonn.aeronautics.index;

import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import dev.eriksonn.aeronautics.content.blocks.mounted_potato_cannon.MountedPotatoCannonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public static class AeroArmInteractionPoints.MountedPotatoCannonPoint
extends AllArmInteractionPointTypes.DepositOnlyArmInteractionPoint {
    public AeroArmInteractionPoints.MountedPotatoCannonPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
        BlockEntity be;
        if (this.cachedState.hasBlockEntity() && (be = this.level.getBlockEntity(this.pos)) instanceof MountedPotatoCannonBlockEntity) {
            MountedPotatoCannonBlockEntity sbe = (MountedPotatoCannonBlockEntity)be;
            return sbe.getInventory().insertSlot(stack, 0, simulate);
        }
        return super.insert(armBlockEntity, stack, simulate);
    }
}
