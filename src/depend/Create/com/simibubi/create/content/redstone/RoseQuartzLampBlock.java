/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.SignalGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.content.redstone;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.redstone.diodes.BrassDiodeBlock;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class RoseQuartzLampBlock
extends Block
implements IWrenchable {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty POWERING = BrassDiodeBlock.POWERING;
    public static final BooleanProperty ACTIVATE = BooleanProperty.create((String)"activate");

    public RoseQuartzLampBlock(BlockBehaviour.Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false))).setValue((Property)POWERING, (Comparable)Boolean.valueOf(false))).setValue((Property)ACTIVATE, (Comparable)Boolean.valueOf(false)));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState stateForPlacement = super.getStateForPlacement(pContext);
        return (BlockState)stateForPlacement.setValue((Property)POWERED, (Comparable)Boolean.valueOf(pContext.getLevel().hasNeighborSignal(pContext.getClickedPos())));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{POWERED, POWERING, ACTIVATE}));
    }

    public boolean shouldCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
        return false;
    }

    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if (pLevel.isClientSide) {
            return;
        }
        boolean isPowered = (Boolean)pState.getValue((Property)POWERED);
        if (isPowered == pLevel.hasNeighborSignal(pPos)) {
            return;
        }
        if (isPowered) {
            pLevel.setBlock(pPos, (BlockState)pState.cycle((Property)POWERED), 2);
            return;
        }
        this.forEachInCluster(pLevel, pPos, (currentPos, currentState) -> {
            pLevel.setBlock(currentPos, (BlockState)currentState.setValue((Property)POWERING, (Comparable)Boolean.valueOf(false)), 2);
            this.scheduleActivation(pLevel, (BlockPos)currentPos);
        });
        pLevel.setBlock(pPos, (BlockState)((BlockState)((BlockState)pState.setValue((Property)POWERED, (Comparable)Boolean.valueOf(true))).setValue((Property)POWERING, (Comparable)Boolean.valueOf(true))).setValue((Property)ACTIVATE, (Comparable)Boolean.valueOf(true)), 2);
        pLevel.updateNeighborsAt(pPos, (Block)this);
        this.scheduleActivation(pLevel, pPos);
    }

    private void scheduleActivation(Level pLevel, BlockPos pPos) {
        if (!pLevel.getBlockTicks().hasScheduledTick(pPos, (Object)this)) {
            pLevel.scheduleTick(pPos, (Block)this, 1);
        }
    }

    private void forEachInCluster(Level pLevel, BlockPos pPos, BiConsumer<BlockPos, BlockState> callback) {
        LinkedList<BlockPos> frontier = new LinkedList<BlockPos>();
        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        frontier.add(pPos);
        visited.add(pPos);
        while (!frontier.isEmpty()) {
            BlockPos pos = (BlockPos)frontier.remove(0);
            for (Direction d : Iterate.directions) {
                BlockState currentState;
                BlockPos currentPos = pos.relative(d);
                if (currentPos.distManhattan((Vec3i)pPos) > 16 || !visited.add(currentPos) || !(currentState = pLevel.getBlockState(currentPos)).is((Block)this)) continue;
                callback.accept(currentPos, currentState);
                frontier.add(currentPos);
            }
        }
    }

    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    public int getSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
        if (pDirection == null) {
            return 0;
        }
        BlockState toState = pLevel.getBlockState(pPos.relative(pDirection.getOpposite()));
        if (toState.is((Block)this)) {
            return 0;
        }
        if (toState.is(Blocks.COMPARATOR)) {
            return this.getDistanceToPowered(pLevel, pPos, pDirection);
        }
        return (Boolean)pState.getValue((Property)POWERING) != false ? 15 : 0;
    }

    private int getDistanceToPowered(BlockGetter level, BlockPos pos, Direction column) {
        BlockPos.MutableBlockPos currentPos = pos.mutable();
        for (int power = 15; power > 0; --power) {
            BlockState blockState = level.getBlockState((BlockPos)currentPos);
            if (!blockState.is((Block)this)) {
                return 0;
            }
            if (((Boolean)blockState.getValue((Property)POWERING)).booleanValue()) {
                return power;
            }
            currentPos.move(column);
        }
        return 0;
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRand) {
        boolean wasPowering = (Boolean)pState.getValue((Property)POWERING);
        boolean shouldBePowering = (Boolean)pState.getValue((Property)ACTIVATE);
        if (wasPowering || shouldBePowering) {
            pLevel.setBlock(pPos, (BlockState)((BlockState)pState.setValue((Property)ACTIVATE, (Comparable)Boolean.valueOf(false))).setValue((Property)POWERING, (Comparable)Boolean.valueOf(shouldBePowering)), 2);
        }
        pLevel.updateNeighborsAt(pPos, (Block)this);
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        return (BlockState)originalState.cycle((Property)POWERING);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        InteractionResult onWrenched = IWrenchable.super.onWrenched(state, context);
        if (!onWrenched.consumesAction()) {
            return onWrenched;
        }
        this.forEachInCluster(context.getLevel(), context.getClickedPos(), (currentPos, currentState) -> context.getLevel().updateNeighborsAt(currentPos, (Block)this));
        return onWrenched;
    }
}
