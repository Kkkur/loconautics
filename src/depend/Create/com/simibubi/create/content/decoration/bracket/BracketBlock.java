/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.decoration.bracket;

import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import java.util.Optional;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class BracketBlock
extends WrenchableDirectionalBlock {
    public static final BooleanProperty AXIS_ALONG_FIRST_COORDINATE = DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE;
    public static final EnumProperty<BracketType> TYPE = EnumProperty.create((String)"type", BracketType.class);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{AXIS_ALONG_FIRST_COORDINATE}).add(new Property[]{TYPE}));
    }

    public BracketBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public Optional<BlockState> getSuitableBracket(BlockState blockState, Direction direction) {
        if (blockState.getBlock() instanceof AbstractSimpleShaftBlock) {
            return this.getSuitableBracket((Direction.Axis)blockState.getValue(RotatedPillarKineticBlock.AXIS), direction, blockState.getBlock() instanceof CogWheelBlock ? BracketType.COG : BracketType.SHAFT);
        }
        return this.getSuitableBracket(FluidPropagator.getStraightPipeAxis(blockState), direction, BracketType.PIPE);
    }

    private Optional<BlockState> getSuitableBracket(Direction.Axis targetBlockAxis, Direction direction, BracketType type) {
        Direction.Axis axis = direction.getAxis();
        if (targetBlockAxis == null || targetBlockAxis == axis) {
            return Optional.empty();
        }
        boolean alongFirst = axis != Direction.Axis.Z ? targetBlockAxis == Direction.Axis.Z : targetBlockAxis == Direction.Axis.Y;
        return Optional.of((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(TYPE, (Comparable)((Object)type))).setValue((Property)FACING, (Comparable)direction)).setValue((Property)AXIS_ALONG_FIRST_COORDINATE, (Comparable)Boolean.valueOf(!alongFirst)));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        if (rot.ordinal() % 2 == 1) {
            state = (BlockState)state.cycle((Property)AXIS_ALONG_FIRST_COORDINATE);
        }
        return super.rotate(state, rot);
    }

    public static enum BracketType implements StringRepresentable
    {
        PIPE,
        COG,
        SHAFT;


        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }
    }
}
