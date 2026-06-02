/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.velocity_sensor;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.block.IBE;
import dev.simulated_team.simulated.content.blocks.util.AbstractDirectionalAxisBlock;
import dev.simulated_team.simulated.content.blocks.velocity_sensor.VelocitySensorBlockEntity;
import dev.simulated_team.simulated.content.blocks.velocity_sensor.VelocitySensorShaper;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.multiloader.CommonRedstoneBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VelocitySensorBlock
extends AbstractDirectionalAxisBlock
implements IBE<VelocitySensorBlockEntity>,
CommonRedstoneBlock {
    public static final MapCodec<VelocitySensorBlock> CODEC = VelocitySensorBlock.simpleCodec(VelocitySensorBlock::new);
    public static IntegerProperty POWERED = IntegerProperty.create((String)"powered", (int)0, (int)2);
    private static final VelocitySensorShaper VELOCITY_SENSOR = VelocitySensorShaper.make();

    public VelocitySensorBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState)super.getStateForPlacement(context).setValue((Property)POWERED, (Comparable)Integer.valueOf(0));
    }

    protected int getSignal(@NotNull BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        int powered = (Integer)blockState.getValue((Property)POWERED);
        if (powered == 0) {
            return 0;
        }
        Direction positiveDir = VelocitySensorBlock.getDirectionOfAxis(blockState);
        if (powered == 2) {
            positiveDir = positiveDir.getOpposite();
        }
        if (direction != positiveDir) {
            return 0;
        }
        int power = 0;
        if (blockState.hasBlockEntity()) {
            power = ((VelocitySensorBlockEntity)blockGetter.getBlockEntity(blockPos)).getRedstoneStrength();
        }
        return power;
    }

    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        Direction facing = (Direction)state.getValue((Property)FACING);
        if (!facing.getAxis().isHorizontal()) {
            return 0;
        }
        if (facing.getAxis() == Direction.EAST.getAxis() && !((Boolean)state.getValue((Property)AXIS_ALONG_FIRST_COORDINATE)).booleanValue()) {
            return 0;
        }
        if (facing.getAxis() == Direction.NORTH.getAxis() && ((Boolean)state.getValue((Property)AXIS_ALONG_FIRST_COORDINATE)).booleanValue()) {
            return 0;
        }
        return this.getSignal(state, level, pos, direction);
    }

    @Override
    public boolean commonConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return direction != null && VelocitySensorBlock.getAxis(state) == direction.getAxis();
    }

    protected boolean isSignalSource(BlockState blockState) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)pBuilder.add(new Property[]{POWERED}));
    }

    public Class<VelocitySensorBlockEntity> getBlockEntityClass() {
        return VelocitySensorBlockEntity.class;
    }

    public BlockEntityType<? extends VelocitySensorBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.VELOCITY_SENSOR.get();
    }

    public VoxelShape getShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return VELOCITY_SENSOR.get((Direction)state.getValue((Property)FACING), (Boolean)state.getValue((Property)AXIS_ALONG_FIRST_COORDINATE));
    }
}
