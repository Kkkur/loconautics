/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.redstone.DirectedDirectionalBlock
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.SignalGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.lasers.laser_sensor;

import com.simibubi.create.content.redstone.DirectedDirectionalBlock;
import com.simibubi.create.foundation.block.IBE;
import dev.simulated_team.simulated.content.blocks.lasers.laser_sensor.LaserSensorBlockEntity;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.multiloader.CommonRedstoneBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class LaserSensorBlock
extends DirectedDirectionalBlock
implements IBE<LaserSensorBlockEntity>,
CommonRedstoneBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public LaserSensorBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{POWERED}));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferredFacing;
        BlockState state = this.defaultBlockState();
        Direction facing = context.getNearestLookingDirection();
        Direction direction = preferredFacing = context.getPlayer() != null && context.getPlayer().isSteppingCarefully() ? facing : facing.getOpposite();
        if (preferredFacing.getAxis() == Direction.Axis.Y) {
            state = (BlockState)state.setValue((Property)TARGET, (Comparable)(preferredFacing == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR));
            preferredFacing = context.getHorizontalDirection();
        }
        return (BlockState)((BlockState)state.setValue((Property)FACING, (Comparable)preferredFacing)).setValue((Property)POWERED, (Comparable)Boolean.valueOf(false));
    }

    public int getSignal(BlockState pState, BlockGetter level, BlockPos pos, Direction pDirection) {
        int power = 0;
        LaserSensorBlockEntity blockEntity = (LaserSensorBlockEntity)this.getBlockEntity(level, pos);
        if (blockEntity != null) {
            power = Math.max(0, Math.min(15, blockEntity.currentPower));
        }
        return power;
    }

    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public boolean commonCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
        return false;
    }

    @Override
    public boolean commonConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        if (direction == null) {
            return false;
        }
        return direction != ((Direction)state.getValue((Property)FACING)).getOpposite();
    }

    public Class<LaserSensorBlockEntity> getBlockEntityClass() {
        return LaserSensorBlockEntity.class;
    }

    public BlockEntityType<? extends LaserSensorBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.LASER_SENSOR.get();
    }
}
