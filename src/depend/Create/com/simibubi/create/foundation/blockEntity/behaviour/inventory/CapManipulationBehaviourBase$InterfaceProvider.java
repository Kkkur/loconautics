/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.BlockFace
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.foundation.blockEntity.behaviour.inventory;

import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

@FunctionalInterface
public static interface CapManipulationBehaviourBase.InterfaceProvider {
    public static CapManipulationBehaviourBase.InterfaceProvider towardBlockFacing() {
        return (w, p, s) -> new BlockFace(p, s.hasProperty((Property)BlockStateProperties.FACING) ? (Direction)s.getValue((Property)BlockStateProperties.FACING) : (Direction)s.getValue((Property)BlockStateProperties.HORIZONTAL_FACING));
    }

    public static CapManipulationBehaviourBase.InterfaceProvider oppositeOfBlockFacing() {
        return (w, p, s) -> new BlockFace(p, (s.hasProperty((Property)BlockStateProperties.FACING) ? (Direction)s.getValue((Property)BlockStateProperties.FACING) : (Direction)s.getValue((Property)BlockStateProperties.HORIZONTAL_FACING)).getOpposite());
    }

    public BlockFace getTarget(Level var1, BlockPos var2, BlockState var3);
}
