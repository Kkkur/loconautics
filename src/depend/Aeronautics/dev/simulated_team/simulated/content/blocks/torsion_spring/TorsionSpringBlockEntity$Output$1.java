/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.simulated_team.simulated.content.blocks.torsion_spring;

import com.simibubi.create.content.kinetics.base.IRotate;
import dev.simulated_team.simulated.content.blocks.torsion_spring.TorsionSpringBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

class TorsionSpringBlockEntity.Output.1
implements IRotate {
    TorsionSpringBlockEntity.Output.1() {
    }

    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue((Property)TorsionSpringBlock.FACING);
    }

    public Direction.Axis getRotationAxis(BlockState state) {
        return ((Direction)state.getValue((Property)TorsionSpringBlock.FACING)).getAxis();
    }
}
