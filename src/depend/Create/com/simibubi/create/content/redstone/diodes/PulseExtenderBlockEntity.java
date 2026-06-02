/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.redstone.diodes;

import com.simibubi.create.content.redstone.diodes.BrassDiodeBlock;
import com.simibubi.create.content.redstone.diodes.BrassDiodeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class PulseExtenderBlockEntity
extends BrassDiodeBlockEntity {
    public PulseExtenderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void updateState(boolean powered, boolean powering, boolean atMax, boolean atMin) {
        if (atMin && !powered) {
            return;
        }
        if (atMin || powered) {
            this.level.setBlockAndUpdate(this.worldPosition, (BlockState)this.getBlockState().setValue((Property)BrassDiodeBlock.POWERING, (Comparable)Boolean.valueOf(true)));
            this.state = this.maxState.getValue();
            return;
        }
        if (this.state == 1) {
            if (powering && !this.level.isClientSide) {
                this.level.setBlockAndUpdate(this.worldPosition, (BlockState)this.getBlockState().setValue((Property)BrassDiodeBlock.POWERING, (Comparable)Boolean.valueOf(false)));
            }
            if (!powered) {
                this.state = 0;
            }
            return;
        }
        if (!powered) {
            --this.state;
        }
    }
}
