/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement
 *  com.simibubi.create.content.schematics.requirement.ItemRequirement$ItemUseType
 *  com.simibubi.create.foundation.utility.BlockHelper
 *  dev.ryanhcode.sable.api.block.BlockSubLevelLiftProvider
 *  javax.annotation.Nullable
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RotatedPillarBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.symmetric_sail;

import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.utility.BlockHelper;
import dev.ryanhcode.sable.api.block.BlockSubLevelLiftProvider;
import dev.simulated_team.simulated.index.SimBlockShapes;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.service.SimItemService;
import dev.simulated_team.simulated.util.placement_helpers.SymmetricSailPlacementHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SymmetricSailBlock
extends RotatedPillarBlock
implements IWrenchable,
BlockSubLevelLiftProvider,
SpecialBlockItemRequirement {
    private static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)new SymmetricSailPlacementHelper(SymmetricSailBlock::checkItem, SymmetricSailBlock::checkState));
    protected final DyeColor color;

    public SymmetricSailBlock(BlockBehaviour.Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
    }

    private static boolean checkItem(ItemStack i) {
        BlockItem bi;
        Item item = i.getItem();
        return item instanceof BlockItem && (bi = (BlockItem)item).getBlock() instanceof SymmetricSailBlock;
    }

    private static boolean checkState(BlockState state) {
        return state.getBlock() instanceof SymmetricSailBlock;
    }

    public static SymmetricSailBlock withCanvas(BlockBehaviour.Properties properties, DyeColor color) {
        return new SymmetricSailBlock(properties, color);
    }

    public void applyDye(BlockState state, Level world, BlockPos pos, Vec3 hit, @javax.annotation.Nullable DyeColor color) {
        BlockState newState = SimBlocks.DYED_SYMMETRIC_SAILS.get(color).getDefaultState();
        if (state != (newState = BlockHelper.copyProperties((BlockState)state, (BlockState)newState))) {
            world.setBlockAndUpdate(pos, newState);
            return;
        }
        List directions = IPlacementHelper.orderedByDistanceExceptAxis((BlockPos)pos, (Vec3)hit, (Direction.Axis)((Direction.Axis)state.getValue((Property)AXIS)));
        for (Direction d : directions) {
            BlockPos offset = pos.relative(d);
            BlockState adjacentState = world.getBlockState(offset);
            Block block = adjacentState.getBlock();
            if (!(block instanceof SymmetricSailBlock) || state.getValue((Property)AXIS) != adjacentState.getValue((Property)AXIS) || state == adjacentState) continue;
            world.setBlockAndUpdate(offset, newState);
            return;
        }
        ArrayList<BlockPos> frontier = new ArrayList<BlockPos>();
        frontier.add(pos);
        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        int timeout = 100;
        while (!frontier.isEmpty() && timeout-- >= 0) {
            BlockPos currentPos = (BlockPos)frontier.removeFirst();
            visited.add(currentPos);
            for (Direction d : Iterate.directions) {
                BlockState adjacentState;
                Block block;
                BlockPos offset;
                if (d.getAxis() == state.getValue((Property)AXIS) || visited.contains(offset = currentPos.relative(d)) || !((block = (adjacentState = world.getBlockState(offset)).getBlock()) instanceof SymmetricSailBlock) || adjacentState.getValue((Property)AXIS) != state.getValue((Property)AXIS)) continue;
                if (state != adjacentState) {
                    world.setBlockAndUpdate(offset, newState);
                }
                frontier.add(offset);
                visited.add(offset);
            }
        }
    }

    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        DyeColor color = SimItemService.getDyeColor(heldItem);
        if (color != null) {
            if (!level.isClientSide) {
                level.playSound(null, blockPos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.1f - level.random.nextFloat() * 0.2f);
            }
            this.applyDye(blockState, level, blockPos, blockHitResult.getLocation(), color);
            return ItemInteractionResult.SUCCESS;
        }
        IPlacementHelper placementHelper = PlacementHelpers.get((int)placementHelperId);
        if (placementHelper.matchesItem(heldItem)) {
            placementHelper.getOffset(player, level, blockState, blockPos, blockHitResult).placeInWorld(level, (BlockItem)heldItem.getItem(), player, interactionHand, blockHitResult);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public DyeColor getColor() {
        return this.color;
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return (BlockState)this.defaultBlockState().setValue((Property)AXIS, (Comparable)pContext.getNearestLookingDirection().getAxis());
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext ctx) {
        return SimBlockShapes.SYMMETRIC_SAIL.get((Direction.Axis)pState.getValue((Property)AXIS));
    }

    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        super.fallOn(level, state, pos, entity, 0.0f);
    }

    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(level, entity);
        } else {
            this.bounce(entity);
        }
    }

    private void bounce(Entity pEntity) {
        Vec3 Vec32 = pEntity.getDeltaMovement();
        if (Vec32.y < 0.0) {
            double d0 = pEntity instanceof LivingEntity ? 1.0 : 0.8;
            pEntity.setDeltaMovement(Vec32.x, -Vec32.y * (double)0.26f * d0, Vec32.z);
        }
    }

    public float sable$getLiftScalar() {
        return 0.0f;
    }

    public float sable$getParallelDragScalar() {
        return 1.75f;
    }

    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return SimBlocks.WHITE_SYMMETRIC_SAIL.asStack();
    }

    @NotNull
    public Direction sable$getNormal(BlockState blockState) {
        return Direction.get((Direction.AxisDirection)Direction.AxisDirection.POSITIVE, (Direction.Axis)((Direction.Axis)blockState.getValue((Property)AXIS)));
    }

    public ItemRequirement getRequiredItems(BlockState state, @Nullable BlockEntity blockEntity) {
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, SimBlocks.WHITE_SYMMETRIC_SAIL.asStack());
    }
}
