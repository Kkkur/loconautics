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

public class PulseRepeaterBlockEntity
extends BrassDiodeBlockEntity {
    public PulseRepeaterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void updateState(boolean powered, boolean powering, boolean atMax, boolean atMin) {
        if (atMin && !powered) {
            return;
        }
        if (this.state > this.maxState.getValue() + 1) {
            if (!powered && !powering) {
                this.state = 0;
            }
            return;
        }
        ++this.state;
        if (this.level.isClientSide) {
            return;
        }
        if (this.state == this.maxState.getValue() - 1 && !powering) {
            this.level.setBlockAndUpdate(this.worldPosition, (BlockState)this.getBlockState().cycle((Property)BrassDiodeBlock.POWERING));
        }
        if (this.state == this.maxState.getValue() + 1 && powering) {
            this.level.setBlockAndUpdate(this.worldPosition, (BlockState)this.getBlockState().cycle((Property)BrassDiodeBlock.POWERING));
        }
    }
}
