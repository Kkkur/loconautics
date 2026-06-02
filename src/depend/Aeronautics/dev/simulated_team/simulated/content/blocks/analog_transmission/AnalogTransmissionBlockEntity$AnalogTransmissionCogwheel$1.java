/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.simpleRelays.ICogWheel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.simulated_team.simulated.content.blocks.analog_transmission;

import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import dev.simulated_team.simulated.content.blocks.analog_transmission.AnalogTransmissionBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

class AnalogTransmissionBlockEntity.AnalogTransmissionCogwheel.1
implements ICogWheel {
    AnalogTransmissionBlockEntity.AnalogTransmissionCogwheel.1() {
    }

    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return false;
    }

    public Direction.Axis getRotationAxis(BlockState state) {
        return (Direction.Axis)state.getValue((Property)AnalogTransmissionBlock.AXIS);
    }
}
