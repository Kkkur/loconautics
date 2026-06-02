/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.WorldlyContainer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.ComposterBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  net.neoforged.neoforge.items.wrapper.SidedInvWrapper
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;

public static class AllArmInteractionPointTypes.ComposterPoint
extends ArmInteractionPoint {
    public AllArmInteractionPointTypes.ComposterPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    protected Vec3 getInteractionPositionVector() {
        return Vec3.atLowerCornerOf((Vec3i)this.pos).add(0.5, 0.8125, 0.5);
    }

    @Override
    public void updateCachedState() {
        BlockState oldState = this.cachedState;
        super.updateCachedState();
        if (this.cachedHandler != null && oldState != this.cachedState) {
            this.level.invalidateCapabilities(this.cachedHandler.pos());
        }
    }

    @Override
    @Nullable
    protected IItemHandler getHandler(ArmBlockEntity armBlockEntity) {
        return null;
    }

    protected WorldlyContainer getContainer() {
        ComposterBlock composterBlock = (ComposterBlock)Blocks.COMPOSTER;
        return composterBlock.getContainer(this.cachedState, (LevelAccessor)this.level, this.pos);
    }

    @Override
    public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
        SidedInvWrapper handler = new SidedInvWrapper(this.getContainer(), Direction.UP);
        return ItemHandlerHelper.insertItem((IItemHandler)handler, (ItemStack)stack, (boolean)simulate);
    }

    @Override
    public ItemStack extract(ArmBlockEntity armBlockEntity, int slot, int amount, boolean simulate) {
        SidedInvWrapper handler = new SidedInvWrapper(this.getContainer(), Direction.DOWN);
        return handler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotCount(ArmBlockEntity armBlockEntity) {
        return 2;
    }
}
