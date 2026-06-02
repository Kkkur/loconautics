/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 */
package com.simibubi.create.content.redstone.smartObserver;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.content.redstone.DirectedDirectionalBlock;
import com.simibubi.create.content.redstone.smartObserver.SmartObserverBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.capabilities.Capabilities;

public class SmartObserverBlock
extends DirectedDirectionalBlock
implements IBE<SmartObserverBlockEntity> {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public SmartObserverBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition((StateDefinition.Builder<Block, BlockState>)builder.add(new Property[]{POWERED}));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        Direction preferredFacing = null;
        for (Direction face : context.getNearestLookingDirections()) {
            BlockPos offsetPos = context.getClickedPos().relative(face);
            Level world = context.getLevel();
            boolean canDetect = false;
            BlockEntity blockEntity = world.getBlockEntity(offsetPos);
            if (BlockEntityBehaviour.get(blockEntity, TransportedItemStackHandlerBehaviour.TYPE) != null) {
                canDetect = true;
            } else if (BlockEntityBehaviour.get(blockEntity, FluidTransportBehaviour.TYPE) != null) {
                canDetect = true;
            } else if (blockEntity != null && (context.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null) != null || context.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, blockEntity.getBlockPos(), null) != null)) {
                canDetect = true;
            } else if (blockEntity instanceof FunnelBlockEntity) {
                canDetect = true;
            }
            if (!canDetect) continue;
            preferredFacing = face;
            break;
        }
        if (preferredFacing == null) {
            Direction facing = context.getNearestLookingDirection();
            Direction direction = preferredFacing = context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? facing : facing.getOpposite();
        }
        if (preferredFacing.getAxis() == Direction.Axis.Y) {
            state = (BlockState)state.setValue((Property)TARGET, (Comparable)(preferredFacing == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR));
            preferredFacing = context.getHorizontalDirection();
        }
        return (BlockState)state.setValue((Property)FACING, preferredFacing);
    }

    public boolean isSignalSource(BlockState state) {
        return (Boolean)state.getValue((Property)POWERED);
    }

    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return this.isSignalSource(blockState) && (side == null || side != SmartObserverBlock.getTargetDirection(blockState).getOpposite()) ? 15 : 0;
    }

    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        worldIn.setBlock(pos, (BlockState)state.setValue((Property)POWERED, (Comparable)Boolean.valueOf(false)), 2);
        worldIn.updateNeighborsAt(pos, (Block)this);
    }

    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return side != ((Direction)state.getValue((Property)FACING)).getOpposite();
    }

    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, worldIn, pos, newState);
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        InvManipulationBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)worldIn, pos, InvManipulationBehaviour.TYPE);
        if (behaviour != null) {
            behaviour.onNeighborChanged(fromPos);
        }
    }

    public void onFunnelTransfer(Level world, BlockPos funnelPos, ItemStack transferred) {
        for (Direction direction : Iterate.directions) {
            BlockPos detectorPos = funnelPos.relative(direction);
            BlockState detectorState = world.getBlockState(detectorPos);
            if (!AllBlocks.SMART_OBSERVER.has(detectorState) || SmartObserverBlock.getTargetDirection(detectorState) != direction.getOpposite()) continue;
            this.withBlockEntityDo((BlockGetter)world, detectorPos, be -> {
                FilteringBehaviour filteringBehaviour = BlockEntityBehaviour.get(be, FilteringBehaviour.TYPE);
                if (filteringBehaviour == null) {
                    return;
                }
                if (!filteringBehaviour.test(transferred)) {
                    return;
                }
                be.activate(4);
            });
        }
    }

    @Override
    public Class<SmartObserverBlockEntity> getBlockEntityClass() {
        return SmartObserverBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SmartObserverBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.SMART_OBSERVER.get();
    }
}
