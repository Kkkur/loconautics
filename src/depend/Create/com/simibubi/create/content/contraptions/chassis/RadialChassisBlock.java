/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.contraptions.chassis;

import com.simibubi.create.content.contraptions.chassis.AbstractChassisBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class RadialChassisBlock
extends AbstractChassisBlock {
    public static final BooleanProperty STICKY_NORTH = BooleanProperty.create((String)"sticky_north");
    public static final BooleanProperty STICKY_SOUTH = BooleanProperty.create((String)"sticky_south");
    public static final BooleanProperty STICKY_EAST = BooleanProperty.create((String)"sticky_east");
    public static final BooleanProperty STICKY_WEST = BooleanProperty.create((String)"sticky_west");

    public RadialChassisBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue((Property)STICKY_EAST, (Comparable)Boolean.valueOf(false))).setValue((Property)STICKY_SOUTH, (Comparable)Boolean.valueOf(false))).setValue((Property)STICKY_NORTH, (Comparable)Boolean.valueOf(false))).setValue((Property)STICKY_WEST, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{STICKY_NORTH, STICKY_EAST, STICKY_SOUTH, STICKY_WEST});
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BooleanProperty getGlueableSide(BlockState state, Direction face) {
        Direction.Axis axis = (Direction.Axis)state.getValue((Property)AXIS);
        if (axis == Direction.Axis.X) {
            if (face == Direction.NORTH) {
                return STICKY_WEST;
            }
            if (face == Direction.SOUTH) {
                return STICKY_EAST;
            }
            if (face == Direction.UP) {
                return STICKY_NORTH;
            }
            if (face == Direction.DOWN) {
                return STICKY_SOUTH;
            }
        }
        if (axis == Direction.Axis.Y) {
            if (face == Direction.NORTH) {
                return STICKY_NORTH;
            }
            if (face == Direction.SOUTH) {
                return STICKY_SOUTH;
            }
            if (face == Direction.EAST) {
                return STICKY_EAST;
            }
            if (face == Direction.WEST) {
                return STICKY_WEST;
            }
        }
        if (axis == Direction.Axis.Z) {
            if (face == Direction.UP) {
                return STICKY_NORTH;
            }
            if (face == Direction.DOWN) {
                return STICKY_SOUTH;
            }
            if (face == Direction.EAST) {
                return STICKY_EAST;
            }
            if (face == Direction.WEST) {
                return STICKY_WEST;
            }
        }
        return null;
    }
}
