/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.content.kinetics.base.HorizontalKineticBlock
 *  com.simibubi.create.foundation.block.IBE
 *  com.simibubi.create.foundation.utility.BlockHelper
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.Containers
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.simulated_team.simulated.content.blocks.portable_engine;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.BlockHelper;
import dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineBlockEntity;
import dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineInventory;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockShapes;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.multiloader.inventory.ContainerSlot;
import dev.simulated_team.simulated.multiloader.inventory.ItemInfoWrapper;
import dev.simulated_team.simulated.service.SimItemService;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PortableEngineBlock
extends HorizontalKineticBlock
implements IBE<PortableEngineBlockEntity> {
    private static final int BURN_TIME_THRESHOLD = 200;
    protected final DyeColor color;

    public PortableEngineBlock(BlockBehaviour.Properties properties, DyeColor color) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)BlockStateProperties.LIT, (Comparable)Boolean.valueOf(false)));
        this.color = color;
    }

    public static boolean isLitState(BlockState blockState) {
        return (Boolean)blockState.getValue((Property)BlockStateProperties.LIT);
    }

    public static Couple<Integer> getSpeedRange() {
        return Couple.create((Object)32, (Object)32);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{BlockStateProperties.LIT});
        super.createBlockStateDefinition(builder);
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && !SimBlocks.PORTABLE_ENGINES.contains(newState.getBlock())) {
            PortableEngineBlockEntity be = (PortableEngineBlockEntity)level.getBlockEntity(pos);
            if (be != null && !be.inventory.isEmpty()) {
                Containers.dropItemStack((Level)level, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (ItemStack)be.inventory.getItem(0));
            }
            level.removeBlockEntity(pos);
        }
    }

    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(HORIZONTAL_FACING);
    }

    public Direction.Axis getRotationAxis(BlockState state) {
        return ((Direction)state.getValue(HORIZONTAL_FACING)).getAxis();
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = this.getPreferredHorizontalFacing(context);
        if (preferred == null || context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            Direction horizontalDirection = context.getHorizontalDirection();
            return (BlockState)this.defaultBlockState().setValue(HORIZONTAL_FACING, (Comparable)(context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? horizontalDirection.getOpposite() : horizontalDirection));
        }
        return (BlockState)this.defaultBlockState().setValue(HORIZONTAL_FACING, (Comparable)preferred);
    }

    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        PortableEngineBlockEntity be = (PortableEngineBlockEntity)level.getBlockEntity(blockPos);
        PortableEngineInventory inventory = be.inventory;
        ContainerSlot slot = inventory.slot;
        ItemStack currentItemStack = slot.getStack().copy();
        if (currentItemStack.isEmpty() && heldItem.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        DyeColor color = SimItemService.getDyeColor(heldItem);
        if (color != null) {
            if (!level.isClientSide) {
                level.playSound(null, blockPos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.1f - level.random.nextFloat() * 0.2f);
            }
            BlockState newState = BlockHelper.copyProperties((BlockState)blockState, (BlockState)SimBlocks.PORTABLE_ENGINES.get(color).getDefaultState());
            level.setBlockAndUpdate(blockPos, newState);
            return ItemInteractionResult.SUCCESS;
        }
        if (AllItems.CREATIVE_BLAZE_CAKE.isIn(heldItem)) {
            if (!level.isClientSide) {
                if (be.isCurrentFuelInfinite()) {
                    if (be.isSuperHeated()) {
                        be.setCurrentBurnTime(0);
                        be.setSuperHeated(false);
                    } else {
                        be.setSuperHeated(true);
                    }
                } else {
                    be.setCurrentBurnTime(PortableEngineBlockEntity.INFINITE_THRESHOLD);
                }
            }
            if (!player.hasInfiniteMaterials()) {
                heldItem.shrink(1);
                player.setItemInHand(interactionHand, heldItem);
            }
        } else {
            if (!heldItem.isEmpty() && !inventory.canInsertItem(ItemInfoWrapper.generateFromStack(heldItem))) {
                return ItemInteractionResult.FAIL;
            }
            if (currentItemStack.isEmpty()) {
                slot.setStack(heldItem);
                player.setItemInHand(interactionHand, ItemStack.EMPTY);
            } else if (ItemStack.isSameItem((ItemStack)heldItem, (ItemStack)currentItemStack) && ItemStack.isSameItemSameComponents((ItemStack)heldItem, (ItemStack)currentItemStack)) {
                int targetAmount = currentItemStack.getCount() + heldItem.getCount();
                int transferAmount = Math.min((targetAmount = Math.min(targetAmount, currentItemStack.getMaxStackSize())) - currentItemStack.getCount(), heldItem.getCount());
                if (transferAmount <= 0) {
                    return ItemInteractionResult.sidedSuccess((boolean)level.isClientSide);
                }
                slot.shrink(-transferAmount);
                heldItem.shrink(transferAmount);
                player.setItemInHand(interactionHand, heldItem);
            } else {
                slot.setStack(ItemStack.EMPTY);
                player.getInventory().placeItemBackInInventory(currentItemStack);
                level.playSound(null, blockPos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, 1.0f + level.getRandom().nextFloat());
            }
            if (be.getTotalBurnTime() >= 720000 && !be.isTotalFuelInfinite()) {
                SimAdvancements.THAT_SHOULD_DO_FOR_NOW.awardTo(player);
            }
        }
        if (!slot.isEmpty()) {
            SimAdvancements.STEAMLESS_ENGINE.awardTo(player);
        }
        be.notifyUpdate();
        return ItemInteractionResult.sidedSuccess((boolean)level.isClientSide);
    }

    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        PortableEngineBlockEntity be = (PortableEngineBlockEntity)this.getBlockEntity((BlockGetter)pLevel, pPos);
        int power = 0;
        if (be != null) {
            if (be.isTotalFuelInfinite()) {
                return 15;
            }
            int ticks = be.getTotalBurnTime();
            if (ticks > 0) {
                power = Math.min(ticks / 200, 14) + 1;
            }
        }
        return power;
    }

    public DyeColor getColor() {
        return this.color;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext ctx) {
        return SimBlockShapes.PORTABLE_ENGINE.get((Direction)pState.getValue(HORIZONTAL_FACING));
    }

    public Class<PortableEngineBlockEntity> getBlockEntityClass() {
        return PortableEngineBlockEntity.class;
    }

    public BlockEntityType<? extends PortableEngineBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.PORTABLE_ENGINE.get();
    }
}
