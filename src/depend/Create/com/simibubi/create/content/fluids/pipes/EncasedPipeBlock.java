/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.protocol.game.DebugPackets
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.PipeBlock
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.ticks.TickPriority
 */
package com.simibubi.create.content.fluids.pipes;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.decoration.encasing.EncasedBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockRotation;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import java.util.Map;
import java.util.function.Supplier;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.ticks.TickPriority;

public class EncasedPipeBlock
extends Block
implements IWrenchable,
SpecialBlockItemRequirement,
IBE<FluidPipeBlockEntity>,
EncasedBlock,
TransformableBlock {
    public static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = PipeBlock.PROPERTY_BY_DIRECTION;
    private final Supplier<Block> casing;

    public EncasedPipeBlock(BlockBehaviour.Properties properties, Supplier<Block> casing) {
        super(properties);
        this.casing = casing;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue((Property)BlockStateProperties.NORTH, (Comparable)Boolean.valueOf(false))).setValue((Property)BlockStateProperties.SOUTH, (Comparable)Boolean.valueOf(false))).setValue((Property)BlockStateProperties.DOWN, (Comparable)Boolean.valueOf(false))).setValue((Property)BlockStateProperties.UP, (Comparable)Boolean.valueOf(false))).setValue((Property)BlockStateProperties.WEST, (Comparable)Boolean.valueOf(false))).setValue((Property)BlockStateProperties.EAST, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{BlockStateProperties.NORTH, BlockStateProperties.EAST, BlockStateProperties.SOUTH, BlockStateProperties.WEST, BlockStateProperties.UP, BlockStateProperties.DOWN});
        super.createBlockStateDefinition(builder);
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        boolean blockTypeChanged;
        boolean bl = blockTypeChanged = state.getBlock() != newState.getBlock();
        if (blockTypeChanged && !world.isClientSide) {
            FluidPropagator.propagateChangedPipe((LevelAccessor)world, pos, state);
        }
        if (state.hasBlockEntity() && (blockTypeChanged || !newState.hasBlockEntity())) {
            world.removeBlockEntity(pos);
        }
    }

    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!world.isClientSide && state != oldState) {
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
        if (!((Boolean)state.getValue((Property)FACING_TO_PROPERTY_MAP.get(d))).booleanValue()) {
            return;
        }
        world.scheduleTick(pos, (Block)this, 1, TickPriority.HIGH);
    }

    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource r) {
        FluidPropagator.propagateChangedPipe((LevelAccessor)world, pos, state);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        context.getLevel().levelEvent(2001, context.getClickedPos(), Block.getId((BlockState)state));
        BlockState equivalentPipe = EncasedPipeBlock.transferSixWayProperties(state, AllBlocks.FLUID_PIPE.getDefaultState());
        Direction firstFound = Direction.UP;
        for (Direction d : Iterate.directions) {
            if (!((Boolean)state.getValue((Property)FACING_TO_PROPERTY_MAP.get(d))).booleanValue()) continue;
            firstFound = d;
            break;
        }
        FluidTransportBehaviour.cacheFlows((LevelAccessor)world, pos);
        world.setBlockAndUpdate(pos, ((FluidPipeBlock)AllBlocks.FLUID_PIPE.get()).updateBlockState(equivalentPipe, firstFound, null, (BlockAndTintGetter)world, pos));
        FluidTransportBehaviour.loadFlows((LevelAccessor)world, pos);
        return InteractionResult.SUCCESS;
    }

    public static BlockState transferSixWayProperties(BlockState from, BlockState to) {
        for (Direction d : Iterate.directions) {
            BooleanProperty property = FACING_TO_PROPERTY_MAP.get(d);
            to = (BlockState)to.setValue((Property)property, (Comparable)((Boolean)from.getValue((Property)property)));
        }
        return to;
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        return ItemRequirement.of(AllBlocks.FLUID_PIPE.getDefaultState(), be);
    }

    @Override
    public Class<FluidPipeBlockEntity> getBlockEntityClass() {
        return FluidPipeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FluidPipeBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.ENCASED_FLUID_PIPE.get();
    }

    @Override
    public Block getCasing() {
        return this.casing.get();
    }

    @Override
    public void handleEncasing(BlockState state, Level level, BlockPos pos, ItemStack heldItem, Player player, InteractionHand hand, BlockHitResult ray) {
        FluidTransportBehaviour.cacheFlows((LevelAccessor)level, pos);
        level.setBlockAndUpdate(pos, EncasedPipeBlock.transferSixWayProperties(state, this.defaultBlockState()));
        FluidTransportBehaviour.loadFlows((LevelAccessor)level, pos);
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return FluidPipeBlockRotation.rotate(pState, pRotation);
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return FluidPipeBlockRotation.mirror(pState, pMirror);
    }

    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        return FluidPipeBlockRotation.transform(state, transform);
    }
}
