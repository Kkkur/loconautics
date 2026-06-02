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
package com.simibubi.create.content.fluids.tank.storage.creative;

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.storage.creative.CreativeFluidTankMountedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CreativeFluidTankMountedStorageType
extends MountedFluidStorageType<CreativeFluidTankMountedStorage> {
    public CreativeFluidTankMountedStorageType() {
        super(CreativeFluidTankMountedStorage.CODEC);
    }

    @Override
    @Nullable
    public CreativeFluidTankMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        if (be instanceof CreativeFluidTankBlockEntity) {
            CreativeFluidTankBlockEntity tank = (CreativeFluidTankBlockEntity)be;
            return CreativeFluidTankMountedStorage.fromTank(tank);
        }
        return null;
    }
}
