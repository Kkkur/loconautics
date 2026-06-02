/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.common.Tags$Items
 *  net.neoforged.neoforge.common.util.FakePlayer
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 */
package com.simibubi.create.content.logistics.itemHatch;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.itemHatch.ItemHatchBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class ItemHatchBlock
extends HorizontalDirectionalBlock
implements IBE<ItemHatchBlockEntity>,
IWrenchable,
ProperWaterloggedBlock {
    public static final MapCodec<ItemHatchBlock> CODEC = ItemHatchBlock.simpleCodec(ItemHatchBlock::new);
    public static final BooleanProperty OPEN = BooleanProperty.create((String)"open");

    public ItemHatchBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)OPEN, (Comparable)Boolean.valueOf(false))).setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{OPEN, FACING, WATERLOGGED}));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        if (state == null) {
            return state;
        }
        if (pContext.getClickedFace().getAxis().isVertical()) {
            return null;
        }
        return this.withWater((BlockState)((BlockState)state.setValue((Property)FACING, (Comparable)pContext.getClickedFace().getOpposite())).setValue((Property)OPEN, (Comparable)Boolean.valueOf(false)), pContext);
    }

    public FluidState getFluidState(BlockState pState) {
        return this.fluidState(pState);
    }

    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        this.updateWater(pLevel, pState, pPos);
        return pState;
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        boolean depositItemInHand;
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }
        if (player instanceof FakePlayer) {
            return ItemInteractionResult.SUCCESS;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos.relative((Direction)state.getValue((Property)FACING)));
        if (blockEntity == null) {
            return ItemInteractionResult.FAIL;
        }
        IItemHandler targetInv = (IItemHandler)level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), null);
        if (targetInv == null) {
            return ItemInteractionResult.FAIL;
        }
        FilteringBehaviour filter = BlockEntityBehaviour.get((BlockGetter)level, pos, FilteringBehaviour.TYPE);
        if (filter == null) {
            return ItemInteractionResult.FAIL;
        }
        Inventory inventory = player.getInventory();
        ArrayList<ItemStack> failedInsertions = new ArrayList<ItemStack>();
        boolean anyInserted = false;
        boolean bl = depositItemInHand = !player.isShiftKeyDown();
        if (!depositItemInHand && stack.is(Tags.Items.TOOLS_WRENCH)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        for (int i = 0; i < inventory.items.size(); ++i) {
            ItemStack remainder;
            ItemStack item;
            if (Inventory.isHotbarSlot((int)i) != depositItemInHand || depositItemInHand && i != inventory.selected || (item = inventory.getItem(i)).isEmpty() || !item.getItem().canFitInsideContainerItems() && !PackageItem.isPackage(item) || !filter.getFilter().isEmpty() && !filter.test(item) || (remainder = ItemHandlerHelper.insertItemStacked((IItemHandler)targetInv, (ItemStack)item, (boolean)true)).getCount() == item.getCount()) continue;
            ItemStack extracted = inventory.removeItem(i, item.getCount() - remainder.getCount());
            remainder = ItemHandlerHelper.insertItemStacked((IItemHandler)targetInv, (ItemStack)extracted, (boolean)false);
            anyInserted = true;
            if (remainder.isEmpty()) continue;
            failedInsertions.add(remainder);
        }
        failedInsertions.forEach(arg_0 -> ((Inventory)inventory).placeItemBackInInventory(arg_0));
        if (!anyInserted) {
            return ItemInteractionResult.SUCCESS;
        }
        AllSoundEvents.ITEM_HATCH.playOnServer(level, (Vec3i)pos);
        level.setBlockAndUpdate(pos, (BlockState)state.setValue((Property)OPEN, (Comparable)Boolean.valueOf(true)));
        level.scheduleTick(pos, (Block)this, 10);
        CreateLang.translate(depositItemInHand ? "item_hatch.deposit_item" : "item_hatch.deposit_inventory", new Object[0]).sendStatus(player);
        return ItemInteractionResult.SUCCESS;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AllShapes.ITEM_HATCH.get(((Direction)pState.getValue((Property)FACING)).getOpposite());
    }

    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (((Boolean)pState.getValue((Property)OPEN)).booleanValue()) {
            pLevel.setBlockAndUpdate(pPos, (BlockState)pState.setValue((Property)OPEN, (Comparable)Boolean.valueOf(false)));
        }
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    public Class<ItemHatchBlockEntity> getBlockEntityClass() {
        return ItemHatchBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ItemHatchBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.ITEM_HATCH.get();
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
