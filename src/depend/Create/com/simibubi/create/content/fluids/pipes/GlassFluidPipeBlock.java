/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SimpleWaterloggedBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.level.pathfinder.PathComputationType
 */
package com.simibubi.create.content.fluids.pipes;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.AxisPipeBlock;
import com.simibubi.create.content.fluids.pipes.StraightPipeBlockEntity;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.block.IBE;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class GlassFluidPipeBlock
extends AxisPipeBlock
implements IBE<StraightPipeBlockEntity>,
SimpleWaterloggedBlock,
SpecialBlockItemRequirement {
    public static final BooleanProperty ALT = BooleanProperty.create((String)"alt");

    public GlassFluidPipeBlock(BlockBehaviour.Properties p_i48339_1_) {
        super(p_i48339_1_);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)ALT, (Comparable)Boolean.valueOf(false))).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        super.createBlockStateDefinition(p_206840_1_.add(new Property[]{ALT, BlockStateProperties.WATERLOGGED}));
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (this.tryRemoveBracket(context)) {
            return InteractionResult.SUCCESS;
        }
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidTransportBehaviour.cacheFlows((LevelAccessor)world, pos);
        BlockState newState = (BlockState)this.toRegularPipe((LevelAccessor)world, pos, state).setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)((Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED)));
        world.setBlock(pos, newState, 3);
        FluidTransportBehaviour.loadFlows((LevelAccessor)world, pos);
        return InteractionResult.SUCCESS;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
        BlockState state = super.getStateForPlacement(context);
        return state == null ? null : (BlockState)state.setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(ifluidstate.getType() == Fluids.WATER));
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.getValue((Property)BlockStateProperties.WATERLOGGED) != false ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        return ItemRequirement.of(AllBlocks.FLUID_PIPE.getDefaultState(), be);
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public Class<StraightPipeBlockEntity> getBlockEntityClass() {
        return StraightPipeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends StraightPipeBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.GLASS_FLUID_PIPE.get();
    }
}
