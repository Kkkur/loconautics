/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.contraptions.actors.trainControls;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.ContraptionWorld;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class ControlsBlock
extends HorizontalDirectionalBlock
implements IWrenchable,
ProperWaterloggedBlock {
    public static final BooleanProperty OPEN = BooleanProperty.create((String)"open");
    public static final BooleanProperty VIRTUAL = BooleanProperty.create((String)"virtual");
    public static final MapCodec<ControlsBlock> CODEC = ControlsBlock.simpleCodec(ControlsBlock::new);

    public ControlsBlock(BlockBehaviour.Properties p_54120_) {
        super(p_54120_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue((Property)OPEN, (Comparable)Boolean.valueOf(false))).setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false))).setValue((Property)VIRTUAL, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{FACING, OPEN, WATERLOGGED, VIRTUAL}));
    }

    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        this.updateWater(pLevel, pState, pCurrentPos);
        return (BlockState)pState.setValue((Property)OPEN, (Comparable)Boolean.valueOf(pLevel instanceof ContraptionWorld));
    }

    public FluidState getFluidState(BlockState pState) {
        return this.fluidState(pState);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = this.withWater(super.getStateForPlacement(pContext), pContext);
        Direction horizontalDirection = pContext.getHorizontalDirection();
        Player player = pContext.getPlayer();
        state = (BlockState)state.setValue((Property)FACING, (Comparable)horizontalDirection.getOpposite());
        if (player != null && player.isShiftKeyDown()) {
            state = (BlockState)state.setValue((Property)FACING, (Comparable)horizontalDirection);
        }
        return state;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.CONTROLS.get((Direction)pState.getValue((Property)FACING));
    }

    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.CONTROLS_COLLISION.get((Direction)pState.getValue((Property)FACING));
    }

    @NotNull
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
