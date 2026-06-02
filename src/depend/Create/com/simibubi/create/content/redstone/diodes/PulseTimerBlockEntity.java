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

public class PulseTimerBlockEntity
extends BrassDiodeBlockEntity {
    public PulseTimerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected int defaultValue() {
        return 20;
    }

    @Override
    protected void updateState(boolean powered, boolean powering, boolean atMax, boolean atMin) {
        this.state = powered || this.state >= this.maxState.getValue() - 1 ? 0 : ++this.state;
        if (this.level.isClientSide) {
            return;
        }
        boolean shouldPower = !powered && (this.maxState.getValue() == 2 ? this.state == 0 : this.state <= 1);
        BlockState blockState = this.getBlockState();
        if ((Boolean)blockState.getValue((Property)BrassDiodeBlock.POWERING) != shouldPower) {
            this.level.setBlockAndUpdate(this.worldPosition, (BlockState)blockState.setValue((Property)BrassDiodeBlock.POWERING, (Comparable)Boolean.valueOf(shouldPower)));
        }
    }
}
