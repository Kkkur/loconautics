/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.fluids.tank.storage;

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.storage.FluidTankMountedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FluidTankMountedStorageType
extends MountedFluidStorageType<FluidTankMountedStorage> {
    public FluidTankMountedStorageType() {
        super(FluidTankMountedStorage.CODEC);
    }

    @Override
    @Nullable
    public FluidTankMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        FluidTankBlockEntity tank;
        if (be instanceof FluidTankBlockEntity && (tank = (FluidTankBlockEntity)be).isController()) {
            return FluidTankMountedStorage.fromTank(tank);
        }
        return null;
    }
}
