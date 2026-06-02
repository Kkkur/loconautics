/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.network.protocol.game.DebugPackets
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraft.world.ticks.TickPriority
 */
package com.simibubi.create.content.fluids.pipes;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.equipment.wrench.IWrenchableWithBracket;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.IAxisPipe;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.Map;
import java.util.Optional;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;

public class AxisPipeBlock
extends RotatedPillarBlock
implements IWrenchableWithBracket,
IAxisPipe {
    public AxisPipeBlock(BlockBehaviour.Properties p_i48339_1_) {
        super(p_i48339_1_);
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        boolean blockTypeChanged;
        boolean bl = blockTypeChanged = state.getBlock() != newState.getBlock();
        if (blockTypeChanged && !world.isClientSide) {
            FluidPropagator.propagateChangedPipe((LevelAccessor)world, pos, state);
        }
        if (state != newState && !isMoving) {
            this.removeBracket((BlockGetter)world, pos, true).ifPresent(stack -> Block.popResource((Level)world, (BlockPos)pos, (ItemStack)stack));
        }
        if (state.hasBlockEntity() && (blockTypeChanged || !newState.hasBlockEntity())) {
            world.removeBlockEntity(pos);
        }
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!AllBlocks.COPPER_CASING.isIn(stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        BlockState newState = AllBlocks.ENCASED_FLUID_PIPE.getDefaultState();
        for (Direction d : Iterate.directionsInAxis((Direction.Axis)this.getAxis(state))) {
            newState = (BlockState)newState.setValue((Property)EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(d), (Comparable)Boolean.valueOf(true));
        }
        FluidTransportBehaviour.cacheFlows((LevelAccessor)level, pos);
        level.setBlockAndUpdate(pos, newState);
        FluidTransportBehaviour.loadFlows((LevelAccessor)level, pos);
        return ItemInteractionResult.SUCCESS;
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (world.isClientSide) {
            return;
        }
        if (state != oldState) {
            world.scheduleTick(pos, (Block)this, 1, TickPriority.HIGH);
        }
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return AllBlocks.FLUID_PIPE.asStack();
    }

    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block otherBlock, BlockPos neighborPos, boolean isMoving) {
        DebugPackets.sendNeighborsUpdatePacket((Level)world, (BlockPos)pos);
        Direction d = FluidPropagator.validateNeighbourChange(state, world, pos, otherBlock, neighborPos, isMoving);
        if (d == null) {
            return;
        }
        if (!AxisPipeBlock.isOpenAt(state, d)) {
            return;
        }
        world.scheduleTick(pos, (Block)this, 1, TickPriority.HIGH);
    }

    public static boolean isOpenAt(BlockState state, Direction d) {
        return d.getAxis() == state.getValue((Property)AXIS);
    }

    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource r) {
        FluidPropagator.propagateChangedPipe((LevelAccessor)world, pos, state);
    }

    public VoxelShape getShape(BlockState state, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        return AllShapes.EIGHT_VOXEL_POLE.get((Direction.Axis)state.getValue((Property)AXIS));
    }

    public BlockState toRegularPipe(LevelAccessor world, BlockPos pos, BlockState state) {
        Direction side = Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)((Direction.Axis)state.getValue((Property)AXIS)));
        Map facingToPropertyMap = FluidPipeBlock.PROPERTY_BY_DIRECTION;
        return ((FluidPipeBlock)AllBlocks.FLUID_PIPE.get()).updateBlockState((BlockState)((BlockState)AllBlocks.FLUID_PIPE.getDefaultState().setValue((Property)facingToPropertyMap.get(side), (Comparable)Boolean.valueOf(true))).setValue((Property)facingToPropertyMap.get(side.getOpposite()), (Comparable)Boolean.valueOf(true)), side, null, (BlockAndTintGetter)world, pos);
    }

    @Override
    public Direction.Axis getAxis(BlockState state) {
        return (Direction.Axis)state.getValue((Property)AXIS);
    }

    @Override
    public Optional<ItemStack> removeBracket(BlockGetter world, BlockPos pos, boolean inOnReplacedContext) {
        BracketedBlockEntityBehaviour behaviour = BlockEntityBehaviour.get(world, pos, BracketedBlockEntityBehaviour.TYPE);
        if (behaviour == null) {
            return Optional.empty();
        }
        BlockState bracket = behaviour.removeBracket(inOnReplacedContext);
        if (bracket == null) {
            return Optional.empty();
        }
        return Optional.of(new ItemStack((ItemLike)bracket.getBlock()));
    }
}
