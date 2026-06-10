/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.bearing.linearbearing;

import com.bearing.linearbearing.RedstoneConverterBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class RedstoneConverterBlockEntity
extends KineticBlockEntity {
    private int lastRedstoneSignal = 0;

    public RedstoneConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick() {
        super.tick();
        if (this.level != null && !this.level.isClientSide) {
            boolean isCurrentlyPowered;
            int currentSignal = this.getRedstoneSignal();
            BlockState state = this.getBlockState();
            boolean shouldBePowered = currentSignal > 0;
            boolean bl = isCurrentlyPowered = state.hasProperty((Property)RedstoneConverterBlock.POWERED) && (Boolean)state.getValue((Property)RedstoneConverterBlock.POWERED) != false;
            if (currentSignal != this.lastRedstoneSignal || shouldBePowered != isCurrentlyPowered) {
                this.lastRedstoneSignal = currentSignal;
                if (shouldBePowered != isCurrentlyPowered && state.hasProperty((Property)RedstoneConverterBlock.POWERED)) {
                    this.level.setBlock(this.worldPosition, (BlockState)state.setValue((Property)RedstoneConverterBlock.POWERED, (Comparable)Boolean.valueOf(shouldBePowered)), 3);
                }
                this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
                this.setChanged();
            }
        }
    }

    public int getRedstoneSignal() {
        float absoluteSpeed = Math.abs(this.getSpeed());
        int signal = Mth.floor((float)(absoluteSpeed / 8.0f));
        return Mth.clamp((int)signal, (int)0, (int)15);
    }
}
