/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 */
package com.simibubi.create.content.contraptions.elevator;

import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public record ElevatorColumn.ColumnCoords(int x, int z, Direction side) {
    public ElevatorColumn.ColumnCoords relative(BlockPos anchor) {
        return new ElevatorColumn.ColumnCoords(this.x + anchor.getX(), this.z + anchor.getZ(), this.side);
    }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("X", this.x);
        tag.putInt("Z", this.z);
        NBTHelper.writeEnum((CompoundTag)tag, (String)"Side", (Enum)this.side);
        return tag;
    }

    public static ElevatorColumn.ColumnCoords read(CompoundTag tag) {
        int x = tag.getInt("X");
        int z = tag.getInt("Z");
        Direction side = (Direction)NBTHelper.readEnum((CompoundTag)tag, (String)"Side", Direction.class);
        return new ElevatorColumn.ColumnCoords(x, z, side);
    }
}
