/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.kinetics.transmission;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.kinetics.transmission.GearshiftBlock;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class ClutchBlock
extends GearshiftBlock {
    public ClutchBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (worldIn.isClientSide) {
            return;
        }
        boolean previouslyPowered = (Boolean)state.getValue((Property)POWERED);
        if (previouslyPowered != worldIn.hasNeighborSignal(pos)) {
            worldIn.setBlock(pos, (BlockState)state.cycle((Property)POWERED), 18);
            this.detachKinetics(worldIn, pos, previouslyPowered);
        }
    }

    @Override
    public BlockEntityType<? extends SplitShaftBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.CLUTCH.get();
    }
}
