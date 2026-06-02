/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.IFluidTank
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.blockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import org.jetbrains.annotations.Nullable;

public interface IMultiBlockEntityContainer {
    public BlockPos getController();

    public <T extends BlockEntity> T getControllerBE();

    public boolean isController();

    public void setController(BlockPos var1);

    public void removeController(boolean var1);

    public BlockPos getLastKnownPos();

    public void preventConnectivityUpdate();

    public void notifyMultiUpdated();

    default public void setExtraData(@Nullable Object data) {
    }

    @Nullable
    default public Object getExtraData() {
        return null;
    }

    default public Object modifyExtraData(Object data) {
        return data;
    }

    public Direction.Axis getMainConnectionAxis();

    default public Direction.Axis getMainAxisOf(BlockEntity be) {
        BlockState state = be.getBlockState();
        Direction.Axis axis = state.hasProperty((Property)BlockStateProperties.HORIZONTAL_AXIS) ? (Direction.Axis)state.getValue((Property)BlockStateProperties.HORIZONTAL_AXIS) : (state.hasProperty((Property)BlockStateProperties.FACING) ? ((Direction)state.getValue((Property)BlockStateProperties.FACING)).getAxis() : (state.hasProperty((Property)BlockStateProperties.HORIZONTAL_FACING) ? ((Direction)state.getValue((Property)BlockStateProperties.HORIZONTAL_FACING)).getAxis() : Direction.Axis.Y));
        return axis;
    }

    public int getMaxLength(Direction.Axis var1, int var2);

    public int getMaxWidth();

    public int getHeight();

    public void setHeight(int var1);

    public int getWidth();

    public void setWidth(int var1);

    public static interface Fluid
    extends IMultiBlockEntityContainer {
        default public boolean hasTank() {
            return false;
        }

        default public int getTankSize(int tank) {
            return 0;
        }

        default public void setTankSize(int tank, int blocks) {
        }

        default public IFluidTank getTank(int tank) {
            return null;
        }

        default public FluidStack getFluid(int tank) {
            return FluidStack.EMPTY;
        }
    }

    public static interface Inventory
    extends IMultiBlockEntityContainer {
        default public boolean hasInventory() {
            return false;
        }
    }
}
