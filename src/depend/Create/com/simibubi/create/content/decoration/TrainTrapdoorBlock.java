/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.TrapDoorBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockSetType
 *  net.minecraft.world.level.block.state.properties.Half
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.BlockHitResult
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 */
package com.simibubi.create.content.decoration;

import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.ApiStatus;

public class TrainTrapdoorBlock
extends TrapDoorBlock
implements IWrenchable {
    @Deprecated(since="6.0.7", forRemoval=true)
    @ApiStatus.ScheduledForRemoval(inVersion="1.21.1+ Port")
    public TrainTrapdoorBlock(BlockBehaviour.Properties properties) {
        super(SlidingDoorBlock.TRAIN_SET_TYPE.get(), properties);
    }

    public TrainTrapdoorBlock(BlockSetType type, BlockBehaviour.Properties properties) {
        super(type, properties);
    }

    public static TrainTrapdoorBlock metal(BlockBehaviour.Properties properties) {
        return new TrainTrapdoorBlock(SlidingDoorBlock.TRAIN_SET_TYPE.get(), properties);
    }

    public static TrainTrapdoorBlock glass(BlockBehaviour.Properties properties) {
        return new TrainTrapdoorBlock(SlidingDoorBlock.GLASS_SET_TYPE.get(), properties);
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        state = (BlockState)state.cycle((Property)OPEN);
        level.setBlock(pos, state, 2);
        if (((Boolean)state.getValue((Property)WATERLOGGED)).booleanValue()) {
            level.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay((LevelReader)level));
        }
        this.playSound(player, level, pos, (Boolean)state.getValue((Property)OPEN));
        return InteractionResult.sidedSuccess((boolean)level.isClientSide);
    }

    public boolean skipRendering(BlockState state, BlockState other, Direction pDirection) {
        return state.is((Block)this) == other.is((Block)this) && TrainTrapdoorBlock.isConnected(state, other, pDirection);
    }

    public static boolean isConnected(BlockState state, BlockState other, Direction pDirection) {
        state = (BlockState)((BlockState)state.setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false))).setValue((Property)POWERED, (Comparable)Boolean.valueOf(false));
        other = (BlockState)((BlockState)other.setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false))).setValue((Property)POWERED, (Comparable)Boolean.valueOf(false));
        boolean open = (Boolean)state.getValue((Property)OPEN);
        Half half = (Half)state.getValue((Property)HALF);
        Direction facing = (Direction)state.getValue((Property)FACING);
        if (open != (Boolean)other.getValue((Property)OPEN)) {
            return false;
        }
        if (!open && half == other.getValue((Property)HALF)) {
            return pDirection.getAxis() != Direction.Axis.Y;
        }
        if (!open && half != other.getValue((Property)HALF) && pDirection.getAxis() == Direction.Axis.Y) {
            return true;
        }
        if (open && facing.getOpposite() == other.getValue((Property)FACING) && pDirection.getAxis() == facing.getAxis()) {
            return true;
        }
        if ((open ? (BlockState)state.setValue((Property)HALF, (Comparable)Half.TOP) : state) != (open ? (BlockState)other.setValue((Property)HALF, (Comparable)Half.TOP) : other)) {
            return false;
        }
        return pDirection.getAxis() != facing.getAxis();
    }
}
