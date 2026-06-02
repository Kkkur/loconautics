/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.ScaffoldingBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.decoration;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MetalScaffoldingBlock
extends ScaffoldingBlock
implements IWrenchable {
    public MetalScaffoldingBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRand) {
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return true;
    }

    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (((Boolean)pState.getValue((Property)BOTTOM)).booleanValue()) {
            return AllShapes.SCAFFOLD_HALF;
        }
        return super.getCollisionShape(pState, pLevel, pPos, pContext);
    }

    public boolean isScaffolding(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return true;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (((Boolean)pState.getValue((Property)BOTTOM)).booleanValue()) {
            return AllShapes.SCAFFOLD_HALF;
        }
        if (!pContext.isHoldingItem(pState.getBlock().asItem())) {
            return AllShapes.SCAFFOLD_FULL;
        }
        return Shapes.block();
    }

    public VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return Shapes.block();
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
        BlockState stateBelow = pLevel.getBlockState(pCurrentPos.below());
        return pFacing == Direction.DOWN ? (BlockState)pState.setValue((Property)BOTTOM, (Comparable)Boolean.valueOf(!stateBelow.is((Block)this) && !stateBelow.isFaceSturdy((BlockGetter)pLevel, pCurrentPos.below(), Direction.UP))) : pState;
    }

    public boolean supportsExternalFaceHiding(BlockState state) {
        return true;
    }

    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        if (!(neighborState.getBlock() instanceof MetalScaffoldingBlock)) {
            return false;
        }
        if (!((Boolean)neighborState.getValue((Property)BOTTOM)).booleanValue() && ((Boolean)state.getValue((Property)BOTTOM)).booleanValue()) {
            return false;
        }
        return dir.getAxis() != Direction.Axis.Y;
    }
}
