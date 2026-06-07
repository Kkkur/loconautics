package com.lycoris.loconautics.content.bearingaxle;

import com.lycoris.loconautics.registry.LoconauticsRegistries;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BearingAxleBlock extends RotatedPillarKineticBlock implements IBE<BearingAxleBlockEntity> {

    public BearingAxleBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(AXIS);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public Class<BearingAxleBlockEntity> getBlockEntityClass() {
        return BearingAxleBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BearingAxleBlockEntity> getBlockEntityType() {
        return LoconauticsRegistries.BEARING_AXLE_BE.get();
    }
}