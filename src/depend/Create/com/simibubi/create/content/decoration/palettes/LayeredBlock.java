/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.decoration.palettes;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class LayeredBlock
extends RotatedPillarBlock {
    public LayeredBlock(BlockBehaviour.Properties p_55926_) {
        super(p_55926_);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState stateForPlacement = super.getStateForPlacement(pContext);
        BlockState placedOn = pContext.getLevel().getBlockState(pContext.getClickedPos().relative(pContext.getClickedFace().getOpposite()));
        if (!(placedOn.getBlock() != this || pContext.getPlayer() != null && pContext.getPlayer().isShiftKeyDown())) {
            stateForPlacement = (BlockState)stateForPlacement.setValue((Property)AXIS, (Comparable)((Direction.Axis)placedOn.getValue((Property)AXIS)));
        }
        return stateForPlacement;
    }
}
