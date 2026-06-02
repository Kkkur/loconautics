/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.simibubi.create.content.kinetics.steamEngine;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PoweredShaftBlock
extends AbstractShaftBlock {
    public PoweredShaftBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.EIGHT_VOXEL_POLE.get((Direction.Axis)pState.getValue((Property)AXIS));
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.POWERED_SHAFT.get();
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown() || !player.mayBuild()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        IPlacementHelper helper = PlacementHelpers.get((int)ShaftBlock.placementHelperId);
        if (helper.matchesItem(stack)) {
            return helper.getOffset(player, level, state, pos, hitResult).placeInWorld(level, (BlockItem)stack.getItem(), player, hand, hitResult);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!PoweredShaftBlock.stillValid(pState, (LevelReader)pLevel, pPos)) {
            pLevel.setBlock(pPos, (BlockState)((BlockState)AllBlocks.SHAFT.getDefaultState().setValue((Property)ShaftBlock.AXIS, (Comparable)((Direction.Axis)pState.getValue((Property)AXIS)))).setValue((Property)WATERLOGGED, (Comparable)((Boolean)pState.getValue((Property)WATERLOGGED))), 3);
        }
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return AllBlocks.SHAFT.asStack();
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return PoweredShaftBlock.stillValid(pState, pLevel, pPos);
    }

    public static boolean stillValid(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        for (Direction d : Iterate.directions) {
            BlockPos enginePos;
            BlockState engineState;
            Block block;
            if (d.getAxis() == pState.getValue((Property)AXIS) || !((block = (engineState = pLevel.getBlockState(enginePos = pPos.relative(d, 2))).getBlock()) instanceof SteamEngineBlock)) continue;
            SteamEngineBlock engine = (SteamEngineBlock)block;
            if (!SteamEngineBlock.getShaftPos(engineState, enginePos).equals((Object)pPos) || !SteamEngineBlock.isShaftValid(engineState, pState)) continue;
            return true;
        }
        return false;
    }

    public static BlockState getEquivalent(BlockState stateForPlacement) {
        return (BlockState)((BlockState)AllBlocks.POWERED_SHAFT.getDefaultState().setValue((Property)AXIS, (Comparable)((Direction.Axis)stateForPlacement.getValue((Property)ShaftBlock.AXIS)))).setValue((Property)WATERLOGGED, (Comparable)((Boolean)stateForPlacement.getValue((Property)WATERLOGGED)));
    }
}
