/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.schematics.cannon;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.kinetics.belt.item.BeltConnectorItem;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.content.schematics.cannon.LaunchedItem;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public static class LaunchedItem.ForBelt
extends LaunchedItem.ForBlockState {
    public int length;
    public BeltBlockEntity.CasingType[] casings;

    public LaunchedItem.ForBelt() {
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        CompoundTag serializeNBT = super.serializeNBT(registries);
        serializeNBT.putInt("Length", this.length);
        serializeNBT.putIntArray("Casing", Arrays.stream(this.casings).map(Enum::ordinal).toList());
        return serializeNBT;
    }

    @Override
    void readNBT(CompoundTag nbt, HolderLookup.Provider registries, HolderGetter<Block> holderGetter) {
        this.length = nbt.getInt("Length");
        int[] intArray = nbt.getIntArray("Casing");
        this.casings = new BeltBlockEntity.CasingType[this.length];
        for (int i = 0; i < this.casings.length; ++i) {
            this.casings[i] = i >= intArray.length ? BeltBlockEntity.CasingType.NONE : BeltBlockEntity.CasingType.values()[Mth.clamp((int)intArray[i], (int)0, (int)(BeltBlockEntity.CasingType.values().length - 1))];
        }
        super.readNBT(nbt, registries, holderGetter);
    }

    public LaunchedItem.ForBelt(BlockPos start, BlockPos target, ItemStack stack, BlockState state, BeltBlockEntity.CasingType[] casings) {
        super(start, target, stack, state, null);
        this.casings = casings;
        this.length = casings.length;
    }

    @Override
    void place(Level world) {
        boolean isStart = this.state.getValue(BeltBlock.PART) == BeltPart.START;
        BlockPos offset = BeltBlock.nextSegmentPosition(this.state, BlockPos.ZERO, isStart);
        int i = this.length - 1;
        Direction.Axis axis = this.state.getValue(BeltBlock.SLOPE) == BeltSlope.SIDEWAYS ? Direction.Axis.Y : ((Direction)this.state.getValue(BeltBlock.HORIZONTAL_FACING)).getClockWise().getAxis();
        world.setBlockAndUpdate(this.target, (BlockState)AllBlocks.SHAFT.getDefaultState().setValue((Property)AbstractSimpleShaftBlock.AXIS, (Comparable)axis));
        BeltConnectorItem.createBelts(world, this.target, this.target.offset(offset.getX() * i, offset.getY() * i, offset.getZ() * i));
        for (int segment = 0; segment < this.length; ++segment) {
            BlockPos casingTarget;
            BlockEntity blockEntity;
            if (this.casings[segment] == BeltBlockEntity.CasingType.NONE || !((blockEntity = world.getBlockEntity(casingTarget = this.target.offset(offset.getX() * segment, offset.getY() * segment, offset.getZ() * segment))) instanceof BeltBlockEntity)) continue;
            BeltBlockEntity bbe = (BeltBlockEntity)blockEntity;
            bbe.setCasingType(this.casings[segment]);
        }
    }
}
