/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.logistics.funnel;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class AndesiteFunnelBlock
extends FunnelBlock {
    public AndesiteFunnelBlock(BlockBehaviour.Properties p_i48415_1_) {
        super(p_i48415_1_);
    }

    @Override
    public BlockState getEquivalentBeltFunnel(BlockGetter world, BlockPos pos, BlockState state) {
        Direction facing = AndesiteFunnelBlock.getFunnelFacing(state);
        return (BlockState)((BlockState)AllBlocks.ANDESITE_BELT_FUNNEL.getDefaultState().setValue((Property)BeltFunnelBlock.HORIZONTAL_FACING, (Comparable)facing)).setValue((Property)POWERED, (Comparable)((Boolean)state.getValue((Property)POWERED)));
    }
}
